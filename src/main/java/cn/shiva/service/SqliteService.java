package cn.shiva.service;

import cn.shiva.entity.FileRecovery;
import cn.shiva.entity.NovelFile;
import cn.shiva.mapper.FileRecoveryMapper;
import cn.shiva.mapper.NovelFileMapper;
import cn.shiva.mapper.SqliteMapper;
import cn.shiva.utils.CommonUtil;
import com.alibaba.fastjson2.util.DateUtils;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.OSSObjectSummary;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * SQLite 基础服务
 *
 * @author shiva   2023-12-17 14:22
 */
@Slf4j
@Service
public class SqliteService {

    @Autowired
    private SqliteMapper sqliteMapper;
    @Autowired
    private AliOssComponent ossComponent;
    @Autowired
    private NovelFileMapper novelFileMapper;
    @Autowired
    private NovelService novelService;
    @Autowired
    private FileRecoveryMapper recoveryMapper;

    /**
     * 初始化OSS对应的文件树
     * 1.删除全部的旧文件记录，清空
     * 2.清空关联表，文件和标签；文件都重新弄了
     * 3.开始遍历文件夹，拿到文件和文件夹都保存到数据库里，区分类型
     * 4.然后开始递归，文件夹的下一级，搞好上级关联
     */
    public void initFromOss() {
        int i = sqliteMapper.clearNovel();
        log.info("清理文件共：{}", i);
        int labelNum = sqliteMapper.clearNovelLabel();
        log.info("清理标签关系共：{}", labelNum);
        //开始递归
        listOss2Db("novel/", 0L);
    }

    /**
     * 开始递归
     */
    public void listOss2Db(String prefix, Long parentId) {
        ListObjectsV2Result listObjectsV2Result = ossComponent.listObjects(prefix);

        // objectSummaries的列表中给出的是目录下的文件:
        for (OSSObjectSummary objectSummary : listObjectsV2Result.getObjectSummaries()) {
            String key = objectSummary.getKey();
            //组装数据保存数据库
            NovelFile build = NovelFile.builder()
                    .name(CommonUtil.getNameFromPath(key))
                    .size(CommonUtil.calcFileSize(objectSummary.getSize()))
                    .type("file")
                    .lastModifyTime(DateUtils.format(objectSummary.getLastModified()))
                    .ossPath(key).filePath("https://" + ossComponent.getBucketName() + ossComponent.getAreaSuffix() + key)
                    .parentId(parentId)
                    .build();
            //在OSS创建的文件夹，会附带一个空文件，这个就不保存了
            if (StringUtils.isBlank(build.getName()) && build.getSize() == 0) {
                continue;
            }
            novelFileMapper.insert(build);
        }
        // 遍历文件夹：
        // commonPrefixs列表中显示的是fun目录下的所有子文件夹。
        // 由于fun/movie/001.avi和fun/movie/007.avi属于fun文件夹下的movie目录，因此这两个文件未在列表中。
        for (String commonPrefix : listObjectsV2Result.getCommonPrefixes()) {
            //组装数据保存数据库
            NovelFile build = NovelFile.builder()
                    .name(
                            CommonUtil.getNameFromFolder(commonPrefix))
                    .type("folder").ossPath(commonPrefix).filePath("https://" + ossComponent.getBucketName() + ossComponent.getAreaSuffix() + commonPrefix
                    )
                    .parentId(parentId)
                    .build();
            novelFileMapper.insert(build);
            listOss2Db(commonPrefix, build.getId());
        }
    }


    /**
     * 删除文件或者文件夹，进入回收站;
     * 1.区分文件和文件夹
     * 2.软删除，就是移动到回收站；
     * 3.原有的标签全部清空
     */
    public void delete(Long novelId) {
        NovelFile novelFile = novelFileMapper.selectById(novelId);
        if (novelFile == null) {
            return;
        }
        //判断类型
        if ("file".equals(novelFile.getType())) {
            novelService.deleteNovel(novelFile);
        }
        if ("folder".equals(novelFile.getType())) {
            //如果下级存在文件，那不允许删除
            List<NovelFile> childNovels = novelFileMapper.listByParentId(novelFile.getId());
            if (!childNovels.isEmpty()) {
                return;
            }
            // 只删sqlite，OSS自己去动
            novelFileMapper.deleteById(novelFile.getId());
        }

    }

    /**
     * 彻底删除回收站文件
     * 先删除OSS文件，再删除数据库
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void completelyDeleteFile(Long recoveryId) {
        FileRecovery fileRecovery = recoveryMapper.selectById(recoveryId);
        if (fileRecovery == null) {
            return;
        }
        ossComponent.deleteObject(fileRecovery.getOssPath());
        //最后删除数据库
        recoveryMapper.deleteById(fileRecovery);
    }

    /**
     * 递归删除，只删除本地数据，不动OSS
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void deleteFold(Long novelId) {
        List<NovelFile> childNovels = novelFileMapper.listByParentId(novelId);
        childNovels.forEach(item -> {
            if ("folder".equals(item.getType())) {
                deleteFold(item.getId());
            } else {
                //文件类型删除
                novelFileMapper.deleteById(item);
            }
        });
        NovelFile novelFile = novelFileMapper.selectById(novelId);
        novelFileMapper.deleteById(novelFile);
    }

    /**
     * 更新文件夹下文件
     * 1.删除文件夹下面的文件和文件夹，重新获取
     * 2.然后调取别的方法，更新就行
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void reloadFolder(Long folderId) {
        //递归删除
        NovelFile novelFile = novelFileMapper.selectById(folderId);
        if (novelFile == null) {
            return;
        }
        //先删掉数据库，重新开始加
        deleteFold(novelFile.getId());
        //重新加
        differentialImport(novelFile.getOssPath());
    }

    /**
     * 文件恢复；
     * 1.判断恢复位置，是不是存在相同文件；如果存在就返回错误消息
     * 2.判断文件夹是否存在，如果文件夹也不存在；那就得先加到数据库
     * 3.准备好了，再开始复制文件；复制文件，然后新增novel数据，删除recovery数据
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void recoveryFile(Long recoveryId) {
        FileRecovery fileRecovery = recoveryMapper.selectById(recoveryId);
        if (fileRecovery == null) {
            throw new RuntimeException("文件不存在！");
        }
        //复制到novel
        NovelFile novel = new NovelFile();
        BeanUtils.copyProperties(fileRecovery, novel);
        novel.setId(null);
        novel.setOssPath(novel.getOssPath().replace("recovery/", "novel/"));
        novel.setFilePath(novel.getFilePath().replace("recovery/", "novel/"));

        //第一步：判断OSS上是不是存在相同文件
        boolean b = ossComponent.doesObjectExist(novel.getOssPath());
        if (b) {
            throw new RuntimeException("已存在相同文件！");
        }
        //第二步：判断文件夹是不是存在
        //根据OSS路径，补全路径文件夹
        Long parentId = competeFolder(novel.getOssPath());
        novel.setParentId(parentId);

        //第三步：
        ossComponent.moveObject(fileRecovery.getOssPath(), novel.getOssPath());
        recoveryMapper.deleteById(fileRecovery);
        novelFileMapper.insert(novel);
    }

    /**
     * 根据路径，已 / 为分隔符，在数据库里补全文件夹；
     * 文件夹都是 / 结尾，文件都是 txt结尾
     * novel/3.这是一个神奇的世界/000 留着偶尔翻一翻/战舰/小塞壬.txt
     */
    public Long competeFolder(String ossPath) {
        //先把尾部的文件名去掉
        if (!ossPath.endsWith("/")) {
            ossPath = ossPath.substring(0, ossPath.lastIndexOf("/"));
        }

        Long parentId = 0L;
        //先根据/进行拆分，然后一层一层拼接，再去数据库里查询
        String[] split = ossPath.split("/");
        for (int i = 1; i <= split.length; i++) {
            //开始拼接，拼接需要按数量，拿到拼接后的数组
            String joinPath = String.join("/", Arrays.copyOfRange(split, 0, i)) + "/";
            String currentName = split[i - 1];
            NovelFile novelFile = novelFileMapper.getByOssPath(joinPath);
            //如果目录已经存在，搞成上级目录
            if (novelFile != null) {
                parentId = novelFile.getId();
                continue;
            }
            //如果不存在，那就得 新增了
            novelFile = new NovelFile();
            novelFile.setName(currentName);
            novelFile.setParentId(parentId);
            novelFile.setType("folder");
            novelFile.setOssPath(joinPath);
            novelFile.setFilePath("https://" + ossComponent.getBucketName() + ossComponent.getAreaSuffix() + novelFile.getOssPath());
            novelFileMapper.insert(novelFile);
            //最后设置上级id
            parentId = novelFile.getId();
        }
        return parentId;
    }

    /**
     * 通过路径，导入新的文件夹
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void differentialImport(String path) {
        boolean b = path.startsWith("novel/");
        if (!b) {
            path = "novel/" + path;
        }
        //先要拿到路径的上级的ID，
        //一直查到同级目录不存在，再开始遍历
        Long parentId = 0L;
        String[] split = path.split("/");
        for (int i = 1; i <= split.length; i++) {
            //开始拼接，拼接需要按数量，拿到拼接后的数组
            String joinPath = String.join("/", Arrays.copyOfRange(split, 0, i)) + "/";
            String currentName = split[i - 1];
            NovelFile novelFile = novelFileMapper.getByOssPath(joinPath);
            //如果目录已经存在，搞成上级目录
            if (novelFile != null) {
                parentId = novelFile.getId();
                continue;
            }
            //先要把当前这个文件夹保存进去
            novelFile = new NovelFile();
            novelFile.setName(currentName);
            novelFile.setParentId(parentId);
            novelFile.setType("folder");
            novelFile.setOssPath(joinPath);
            novelFile.setFilePath("https://" + ossComponent.getBucketName() + ossComponent.getAreaSuffix() + novelFile.getOssPath());
            novelFileMapper.insert(novelFile);
            parentId = novelFile.getId();
            //查询到不存在的
            listOss2Db(joinPath, parentId);
            break;
        }
    }

    /**
     * 1.先检查上当前文件夹下面，有没有相同名字的文件；有的话不能传
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void uploadNovel(Long folderId, MultipartFile file) throws Exception {
        //根目录文件
        NovelFile rootFolder = novelFileMapper.selectById(folderId);
        //下一级的列表
        List<NovelFile> novelFiles = novelFileMapper.listByParentId(folderId);
        if (!novelFiles.isEmpty()) {
            //存在相同名字的，不能传
            long count = novelFiles.stream().filter(i -> file.getOriginalFilename().equals(i.getName())).count();
            if (count > 0) {
                return;
            }
        }

        //最后实际上传
        NovelFile upload = ossComponent.upload(rootFolder.getOssPath(), file);
        upload.setSize(CommonUtil.calcFileSize(file.getSize()));
        upload.setParentId(folderId);
        novelFileMapper.insert(upload);
    }

    /**
     * 文件重新上传，更新一下大小就行了
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void reUploadNovel(Long novelId, MultipartFile file) throws Exception {
        //文件
        NovelFile novelFile = novelFileMapper.selectById(novelId);
        //上传文件
        String pathWithoutName = novelFile.getOssPath().replace(novelFile.getName(), "");
        NovelFile upload = ossComponent.upload(pathWithoutName, file, "false");
        //更新大小
        novelFile.setSize(CommonUtil.calcFileSize(file.getSize()));
        novelFileMapper.updateById(upload);
    }


}

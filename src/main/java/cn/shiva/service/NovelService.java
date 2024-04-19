package cn.shiva.service;

import cn.shiva.core.domain.R;
import cn.shiva.entity.FileRecovery;
import cn.shiva.entity.NovelFile;
import cn.shiva.mapper.FileRecoveryMapper;
import cn.shiva.mapper.NovelFileMapper;
import cn.shiva.mapper.NovelLabelMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文件相关服务
 *
 * @author shiva   2023-12-26 9:07
 */
@Slf4j
@Service
public class NovelService {

    @Autowired
    private NovelFileMapper novelFileMapper;
    @Autowired
    private AliOssComponent ossComponent;
    @Autowired
    private FileRecoveryMapper recoveryMapper;
    @Autowired
    private NovelLabelMapper labelMapper;

    /**
     * 移动OSS文件；删除本地novelFile文件；新增回收站文件
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void deleteNovel(NovelFile novel) {
        //复制到旧版
        FileRecovery recovery = new FileRecovery();
        BeanUtils.copyProperties(novel, recovery);
        recovery.setId(null);
        recovery.setOssPath(recovery.getOssPath().replace("novel/", "recovery/"));
        recovery.setFilePath(recovery.getFilePath().replace("novel/", "recovery/"));
        //sqlite修改
        recoveryMapper.insert(recovery);
        novelFileMapper.deleteById(novel);
        //中间表的标签要删除；
        labelMapper.deleteByNovelId(novel.getId());
        //OSS移动
        ossComponent.moveObject(novel.getOssPath(), recovery.getOssPath());
    }

    /**
     * 将文件移动到新文件夹
     *
     * @param folderId 新文件夹id
     * @param novelId  文件id
     */
    @Transactional(readOnly = false)
    public R<String> actualMove(Long folderId, Long novelId) {
        boolean rootFlag = false;
        if (0 == folderId) {
            rootFlag = true;
            NovelFile byOssPath = novelFileMapper.getByOssPath("novel/");
            folderId = byOssPath.getId();
        }
        //拿到文件和文件夹
        NovelFile novelFile = novelFileMapper.selectById(novelId);
        NovelFile newFolder = novelFileMapper.selectById(folderId);
        String oldPath = novelFile.getOssPath();
        //整理好新的数据
        //如果是根目录，直接设置0
        novelFile.setParentId(rootFlag ? 0 : folderId);
        novelFile.setOssPath(newFolder.getOssPath() + novelFile.getName());
        novelFile.setFilePath("https://" + ossComponent.getBucketName() + ossComponent.getAreaSuffix() + novelFile.getOssPath());
        //先移动，得判断下是不是存在文件
        boolean exist = ossComponent.doesObjectExist(novelFile.getOssPath());
        if (exist) {
            //存在直接结束
            return R.fail("目标路径已存在文件");
        }
        ossComponent.moveObject(oldPath, novelFile.getOssPath());

        novelFileMapper.updateById(novelFile);
        return R.ok("移动成功");
    }

    /**
     * 重命名文件
     * 1.先把数据补全，全部正确为主
     * 2.判断文件名字是否重复
     */
    public R<String> renameFile(Long novelId, String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return R.fail("文件名不正确!");
        }
        if (!fileName.endsWith(".txt")) {
            fileName = fileName + ".txt";
        }
        //先判断文件名重复
        NovelFile novelFile = novelFileMapper.selectById(novelId);
        String oldOssPath = novelFile.getOssPath();
        String newOssPath = oldOssPath.substring(0, novelFile.getOssPath().lastIndexOf("/") + 1) + fileName;
        if (ossComponent.doesObjectExist(newOssPath)) {
            return R.fail("目标路径已存在文件");
        }
        //拼接新的
        novelFile.setName(fileName);
        novelFile.setOssPath(newOssPath);
        novelFile.setFilePath("https://" + ossComponent.getBucketName() + ossComponent.getAreaSuffix() + novelFile.getOssPath());
        // 实际重命名
        ossComponent.moveObject(oldOssPath, newOssPath);
        novelFileMapper.updateById(novelFile);
        return R.ok("重命名成功");
    }

}

package cn.shiva.service;

import cn.shiva.entity.NovelFile;
import cn.shiva.mapper.NovelFileMapper;
import cn.shiva.mapper.SqliteMapper;
import cn.shiva.utils.CommonUtil;
import com.alibaba.fastjson2.util.DateUtils;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.OSSObjectSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * SQLite 基础服务
 *
 * @author shiva   2023-12-17 14:22
 */
@Slf4j
@Service
public class SqliteService {

    @Value("${aliOss.bucketName}")
    private String bucketName;
    @Value("${aliOss.areaSuffix}")
    private String areaSuffix;

    @Autowired
    private SqliteMapper sqliteMapper;
    @Autowired
    private AliOssComponent ossComponent;
    @Autowired
    private NovelFileMapper novelFileMapper;
    @Autowired
    private NovelService novelService;

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
                    .ossPath(key)
                    .filePath("https://" + bucketName + areaSuffix + key)
                    .parentId(parentId)
                    .build();
            novelFileMapper.insert(build);
        }
        // 遍历文件夹：
        // commonPrefixs列表中显示的是fun目录下的所有子文件夹。
        // 由于fun/movie/001.avi和fun/movie/007.avi属于fun文件夹下的movie目录，因此这两个文件未在列表中。
        for (String commonPrefix : listObjectsV2Result.getCommonPrefixes()) {
            //组装数据保存数据库
            NovelFile build = NovelFile.builder()
                    .name(CommonUtil.getNameFromFolder(commonPrefix))
                    .type("folder")
                    .ossPath(commonPrefix)
                    .filePath("https://" + bucketName + areaSuffix + commonPrefix)
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

        }

    }
}

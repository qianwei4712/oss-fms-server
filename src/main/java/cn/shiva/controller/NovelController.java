package cn.shiva.controller;

import cn.shiva.core.domain.R;
import cn.shiva.entity.NovelFile;
import cn.shiva.entity.bo.FileDownloadBO;
import cn.shiva.mapper.NovelFileMapper;
import cn.shiva.mapper.NovelLabelMapper;
import cn.shiva.service.AliOssComponent;
import cn.shiva.service.NovelService;
import cn.shiva.service.SqliteService;
import cn.shiva.utils.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author shiva   2023-12-17 23:00
 */
@RestController
@RequestMapping(value = "/api/novel")
public class NovelController {

    @Autowired
    private NovelFileMapper novelFileMapper;
    @Autowired
    private SqliteService sqliteService;
    @Autowired
    private NovelLabelMapper novelLabelMapper;
    @Autowired
    private ThreadPool pool;
    @Autowired
    private AliOssComponent aliOssComponent;
    @Autowired
    private NovelService novelService;

    /**
     * 根据上级目录id,查询列表；
     * 文件夹在最前面，下级文件不直接展示
     */
    @GetMapping("pageNovelAndFolder")
    public R<List<NovelFile>> pageNovelAndFolder(Long parentId) throws InterruptedException {
        List<NovelFile> list = novelFileMapper.listByParentId(parentId);
        //遍历，多线程获取数据
        CountDownLatch latch = new CountDownLatch(list.size());
        for (NovelFile novelFile : list) {
            pool.instance().execute(() -> {
                novelFile.setLabels(novelLabelMapper.listLabelsByNovelId(novelFile.getId()));
                novelFile.setRealPath(novelFile.getOssPath().replace("novel/", "").replace(novelFile.getName(), ""));
                latch.countDown();
            });
        }
        boolean await = latch.await(15, TimeUnit.SECONDS);
        return R.ok(list);
    }

    /**
     * 清空数据库，从OSS重置
     */
    @GetMapping("initFromOss")
    public R<String> initFromOss() {
        sqliteService.initFromOss();
        return R.ok();
    }

    /**
     * 文件下载，先到OSS拿到文件字符串，返回回去在前端下载
     */
    @GetMapping("downloadFromOss")
    public R<FileDownloadBO> downloadFromOss(Long novelId) throws IOException {
        NovelFile novelFile = novelFileMapper.selectById(novelId);
        if (novelFile == null) {
            return R.ok();
        }
        String content = aliOssComponent.streamDownload(novelFile.getOssPath());
        content = new String(content.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        return R.ok(FileDownloadBO.builder()
                .content(content)
                .name(novelFile.getName())
                .build());
    }

    /**
     * 文件搜索，一次性查询到全部
     */
    @GetMapping("searchNovel")
    public R<List<NovelFile>> searchNovel(String key) throws InterruptedException {
        List<NovelFile> list = novelFileMapper.listBySearchParam(key);
        //遍历，多线程获取数据
        CountDownLatch latch = new CountDownLatch(list.size());
        for (NovelFile novelFile : list) {
            pool.instance().execute(() -> {
                novelFile.setLabels(novelLabelMapper.listLabelsByNovelId(novelFile.getId()));
                novelFile.setRealPath(novelFile.getOssPath().replace("novel/", "").replace(novelFile.getName(), ""));
                latch.countDown();
            });
        }
        boolean await = latch.await(15, TimeUnit.SECONDS);
        return R.ok(list);
    }

    /**
     * 删除文件或者文件夹，进入回收站;
     * 1.区分文件和文件夹
     * 2.软删除，就是移动到回收站；
     * 3.原有的标签全部清空
     */
    @GetMapping("delete")
    public R<String> delete(Long novelId) throws Exception {
        if (novelId == null) {
            return R.fail("文件不存在");
        }
        sqliteService.delete(novelId);
        return R.ok();
    }

    /**
     * 差量导入，按新路径进行导入
     */
    @GetMapping("differentialImport")
    public R<String> differentialImport(String path) {
        sqliteService.differentialImport(path);
        return R.ok();
    }

    /**
     * 根据上级文件夹id,拿到下级列表
     */
    @GetMapping("listFolder")
    public R<List<NovelFile>> listFolder(Long parentId) {
        List<NovelFile> list = novelFileMapper.listFolderParentId(parentId);
        return R.ok(list);
    }

    /**
     * 实际移动文件夹
     */
    @GetMapping("actualMove")
    public R<String> actualMove(Long folderId, Long novelId) {
        return novelService.actualMove(folderId, novelId);
    }


    //TODO 重命名文件

    //TODO 移动文件到新的路径


    //TODO 更新简述，其他信息不允许更新

}

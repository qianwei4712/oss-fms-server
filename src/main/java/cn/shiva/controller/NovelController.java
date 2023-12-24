package cn.shiva.controller;

import cn.shiva.core.domain.R;
import cn.shiva.entity.NovelFile;
import cn.shiva.mapper.NovelFileMapper;
import cn.shiva.mapper.NovelLabelMapper;
import cn.shiva.service.SqliteService;
import cn.shiva.utils.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    //TODO 上传单个小说：可以填写简述

    //TODO 重命名文件

    //TODO 移动文件到新的路径

    //TODO 移动整个文件夹到新路径

    //TODO 更新简述，其他信息不允许更新


    //TODO 删除文件，进入回收站

    //TODO 获得访问地址

    //TODO 下载到终端


}

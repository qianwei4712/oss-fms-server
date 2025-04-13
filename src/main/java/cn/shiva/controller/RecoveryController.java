package cn.shiva.controller;

import cn.shiva.core.domain.R;
import cn.shiva.entity.FileRecovery;
import cn.shiva.mapper.FileRecoveryMapper;
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
@RequestMapping(value = "/api/recovery")
public class RecoveryController {

    @Autowired
    private ThreadPool pool;
    @Autowired
    private FileRecoveryMapper recoveryMapper;
    @Autowired
    private SqliteService sqliteService;

    @GetMapping("allRecoveryFile")
    public R<List<FileRecovery>> allRecoveryFile() throws InterruptedException {
        List<FileRecovery> list = recoveryMapper.listAll();
        //遍历，多线程获取数据
        CountDownLatch latch = new CountDownLatch(list.size());
        for (FileRecovery fileRecovery : list) {
            pool.instance().execute(() -> {
                fileRecovery.setRealPath(fileRecovery.getOssPath().replace("recovery/", "").replace(fileRecovery.getName(), ""));
                latch.countDown();
            });
        }
        boolean await = latch.await(15, TimeUnit.SECONDS);
        return R.ok(list);
    }


    /**
     * 恢复文件;
     */
    @GetMapping("recoveryFile")
    public R<String> recoveryFile(Long recoveryId) {
        try {
            sqliteService.recoveryFile(recoveryId);
            return R.ok();
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 彻底删除文件
     */
    @GetMapping("completelyDelete")
    public R<String> completelyDelete(Long recoveryId) {
        sqliteService.completelyDeleteFile(recoveryId);
        return R.ok();
    }

    /**
     * 删除全部文件
     */
    @GetMapping("allDelete")
    public R<String> allDelete() {
        sqliteService.deleteAllFromRecovery();
        return R.ok();
    }

}

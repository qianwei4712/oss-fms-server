package cn.shiva.controller;

import cn.shiva.core.domain.R;
import cn.shiva.entity.FileRecovery;
import cn.shiva.mapper.FileRecoveryMapper;
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


    //TODO 恢复文件
    //TODO 彻底删除文件，批量

}

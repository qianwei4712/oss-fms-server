package cn.shiva.core.initialization;

import cn.shiva.service.SqliteService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * @author shiva   2025-04-11 13:28
 */
@Component
@DependsOn("sqliteTableInit")
public class DefaultSyncOssData {

    @Autowired
    private SqliteService sqliteService;
    
    /**
     * 搞了一个服务器镜像，在需要的时候按时按量启动服务
     * 但是出现一个问题；镜像回复的内容，不会更新数据库
     * 因此可以在启动的时候，默认重新同步一下OSS文件目录
     */
    @PostConstruct
    public void syncOss() {
        sqliteService.initFromOss();
    }

}

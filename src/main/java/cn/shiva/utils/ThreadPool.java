package cn.shiva.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author ruoyi
 **/
@Configuration
public class ThreadPool {

    @Bean
    public ThreadPoolTaskExecutor instance() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置最大线程数
        executor.setMaxPoolSize(30);
        //配置核心线程数
        executor.setCorePoolSize(5);
        //配置队列大小
        executor.setQueueCapacity(5000);
        // 空闲的多余线程最大存活时间
        executor.setKeepAliveSeconds(600);
        // 配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("pool-");
        // 线程池对拒绝任务(无线程可用)的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }

}

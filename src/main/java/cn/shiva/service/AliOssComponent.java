package cn.shiva.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author shiva   2023-12-17 18:14
 */
@Slf4j
@Component
public class AliOssComponent {

    @Value("${aliOss.endpoint}")
    private String endpoint;
    @Value("${aliOss.accessKeyId}")
    private String accessKeyId;
    @Value("${aliOss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${aliOss.bucketName}")
    private String bucketName;
    @Value("${aliOss.areaSuffix}")
    private String areaSuffix;

    private OSS ossClient;

    /**
     * TODO 文件上传，禁止覆盖同名文件
     */

    /**
     * TODO 单个文件下载
     */

    /**
     * TODO 范围下载，多文件下载
     */

    /**
     * TODO 判断文件是否存在
     */

    /**
     * TODO 获取文件元数据
     */

    /**
     * TODO 重命名文件
     */

    /**
     * TODO 文件移动
     */

    /**
     * TODO 文件夹移动
     */




    private void initClient() {
        //初始化连接器
        if (ossClient == null) {
            // 初始化创建连接
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        }
        if (!ossClient.doesBucketExist(bucketName)) {
            log.info("您的Bucket不存在，创建Bucket：{}。", bucketName);
            ossClient.createBucket(bucketName);
            //设置空间为公共读
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        }
    }
}

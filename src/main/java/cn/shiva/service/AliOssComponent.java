package cn.shiva.service;

import cn.shiva.entity.NovelFile;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.util.DateUtils;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * @author shiva   2023-12-17 18:14
 */
@Slf4j
@Component
public class AliOssComponent {

    @Autowired
    private ConfigService configService;

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String areaSuffix;

    private OSS ossClient;

    /**
     * 文件上传，禁止覆盖同名文件、只允许上传TXT
     *
     * @param folderName 文件夹名称，拼接在文件名中，逐级拼接，全部都有
     * @param file       上传文件
     * @return 阿里云OSS的全路径
     */
    public NovelFile upload(String folderName, MultipartFile file) throws Exception {
        return upload(folderName, file, "true");
    }

    /**
     * 文件上传，禁止覆盖同名文件、只允许上传TXT
     *
     * @param folderName      文件夹名称，拼接在文件名中，逐级拼接，全部都有
     * @param file            上传文件
     * @param forbidOverwrite 是否允许覆盖上传，字符串 true(禁止覆盖)、false(允许覆盖)
     * @return 阿里云OSS的全路径
     */
    public NovelFile upload(String folderName, MultipartFile file, String forbidOverwrite) throws Exception {
        if (file == null || file.getSize() == 0) {
            throw new Exception("未检测到上传文件，请检查文件是否正确！");
        }
        //初始化连接器
        initClient();

        // 拿到原文件名
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String originalFileNameOutSuffix = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        //先拿到文件后缀
        String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        //allowedExtension不为空的情况下，判断格式是否允许
        if (!"TXT".equalsIgnoreCase(fileSuffix.toUpperCase(Locale.ROOT))) {
            //不存在于允许的格式内，直接结束不保存
            throw new Exception("该文件格式不允许上传！");
        }

        //将MultipartFile转化为IO.file
        File finalFile = File.createTempFile(originalFileNameOutSuffix + UUID.randomUUID(), fileSuffix);
        file.transferTo(finalFile);
        //文件地址，路径 + 文件名
        String name = folderName + originalFilename;

        ObjectMetadata metadata = new ObjectMetadata();
        //禁止覆盖
        metadata.setHeader("x-oss-forbid-overwrite", forbidOverwrite);
        metadata.setHeader("Content-Type", "text/plain;charset=utf-8");
        PutObjectResult putObjectResult = ossClient.putObject(bucketName, name, finalFile, metadata);
        //拼接路径返回
        String url = "https://" + bucketName + areaSuffix + name;

        //删除转化过程中生成的缓存文件
        finalFile.delete();

        return NovelFile.builder().name(originalFilename).type("file").lastModifyTime(DateUtils.format(new Date())).ossPath(name).filePath(url).build();
    }


    /**
     * 获得临时访问路径，有效期5分钟
     *
     * @param filePath 带路径的全名
     */
    public String temporaryAccess(String filePath) {
        initClient();
        // 设置签名URL过期时间，默认5分钟
        Date expiration = new Date(new Date().getTime() + 5 * 60 * 1000);
        // 生成以GET方法访问的签名URL，在签名URL有效期内访客可以直接通过浏览器访问相关内容。
        URL url = ossClient.generatePresignedUrl(bucketName, filePath, expiration);
        return url.toString();
    }

    /**
     * 单个文件下载，通过流式下载，拿到string
     *
     * @param objectName 文件名
     */
    public String streamDownload(String objectName) throws IOException {
        StringBuilder sb = new StringBuilder();
        initClient();
        // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
        OSSObject ossObject = ossClient.getObject(bucketName, objectName);
        // 读取文件内容。
        BufferedReader reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        // 数据读取完成后，获取的流必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
        reader.close();
        // ossObject对象使用完毕后必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
        ossObject.close();
        return sb.toString();
    }

    /**
     * 单个文件下载，下载到本地转为file
     *
     * @param objectName 文件名
     * @param basicName  文件名，纯名字，不带路径和后缀
     */
    public File fileDownload(String objectName, String basicName) throws IOException {
        initClient();
        // 下载Object到本地文件，并保存到指定的本地路径中。如果指定的本地文件存在会覆盖，不存在则新建。
        File file = new File(basicName);
        // 如果未指定本地路径，则下载后的文件默认保存到示例程序所属项目对应本地路径中。
        ossClient.getObject(new GetObjectRequest(bucketName, objectName), file);
        return file;
    }


    /**
     * 判断文件是否存在
     *
     * @param objectName 带路径的全名
     */
    public boolean doesObjectExist(String objectName) {
        initClient();
        // 判断文件是否存在。如果返回值为true，则文件存在，否则存储空间或者文件不存在。
        // 设置是否进行重定向或者镜像回源。默认值为true，表示忽略302重定向和镜像回源；如果设置isINoss为false，则进行302重定向或者镜像回源。
        return ossClient.doesObjectExist(bucketName, objectName);
    }

    /**
     * 复制再删除，可以实现：重命名文件、移动文件
     *
     * @param sourceKey      原路径，带路径和名字
     * @param destinationKey 新路径，带路径和名字
     */
    public void moveObject(String sourceKey, String destinationKey) {
        initClient();
        // 将examplebucket下的srcobject.txt拷贝至同一Bucket下的destobject.txt。
        ossClient.copyObject(bucketName, sourceKey, bucketName, destinationKey);
        // 删除srcobject.txt。
        ossClient.deleteObject(bucketName, sourceKey);
    }

    /**
     * 删除文件，从回收站内删除
     *
     * @param objectName 原路径，带路径和名字
     */
    public void deleteObject(String objectName) {
        initClient();
        ossClient.deleteObject(bucketName, objectName);
    }

    /**
     * 根据前缀，获取到当前文件夹下的文件和文件夹；
     * 例如，传入 novel/，返回的result.getObjectSummaries().key是文件名，result.getCommonPrefixes()是文件夹名字
     *
     * @param prefix 前缀文件夹，例如：novel/
     */
    public ListObjectsV2Result listObjects(String prefix) {
        initClient();

        // 构造ListObjectsV2Request请求。
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request(bucketName);
        // 设置prefix参数来获取fun目录下的所有文件与文件夹。
        listObjectsV2Request.setPrefix(prefix);
        // 设置正斜线（/）为文件夹的分隔符。
        listObjectsV2Request.setDelimiter("/");
        // 最大返回数量
        listObjectsV2Request.setMaxKeys(1000);
        // 发起列举请求。
        ListObjectsV2Result result = ossClient.listObjectsV2(listObjectsV2Request);
        return result;
       /* // 遍历文件。
        System.out.println("Objects:");
        // objectSummaries的列表中给出的是fun目录下的文件。
        for (OSSObjectSummary objectSummary : result.getObjectSummaries()) {
            System.out.println(objectSummary.getKey());
        }
        // 遍历commonPrefix。
        System.out.println("\nCommonPrefixes:");
        // commonPrefixs列表中显示的是fun目录下的所有子文件夹。由于fun/movie/001.avi和fun/movie/007.avi属于fun文件夹下的movie目录，因此这两个文件未在列表中。
        for (String commonPrefix : result.getCommonPrefixes()) {
            System.out.println(commonPrefix);
        }*/
    }


    private void initClient() {
        //初始化连接器
        if (ossClient == null) {
            // 从数据库拿到对应的数据
            initParams();
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

    /**
     * 因为加了一个项目启动后，默认同步一下OSS的文件目录
     * <p>因为我在阿里云搞了一个镜像，使用镜像创建临时服务器，用来节省服务器成本<p/>
     * <p>但是创建之后，sqlite还是旧的数据，所以需要同步；</p>
     * 这时候会影响其他情况，OSS还没准备好、还没有建立链接
     */
    public boolean clientExists() {
        initClient();
        return ossClient != null;
    }

    /**
     * 在更新了OSS配置之后，需要重新获取OSS链接
     */
    public void cleanClient() {
        ossClient = null;
    }

    /**
     * 从数据库中，拿到全部的参数；然后筛选出自己需要的
     */
    private void initParams() {
        JSONObject jsonObject = configService.getAllParamsByJson();
        endpoint = jsonObject.getString("endpoint");
        accessKeyId = jsonObject.getString("accessKeyId");
        accessKeySecret = jsonObject.getString("accessKeySecret");
        bucketName = jsonObject.getString("bucketName");
        areaSuffix = jsonObject.getString("areaSuffix");
    }

    public String getBucketName() {
        if (StringUtils.isBlank(bucketName)) {
            bucketName = configService.key("bucketName");
        }
        return bucketName;
    }

    public String getAreaSuffix() {
        if (StringUtils.isBlank(areaSuffix)) {
            areaSuffix = configService.key("areaSuffix");
        }
        return areaSuffix;
    }

    private String getFileSuffix(MultipartFile file) {
        // 文件原名
        String originalFilename = file.getOriginalFilename();
        // 拿到文件格式
        assert originalFilename != null;
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }
}

import cn.shiva.OssFmsServerApplication;
import cn.shiva.service.AliOssComponent;
import com.aliyun.oss.model.ListObjectsV2Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OssFmsServerApplication.class})
public class AliyunOSSJunit {

    @Autowired
    private AliOssComponent ossComponent;

    @Test
    public void uploadFile() {
    }

    @Test
    public void initOssData() {
        //初始化OSS的数据到数据库
        ListObjectsV2Result listObjectsV2Result = ossComponent.listObjects("novel/");
    }

    @Test
    public void temporaryAccess() {
        String s = ossComponent.temporaryAccess("测试图片.png");
        System.out.println(s);
    }

    @Test
    public void streamDownload() throws IOException {
        String s = ossComponent.streamDownload("hello.txt");
        System.out.println(s);
    }
}

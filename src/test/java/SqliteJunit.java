import cn.shiva.OssFmsServerApplication;
import cn.shiva.entity.NovelFile;
import cn.shiva.entity.NovelLabel;
import cn.shiva.mapper.NovelFileMapper;
import cn.shiva.mapper.NovelLabelMapper;
import cn.shiva.service.SqliteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OssFmsServerApplication.class})
public class SqliteJunit {

    @Autowired
    private NovelLabelMapper novelLabelMapper;
    @Autowired
    private NovelFileMapper novelFileMapper;
    @Autowired
    private SqliteService sqliteService;

    //测试mybatis搭配sqlite
    @Test
    public void tableTest() {
        NovelLabel novelLabel = novelLabelMapper.selectById(1);
        System.out.println(novelLabel);
        NovelFile novelFile = novelFileMapper.selectById(1);
        System.out.println(novelFile);
    }

    @Test
    public void initFromOss() {
        sqliteService.initFromOss();
    }

    @Test
    public void competeFolder() {
        sqliteService.competeFolder("novel/3.这是一个神奇的世界/000 留着偶尔翻一翻/战舰/小塞壬.txt");
    }


}

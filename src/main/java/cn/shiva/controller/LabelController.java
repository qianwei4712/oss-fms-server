package cn.shiva.controller;

import cn.shiva.core.domain.R;
import cn.shiva.entity.NovelFile;
import cn.shiva.entity.NovelLabel;
import cn.shiva.mapper.NovelFileMapper;
import cn.shiva.mapper.NovelLabelMapper;
import cn.shiva.service.NovelService;
import cn.shiva.utils.ThreadPool;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping(value = "/api/label")
public class LabelController {

    @Autowired
    private NovelLabelMapper novelLabelMapper;
    @Autowired
    private NovelService novelService;
    @Autowired
    private NovelFileMapper novelFileMapper;
    @Autowired
    private ThreadPool pool;

    /**
     * 返回全部的标签列表
     */
    @GetMapping("/allLabels")
    public R<List<NovelLabel>> allLabels() {
        List<NovelLabel> novelLabels = novelLabelMapper.selectList(new QueryWrapper<>());
        return R.ok(novelLabels);
    }

    /**
     * 新增标签
     */
    @GetMapping("/addLabel")
    public R<String> addLabel(String name) {
        if (StringUtils.isBlank(name)) {
            return R.ok();
        }
        name = name.trim();
        //先判断是否存在相同名字
        int count = novelLabelMapper.countByName(name);
        if (count > 0) {
            return R.ok();
        }
        NovelLabel novelLabel = new NovelLabel();
        novelLabel.setName(name);
        novelLabelMapper.insert(novelLabel);
        return R.ok();
    }

    /**
     * 编辑标签名
     */
    @GetMapping("/updateLabel")
    public R<String> updateLabel(Long id, String name) {
        if (StringUtils.isBlank(name) || id == null) {
            return R.ok();
        }
        NovelLabel novelLabel = novelLabelMapper.selectById(id);
        novelLabel.setName(name);
        novelLabelMapper.updateById(novelLabel);
        return R.ok();
    }

    /**
     * 删除某个标签，已经关联的也删了
     */
    @GetMapping("/deleteLabel")
    public R<String> deleteLabel(Long id) {
        if (id == null) {
            return R.ok();
        }
        novelService.deleteLabel(id);
        return R.ok();
    }

    //文件-标签操作 **************************************************************************************************************************

    /**
     * 给文件、文件夹添加标签
     */
    @GetMapping("/addRelationLabel")
    public R<String> addRelationLabel(Long novelId, Long labelId) {
        if (novelId == null || labelId == null) {
            return R.ok();
        }
        novelLabelMapper.insertMidNovelLabel(novelId, labelId);
        return R.ok();
    }

    /**
     * 将文件、文件夹移除某个标签
     */
    @GetMapping("/removeRelationLabel")
    public R<String> removeRelationLabel(Long novelId, Long labelId) {
        if (novelId == null || labelId == null) {
            return R.ok();
        }
        novelLabelMapper.removeMidNovelLabel(novelId, labelId);
        return R.ok();
    }

    /**
     * 根据标签，获取到文件列表
     */
    @GetMapping("listNovelByLabelId")
    public R<List<NovelFile>> pageNovelAndFolder(Long labelId) throws InterruptedException {
        List<NovelFile> list = novelFileMapper.listByLabelId(labelId);
        //遍历，多线程获取数据
        CountDownLatch latch = new CountDownLatch(list.size());
        for (NovelFile novelFile : list) {
            pool.instance().execute(() -> {
                novelFile.setRealPath(novelFile.getOssPath().replace("novel/", "").replace(novelFile.getName(), ""));
                latch.countDown();
            });
        }
        boolean await = latch.await(15, TimeUnit.SECONDS);
        return R.ok(list);
    }

}

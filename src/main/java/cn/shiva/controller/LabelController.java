package cn.shiva.controller;

import cn.shiva.core.domain.R;
import cn.shiva.entity.NovelLabel;
import cn.shiva.mapper.NovelLabelMapper;
import cn.shiva.service.NovelService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    //TODO 给文件、文件夹添加标签
    //TODO 将文件、文件夹移除某个标签

}

package cn.shiva.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shiva   2023-12-17 23:00
 */
@RestController
@RequestMapping(value = "/api/label")
public class LabelController {

    /**
     * TODO 返回全部的标签列表；过cache
     */

    //TODO 通过关键词搜索，返回标签；过cache

    //TODO 删除某个标签，同时删除历史以及打过的记录

    //TODO 编辑标签名，更新cache

    //TODO 新增标签

    //文件-标签操作 **************************************************************************************************************************

    //TODO 给文件、文件夹添加标签
    //TODO 将文件、文件夹移除某个标签

}

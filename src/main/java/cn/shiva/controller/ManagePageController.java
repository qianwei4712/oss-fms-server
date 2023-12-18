package cn.shiva.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 管理端页面跳转
 *
 * @author shiva   2023-12-17 23:52
 */
@Controller
@RequestMapping(value = "/m/")
public class ManagePageController {

    @RequestMapping(value = {"", "/index"})
    public String index() {
        return "guide/index";
    }

}

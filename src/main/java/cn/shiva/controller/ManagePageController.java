package cn.shiva.controller;

import cn.shiva.mapper.ConfigMapper;
import cn.shiva.service.ConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ConfigService configService;

    /**
     * 打开页面，这里直接做好switch
     */
    @RequestMapping(value = {"/page"})
    public String page(String type) {
        switch (type) {
            case "password":
                return "guide/passwordInit";
            default:
                return "manage/index";
        }
    }

    /**
     * 密码初始化;
     * 1.判空，重新设置密码
     * 2.更新密码，设置密码后，跳转到下一个步骤
     */
    @RequestMapping(value = "pwdInit")
    public String pwdInit(String pwd) {
        if (StringUtils.isBlank(pwd)) {
            return "guide/passwordInit";
        }
        configService.updateConfig("password", pwd);
        //是否需要继续引导
        String guideNext = judgeGuide();
        return StringUtils.isBlank(guideNext) ? guideNext : "manage/index";
    }

    /**
     * 主页
     */
    @RequestMapping(value = {"", "/index"})
    public String index() {
        String guideNext = judgeGuide();
        return StringUtils.isBlank(guideNext) ? guideNext : "manage/index";
    }


    /**
     * 判断是否还需要继续引导
     */
    private String judgeGuide() {
        String password = configService.password();
        if (StringUtils.isBlank(password)) {
            return "guide/passwordInit";
        }
        return null;
    }


}

package cn.shiva.controller;

import cn.shiva.service.ConfigService;
import cn.shiva.utils.CacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

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
            case "login":
                return "manage/login";
            case "password":
                return "guide/passwordInit";
            case "settings":
                return "manage/settings";
            case "recovery":
                return "manage/recovery";
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
        return StringUtils.isNotBlank(guideNext) ? guideNext : "manage/index";
    }

    /**
     * 根据输入的密码，登录后返回token
     */
    @RequestMapping(value = "login")
    public String login(Model model, String pwd) {
        //是否需要继续引导
        String guideNext = judgeGuide();
        if (StringUtils.isNotBlank(guideNext)) {
            return guideNext;
        }
        //密码判断
        String password = configService.password();
        if (!pwd.equals(password)) {
            //密码不对
            return "manage/login";
        }
        //生成一个token，然后返回前端
        String token = UUID.randomUUID().toString();
        CacheUtil.put("TOKEN:" + token, 1);
        //返回token到前端去
        model.addAttribute("token", token);
        return "manage/index";
    }

    /**
     * 主页
     */
    @RequestMapping(value = {"", "/index"})
    public String index() {
        String guideNext = judgeGuide();
        return StringUtils.isNotBlank(guideNext) ? guideNext : "manage/index";
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

package cn.shiva.controller;

import cn.shiva.core.domain.R;
import cn.shiva.entity.bo.OssParams;
import cn.shiva.service.AliOssComponent;
import cn.shiva.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author shiva   2023-12-17 23:00
 */
@RestController
@RequestMapping(value = "/api/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;
    @Autowired
    private AliOssComponent ossComponent;

    /**
     * 获取到OSS相关配置参数包装对象
     */
    @GetMapping("getOssParams")
    public R<OssParams> getOssParams() {
        return R.ok(configService.getOssParams());
    }

    @PostMapping("saveOssParams")
    public R<String> saveOssParams(@RequestBody OssParams ossParams) {
        ossComponent.cleanClient();
        configService.saveOssParams(ossParams);
        return R.ok();
    }
}

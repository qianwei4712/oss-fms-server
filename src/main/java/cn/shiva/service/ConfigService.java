package cn.shiva.service;

import cn.shiva.mapper.ConfigMapper;
import cn.shiva.utils.CacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 配置数据相关服务
 *
 * @author shiva   2023-12-17 14:22
 */
@Service
public class ConfigService {
    /**
     * config数据库内，可能存在以下配置：
     * 1. password : 系统密码
     * 2. OSS相关配置
     */
    @Autowired
    private ConfigMapper configMapper;

    /**
     * 拿到本项目的密码；先拿缓存，缓存没有拿数据库
     */
    public String password() {
        String password = (String) CacheUtil.get("password");
        if (StringUtils.isBlank(password)) {
            password = configMapper.key("password");
            CacheUtil.put("password", password);
        }
        return password;
    }


}

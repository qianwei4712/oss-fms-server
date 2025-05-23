package cn.shiva.service;

import cn.shiva.entity.Config;
import cn.shiva.entity.bo.OssParams;
import cn.shiva.mapper.ConfigMapper;
import cn.shiva.utils.CacheUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * 拿到登录后的UUID集合，如果redis存在key,那就返回true
     */
    public boolean judgeToken(String key) {
        Object o = CacheUtil.get("TOKEN:" + key);
        return o != null;
    }

    /**
     * 拿到本项目的密码；先拿缓存，缓存没有拿数据库
     */
    public String password() {
        String password = (String) CacheUtil.get("password");
        if (StringUtils.isBlank(password)) {
            password = configMapper.key("password");
            if (StringUtils.isBlank(password)) {
                //第一次登录，无法传入null
                return null;
            }
            CacheUtil.put("password", password);
        }
        return password;
    }

    /**
     * 新增、或者更新 config参数；
     * 先清楚缓存
     */
    public void updateConfig(String key, String value) {
        CacheUtil.remove(key);
        //开始更新操作
        Config config = new Config();
        config.setConfigKey(key);
        config.setConfigValue(value);
        //先检查
        String oldValue = configMapper.key(key);
        if (StringUtils.isBlank(oldValue)) {
            configMapper.insert(config);
        } else {
            configMapper.updateById(config);
        }

    }

    /**
     * 从数据库拿到全部的参数，并搞成json
     */
    public JSONObject getAllParamsByJson() {
        JSONObject jsonObject = JSONObject.of();
        List<Config> configs = configMapper.selectList(new QueryWrapper<>());
        for (Config config : configs) {
            jsonObject.put(config.getConfigKey(), config.getConfigValue());
        }
        return jsonObject;
    }

    public String key(String key) {
        return configMapper.key(key);
    }

    /**
     * 从数据库拿到OSS相关的配置参数包装类
     */
    public OssParams getOssParams() {
        JSONObject allParamsByJson = getAllParamsByJson();
        return OssParams.builder()
                .endpoint(allParamsByJson.getString("endpoint"))
                .accessKeyId(allParamsByJson.getString("accessKeyId"))
                .accessKeySecret(allParamsByJson.getString("accessKeySecret"))
                .areaSuffix(allParamsByJson.getString("areaSuffix"))
                .bucketName(allParamsByJson.getString("bucketName"))
                .build();
    }

    /**
     * 保存，清空连接
     */
    public void saveOssParams(OssParams ossParams) {
        updateConfig("endpoint", ossParams.getEndpoint());
        updateConfig("accessKeyId", ossParams.getAccessKeyId());
        updateConfig("accessKeySecret", ossParams.getAccessKeySecret());
        updateConfig("areaSuffix", ossParams.getAreaSuffix());
        updateConfig("bucketName", ossParams.getBucketName());
    }
}

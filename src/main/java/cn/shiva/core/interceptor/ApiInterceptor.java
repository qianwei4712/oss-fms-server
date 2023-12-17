package cn.shiva.core.interceptor;

import cn.shiva.core.domain.R;
import cn.shiva.core.domain.RsaContentDTO;
import cn.shiva.service.ConfigService;
import cn.shiva.utils.RsaUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * @author shiva   2023-12-17 17:33
 */
@Slf4j
@Component
public class ApiInterceptor implements HandlerInterceptor {
    @Value("${api.privateKey}")
    private String privateKey;
    @Value("${api.sourceCode}")
    private String sourceCode;
    @Autowired
    private ConfigService configService;

    /**
     * 使用JWT进行校验，参数放到负载内
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,X-Nideshop-Token,X-URL-PATH,content-type");
        //TODO 如果初始化未完成，先提示跳到初始化流程界面：密码未设置、OSS未配置

        //token，就是加密文本
        String token = request.getHeader("Authorization");
        //请求来源类型
        String sourceType = request.getHeader("Auth-Source-Type");

        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        //检查是否有 ApiIgnore 注释，有则跳过认证
        if (method.isAnnotationPresent(ApiIgnore.class)) {
            ApiIgnore ignore = method.getAnnotation(ApiIgnore.class);
            if (ignore != null) {
                return true;
            }
        }

        // 没token，或者没来源
        if (StringUtils.isBlank(token) || StringUtils.isBlank(sourceType)) {
            sendJsonMessage(response, R.fail(403, "身份信息不全，请重试!"));
            return false;
        }
        //开始解密
        RsaContentDTO rsaContentDTO;
        try {
            String content = RsaUtil.decryptByPrivateKey(token, privateKey);
            rsaContentDTO = JSONObject.parseObject(content, RsaContentDTO.class);
            //解密出来的code和请求头里的code不一样；或者和系统预设的code不一样
            if (rsaContentDTO == null || !sourceType.equals(rsaContentDTO.getCode()) || !sourceCode.contains(sourceType)) {
                sendJsonMessage(response, R.fail(403, "签名验证失败。请检查后重试!"));
                return false;
            }
        } catch (Exception e) {
            sendJsonMessage(response, R.fail(403, "签名验证失败。请检查后重试!"));
            return false;
        }
        // 最后需要做检验，是否密码正确
        String password = configService.password();
        if (StringUtils.isBlank(password) || !password.equals(rsaContentDTO.getKey())) {
            sendJsonMessage(response, R.fail(403, "签名验证失败。请检查后重试!"));
            return false;
        }
        return true;
    }


    /**
     * 将某个对象转换成json格式并发送到客户端
     */
    public static void sendJsonMessage(HttpServletResponse response, Object obj) throws Exception {
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.print(JSONObject.toJSONString(obj));
        writer.close();
        response.flushBuffer();
    }

}

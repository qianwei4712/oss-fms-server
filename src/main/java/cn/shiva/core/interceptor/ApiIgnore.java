package cn.shiva.core.interceptor;

import java.lang.annotation.*;

/**
 * @author shiva   2023-12-17 17:41
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiIgnore {

    /**
     * 跳过RSA验证
     */
    boolean ignored() default true;
}

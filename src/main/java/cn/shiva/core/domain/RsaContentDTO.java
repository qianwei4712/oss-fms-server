package cn.shiva.core.domain;

import lombok.Data;

/**
 * @author shiva   2023-11-14 21:25
 */
@Data
public class RsaContentDTO {

    /**
     * 请求来源，默认值，分为终端和服务端
     */
    private String code;
    /**
     * 唯一键，这里传的是token UUID
     */
    private String key;
}
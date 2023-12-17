package cn.shiva.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author shiva   2023-12-17 20:26
 */
@Data
@TableName("sys_config")
public class Config {
    @TableId
    private String configKey;
    private String configValue;
}

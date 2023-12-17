package cn.shiva.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 标签表
 *
 * @author shiva   2023-12-17 15:33
 */
@Data
@TableName("novel_label")
public class NovelLabel {

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 标签名
     */
    private String name;
}

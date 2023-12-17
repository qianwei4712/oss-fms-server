package cn.shiva.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author shiva   2023-12-17 17:16
 */
@Data
@TableName("mid_file_label")
public class MidFileLabel {

    /**
     * 小说文件ID
     */
    private Long fileId;
    /**
     * 标签ID
     */
    private Long labelId;
}

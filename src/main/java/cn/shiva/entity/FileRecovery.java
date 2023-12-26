package cn.shiva.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author shiva   2023-12-17 17:20
 */
@Data
@TableName("file_recovery")
public class FileRecovery {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 内容简述
     */
    private String sketch;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 最后修改时间
     */
    private String lastModifyTime;

    /**
     * 全路径
     */
    private String ossPath;

    /**
     * 路径名地址
     */
    private String filePath;
}

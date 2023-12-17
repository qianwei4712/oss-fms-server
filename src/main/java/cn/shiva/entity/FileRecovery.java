package cn.shiva.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

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
    private Integer size;

    /**
     * 最后修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastModifyTime;

    /**
     * 全路径
     */
    private String ossPath;

    /**
     * 路径名地址
     */
    private String filePath;
}

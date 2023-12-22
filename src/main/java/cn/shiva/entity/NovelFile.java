package cn.shiva.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 小说文件表
 *
 * @author shiva   2023-12-17 16:34
 */
@Data
@TableName("novel_file")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovelFile {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 上级ID
     */
    private Long parentId;

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
     * 类型，file-文件；folder-文件夹
     */
    private String type;


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

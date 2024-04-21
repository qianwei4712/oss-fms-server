package cn.shiva.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author shiva   2023-12-17 15:40
 */
@Mapper
public interface SqliteMapper {

    /**
     * 清空小说表
     */
    @Delete("DELETE FROM novel_file;")
    int clearNovel();

    /**
     * 清空小说标签关联表
     */
    @Delete("DELETE FROM mid_file_label;")
    int clearNovelLabel();
}

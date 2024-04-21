package cn.shiva.mapper;

import cn.shiva.entity.NovelLabel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author shiva   2023-12-17 16:25
 */
@Mapper
public interface NovelLabelMapper extends BaseMapper<NovelLabel> {

    /**
     * 根据文件id，拿到标签组
     */
    List<NovelLabel> listLabelsByNovelId(@Param("novelId") Long novelId);

    /**
     * 根据novel id 删除标签关系
     */
    int deleteByNovelId(@Param("novelId") Long novelId);

    /**
     * 根据标签ID，进行删除
     */
    int deleteByLabelId(@Param("labelId") Long labelId);

    /**
     * 根据名字检索重名
     */
    @Select("SELECT COUNT(*) FROM novel_label WHERE name = #{name}")
    int countByName(@Param("name") String name);

    /**
     * 新增一条关联
     */
    @Insert("INSERT INTO mid_file_label VALUES(#{novelId}, #{labelId})")
    int insertMidNovelLabel(@Param("novelId") Long novelId, @Param("labelId") Long labelId);

    /**
     * 移除关联
     */
    @Delete("DELETE FROM mid_file_label WHERE file_id = #{novelId} and label_id = #{labelId} ")
    int removeMidNovelLabel(@Param("novelId") Long novelId, @Param("labelId") Long labelId);

}

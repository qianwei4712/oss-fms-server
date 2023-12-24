package cn.shiva.mapper;

import cn.shiva.entity.NovelLabel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shiva   2023-12-17 16:25
 */
@Mapper
public interface NovelLabelMapper extends BaseMapper<NovelLabel> {

    /**
     * 根据文件id，拿到标签组
     */
    List<String> listLabelsByNovelId(@Param("novelId") Long novelId);
}

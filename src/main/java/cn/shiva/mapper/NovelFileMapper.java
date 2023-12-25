package cn.shiva.mapper;

import cn.shiva.entity.NovelFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shiva   2023-12-17 16:25
 */
@Mapper
public interface NovelFileMapper extends BaseMapper<NovelFile> {
    /**
     * 根据上级ID，拿到文件列表
     */
    List<NovelFile> listByParentId(@Param("parentId") Long parentId);

    /**
     * 搜索关键词
     */
    List<NovelFile> listBySearchParam(@Param("searchParam") String searchParam);
}

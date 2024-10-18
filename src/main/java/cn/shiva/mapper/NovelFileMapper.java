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

    /**
     * 根据路径获取到文件对象
     */
    NovelFile getByOssPath(@Param("ossPath") String ossPath);

    /**
     * 根据上级id，拿到文件夹列表
     */
    List<NovelFile> listFolderParentId(@Param("parentId") Long parentId);

    /**
     * 根据标签ID，获取文件列表
     */
    List<NovelFile> listByLabelId(@Param("labelId") Long labelId);
}

package cn.shiva.mapper;

import cn.shiva.entity.FileRecovery;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author shiva   2023-12-26 21:53
 */
@Mapper
public interface FileRecoveryMapper extends BaseMapper<FileRecovery> {
    @Select("SELECT * FROM file_recovery ORDER BY type desc, name")
    List<FileRecovery> listAll();
}

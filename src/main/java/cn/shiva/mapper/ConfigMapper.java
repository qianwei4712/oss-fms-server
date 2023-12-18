package cn.shiva.mapper;

import cn.shiva.entity.Config;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author shiva   2023-12-17 15:40
 */
@Mapper
public interface ConfigMapper extends BaseMapper<Config> {

    /**
     * 判断表是否存在
     */
    @Select("SELECT name FROM sqlite_master WHERE type='table' AND name= #{tableName}")
    String judgeTableExist(@Param("tableName") String tableName);

    /**
     * 拿到指定configKey的数据
     */
    @Select("SELECT config_value FROM sys_config WHERE config_key = #{key}")
    String key(@Param("key") String key);
}

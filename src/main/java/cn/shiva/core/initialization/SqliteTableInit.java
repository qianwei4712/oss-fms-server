package cn.shiva.core.initialization;

import cn.shiva.mapper.ConfigMapper;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * 初始化表结构
 *
 * @author shiva   2023-12-17 14:37
 */
@Service
public class SqliteTableInit {

    @Autowired
    private ConfigMapper configMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        String createTable = "";
        //标签表
        String tableName = configMapper.judgeTableExist("novel_label");
        if (StringUtils.isBlank(tableName)) {
            createTable = "CREATE TABLE \"novel_label\"( \"id\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \"name\" TEXT);";
            jdbcTemplate.update(createTable);
        }
        //文件表
        tableName = configMapper.judgeTableExist("novel_file");
        if (StringUtils.isBlank(tableName)) {
            createTable = "CREATE TABLE \"novel_file\"( \"id\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \"name\" TEXT, \"sketch\" TEXT, \"size\" integer, \"last_modify_time\" text, \"oss_path\" TEXT);";
            jdbcTemplate.update(createTable);
        }
        //标签中间表
        tableName = configMapper.judgeTableExist("mid_file_label");
        if (StringUtils.isBlank(tableName)) {
            createTable = "CREATE TABLE \"mid_file_label\"( \"file_id\" INTEGER NOT NULL, \"label_id\" INTEGER NOT NULL);";
            jdbcTemplate.update(createTable);
        }
        //回收站表
        tableName = configMapper.judgeTableExist("file_recovery");
        if (StringUtils.isBlank(tableName)) {
            createTable = "CREATE TABLE \"file_recovery\"( \"id\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \"name\" TEXT, \"sketch\" TEXT, \"size\" integer, \"last_modify_time\" text, \"oss_path\" TEXT);";
            jdbcTemplate.update(createTable);
        }
        //配置表
        tableName = configMapper.judgeTableExist("sys_config");
        if (StringUtils.isBlank(tableName)) {
            createTable = "CREATE TABLE \"sys_config\"( \"config_key\" TEXT NOT NULL, \"config_value\" TEXT, PRIMARY KEY (\"config_key\"));";
            jdbcTemplate.update(createTable);
        }

    }

}

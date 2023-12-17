> 本项目是为了给自己用的一个文件检索小工具；
> 
> 开发本系统，目的是为了给自己的txt文件（/滑稽）做一个分类，并且可以在手机web端快速检索并下载

### 主要功能如下

- 文件管理：上传、删除、还原、重命名、移动
- 标签管理：列表、检索、打标签、移除标签、文件夹标签
- 数据维护：数据导出备份、SQL数据导入恢复、OSS数据初始化

### 技术选型

服务后端：Springboot3.2 + JDK21 + mybatis plus + Sqlite3 + caffeine

管理前端：vue3 + element plus

移动终端：


### 项目内代码细节说明

1. `SqliteTableInit` : 通过 `@PostConstruct` 自动创建SQLite表，执行DDL前先判断是否存在表


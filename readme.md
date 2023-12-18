> 本项目是为了给自己用的一个文件检索小工具；
>
> 开发本系统，目的是为了给自己的txt文件（/滑稽）做一个分类，并且可以在手机web端快速检索并下载

### 主要功能如下

- 文件管理：上传、删除、还原、重命名、移动
- 标签管理：列表、检索、打标签、移除标签、文件夹标签
- 数据维护：数据导出备份、SQL数据导入恢复、OSS数据初始化

### 技术选型

服务后端：Springboot3.2 + JDK21 + mybatis plus + Sqlite3 + caffeine

管理前端（不分离）：thymeleaf + vue3 + element plus；懒得做分离项目了

移动终端：

### 项目内代码细节说明

1. 请提前开好阿里云OSS的权限，我用的是RAM子账户，授权： AliyunSTSAssumeRoleAccess、AliyunOSSFullAccess
2. `SqliteTableInit` : 通过 `@PostConstruct` 自动创建SQLite表，执行DDL前先判断是否存在表
3. `ApiInterceptor` ：拦截全部请求，通过 RSA 加密进行身份认证；RSA 解密后得到设定好的密码，再与系统缓存中的密码对比
4. `AliOssComponent` ：阿里云的方法工具类，文件全部在阿里云OSS，标签和信息在 SQLite，检索 SQLite 后，再从 OSS 下载
5. `CacheUtil` ：基于 Caffeine 的简单封装，缓存工具类


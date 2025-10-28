-- 快速修复首页所需的两个数据表
-- 可直接在 Navicat 或其他 MySQL 客户端执行

USE bb;

SET FOREIGN_KEY_CHECKS = 0;

-- 删除旧表
DROP TABLE IF EXISTS `public_resource`;
DROP TABLE IF EXISTS `public_search_history`;

SET FOREIGN_KEY_CHECKS = 1;

-- 创建公共搜索历史表
CREATE TABLE IF NOT EXISTS `public_search_history` (
    `id`               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID，自增',
    `user_id`          BIGINT COMMENT '发起搜索的用户ID',
    `keyword`          VARCHAR(200) NOT NULL COMMENT '搜索关键字',
    `role_filter`      VARCHAR(60) COMMENT '角色筛选条件',
    `category_filter`  VARCHAR(120) COMMENT '模块筛选条件',
    `location_filter`  VARCHAR(120) COMMENT '地点筛选条件',
    `advanced_options` TEXT COMMENT '高级筛选参数',
    `created_at`       DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '搜索时间'
) COMMENT='公共模块搜索历史记录';

-- 创建公共资源表
CREATE TABLE IF NOT EXISTS `public_resource` (
    `id`           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID，自增',
    `uploader_id`  BIGINT COMMENT '上传人用户ID',
    `file_name`    VARCHAR(255) NOT NULL COMMENT '文件原始名称',
    `file_type`    VARCHAR(120) COMMENT '文件类型',
    `file_size`    BIGINT COMMENT '文件大小(字节)',
    `storage_path` VARCHAR(500) NOT NULL COMMENT '文件物理存储路径',
    `download_url` VARCHAR(255) COMMENT '文件下载接口地址',
    `description`  TEXT COMMENT '文件说明或备注',
    `created_at`   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    FOREIGN KEY (`uploader_id`) REFERENCES `sys_user`(`id`)
) COMMENT='公共资料库文件表';

-- 验证表创建
SELECT 
    '表已创建成功！' AS 状态,
    (SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'bb' AND TABLE_NAME = 'public_resource') AS public_resource表,
    (SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'bb' AND TABLE_NAME = 'public_search_history') AS public_search_history表;


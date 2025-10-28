-- 修复employer表结构，添加缺失的user_id字段
USE bb;

-- 检查employer表当前结构
DESCRIBE employer;

-- 删除现有的employer表并重新创建（如果表中有重要数据，请先备份）
DROP TABLE IF EXISTS employer;

-- 重新创建employer表，包含user_id字段
CREATE TABLE employer (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID，自增',
    user_id        BIGINT      NOT NULL COMMENT '关联sys_user的ID',
    company_name   VARCHAR(150) NOT NULL COMMENT '企业名称',
    contact_person VARCHAR(100) COMMENT '企业联系人',
    contact_email  VARCHAR(120) COMMENT '联系人邮箱',
    contact_phone  VARCHAR(40) COMMENT '联系人电话',
    description    TEXT COMMENT '企业简介',
    website        VARCHAR(200) COMMENT '企业官网网址',
    CONSTRAINT fk_employer_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) COMMENT='企业信息表';

-- 查看修复后的表结构
DESCRIBE employer;

SELECT '✓ employer表结构已修复完成' AS status;


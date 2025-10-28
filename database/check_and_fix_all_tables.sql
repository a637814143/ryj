-- 检查并修复所有数据库表结构
USE bb;

-- 显示当前所有表
SHOW TABLES;

-- 检查employer表结构
SELECT '检查employer表...' AS message;
SHOW CREATE TABLE employer;

-- 如果employer表结构不正确，重新创建
DROP TABLE IF EXISTS job_posting;
DROP TABLE IF EXISTS employer;

CREATE TABLE IF NOT EXISTS employer (
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

-- 重新创建job_posting表（依赖employer表）
CREATE TABLE IF NOT EXISTS job_posting (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID，自增',
    employer_id    BIGINT      NOT NULL COMMENT '关联企业表employer的ID',
    title          VARCHAR(150) NOT NULL COMMENT '职位标题',
    description    TEXT COMMENT '职位详细描述',
    salary_range   VARCHAR(80) COMMENT '薪资范围描述',
    location       VARCHAR(120) COMMENT '工作地点',
    work_type      ENUM('FULL_TIME','PART_TIME','INTERNSHIP','REMOTE','FLEXIBLE') COMMENT '工作类型：全职/兼职/实习/远程/灵活',
    status         ENUM('OPEN','CLOSED','DRAFT') DEFAULT 'OPEN' COMMENT '岗位状态：开放/关闭/草稿',
    published_date DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '职位发布时间',
    closing_date   DATE COMMENT '职位截止日期',
    FOREIGN KEY (employer_id) REFERENCES employer(id)
) COMMENT='招聘岗位发布表';

-- 检查修复后的表结构
SELECT '修复完成，查看employer表结构：' AS message;
DESCRIBE employer;

SELECT '查看job_posting表结构：' AS message;
DESCRIBE job_posting;

SELECT '✓ 所有表修复完成' AS status;


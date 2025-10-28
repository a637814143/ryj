-- ========================================
-- 快速修复employer表 - 添加缺失的user_id字段
-- ========================================
-- 请在Navicat或其他MySQL工具中执行此脚本
-- ========================================

USE bb;

-- 1. 先删除依赖employer表的表
DROP TABLE IF EXISTS interview;
DROP TABLE IF EXISTS job_application;
DROP TABLE IF EXISTS job_requirement;
DROP TABLE IF EXISTS job_posting;
DROP TABLE IF EXISTS employer;

-- 2. 重新创建employer表（包含user_id字段）
CREATE TABLE employer (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID，自增',
    user_id        BIGINT NOT NULL COMMENT '关联sys_user的ID',
    company_name   VARCHAR(150) NOT NULL COMMENT '企业名称',
    contact_person VARCHAR(100) COMMENT '企业联系人',
    contact_email  VARCHAR(120) COMMENT '联系人邮箱',
    contact_phone  VARCHAR(40) COMMENT '联系人电话',
    description    TEXT COMMENT '企业简介',
    website        VARCHAR(200) COMMENT '企业官网网址',
    CONSTRAINT fk_employer_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) COMMENT='企业信息表';

-- 3. 重新创建job_posting表
CREATE TABLE job_posting (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID，自增',
    employer_id    BIGINT NOT NULL COMMENT '关联企业表employer的ID',
    title          VARCHAR(150) NOT NULL COMMENT '职位标题',
    description    TEXT COMMENT '职位详细描述',
    salary_range   VARCHAR(80) COMMENT '薪资范围描述',
    location       VARCHAR(120) COMMENT '工作地点',
    work_type      ENUM('FULL_TIME','PART_TIME','INTERNSHIP','REMOTE','FLEXIBLE') COMMENT '工作类型',
    status         ENUM('OPEN','CLOSED','DRAFT') DEFAULT 'OPEN' COMMENT '岗位状态',
    published_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '职位发布时间',
    closing_date   DATE COMMENT '职位截止日期',
    FOREIGN KEY (employer_id) REFERENCES employer(id)
) COMMENT='招聘岗位发布表';

-- 4. 重新创建job_requirement表
CREATE TABLE job_requirement (
    job_id      BIGINT NOT NULL COMMENT '关联岗位表job_posting的ID',
    requirement VARCHAR(200) NOT NULL COMMENT '岗位要求描述',
    PRIMARY KEY (job_id, requirement),
    FOREIGN KEY (job_id) REFERENCES job_posting(id)
) COMMENT='岗位要求列表';

-- 5. 重新创建job_application表
CREATE TABLE job_application (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID，自增',
    job_id       BIGINT NOT NULL COMMENT '关联招聘岗位表job_posting的ID',
    student_id   BIGINT NOT NULL COMMENT '关联学生资料表student_profile的ID',
    resume_id    BIGINT NOT NULL COMMENT '关联简历表resume的ID',
    status       ENUM('SUBMITTED','REVIEWING','INTERVIEW','OFFERED','REJECTED') DEFAULT 'SUBMITTED' COMMENT '申请状态',
    cover_letter TEXT COMMENT '求职信内容',
    applied_at   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请提交时间',
    FOREIGN KEY (job_id) REFERENCES job_posting(id),
    FOREIGN KEY (student_id) REFERENCES student_profile(id),
    FOREIGN KEY (resume_id) REFERENCES resume(id)
) COMMENT='学生岗位申请记录表';

-- 6. 重新创建interview表
CREATE TABLE interview (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID，自增',
    job_id          BIGINT NOT NULL COMMENT '关联招聘岗位表job_posting的ID',
    application_id  BIGINT NOT NULL COMMENT '关联岗位申请表job_application的ID',
    scheduled_time  DATETIME NOT NULL COMMENT '面试预约时间',
    location        VARCHAR(200) COMMENT '线下面试地点',
    meeting_link    VARCHAR(255) COMMENT '线上面试会议链接',
    status          ENUM('SCHEDULED','COMPLETED','CANCELLED') DEFAULT 'SCHEDULED' COMMENT '面试状态',
    feedback        TEXT COMMENT '面试反馈或评语',
    FOREIGN KEY (job_id) REFERENCES job_posting(id),
    FOREIGN KEY (application_id) REFERENCES job_application(id)
) COMMENT='面试安排记录表';

-- 验证表结构
SELECT '✓ 修复完成！查看employer表结构：' AS message;
DESCRIBE employer;

SELECT '✓ 所有相关表已重新创建' AS status;


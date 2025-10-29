-- 修复interview表结构
USE bb;

-- 删除旧表（如果存在）
DROP TABLE IF EXISTS interview;

-- 重新创建interview表
CREATE TABLE interview (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID，自增',
    job_id          BIGINT NOT NULL COMMENT '关联招聘岗位表job_posting的ID',
    application_id  BIGINT NOT NULL COMMENT '关联岗位申请表job_application的ID',
    scheduled_time  DATETIME NOT NULL COMMENT '面试预约时间',
    location        VARCHAR(200) COMMENT '线下面试地点',
    meeting_link    VARCHAR(255) COMMENT '线上面试会议链接',
    status          ENUM('SCHEDULED','COMPLETED','CANCELLED') DEFAULT 'SCHEDULED' COMMENT '面试状态：已安排/已完成/已取消',
    feedback        TEXT COMMENT '面试反馈或评语',
    FOREIGN KEY (job_id) REFERENCES job_posting(id) ON DELETE CASCADE,
    FOREIGN KEY (application_id) REFERENCES job_application(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试安排记录表';

-- 验证表结构
SELECT '✓ interview表修复成功' AS message;
DESCRIBE interview;

-- 查看表状态
SELECT 
    TABLE_NAME AS '表名',
    ENGINE AS '引擎',
    TABLE_ROWS AS '行数',
    AUTO_INCREMENT AS '自增值',
    CREATE_TIME AS '创建时间'
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'bb' AND TABLE_NAME = 'interview';



USE bb;

-- 检查并添加 homeroom_teacher_id 字段
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'bb' AND TABLE_NAME = 'student_profile_update_request' 
    AND COLUMN_NAME = 'homeroom_teacher_id');

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE student_profile_update_request ADD COLUMN homeroom_teacher_id BIGINT DEFAULT NULL COMMENT ''班主任教师ID'' AFTER reviewer_id', 
    'SELECT ''homeroom_teacher_id already exists'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 修改 status 字段枚举值
ALTER TABLE student_profile_update_request 
MODIFY COLUMN status VARCHAR(50) DEFAULT 'PENDING' COMMENT '审核状态';

-- 检查并添加 teacher 表字段
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'bb' AND TABLE_NAME = 'teacher' AND COLUMN_NAME = 'major');
SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE teacher ADD COLUMN major VARCHAR(100) DEFAULT NULL COMMENT ''专业方向'' AFTER department', 
    'SELECT ''major already exists'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'bb' AND TABLE_NAME = 'teacher' AND COLUMN_NAME = 'focus');
SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE teacher ADD COLUMN focus VARCHAR(255) DEFAULT NULL COMMENT ''研究方向'' AFTER major', 
    'SELECT ''focus already exists'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'bb' AND TABLE_NAME = 'teacher' AND COLUMN_NAME = 'biography');
SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE teacher ADD COLUMN biography TEXT DEFAULT NULL COMMENT ''个人简介'' AFTER focus', 
    'SELECT ''biography already exists'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


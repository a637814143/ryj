-- 插入面试测试数据
USE bb;

-- 确保有job_posting和job_application数据
-- 如果没有，先插入一些测试数据

-- 插入测试面试记录
INSERT INTO interview (job_id, application_id, scheduled_time, location, meeting_link, status, feedback) 
SELECT 
    jp.id AS job_id,
    ja.id AS application_id,
    DATE_ADD(NOW(), INTERVAL FLOOR(RAND() * 7 + 1) DAY) AS scheduled_time,
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN '北京市海淀区中关村软件园'
        WHEN 1 THEN '上海市浦东新区陆家嘴金融中心'
        ELSE '深圳市南山区科技园'
    END AS location,
    CONCAT('https://meeting.zoom.us/', FLOOR(RAND() * 1000000000)) AS meeting_link,
    'SCHEDULED' AS status,
    NULL AS feedback
FROM job_posting jp
INNER JOIN job_application ja ON ja.job_id = jp.id
WHERE NOT EXISTS (
    SELECT 1 FROM interview i WHERE i.application_id = ja.id
)
LIMIT 5;

-- 验证插入结果
SELECT '✓ 测试数据插入成功' AS message;

SELECT 
    i.id AS '面试ID',
    i.job_id AS '岗位ID',
    i.application_id AS '申请ID',
    i.scheduled_time AS '面试时间',
    i.location AS '地点',
    i.status AS '状态'
FROM interview i
ORDER BY i.id DESC
LIMIT 10;



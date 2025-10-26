-- 快速修复：为所有学生创建 student_profile 记录
USE bb;

-- 1. 先查看当前情况
SELECT '===== 当前学生用户情况 =====' AS Info;
SELECT 
    su.id,
    su.username,
    su.full_name,
    su.role,
    CASE 
        WHEN sp.id IS NULL THEN '❌ 缺少档案'
        ELSE '✅ 有档案'
    END AS profile_status
FROM sys_user su
LEFT JOIN student_profile sp ON su.id = sp.id
WHERE su.role = 'STUDENT';

-- 2. 为缺少档案的学生创建记录
SELECT '===== 开始修复 =====' AS Info;

INSERT IGNORE INTO student_profile (id, gender, age, major, biography, graduation_year)
SELECT 
    su.id,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL
FROM sys_user su
WHERE su.role = 'STUDENT'
  AND NOT EXISTS (
      SELECT 1 FROM student_profile sp WHERE sp.id = su.id
  );

-- 3. 验证修复结果
SELECT '===== 修复完成，验证结果 =====' AS Info;
SELECT 
    su.id,
    su.username,
    su.full_name,
    su.role,
    CASE 
        WHEN sp.id IS NULL THEN '❌ 仍然缺少档案'
        ELSE '✅ 档案已创建'
    END AS profile_status
FROM sys_user su
LEFT JOIN student_profile sp ON su.id = sp.id
WHERE su.role = 'STUDENT';

-- 4. 统计信息
SELECT '===== 统计信息 =====' AS Info;
SELECT 
    (SELECT COUNT(*) FROM sys_user WHERE role = 'STUDENT') AS total_students,
    (SELECT COUNT(*) FROM student_profile) AS students_with_profile,
    CASE 
        WHEN (SELECT COUNT(*) FROM sys_user WHERE role = 'STUDENT') = (SELECT COUNT(*) FROM student_profile)
        THEN '✅ 所有学生都有档案'
        ELSE '⚠️ 仍有学生缺少档案'
    END AS status;

SELECT '✅ 修复完成！请重启后端服务并刷新前端页面。' AS Result;


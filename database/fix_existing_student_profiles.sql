-- 为已存在的学生用户补充创建 student_profile 记录
USE bb;

-- 为所有角色为 STUDENT 但没有对应 student_profile 的用户创建记录
INSERT INTO student_profile (id, gender, age, major, biography, graduation_year)
SELECT 
    su.id,
    NULL AS gender,
    NULL AS age,
    NULL AS major,
    NULL AS biography,
    NULL AS graduation_year
FROM sys_user su
WHERE su.role = 'STUDENT'
  AND NOT EXISTS (
      SELECT 1 FROM student_profile sp WHERE sp.id = su.id
  );

-- 查看创建结果
SELECT 
    COUNT(*) AS total_students,
    (SELECT COUNT(*) FROM student_profile) AS students_with_profile
FROM sys_user
WHERE role = 'STUDENT';

SELECT 'Student profiles fixed successfully!' AS Status;


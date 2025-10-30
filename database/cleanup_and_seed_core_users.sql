-- 清理无关账号并保留系统核心教师/学生账号
USE bb;

SET @teacher_username := 'teacher_zhang';
SET @student_username := 'student1';

-- 删除无效的教师指导记录（教师或学生不在保留名单中）
DELETE tg FROM teacher_guidance tg
LEFT JOIN teacher t ON tg.teacher_id = t.id
LEFT JOIN sys_user tsu ON t.user_id = tsu.id
WHERE (tsu.username IS NULL OR tsu.username <> @teacher_username)
   OR tg.student_id NOT IN (SELECT id FROM sys_user WHERE username = @student_username);

-- 清理教师档案及教师账号
DELETE t FROM teacher t
LEFT JOIN sys_user su ON t.user_id = su.id
WHERE su.username IS NULL OR su.username <> @teacher_username;

DELETE FROM sys_user
WHERE role = 'TEACHER'
  AND username <> @teacher_username;

-- 删除除 student1 以外学生相关的业务数据
DELETE i FROM interview i
LEFT JOIN job_application ja ON i.application_id = ja.id
WHERE ja.student_id IS NOT NULL
  AND ja.student_id NOT IN (SELECT id FROM sys_user WHERE username = @student_username);

DELETE FROM job_application
WHERE student_id NOT IN (SELECT id FROM sys_user WHERE username = @student_username);

DELETE eic FROM employment_intention_city eic
LEFT JOIN employment_intention ei ON eic.intention_id = ei.id
WHERE ei.student_id NOT IN (SELECT id FROM sys_user WHERE username = @student_username);

DELETE FROM employment_intention
WHERE student_id NOT IN (SELECT id FROM sys_user WHERE username = @student_username);

DELETE rs FROM resume_skill rs
WHERE rs.resume_id IN (
    SELECT r.id FROM resume r
    WHERE r.student_id NOT IN (SELECT id FROM sys_user WHERE username = @student_username)
);

DELETE re FROM resume_experience re
WHERE re.resume_id IN (
    SELECT r.id FROM resume r
    WHERE r.student_id NOT IN (SELECT id FROM sys_user WHERE username = @student_username)
);

DELETE FROM resume
WHERE student_id NOT IN (SELECT id FROM sys_user WHERE username = @student_username);

DELETE FROM student_award
WHERE student_id NOT IN (SELECT id FROM sys_user WHERE username = @student_username);

DELETE FROM student_experience
WHERE student_id NOT IN (SELECT id FROM sys_user WHERE username = @student_username);

DELETE FROM student_education
WHERE student_id NOT IN (SELECT id FROM sys_user WHERE username = @student_username);

DELETE FROM student_profile_update_request
WHERE student_id NOT IN (SELECT id FROM sys_user WHERE username = @student_username);

DELETE FROM student_profile
WHERE id NOT IN (SELECT id FROM sys_user WHERE username = @student_username);

DELETE FROM sys_user
WHERE role = 'STUDENT'
  AND username <> @student_username;

-- 删除无法登录的禁用账号
DELETE FROM sys_user
WHERE status <> 'ACTIVE'
  AND username NOT IN (@teacher_username, @student_username);

-- 重新写入核心教师账号（张晓玲）
INSERT INTO sys_user (id, username, password_hash, full_name, email, phone, role, status, created_at, updated_at)
VALUES
    (2004, @teacher_username, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '张晓玲', 'zhang.xiaoling@university.edu.cn', '010-10000024', 'TEACHER', 'ACTIVE', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    email = VALUES(email),
    phone = VALUES(phone),
    status = VALUES(status),
    updated_at = NOW();

INSERT INTO teacher (id, user_id, department, email, phone, major, focus, biography)
VALUES
    (4, 2004, '计算机学院', 'zhang.xiaoling@university.edu.cn', '010-10000024',
     '软件工程', '就业指导 | 职业规划', '就业指导教师，致力于帮助学生实现职业发展目标')
ON DUPLICATE KEY UPDATE
    department = VALUES(department),
    email = VALUES(email),
    phone = VALUES(phone),
    major = VALUES(major),
    focus = VALUES(focus),
    biography = VALUES(biography);

-- 重新写入核心学生账号（student1）
INSERT INTO sys_user (id, username, password_hash, full_name, email, phone, role, status, created_at, updated_at)
VALUES
    (3001, @student_username, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     '示例学生', 'student1@example.com', NULL, 'STUDENT', 'ACTIVE', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    email = VALUES(email),
    status = VALUES(status),
    updated_at = NOW();

INSERT INTO student_profile (id, gender, age, major, biography, graduation_year)
VALUES
    (3001, NULL, NULL, '计算机科学与技术', NULL, 2025)
ON DUPLICATE KEY UPDATE
    major = VALUES(major),
    graduation_year = VALUES(graduation_year);

SELECT '✓ 数据清理与核心账号写入完成' AS status;

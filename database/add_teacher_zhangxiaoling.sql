-- 添加张晓玲老师账号
-- 密码：000000（已使用bcrypt加密）

USE bb;

-- 1. 添加系统用户（TEACHER角色）
INSERT INTO sys_user (id, username, password_hash, full_name, email, phone, role, status, created_at, updated_at) 
VALUES 
    (2004, 'teacher_zhang', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '张晓玲', 'zhang.xiaoling@university.edu.cn', '010-10000024', 'TEACHER', 'ACTIVE', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    password_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    full_name = '张晓玲',
    email = 'zhang.xiaoling@university.edu.cn',
    phone = '010-10000024',
    role = 'TEACHER',
    status = 'ACTIVE',
    updated_at = NOW();

-- 2. 添加教师详细信息
INSERT INTO teacher (id, user_id, department, email, phone, major, focus, biography)
VALUES
    (4, 2004, '计算机学院', 'zhang.xiaoling@university.edu.cn', '010-10000024',
     '软件工程', '就业指导 | 职业规划', '就业指导教师，致力于帮助学生实现职业发展目标')
ON DUPLICATE KEY UPDATE
    department = '计算机学院',
    email = 'zhang.xiaoling@university.edu.cn',
    phone = '010-10000024',
    major = '软件工程',
    focus = '就业指导 | 职业规划',
    biography = '就业指导教师，致力于帮助学生实现职业发展目标';

-- 3. 为张晓玲老师分配一些指导学生（可选）
-- 与韩雪、陈宇建立指导关系
INSERT INTO teacher_guidance (teacher_id, student_id, note, created_at)
VALUES
    (4, 3001, '第一次见面，了解学生基本情况和就业意向', NOW()),
    (4, 3002, '建议学生完善简历，准备面试技巧培训', NOW())
ON DUPLICATE KEY UPDATE
    note = note;

-- 验证添加结果
SELECT '✓ 张晓玲老师账号添加成功' AS status;

SELECT 
    u.id AS '用户ID',
    u.username AS '用户名',
    u.full_name AS '姓名',
    u.email AS '邮箱',
    u.phone AS '电话',
    u.role AS '角色',
    u.status AS '状态'
FROM sys_user u
WHERE u.username = 'teacher_zhang';

SELECT 
    t.id AS '教师ID',
    t.user_id AS '关联用户ID',
    t.department AS '院系',
    t.email AS '邮箱',
    t.phone AS '电话'
FROM teacher t
WHERE t.user_id = 2004;


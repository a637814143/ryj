-- 为公共首页展示准备示例数据
USE bb;

-- 1. 基础用户（管理员、教师、学生）
INSERT INTO sys_user (id, username, password_hash, full_name, email, phone, role, status, created_at, updated_at) VALUES
    (9001, 'admin_portal', '$2a$10$abcdefghijklmnopqrstuv', '就业办管理员', 'career-admin@university.edu.cn', '010-10000001', 'ADMIN', 'ACTIVE', '2024-02-01 09:00:00', '2024-02-01 09:00:00'),
    (2001, 'teacher_liu', '$2b$10$LNavVgcJM96eJf6EQRDuvejufLebKoW.QL6DIrFjQxEaUQLq5CI5S', '刘畅', 'liu.chang@university.edu.cn', '010-10000021', 'TEACHER', 'ACTIVE', '2024-02-02 09:00:00', '2024-02-02 09:00:00'),
    (2002, 'teacher_wang', '$2b$10$LNavVgcJM96eJf6EQRDuvejufLebKoW.QL6DIrFjQxEaUQLq5CI5S', '王璐', 'wang.lu@university.edu.cn', '010-10000022', 'TEACHER', 'ACTIVE', '2024-02-02 09:05:00', '2024-02-02 09:05:00'),
    (2003, 'teacher_zhao', '$2b$10$LNavVgcJM96eJf6EQRDuvejufLebKoW.QL6DIrFjQxEaUQLq5CI5S', '赵楠', 'zhao.nan@university.edu.cn', '010-10000023', 'TEACHER', 'ACTIVE', '2024-02-02 09:10:00', '2024-02-02 09:10:00'),
    (3001, 'student_han', '$2b$10$LNavVgcJM96eJf6EQRDuvejufLebKoW.QL6DIrFjQxEaUQLq5CI5S', '韩雪', 'han.xue@student.edu.cn', '13600000001', 'STUDENT', 'ACTIVE', '2024-02-03 10:00:00', '2024-02-03 10:00:00'),
    (3002, 'student_chen', '$2b$10$LNavVgcJM96eJf6EQRDuvejufLebKoW.QL6DIrFjQxEaUQLq5CI5S', '陈宇', 'chen.yu@student.edu.cn', '13600000002', 'STUDENT', 'ACTIVE', '2024-02-03 10:05:00', '2024-02-03 10:05:00'),
    (3003, 'student_li', '$2b$10$LNavVgcJM96eJf6EQRDuvejufLebKoW.QL6DIrFjQxEaUQLq5CI5S', '李想', 'li.xiang@student.edu.cn', '13600000003', 'STUDENT', 'ACTIVE', '2024-02-03 10:10:00', '2024-02-03 10:10:00')
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    email = VALUES(email),
    phone = VALUES(phone),
    role = VALUES(role),
    status = VALUES(status);

-- 2. 教师信息
INSERT INTO teacher (id, user_id, department, email, phone) VALUES
    (1, 2001, '计算机科学与技术学院', 'liu.chang@university.edu.cn', '010-10000021'),
    (2, 2002, '软件工程学院', 'wang.lu@university.edu.cn', '010-10000022'),
    (3, 2003, '信息管理学院', 'zhao.nan@university.edu.cn', '010-10000023')
ON DUPLICATE KEY UPDATE
    department = VALUES(department),
    email = VALUES(email),
    phone = VALUES(phone);

-- 3. 学生档案信息
INSERT INTO student_profile (id, gender, age, major, biography, graduation_year) VALUES
    (3001, '女', 22, '计算机科学与技术', '热衷人工智能与校园科技创新，参与多项科研项目。', 2024),
    (3002, '男', 23, '软件工程', '熟悉前后端开发流程，关注用户体验设计。', 2024),
    (3003, '男', 21, '信息管理与信息系统', '擅长数据分析，关注就业市场趋势与政策。', 2025)
ON DUPLICATE KEY UPDATE
    gender = VALUES(gender),
    age = VALUES(age),
    major = VALUES(major),
    biography = VALUES(biography),
    graduation_year = VALUES(graduation_year);

-- 4. 系统通知
INSERT INTO system_notification (id, user_id, category, title, content, read_flag, created_at) VALUES
    (1, 9001, 'SYSTEM', '春季校园招聘全面启动', '2024 年春季双选会正式开放报名，欢迎企业与学生踊跃参与。', 0, '2024-02-20 09:00:00'),
    (2, 9001, 'GUIDANCE', '就业指导公开课上线', '名师带来求职准备、面试技巧专题课程，可在平台预约观看。', 0, '2024-02-22 14:30:00'),
    (3, 9001, 'APPLICATION', '重点企业内推名额开放', '腾讯、阿里等重点企业开放校招内推名额，请尽快完善简历。', 0, '2024-02-25 10:15:00'),
    (4, 9001, 'INTERVIEW', '面试礼仪训练营报名通知', '就业中心联合企业导师推出线下面试礼仪训练营，名额有限。', 0, '2024-02-28 16:45:00')
ON DUPLICATE KEY UPDATE
    category = VALUES(category),
    title = VALUES(title),
    content = VALUES(content),
    read_flag = VALUES(read_flag),
    created_at = VALUES(created_at);

-- 5. 公共资料
INSERT INTO public_resource (id, uploader_id, file_name, file_type, file_size, storage_path, download_url, description, created_at) VALUES
    (1, 9001, '通用简历模板.txt', 'text/plain', 427, 'uploads/resources/resume-template.txt', '/api/public/files/1/download', '标准简历编写结构与要点提示', '2024-02-18 08:30:00'),
    (2, 2001, '面试准备清单.txt', 'text/plain', 491, 'uploads/resources/interview-checklist.txt', '/api/public/files/2/download', '教师精选的线下面试准备要点', '2024-02-19 09:00:00'),
    (3, 2002, '校企合作指南.txt', 'text/plain', 461, 'uploads/resources/employer-cooperation-guide.txt', '/api/public/files/3/download', '面向企业的合作流程与服务说明', '2024-02-21 11:20:00')
ON DUPLICATE KEY UPDATE
    uploader_id = VALUES(uploader_id),
    file_name = VALUES(file_name),
    file_type = VALUES(file_type),
    file_size = VALUES(file_size),
    storage_path = VALUES(storage_path),
    download_url = VALUES(download_url),
    description = VALUES(description),
    created_at = VALUES(created_at);

-- 6. 搜索历史示例
INSERT INTO public_search_history (id, user_id, keyword, role_filter, category_filter, location_filter, advanced_options, created_at) VALUES
    (1, 3001, '前端开发', 'STUDENT', 'job,notification', '北京', 'keyword=%E5%89%8D%E7%AB%AF%E5%BC%80%E5%8F%91&role=STUDENT', '2024-02-24 20:15:00'),
    (2, 3001, '面试礼仪', 'STUDENT', 'resource', NULL, 'keyword=%E9%9D%A2%E8%AF%95%E7%A4%BC%E4%BB%AA&category=resource', '2024-02-26 09:30:00'),
    (3, 3002, '内推', 'STUDENT', 'job', '深圳', 'keyword=%E5%86%85%E6%8E%A8&location=%E6%B7%B1%E5%9C%B3', '2024-02-27 18:40:00')
ON DUPLICATE KEY UPDATE
    keyword = VALUES(keyword),
    role_filter = VALUES(role_filter),
    category_filter = VALUES(category_filter),
    location_filter = VALUES(location_filter),
    advanced_options = VALUES(advanced_options),
    created_at = VALUES(created_at);

-- 查询校验
SELECT '公共首页演示数据已导入完成' AS status;

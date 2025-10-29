-- 教师角色演示数据
USE bb;

-- 1. 学生简历示例，供教师关联就业进度
INSERT INTO resume (id, student_id, title, summary, portfolio_url, created_at, updated_at) VALUES
    (7001, 3001, '智能算法工程师简历', '专注计算机视觉与深度学习算法研发，具备多项科研竞赛经历。', NULL, '2024-02-01 10:00:00', '2024-02-20 18:00:00'),
    (7002, 3002, '全栈开发工程师履历', '熟悉 Java / Vue 全栈开发，关注高性能系统设计与用户体验。', 'https://portfolio.student-chen.dev', '2024-02-03 09:30:00', '2024-02-22 16:30:00'),
    (7003, 3003, '数据分析与运营简历', '擅长数据建模与可视化，热衷于数字化运营策略研究。', NULL, '2024-02-05 08:45:00', '2024-02-21 14:10:00')
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    summary = VALUES(summary),
    portfolio_url = VALUES(portfolio_url),
    updated_at = VALUES(updated_at);

-- 2. 学生档案更新申请，供教师审核
INSERT INTO student_profile_update_request (id, student_id, gender, age, major, biography, graduation_year, status, created_at, reviewed_at, reviewer_id, review_comment) VALUES
    (6001, 3001, '女', 22, '人工智能', '新增一段科研项目经历，请老师审核更新。', 2024, 'PENDING', '2024-02-29 09:15:00', NULL, NULL, NULL),
    (6002, 3002, '男', 23, '软件工程', '希望补充开源社区贡献与实习成果。', 2024, 'PENDING', '2024-02-27 15:40:00', NULL, NULL, NULL),
    (6003, 3003, '男', 21, '信息管理与信息系统', '完善了数据分析方向的实战经验。', 2025, 'APPROVED', '2024-02-18 14:00:00', '2024-02-20 10:00:00', 2001, '资料翔实，建议继续积累行业案例。')
ON DUPLICATE KEY UPDATE
    gender = VALUES(gender),
    age = VALUES(age),
    major = VALUES(major),
    biography = VALUES(biography),
    graduation_year = VALUES(graduation_year),
    status = VALUES(status),
    reviewed_at = VALUES(reviewed_at),
    reviewer_id = VALUES(reviewer_id),
    review_comment = VALUES(review_comment);

-- 3. 教师指导记录，建立教师与学生的关联关系
INSERT INTO teacher_guidance (id, teacher_id, student_id, note, created_at) VALUES
    (101, 1, 3001, '梳理算法岗位面试重点，安排模拟面试训练。', '2024-02-20 09:00:00'),
    (102, 1, 3002, '针对全栈岗位完善项目闭环总结，补充产品思考。', '2024-02-21 15:20:00'),
    (103, 1, 3001, '跟进实验室科研成果，准备校企联合路演资料。', '2024-02-25 10:15:00'),
    (104, 1, 3003, '更新数据分析作品集，强调业务转化指标。', '2024-02-24 16:45:00'),
    (105, 2, 3003, '信息学院联合导师会谈，规划交叉学科实践。', '2024-02-26 13:30:00')
ON DUPLICATE KEY UPDATE
    note = VALUES(note),
    created_at = VALUES(created_at);

-- 4. 学生求职申请记录，展示校企协同情况
INSERT INTO job_application (id, job_id, student_id, resume_id, status, cover_letter, applied_at) VALUES
    (8001, 4, 3001, 7001, 'REVIEWING', '期待加入贵司算法团队，深入参与推荐系统优化。', '2024-02-26 11:20:00'),
    (8002, 1, 3002, 7002, 'INTERVIEW', '擅长高并发场景的全栈交付，期待与贵司进一步沟通。', '2024-02-23 10:05:00'),
    (8003, 2, 3001, 7001, 'SUBMITTED', '具备前后端协作经验，关注企业级前端工程体系建设。', '2024-02-28 19:40:00'),
    (8004, 6, 3003, 7003, 'SUBMITTED', '熟悉数据中台与用户增长策略，期待参与供应链数字化项目。', '2024-02-24 13:55:00')
ON DUPLICATE KEY UPDATE
    resume_id = VALUES(resume_id),
    status = VALUES(status),
    cover_letter = VALUES(cover_letter),
    applied_at = VALUES(applied_at);

-- 5. 面试安排记录，体现教师关注的学生进度
INSERT INTO interview (id, job_id, application_id, scheduled_time, location, meeting_link, status, feedback) VALUES
    (9001, 1, 8002, '2024-03-05 14:00:00', '北京市海淀区中关村创新园 3 号楼', NULL, 'SCHEDULED', NULL),
    (9002, 4, 8001, '2024-02-27 10:30:00', NULL, 'https://meet.example.com/alg-mentor', 'COMPLETED', '候选人表现稳定，建议继续强化案例表达。')
ON DUPLICATE KEY UPDATE
    scheduled_time = VALUES(scheduled_time),
    location = VALUES(location),
    meeting_link = VALUES(meeting_link),
    status = VALUES(status),
    feedback = VALUES(feedback);

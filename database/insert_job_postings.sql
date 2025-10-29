-- 插入更多招聘职位数据
USE bb;

-- 1. 先创建企业用户和企业信息
INSERT INTO sys_user (id, username, password_hash, full_name, email, phone, role, status, created_at, updated_at) VALUES
    (4001, 'employer_tencent', '$2b$10$LNavVgcJM96eJf6EQRDuvejufLebKoW.QL6DIrFjQxEaUQLq5CI5S', '腾讯科技', 'hr@tencent.com', '0755-86013388', 'EMPLOYER', 'ACTIVE', '2024-01-10 09:00:00', '2024-01-10 09:00:00'),
    (4002, 'employer_alibaba', '$2b$10$LNavVgcJM96eJf6EQRDuvejufLebKoW.QL6DIrFjQxEaUQLq5CI5S', '阿里巴巴', 'hr@alibaba.com', '0571-85022088', 'EMPLOYER', 'ACTIVE', '2024-01-11 09:00:00', '2024-01-11 09:00:00'),
    (4003, 'employer_bytedance', '$2b$10$LNavVgcJM96eJf6EQRDuvejufLebKoW.QL6DIrFjQxEaUQLq5CI5S', '字节跳动', 'hr@bytedance.com', '010-84594000', 'EMPLOYER', 'ACTIVE', '2024-01-12 09:00:00', '2024-01-12 09:00:00'),
    (4004, 'employer_huawei', '$2b$10$LNavVgcJM96eJf6EQRDuvejufLebKoW.QL6DIrFjQxEaUQLq5CI5S', '华为技术', 'hr@huawei.com', '0755-28780808', 'EMPLOYER', 'ACTIVE', '2024-01-13 09:00:00', '2024-01-13 09:00:00'),
    (4005, 'employer_baidu', '$2b$10$LNavVgcJM96eJf6EQRDuvejufLebKoW.QL6DIrFjQxEaUQLq5CI5S', '百度', 'hr@baidu.com', '010-59928888', 'EMPLOYER', 'ACTIVE', '2024-01-14 09:00:00', '2024-01-14 09:00:00'),
    (4006, 'employer_meituan', '$2b$10$LNavVgcJM96eJf6EQRDuvejufLebKoW.QL6DIrFjQxEaUQLq5CI5S', '美团', 'hr@meituan.com', '010-84594000', 'EMPLOYER', 'ACTIVE', '2024-01-15 09:00:00', '2024-01-15 09:00:00'),
    (4007, 'employer_jd', '$2b$10$LNavVgcJM96eJf6EQRDuvejufLebKoW.QL6DIrFjQxEaUQLq5CI5S', '京东', 'hr@jd.com', '010-59993000', 'EMPLOYER', 'ACTIVE', '2024-01-16 09:00:00', '2024-01-16 09:00:00'),
    (4008, 'employer_netease', '$2b$10$LNavVgcJM96eJf6EQRDuvejufLebKoW.QL6DIrFjQxEaUQLq5CI5S', '网易', 'hr@163.com', '020-85105163', 'EMPLOYER', 'ACTIVE', '2024-01-17 09:00:00', '2024-01-17 09:00:00')
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    email = VALUES(email),
    phone = VALUES(phone);

-- 2. 创建企业详细信息
INSERT INTO employer (id, user_id, company_name, contact_person, contact_email, contact_phone, description, website) VALUES
    (1, 4001, '腾讯科技有限公司', '张婷', 'zhang.ting@tencent.com', '0755-86013388', '中国领先的互联网增值服务提供商，业务涵盖社交、游戏、金融等多个领域。', 'https://www.tencent.com'),
    (2, 4002, '阿里巴巴集团', '李明', 'li.ming@alibaba.com', '0571-85022088', '全球领先的电商和云计算企业，致力于让天下没有难做的生意。', 'https://www.alibaba.com'),
    (3, 4003, '字节跳动科技有限公司', '王芳', 'wang.fang@bytedance.com', '010-84594000', '全球化科技公司，旗下拥有抖音、今日头条等热门产品。', 'https://www.bytedance.com'),
    (4, 4004, '华为技术有限公司', '刘强', 'liu.qiang@huawei.com', '0755-28780808', '全球领先的ICT基础设施和智能终端提供商。', 'https://www.huawei.com'),
    (5, 4005, '百度在线网络技术有限公司', '赵敏', 'zhao.min@baidu.com', '010-59928888', '全球领先的人工智能公司，拥有强大的搜索引擎技术。', 'https://www.baidu.com'),
    (6, 4006, '美团', '孙悦', 'sun.yue@meituan.com', '010-84594000', '中国领先的生活服务电子商务平台。', 'https://www.meituan.com'),
    (7, 4007, '京东集团', '周杰', 'zhou.jie@jd.com', '010-59993000', '中国领先的综合性电商平台和零售基础设施服务商。', 'https://www.jd.com'),
    (8, 4008, '网易公司', '郑娜', 'zheng.na@163.com', '020-85105163', '中国领先的互联网技术公司，专注于游戏、邮箱、音乐等业务。', 'https://www.163.com')
ON DUPLICATE KEY UPDATE
    company_name = VALUES(company_name),
    contact_person = VALUES(contact_person),
    contact_email = VALUES(contact_email),
    contact_phone = VALUES(contact_phone),
    description = VALUES(description),
    website = VALUES(website);

-- 3. 创建招聘职位
INSERT INTO job_posting (id, employer_id, title, description, salary_range, location, work_type, status, published_date, closing_date) VALUES
    -- 腾讯职位
    (1, 1, '前端开发工程师', '负责公司核心产品的前端架构设计与开发，使用Vue3、React等主流框架。要求熟悉前端工程化，有良好的代码规范意识。', '15-30K', '深圳', 'FULL_TIME', 'OPEN', '2024-02-01 10:00:00', '2024-04-30'),
    (2, 1, 'Java后端开发工程师', '参与微信后台服务开发，负责高并发系统的设计与优化。要求熟悉Spring Boot、MyBatis等框架，有分布式系统经验优先。', '20-40K', '深圳', 'FULL_TIME', 'OPEN', '2024-02-02 10:00:00', '2024-04-30'),
    (3, 1, '产品经理实习生', '协助产品经理进行需求调研、产品设计和项目跟进。要求对互联网产品有热情，逻辑思维清晰。', '150-250/天', '深圳', 'INTERNSHIP', 'OPEN', '2024-02-03 10:00:00', '2024-03-31'),
    
    -- 阿里巴巴职位
    (4, 2, '算法工程师', '负责推荐系统、搜索算法的研发与优化。要求熟悉机器学习、深度学习算法，有大规模数据处理经验。', '25-50K', '杭州', 'FULL_TIME', 'OPEN', '2024-02-01 11:00:00', '2024-05-31'),
    (5, 2, '云计算开发工程师', '参与阿里云产品的研发，负责分布式存储、计算平台的设计与实现。', '22-45K', '杭州', 'FULL_TIME', 'OPEN', '2024-02-02 11:00:00', '2024-05-31'),
    (6, 2, '前端开发工程师', '负责淘宝、天猫等电商平台的前端开发，优化用户体验。', '18-35K', '杭州', 'FULL_TIME', 'OPEN', '2024-02-03 11:00:00', '2024-04-30'),
    
    -- 字节跳动职位
    (7, 3, 'Android开发工程师', '负责抖音App的功能开发与性能优化。要求熟悉Android开发，有音视频开发经验优先。', '20-40K', '北京', 'FULL_TIME', 'OPEN', '2024-02-01 12:00:00', '2024-04-30'),
    (8, 3, '数据分析师', '负责产品数据分析，提供数据支持和业务洞察。要求熟悉SQL、Python，有数据可视化经验。', '15-30K', '北京', 'FULL_TIME', 'OPEN', '2024-02-02 12:00:00', '2024-04-30'),
    (9, 3, '测试开发工程师', '负责自动化测试平台的开发与维护，提升测试效率。', '18-35K', '北京', 'FULL_TIME', 'OPEN', '2024-02-03 12:00:00', '2024-04-30'),
    
    -- 华为职位
    (10, 4, '嵌入式软件工程师', '负责通信设备的嵌入式软件开发。要求熟悉C/C++，了解Linux系统。', '15-30K', '深圳', 'FULL_TIME', 'OPEN', '2024-02-01 13:00:00', '2024-05-31'),
    (11, 4, '5G研发工程师', '参与5G核心网、基站等产品的研发。要求通信、计算机相关专业，有通信协议开发经验优先。', '20-40K', '深圳', 'FULL_TIME', 'OPEN', '2024-02-02 13:00:00', '2024-05-31'),
    
    -- 百度职位
    (12, 5, 'AI研究员', '从事自然语言处理、计算机视觉等方向的研究。要求有深度学习研究经验，发表过高水平论文优先。', '30-60K', '北京', 'FULL_TIME', 'OPEN', '2024-02-01 14:00:00', '2024-05-31'),
    (13, 5, 'Python开发工程师', '负责百度AI平台的后端开发。要求熟悉Python、Django/Flask框架。', '18-35K', '北京', 'FULL_TIME', 'OPEN', '2024-02-02 14:00:00', '2024-04-30'),
    
    -- 美团职位
    (14, 6, 'Go开发工程师', '负责美团外卖后台服务开发，处理高并发业务场景。要求熟悉Go语言，有微服务架构经验。', '20-40K', '北京', 'FULL_TIME', 'OPEN', '2024-02-01 15:00:00', '2024-04-30'),
    (15, 6, '运营实习生', '协助运营团队进行活动策划、数据分析等工作。', '120-200/天', '北京', 'INTERNSHIP', 'OPEN', '2024-02-02 15:00:00', '2024-03-31'),
    
    -- 京东职位
    (16, 7, '供应链算法工程师', '负责智能仓储、配送路径优化等算法研发。', '22-45K', '北京', 'FULL_TIME', 'OPEN', '2024-02-01 16:00:00', '2024-05-31'),
    (17, 7, '全栈开发工程师', '参与电商平台全栈开发，负责前后端技术选型和架构设计。', '20-40K', '北京', 'FULL_TIME', 'OPEN', '2024-02-02 16:00:00', '2024-04-30'),
    
    -- 网易职位
    (18, 8, '游戏开发工程师', '负责网易游戏的客户端或服务端开发。要求熟悉Unity或UE4，有游戏开发经验优先。', '18-38K', '广州', 'FULL_TIME', 'OPEN', '2024-02-01 17:00:00', '2024-04-30'),
    (19, 8, 'UI设计师', '负责产品界面设计，输出高质量的视觉设计稿。要求有良好的审美能力和设计功底。', '12-25K', '广州', 'FULL_TIME', 'OPEN', '2024-02-02 17:00:00', '2024-04-30'),
    (20, 8, '音乐产品经理', '负责网易云音乐产品规划与迭代。要求对音乐行业有深入了解。', '18-35K', '广州', 'FULL_TIME', 'OPEN', '2024-02-03 17:00:00', '2024-04-30')
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    description = VALUES(description),
    salary_range = VALUES(salary_range),
    location = VALUES(location),
    work_type = VALUES(work_type),
    status = VALUES(status),
    published_date = VALUES(published_date),
    closing_date = VALUES(closing_date);

-- 4. 添加职位要求
INSERT INTO job_requirement (job_id, requirement) VALUES
    -- 腾讯前端
    (1, '计算机相关专业本科及以上学历'),
    (1, '3年以上前端开发经验'),
    (1, '精通Vue3或React框架'),
    (1, '熟悉前端工程化工具'),
    
    -- 腾讯Java
    (2, '计算机相关专业本科及以上学历'),
    (2, '熟悉Java、Spring Boot、MyBatis'),
    (2, '有分布式系统开发经验'),
    (2, '良好的代码规范和文档习惯'),
    
    -- 腾讯实习生
    (3, '本科及以上在读，计算机、设计等相关专业'),
    (3, '对互联网产品有热情'),
    (3, '每周至少实习3天，持续3个月以上'),
    
    -- 阿里算法
    (4, '计算机、数学等相关专业硕士及以上学历'),
    (4, '熟悉常见机器学习、深度学习算法'),
    (4, '熟练使用Python、TensorFlow或PyTorch'),
    (4, '有推荐系统或搜索引擎开发经验优先'),
    
    -- 阿里云计算
    (5, '计算机相关专业本科及以上学历'),
    (5, '熟悉分布式系统原理'),
    (5, '有大规模系统开发经验'),
    
    -- 阿里前端
    (6, '计算机相关专业本科及以上学历'),
    (6, '熟悉React、Vue等主流框架'),
    (6, '有电商或大型Web应用开发经验优先'),
    
    -- 字节Android
    (7, '计算机相关专业本科及以上学历'),
    (7, '熟悉Android开发，熟练使用Kotlin或Java'),
    (7, '有音视频开发经验优先'),
    
    -- 字节数据分析
    (8, '统计学、数学、计算机等相关专业本科及以上'),
    (8, '熟练使用SQL、Python进行数据分析'),
    (8, '熟悉常用数据可视化工具'),
    
    -- 字节测试
    (9, '计算机相关专业本科及以上学历'),
    (9, '熟悉自动化测试框架'),
    (9, '有测试平台开发经验优先'),
    
    -- 华为嵌入式
    (10, '计算机、电子等相关专业本科及以上'),
    (10, '熟练掌握C/C++语言'),
    (10, '了解Linux操作系统'),
    
    -- 华为5G
    (11, '通信工程、计算机等相关专业硕士及以上'),
    (11, '熟悉通信协议栈'),
    (11, '有5G或4G开发经验优先'),
    
    -- 百度AI
    (12, '计算机、数学等相关专业博士学历'),
    (12, '在NLP或CV领域有深入研究'),
    (12, '在顶级会议发表过论文'),
    
    -- 百度Python
    (13, '计算机相关专业本科及以上学历'),
    (13, '熟练使用Python语言'),
    (13, '熟悉Django或Flask框架'),
    
    -- 美团Go
    (14, '计算机相关专业本科及以上学历'),
    (14, '熟练使用Go语言'),
    (14, '有微服务开发经验'),
    
    -- 美团运营实习
    (15, '本科及以上在读'),
    (15, '对互联网运营感兴趣'),
    (15, '有数据分析能力'),
    
    -- 京东供应链
    (16, '计算机、数学、运筹学等相关专业硕士及以上'),
    (16, '熟悉优化算法'),
    (16, '有物流、供应链算法经验优先'),
    
    -- 京东全栈
    (17, '计算机相关专业本科及以上学历'),
    (17, '熟悉前后端开发技术'),
    (17, '有全栈项目经验'),
    
    -- 网易游戏
    (18, '计算机相关专业本科及以上学历'),
    (18, '熟悉Unity3D或Unreal Engine'),
    (18, '热爱游戏，有游戏开发经验优先'),
    
    -- 网易UI
    (19, '设计相关专业本科及以上学历'),
    (19, '精通Sketch、Figma等设计工具'),
    (19, '有良好的审美和创意能力'),
    
    -- 网易音乐PM
    (20, '本科及以上学历'),
    (20, '对音乐行业有深入了解'),
    (20, '3年以上产品经理经验')
ON DUPLICATE KEY UPDATE
    requirement = VALUES(requirement);

-- 查询校验
SELECT CONCAT('成功插入 ', COUNT(*), ' 个企业') AS status FROM employer WHERE id BETWEEN 1 AND 8;
SELECT CONCAT('成功插入 ', COUNT(*), ' 个职位') AS status FROM job_posting WHERE id BETWEEN 1 AND 20;
SELECT CONCAT('成功插入 ', COUNT(*), ' 条职位要求') AS status FROM job_requirement WHERE job_id BETWEEN 1 AND 20;


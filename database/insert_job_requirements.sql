-- 插入职位要求数据
USE bb;

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 职位1的要求：Java后端开发工程师
INSERT INTO job_requirement (job_id, requirement) VALUES
(1, '本科及以上学历，计算机相关专业'),
(1, '3年以上Java开发经验'),
(1, '熟练掌握Spring Boot、MyBatis等主流框架'),
(1, '熟悉MySQL数据库，了解Redis缓存'),
(1, '有分布式系统开发经验者优先');

-- 职位2的要求：前端开发实习生
INSERT INTO job_requirement (job_id, requirement) VALUES
(2, '本科及以上学历在读，计算机相关专业'),
(2, '熟悉HTML、CSS、JavaScript基础知识'),
(2, '了解Vue.js或React框架'),
(2, '每周可实习3天及以上'),
(2, '学习能力强，有团队合作精神');

-- 职位3的要求：Python数据分析师
INSERT INTO job_requirement (job_id, requirement) VALUES
(3, '本科及以上学历，统计、数学、计算机相关专业'),
(3, '熟练使用Python进行数据分析'),
(3, '掌握Pandas、NumPy等数据分析库'),
(3, '熟悉SQL，能编写复杂查询'),
(3, '有数据可视化项目经验者优先');

-- 职位4的要求：算法工程师
INSERT INTO job_requirement (job_id, requirement) VALUES
(4, '硕士及以上学历，计算机、人工智能相关专业'),
(4, '扎实的机器学习理论基础'),
(4, '熟悉TensorFlow或PyTorch深度学习框架'),
(4, '有推荐系统或搜索引擎项目经验优先'),
(4, '良好的英文论文阅读能力');

-- 职位5的要求：产品经理
INSERT INTO job_requirement (job_id, requirement) VALUES
(5, '本科及以上学历，3年以上产品经理经验'),
(5, '熟悉产品设计流程和方法论'),
(5, '优秀的沟通协调能力'),
(5, '具备数据分析能力'),
(5, '熟练使用Axure、Figma等原型工具');

-- 职位6的要求：UI/UX设计师
INSERT INTO job_requirement (job_id, requirement) VALUES
(6, '本科及以上学历，设计相关专业'),
(6, '2年以上UI/UX设计经验'),
(6, '精通Figma、Sketch等设计工具'),
(6, '有完整的移动端或Web端项目作品'),
(6, '良好的审美能力和创意思维');

-- 职位7的要求：测试工程师
INSERT INTO job_requirement (job_id, requirement) VALUES
(7, '本科及以上学历，计算机相关专业'),
(7, '2年以上软件测试经验'),
(7, '熟悉测试流程和测试方法'),
(7, '了解自动化测试工具，如Selenium、Appium'),
(7, '细心负责，逻辑思维能力强');

-- 职位8的要求：人工智能研究员
INSERT INTO job_requirement (job_id, requirement) VALUES
(8, '硕士及以上学历，计算机、人工智能相关专业'),
(8, '深厚的NLP理论基础'),
(8, '熟悉Transformer、BERT等模型'),
(8, '有大语言模型研发经验者优先'),
(8, '在顶会发表过论文者优先');

-- 职位9的要求：Android开发工程师
INSERT INTO job_requirement (job_id, requirement) VALUES
(9, '本科及以上学历，计算机相关专业'),
(9, '3年以上Android开发经验'),
(9, '熟练掌握Kotlin和Java'),
(9, '熟悉Android框架和常用组件'),
(9, '有性能优化经验者优先');

-- 职位10的要求：前端开发工程师
INSERT INTO job_requirement (job_id, requirement) VALUES
(10, '本科及以上学历，计算机相关专业'),
(10, '2年以上前端开发经验'),
(10, '精通Vue.js或React框架'),
(10, '熟悉TypeScript、Webpack'),
(10, '有移动端H5开发经验者优先');

-- 职位11的要求：游戏策划
INSERT INTO job_requirement (job_id, requirement) VALUES
(11, '本科及以上学历，热爱游戏'),
(11, '有游戏策划经验或相关作品'),
(11, '熟悉各类游戏玩法和机制'),
(11, '良好的文档撰写能力'),
(11, '时间灵活，可远程办公');

-- 职位12的要求：运维工程师
INSERT INTO job_requirement (job_id, requirement) VALUES
(12, '本科及以上学历，计算机相关专业'),
(12, '3年以上运维经验'),
(12, '精通Linux系统管理'),
(12, '熟悉Docker、Kubernetes容器技术'),
(12, '有自动化运维经验');

-- 职位13的要求：供应链管理专员
INSERT INTO job_requirement (job_id, requirement) VALUES
(13, '本科及以上学历，物流管理、供应链相关专业'),
(13, '2年以上供应链管理经验'),
(13, '熟悉采购流程和供应商管理'),
(13, '良好的数据分析能力'),
(13, '优秀的沟通协调能力');

-- 职位14的要求：内容运营
INSERT INTO job_requirement (job_id, requirement) VALUES
(14, '本科及以上学历，中文、新闻、市场营销相关专业'),
(14, '1年以上内容运营经验'),
(14, '优秀的文案撰写能力'),
(14, '熟悉微信、微博等新媒体平台'),
(14, '有创意，思维活跃');

-- 职位15的要求：远程全栈开发工程师
INSERT INTO job_requirement (job_id, requirement) VALUES
(15, '本科及以上学历，计算机相关专业'),
(15, '5年以上全栈开发经验'),
(15, '精通前端框架（Vue/React）和后端开发（Node.js/Java/Python）'),
(15, '有独立完成项目的能力'),
(15, '自驱力强，适应远程办公');

SELECT '职位要求导入完成' as status, COUNT(*) as total_requirements FROM job_requirement;


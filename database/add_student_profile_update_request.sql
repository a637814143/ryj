-- 添加学生资料更新请求表
USE bb;

CREATE TABLE IF NOT EXISTS student_profile_update_request (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID，自增',
    student_id    BIGINT NOT NULL COMMENT '关联学生资料表student_profile的ID',
    gender        VARCHAR(10) COMMENT '更新的性别',
    age           INT COMMENT '更新的年龄',
    major         VARCHAR(100) COMMENT '更新的专业',
    biography     TEXT COMMENT '更新的个人简介',
    graduation_year INT COMMENT '更新的预计毕业年份',
    status        ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING' COMMENT '审核状态：待审核/已批准/已拒绝',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '请求创建时间',
    reviewed_at   DATETIME COMMENT '审核时间',
    reviewer_id   BIGINT COMMENT '审核人ID（教师或管理员）',
    review_comment TEXT COMMENT '审核意见',
    FOREIGN KEY (student_id) REFERENCES student_profile(id),
    FOREIGN KEY (reviewer_id) REFERENCES sys_user(id)
) COMMENT='学生资料更新请求表';

SELECT 'Table student_profile_update_request created successfully!' AS Status;


-- 大学生就业管理系统核心数据表设计 (MySQL)

CREATE TABLE sys_user (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(120) NOT NULL,
    phone         VARCHAR(30),
    role          ENUM('STUDENT','TEACHER','EMPLOYER','ADMIN') NOT NULL,
    status        ENUM('ACTIVE','DISABLED') DEFAULT 'ACTIVE',
    created_at    DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE student_profile (
    id                BIGINT PRIMARY KEY,
    gender            VARCHAR(10),
    age               INT,
    major             VARCHAR(100),
    biography         TEXT,
    graduation_year   INT,
    FOREIGN KEY (id) REFERENCES sys_user(id)
);

CREATE TABLE student_education (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id    BIGINT      NOT NULL,
    school        VARCHAR(120) NOT NULL,
    major         VARCHAR(120),
    degree        VARCHAR(60),
    start_date    DATE,
    end_date      DATE,
    description   TEXT,
    FOREIGN KEY (student_id) REFERENCES student_profile(id)
);

CREATE TABLE student_experience (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id    BIGINT      NOT NULL,
    title         VARCHAR(150) NOT NULL,
    organization  VARCHAR(150),
    start_date    DATE,
    end_date      DATE,
    description   TEXT,
    experience_type ENUM('INTERNSHIP','PROJECT','VOLUNTEER','OTHER') DEFAULT 'OTHER',
    FOREIGN KEY (student_id) REFERENCES student_profile(id)
);

CREATE TABLE student_award (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id  BIGINT      NOT NULL,
    name        VARCHAR(150) NOT NULL,
    award_date  DATE,
    level       VARCHAR(60),
    description TEXT,
    FOREIGN KEY (student_id) REFERENCES student_profile(id)
);

CREATE TABLE employment_intention (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id        BIGINT      NOT NULL UNIQUE,
    expected_position VARCHAR(120),
    salary_range      VARCHAR(60),
    work_type         ENUM('FULL_TIME','PART_TIME','INTERNSHIP','FLEXIBLE'),
    notes             TEXT,
    FOREIGN KEY (student_id) REFERENCES student_profile(id)
);

CREATE TABLE employment_intention_city (
    intention_id BIGINT NOT NULL,
    city         VARCHAR(80) NOT NULL,
    PRIMARY KEY (intention_id, city),
    FOREIGN KEY (intention_id) REFERENCES employment_intention(id)
);

CREATE TABLE resume (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id    BIGINT      NOT NULL,
    title         VARCHAR(150) NOT NULL,
    summary       TEXT,
    portfolio_url VARCHAR(255),
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student_profile(id)
);

CREATE TABLE resume_skill (
    resume_id BIGINT      NOT NULL,
    skill     VARCHAR(80) NOT NULL,
    proficiency TINYINT,
    PRIMARY KEY (resume_id, skill),
    FOREIGN KEY (resume_id) REFERENCES resume(id)
);

CREATE TABLE resume_experience (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    resume_id   BIGINT      NOT NULL,
    title       VARCHAR(150) NOT NULL,
    organization VARCHAR(150),
    start_date  DATE,
    end_date    DATE,
    description TEXT,
    FOREIGN KEY (resume_id) REFERENCES resume(id)
);

CREATE TABLE employer (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    company_name   VARCHAR(150) NOT NULL,
    contact_person VARCHAR(100),
    contact_email  VARCHAR(120),
    contact_phone  VARCHAR(40),
    description    TEXT,
    website        VARCHAR(200)
);

CREATE TABLE job_posting (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    employer_id    BIGINT      NOT NULL,
    title          VARCHAR(150) NOT NULL,
    description    TEXT,
    salary_range   VARCHAR(80),
    location       VARCHAR(120),
    work_type      ENUM('FULL_TIME','PART_TIME','INTERNSHIP','REMOTE'),
    status         ENUM('OPEN','CLOSED','DRAFT') DEFAULT 'OPEN',
    published_date DATE        DEFAULT CURRENT_DATE,
    closing_date   DATE,
    FOREIGN KEY (employer_id) REFERENCES employer(id)
);

CREATE TABLE job_requirement (
    job_id      BIGINT      NOT NULL,
    requirement VARCHAR(200) NOT NULL,
    PRIMARY KEY (job_id, requirement),
    FOREIGN KEY (job_id) REFERENCES job_posting(id)
);

CREATE TABLE job_application (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_id       BIGINT      NOT NULL,
    student_id   BIGINT      NOT NULL,
    resume_id    BIGINT      NOT NULL,
    status       ENUM('SUBMITTED','REVIEWING','INTERVIEW','OFFERED','REJECTED') DEFAULT 'SUBMITTED',
    cover_letter TEXT,
    applied_at   DATETIME    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_id) REFERENCES job_posting(id),
    FOREIGN KEY (student_id) REFERENCES student_profile(id),
    FOREIGN KEY (resume_id) REFERENCES resume(id)
);

CREATE TABLE interview (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_id          BIGINT      NOT NULL,
    application_id  BIGINT      NOT NULL,
    scheduled_time  DATETIME    NOT NULL,
    location        VARCHAR(200),
    meeting_link    VARCHAR(255),
    status          ENUM('SCHEDULED','COMPLETED','CANCELLED') DEFAULT 'SCHEDULED',
    feedback        TEXT,
    FOREIGN KEY (job_id) REFERENCES job_posting(id),
    FOREIGN KEY (application_id) REFERENCES job_application(id)
);

CREATE TABLE teacher (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT      NOT NULL,
    department  VARCHAR(120),
    email       VARCHAR(120),
    phone       VARCHAR(30),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE TABLE teacher_guidance (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_id  BIGINT      NOT NULL,
    student_id  BIGINT      NOT NULL,
    note        TEXT,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES teacher(id),
    FOREIGN KEY (student_id) REFERENCES student_profile(id)
);

CREATE TABLE system_notification (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT      NOT NULL,
    category    ENUM('SYSTEM','INTERVIEW','APPLICATION','GUIDANCE') DEFAULT 'SYSTEM',
    title       VARCHAR(150) NOT NULL,
    content     TEXT,
    read_flag   TINYINT(1) DEFAULT 0,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
);


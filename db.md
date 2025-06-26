-- auto-generated definition
create table admin
(
id          char(10)  default lpad((nextval('admin_id_seq'::regclass))::text, 10, '0'::text) not null
primary key,
username    varchar(8)                                                                       not null
unique,
create_time timestamp default CURRENT_TIMESTAMP,
update_time timestamp default CURRENT_TIMESTAMP,
delete_time timestamp,
tag         boolean   default true,
password    varchar(200)
);

alter table admin
owner to miirso;

-- auto-generated definition
create table chapters
(
id          serial
primary key,
course_id   varchar(8) // 同courses的id
references courses,
title       varchar(255) not null,
content     text,
"order"     integer,
create_time timestamp default CURRENT_TIMESTAMP,
update_time timestamp default CURRENT_TIMESTAMP,
tag         boolean   default true
);

alter table chapters
owner to miirso;

-- auto-generated definition
create table classes
(
id          serial
primary key,
name        varchar(255) not null
constraint unique_class_name
unique,
major_id    varchar(8),
create_time timestamp default CURRENT_TIMESTAMP,
update_time timestamp default CURRENT_TIMESTAMP,
tag         boolean   default true
);

alter table classes
owner to miirso;

-- auto-generated definition
create table course_classes
(
id         serial
primary key,
course_id  varchar(8) // 同chapter的course_id 同courses的id
references courses,
class_name varchar(255) // 同class的name
references classes (name)
);

alter table course_classes
owner to miirso;

-- auto-generated definition
create table course_teachers
(
id         serial
primary key,
course_id  varchar(8 // 同chapter的course_id 同courses的id
references courses,
teacher_id char(10) // 同teacher的id
references teacher
);

alter table course_teachers
owner to miirso;

-- auto-generated definition
create table courses
(
id                varchar(8)   not null
primary key,
subject_id        integer
references subjects,
name              varchar(255) not null, // 同course_class的class_name 
description       text,
cover_image       varchar(255),
start_date        date,
end_date          date,
assessment_method varchar(16),
create_time       timestamp default CURRENT_TIMESTAMP,
update_time       timestamp default CURRENT_TIMESTAMP,
tag               boolean   default true,
type              varchar(1)
);

comment on column courses.type is '课程类型（1位字符，用于课程ID末尾）';

alter table courses
owner to miirso;

-- auto-generated definition
create table dashboard_analytics
(
id               serial
primary key,
metric_name      varchar(255) not null,
metric_value     varchar(255) not null,
metric_unit      varchar(50),
calculation_date date         not null,
scope_type       varchar(50),
scope_id         varchar(255)
);

alter table dashboard_analytics
owner to miirso;

-- auto-generated definition
create table discussions
(
id          serial
primary key,
course_id   varchar(8)
references courses,
user_id     varchar(255) not null,
user_role   varchar(50)  not null,
parent_id   integer
references discussions,
content     text         not null,
create_time timestamp default CURRENT_TIMESTAMP,
update_time timestamp default CURRENT_TIMESTAMP,
tag         boolean   default true
);

alter table discussions
owner to miirso;

-- auto-generated definition
create table enrollments
(
id              serial
primary key,
student_id      bigint
references student,
course_id       varchar(8)
references courses,
enrollment_date timestamp default CURRENT_TIMESTAMP,
create_time     timestamp default CURRENT_TIMESTAMP,
update_time     timestamp default CURRENT_TIMESTAMP,
delete_time     timestamp,
tag             boolean   default true
);

alter table enrollments
owner to miirso;

-- auto-generated definition
create table student
(
id          bigint    default nextval('student_id_seq'::regclass) not null
primary key,
student_no  char(8)                                               not null
unique,
real_name   varchar(50)                                           not null,
gender      smallint
constraint student_gender_check
check (gender = ANY (ARRAY [0, 1])),
username    varchar(32)                                           not null
unique,
phone       char(11),
email       varchar(64),
major_code  varchar(2),
class_code  varchar(32),
college     varchar(100),
school      varchar(100),
create_time timestamp default CURRENT_TIMESTAMP,
update_time timestamp default CURRENT_TIMESTAMP,
delete_time timestamp,
tag         boolean   default true,
password    varchar(128)
);

comment on column student.major_code is '专业代码（2位）';

alter table student
owner to miirso;

-- auto-generated definition
create table student_class_relation
(
id          serial
primary key,
student_no  char(16)    not null
references student (student_no),
class_code  varchar(16) not null,
create_time timestamp default CURRENT_TIMESTAMP,
update_time timestamp default CURRENT_TIMESTAMP,
delete_time timestamp,
tag         boolean   default true,
constraint student_class_unique
unique (student_no, class_code)
);

comment on column student_class_relation.student_no is '学生学号';

comment on column student_class_relation.class_code is '班级代码';

alter table student_class_relation
owner to miirso;

create index idx_student_class_relation_student_no
on student_class_relation (student_no);

create index idx_student_class_relation_class_code
on student_class_relation (class_code);

-- auto-generated definition
create table subjects
(
id          serial
primary key,
name        varchar(255) not null
unique,
description text,
create_time timestamp default CURRENT_TIMESTAMP,
update_time timestamp default CURRENT_TIMESTAMP,
tag         boolean   default true
);

alter table subjects
owner to miirso;

-- auto-generated definition
create table teacher
(
seq_id      bigint generated always as identity,
id          char(10) generated always as (('2'::text || lpad((seq_id)::text, 9, '0'::text))) stored not null
primary key,
real_name   varchar(50)                                                                             not null,
gender      smallint
constraint teacher_gender_check
check (gender = ANY (ARRAY [0, 1])),
phone       char(11),
teacher_no  varchar(10)                                                                             not null
unique,
school      varchar(100),
college     varchar(100),
username    varchar(10)                                                                             not null
unique,
password    varchar(100)                                                                            not null,
create_time timestamp default CURRENT_TIMESTAMP,
update_time timestamp default CURRENT_TIMESTAMP,
delete_time timestamp,
tag         boolean   default true
);

alter table teacher
owner to miirso;


-- 课程简介表
CREATE TABLE course_introductions
(
id                SERIAL PRIMARY KEY,
course_id         VARCHAR(8) NOT NULL,          -- 课程ID（不使用外键约束）
introduction_text TEXT NOT NULL,                -- 课程简介文本内容
introduction_html TEXT,                         -- HTML格式的简介（支持富文本）
keywords          VARCHAR(500),                 -- 课程关键词（逗号分隔）
learning_goals    TEXT,                         -- 学习目标
prerequisites     TEXT,                         -- 先修要求
target_audience   VARCHAR(255),                 -- 目标受众
difficulty_level  VARCHAR(20),                  -- 难度等级（初级/中级/高级）
estimated_hours   INTEGER,                      -- 预计学习时长（小时）
create_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
update_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
delete_time       TIMESTAMP,
tag               BOOLEAN DEFAULT TRUE
);

COMMENT ON TABLE course_introductions IS '课程简介表';
COMMENT ON COLUMN course_introductions.course_id IS '课程ID，对应courses表的id字段';
COMMENT ON COLUMN course_introductions.introduction_text IS '课程简介纯文本内容';
COMMENT ON COLUMN course_introductions.introduction_html IS '课程简介HTML格式内容，支持富文本编辑';
COMMENT ON COLUMN course_introductions.keywords IS '课程关键词，用逗号分隔';
COMMENT ON COLUMN course_introductions.learning_goals IS '学习目标描述';
COMMENT ON COLUMN course_introductions.prerequisites IS '先修课程或知识要求';
COMMENT ON COLUMN course_introductions.target_audience IS '目标受众描述';
COMMENT ON COLUMN course_introductions.difficulty_level IS '课程难度等级';
COMMENT ON COLUMN course_introductions.estimated_hours IS '预计学习时长（小时）';
COMMENT ON COLUMN course_introductions.tag IS '逻辑删除标记';

-- 创建索引
CREATE INDEX idx_course_introductions_course_id ON course_introductions (course_id);
CREATE INDEX idx_course_introductions_difficulty ON course_introductions (difficulty_level);

ALTER TABLE course_introductions OWNER TO miirso;

-- 课程大纲表（文件存储版本）
CREATE TABLE course_outlines
(
id                SERIAL PRIMARY KEY,
course_id         VARCHAR(8) NOT NULL,              -- 课程ID
outline_file_path VARCHAR(500) NOT NULL,            -- 大纲文件存储路径
file_name         VARCHAR(255) NOT NULL,            -- 原始文件名
version           INTEGER DEFAULT 1,                -- 版本号
create_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
update_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
delete_time       TIMESTAMP,
tag               BOOLEAN DEFAULT TRUE,
CONSTRAINT unique_course_outline_version UNIQUE (course_id, version)
);

COMMENT ON TABLE course_outlines IS '课程大纲表';
COMMENT ON COLUMN course_outlines.course_id IS '课程ID，对应courses表的id字段';
COMMENT ON COLUMN course_outlines.outline_file_path IS '大纲文件在服务器上的存储路径';
COMMENT ON COLUMN course_outlines.file_name IS '原始上传的文件名';
COMMENT ON COLUMN course_outlines.version IS '大纲版本号，支持版本管理';
COMMENT ON COLUMN course_outlines.tag IS '逻辑删除标记';

-- 创建索引
CREATE INDEX idx_course_outlines_course_id ON course_outlines (course_id);
CREATE INDEX idx_course_outlines_version ON course_outlines (course_id, version);

ALTER TABLE course_outlines OWNER TO miirso;

-- 试卷表 (exam_papers)
CREATE TABLE exam_papers
(
    id              SERIAL PRIMARY KEY,                    -- 试卷ID
    course_id       VARCHAR(8) NOT NULL,                   -- 课程ID
    chapter_id      INTEGER,                               -- 章节ID（可为空，表示整个课程的综合试卷）
    paper_name      VARCHAR(255) NOT NULL,                 -- 试卷名称
    paper_type      VARCHAR(20) NOT NULL,                  -- 试卷类型：chapter(章节测试)、course(课程测试)、final(期末考试)
    total_questions INTEGER DEFAULT 0,                     -- 试卷总题数
    total_score     INTEGER DEFAULT 100,                   -- 试卷总分
    time_limit      INTEGER,                               -- 考试时间限制（分钟）
    difficulty      VARCHAR(20) DEFAULT 'medium',          -- 难度等级：easy、medium、hard
    status          VARCHAR(20) DEFAULT 'active',          -- 状态：active、inactive、deleted
    ai_generated    BOOLEAN DEFAULT TRUE,                  -- 是否AI生成
    generation_prompt TEXT,                                -- AI生成时使用的提示词
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 创建时间
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 更新时间
    delete_time     TIMESTAMP,                             -- 删除时间
    tag             BOOLEAN DEFAULT TRUE                   -- 逻辑删除标记
);

COMMENT ON TABLE exam_papers IS '试卷表';
COMMENT ON COLUMN exam_papers.paper_type IS '试卷类型：chapter(章节测试)、course(课程测试)、final(期末考试)';

-- 试题表 (exam_questions)
CREATE TABLE exam_questions
(
    id              SERIAL PRIMARY KEY,                    -- 题目ID
    paper_id        INTEGER NOT NULL,                      -- 试卷ID
    question_type   VARCHAR(20) NOT NULL,                  -- 题目类型：single_choice、multiple_choice、short_answer
    question_content   TEXT NOT NULL,                         -- 题目内容
    question_order  INTEGER NOT NULL,                      -- 题目在试卷中的顺序
    score           INTEGER DEFAULT 5,                     -- 题目分值
    difficulty      VARCHAR(20) DEFAULT 'medium',          -- 难度等级
    ai_generated    BOOLEAN DEFAULT TRUE,                  -- 是否AI生成
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 创建时间
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 更新时间
    delete_time     TIMESTAMP,                             -- 删除时间
    tag             BOOLEAN DEFAULT TRUE,                  -- 逻辑删除标记
    
    CONSTRAINT fk_exam_questions_paper_id FOREIGN KEY (paper_id) REFERENCES exam_papers(id)
);

COMMENT ON TABLE exam_questions IS '试题表';
COMMENT ON COLUMN exam_questions.question_type IS '题目类型：single_choice(单选题)、multiple_choice(多选题)、short_answer(简答题)';

-- 试题选项表 (exam_question_options)
CREATE TABLE exam_question_options
(
    id              SERIAL PRIMARY KEY,                    -- 选项ID
    question_id     INTEGER NOT NULL,                      -- 题目ID
    option_label    VARCHAR(5) NOT NULL,                   -- 选项标签（A、B、C、D等）
    option_content     TEXT NOT NULL,                         -- 选项内容
    option_order    INTEGER NOT NULL,                      -- 选项顺序
    is_correct      BOOLEAN DEFAULT FALSE,                 -- 是否为正确答案
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 创建时间
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 更新时间
    delete_time     TIMESTAMP,                             -- 删除时间
    tag             BOOLEAN DEFAULT TRUE,                  -- 逻辑删除标记
    
    CONSTRAINT fk_exam_question_options_question_id FOREIGN KEY (question_id) REFERENCES exam_questions(id)
);

COMMENT ON TABLE exam_question_options IS '试题选项表';

-- 试题答案表 (exam_question_answers)
CREATE TABLE exam_question_answers
(
    id              SERIAL PRIMARY KEY,                    -- 答案ID
    question_id     INTEGER NOT NULL,                      -- 题目ID
    correct_answer  TEXT NOT NULL,                         -- 正确答案
    answer_explanation TEXT,                               -- 答案解析
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 创建时间
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 更新时间
    delete_time     TIMESTAMP,                             -- 删除时间
    tag             BOOLEAN DEFAULT TRUE,                  -- 逻辑删除标记
    
    CONSTRAINT fk_exam_question_answers_question_id FOREIGN KEY (question_id) REFERENCES exam_questions(id),
    CONSTRAINT uk_exam_question_answers_question_id UNIQUE (question_id)
);

COMMENT ON TABLE exam_question_answers IS '试题答案表';
COMMENT ON COLUMN exam_question_answers.correct_answer IS '正确答案：单选题存选项标签，多选题存逗号分隔的标签，简答题存答案文本';

-- 学生答题记录表 (student_exam_records)
CREATE TABLE student_exam_records
(
    id              SERIAL PRIMARY KEY,                    -- 记录ID
    student_id      BIGINT NOT NULL,                       -- 学生ID
    paper_id        INTEGER NOT NULL,                      -- 试卷ID
    start_time      TIMESTAMP,                             -- 开始答题时间
    submit_time     TIMESTAMP,                             -- 提交时间
    total_score     INTEGER DEFAULT 0,                     -- 总得分
    correct_count   INTEGER DEFAULT 0,                     -- 正确题数
    wrong_count     INTEGER DEFAULT 0,                     -- 错误题数
    accuracy_rate   DECIMAL(5,2) DEFAULT 0.00,            -- 正确率（百分比）
    time_spent      INTEGER,                               -- 用时（分钟）
    status          VARCHAR(20) DEFAULT 'in_progress',     -- 状态：in_progress、submitted、graded
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 创建时间
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 更新时间
    delete_time     TIMESTAMP,                             -- 删除时间
    tag             BOOLEAN DEFAULT TRUE,                  -- 逻辑删除标记
    
    CONSTRAINT fk_student_exam_records_paper_id FOREIGN KEY (paper_id) REFERENCES exam_papers(id)
);

COMMENT ON TABLE student_exam_records IS '学生答题记录表';

-- 学生答题详情表 (student_answer_details)
CREATE TABLE student_answer_details
(
    id              SERIAL PRIMARY KEY,                    -- 详情ID
    exam_record_id  INTEGER NOT NULL,                      -- 答题记录ID
    question_id     INTEGER NOT NULL,                      -- 题目ID
    student_answer  TEXT,                                  -- 学生答案
    is_correct      BOOLEAN DEFAULT FALSE,                 -- 是否正确
    score_gained    INTEGER DEFAULT 0,                     -- 得分
    answer_time     TIMESTAMP,                             -- 答题时间
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 创建时间
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 更新时间
    delete_time     TIMESTAMP,                             -- 删除时间
    tag             BOOLEAN DEFAULT TRUE,                  -- 逻辑删除标记
    
    CONSTRAINT fk_student_answer_details_exam_record_id FOREIGN KEY (exam_record_id) REFERENCES student_exam_records(id),
    CONSTRAINT fk_student_answer_details_question_id FOREIGN KEY (question_id) REFERENCES exam_questions(id)
);

COMMENT ON TABLE student_answer_details IS '学生答题详情表';

-- AI教学建议表 (ai_teaching_suggestions)
CREATE TABLE ai_teaching_suggestions
(
    id              SERIAL PRIMARY KEY,                    -- 建议ID
    course_id       VARCHAR(8) NOT NULL,                   -- 课程ID
    paper_id        INTEGER,                               -- 试卷ID（可为空，表示整个课程的建议）
    student_id      BIGINT,                                -- 学生ID（可为空，表示针对所有学生的建议）
    suggestion_type VARCHAR(30) NOT NULL,                  -- 建议类型：course_general、paper_specific、student_personal
    suggestion_title VARCHAR(255) NOT NULL,                -- 建议标题
    suggestion_content TEXT NOT NULL,                      -- 建议内容
    based_on_data   TEXT,                                  -- 基于的数据分析
    ai_model        VARCHAR(50),                           -- 使用的AI模型
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 创建时间
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 更新时间
    delete_time     TIMESTAMP,                             -- 删除时间
    tag             BOOLEAN DEFAULT TRUE,                  -- 逻辑删除标记
    
    CONSTRAINT fk_ai_teaching_suggestions_paper_id FOREIGN KEY (paper_id) REFERENCES exam_papers(id)
);

COMMENT ON TABLE ai_teaching_suggestions IS 'AI教学建议表';
COMMENT ON COLUMN ai_teaching_suggestions.suggestion_type IS '建议类型：course_general(课程通用)、paper_specific(试卷特定)、student_personal(学生个人)';

-- 创建索引
CREATE INDEX idx_exam_papers_course_id ON exam_papers(course_id);
CREATE INDEX idx_exam_papers_chapter_id ON exam_papers(chapter_id);
CREATE INDEX idx_exam_questions_paper_id ON exam_questions(paper_id);
CREATE INDEX idx_exam_question_options_question_id ON exam_question_options(question_id);
CREATE INDEX idx_exam_question_answers_question_id ON exam_question_answers(question_id);
CREATE INDEX idx_student_exam_records_student_id ON student_exam_records(student_id);
CREATE INDEX idx_student_exam_records_paper_id ON student_exam_records(paper_id);
CREATE INDEX idx_student_answer_details_exam_record_id ON student_answer_details(exam_record_id);
CREATE INDEX idx_student_answer_details_question_id ON student_answer_details(question_id);
CREATE INDEX idx_ai_teaching_suggestions_course_id ON ai_teaching_suggestions(course_id);
CREATE INDEX idx_ai_teaching_suggestions_paper_id ON ai_teaching_suggestions(paper_id);

-- 设置表所有者
ALTER TABLE exam_papers OWNER TO miirso;
ALTER TABLE exam_questions OWNER TO miirso;
ALTER TABLE exam_question_options OWNER TO miirso;
ALTER TABLE exam_question_answers OWNER TO miirso;
ALTER TABLE student_exam_records OWNER TO miirso;
ALTER TABLE student_answer_details OWNER TO miirso;
ALTER TABLE ai_teaching_suggestions OWNER TO miirso;

CREATE TABLE course_qa (
id serial PRIMARY KEY,
course_id varchar(8) NOT NULL,         -- 与 courses.id 保持一致
student_id char(10) NOT NULL,          -- 与 student.id 保持一致
question_content text NOT NULL,
is_collected boolean DEFAULT false,
create_time timestamp DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE course_qa IS '课程QA表';
COMMENT ON COLUMN course_qa.id IS '主键ID';
COMMENT ON COLUMN course_qa.course_id IS '课程ID';
COMMENT ON COLUMN course_qa.student_id IS '提问学生ID';
COMMENT ON COLUMN course_qa.question_content IS '问题内容';
COMMENT ON COLUMN course_qa.is_collected IS '是否被教师收录';
COMMENT ON COLUMN course_qa.create_time IS '创建时间';

CREATE TABLE course_qa_answer (
id serial PRIMARY KEY,
qa_id integer NOT NULL REFERENCES course_qa(id),
answer_user_id char(10) NOT NULL,      -- 与 student/teacher.id 保持一致
answer_user_role varchar(16) NOT NULL, -- student/teacher
answer_content text NOT NULL,
create_time timestamp DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE course_qa_answer IS '课程QA回答表';
COMMENT ON COLUMN course_qa_answer.id IS '主键ID';
COMMENT ON COLUMN course_qa_answer.qa_id IS '所属QA问题ID';
COMMENT ON COLUMN course_qa_answer.answer_user_id IS '回答者ID';
COMMENT ON COLUMN course_qa_answer.answer_user_role IS '回答者角色 student/teacher';
COMMENT ON COLUMN course_qa_answer.answer_content IS '回答内容';
COMMENT ON COLUMN course_qa_answer.create_time IS '回答时间';

CREATE TABLE course_discussion (
id serial PRIMARY KEY,
course_id varchar(8) NOT NULL,         -- 与 courses.id 保持一致
user_id char(10) NOT NULL,             -- 与 student/teacher.id 保持一致
user_role varchar(16) NOT NULL,        -- student/teacher
content text NOT NULL,
create_time timestamp DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE course_discussion IS '课程讨论区表';
COMMENT ON COLUMN course_discussion.id IS '主键ID';
COMMENT ON COLUMN course_discussion.course_id IS '课程ID';
COMMENT ON COLUMN course_discussion.user_id IS '发言用户ID';
COMMENT ON COLUMN course_discussion.user_role IS '用户角色 student/teacher';
COMMENT ON COLUMN course_discussion.content IS '讨论内容';
COMMENT ON COLUMN course_discussion.create_time IS '发言时间';

CREATE TABLE course_evaluation (
id serial PRIMARY KEY,
course_id varchar(8) NOT NULL,         -- 与 courses.id 保持一致
student_id char(10) NOT NULL,          -- 与 student.id 保持一致
content_score int NOT NULL,            -- 内容丰富度评分 1-5
difficulty_score int NOT NULL,         -- 难度适宜度评分 1-5
teaching_score int NOT NULL,           -- 教学质量评分 1-5
harvest_score int NOT NULL,            -- 收获程度评分 1-5
comment text,
create_time timestamp DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE course_evaluation IS '课程评价表';
COMMENT ON COLUMN course_evaluation.id IS '主键ID';
COMMENT ON COLUMN course_evaluation.course_id IS '课程ID';
COMMENT ON COLUMN course_evaluation.student_id IS '评价学生ID';
COMMENT ON COLUMN course_evaluation.content_score IS '内容丰富度评分 1-5';
COMMENT ON COLUMN course_evaluation.difficulty_score IS '难度适宜度评分 1-5';
COMMENT ON COLUMN course_evaluation.teaching_score IS '教学质量评分 1-5';
COMMENT ON COLUMN course_evaluation.harvest_score IS '收获程度评分 1-5';
COMMENT ON COLUMN course_evaluation.comment IS '评价内容';
COMMENT ON COLUMN course_evaluation.create_time IS '评价时间';

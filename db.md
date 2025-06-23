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



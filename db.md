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
    course_id   varchar(8)
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
    name        varchar(255) not null,
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
    id        serial
        primary key,
    course_id varchar(8)
        references courses,
    class_id  bigint
        references classes
);

alter table course_classes
    owner to miirso;

-- auto-generated definition
create table course_teachers
(
    id         serial
        primary key,
    course_id  varchar(8)
        references courses,
    teacher_id char(10)
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
    name              varchar(255) not null,
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
create table questions
(
    id          varchar(16) not null
        primary key,
    course_id   varchar(8)
        references courses,
    chapter_id  integer
        references chapters,
    type        varchar(1)  not null,
    content     text        not null,
    options     jsonb,
    answer      text        not null,
    analysis    text,
    create_time timestamp default CURRENT_TIMESTAMP,
    update_time timestamp default CURRENT_TIMESTAMP,
    tag         boolean   default true
);

alter table questions
    owner to miirso;

-- auto-generated definition
create table resources
(
    id            serial
        primary key,
    course_id     varchar(8)
        references courses,
    user_id       varchar(255) not null,
    user_role     varchar(50)  not null,
    title         varchar(255) not null,
    description   text,
    resource_type varchar(50),
    file_name     varchar(255),
    file_path     varchar(255),
    file_size     integer,
    version       integer   default 1,
    is_shared     boolean   default false,
    upload_time   timestamp default CURRENT_TIMESTAMP,
    tag           boolean   default true
);

alter table resources
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

-- auto-generated definition
create table "user"
(
    id          char(10)     not null
        constraint user_login_pkey
            primary key,
    username    varchar(10)  not null
        constraint user_login_username_key
            unique,
    password    varchar(100) not null,
    create_time timestamp default CURRENT_TIMESTAMP,
    update_time timestamp default CURRENT_TIMESTAMP,
    delete_time timestamp,
    tag         boolean   default true,
    type        integer      not null
);

alter table "user"
    owner to miirso;

-- auto-generated definition
create table user_activity_log
(
    id            bigserial
        primary key,
    user_id       varchar(255) not null,
    user_role     varchar(50)  not null,
    activity_type varchar(100) not null,
    target_type   varchar(100),
    target_id     varchar(255),
    details       jsonb,
    activity_time timestamp default CURRENT_TIMESTAMP
);

alter table user_activity_log
    owner to miirso;


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

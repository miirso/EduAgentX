-- 试题系统数据库表设计
-- 作者: miirso
-- 日期: 2025-06-23

-- 1. 试卷表 (exam_papers)
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

-- 2. 试题表 (exam_questions)
CREATE TABLE exam_questions
(
    id              SERIAL PRIMARY KEY,                    -- 题目ID
    paper_id        INTEGER NOT NULL,                      -- 试卷ID
    question_type   VARCHAR(20) NOT NULL,                  -- 题目类型：single_choice、multiple_choice、short_answer
    question_text   TEXT NOT NULL,                         -- 题目内容
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

-- 3. 试题选项表 (exam_question_options)
CREATE TABLE exam_question_options
(
    id              SERIAL PRIMARY KEY,                    -- 选项ID
    question_id     INTEGER NOT NULL,                      -- 题目ID
    option_label    VARCHAR(5) NOT NULL,                   -- 选项标签（A、B、C、D等）
    option_text     TEXT NOT NULL,                         -- 选项内容
    option_order    INTEGER NOT NULL,                      -- 选项顺序
    is_correct      BOOLEAN DEFAULT FALSE,                 -- 是否为正确答案
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 创建时间
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 更新时间
    delete_time     TIMESTAMP,                             -- 删除时间
    tag             BOOLEAN DEFAULT TRUE,                  -- 逻辑删除标记
    
    CONSTRAINT fk_exam_question_options_question_id FOREIGN KEY (question_id) REFERENCES exam_questions(id)
);

-- 4. 试题答案表 (exam_question_answers)
CREATE TABLE exam_question_answers
(
    id              SERIAL PRIMARY KEY,                    -- 答案ID
    question_id     INTEGER NOT NULL,                      -- 题目ID
    correct_answer  TEXT NOT NULL,                         -- 正确答案（单选题存选项标签，多选题存逗号分隔的标签，简答题存答案文本）
    answer_explanation TEXT,                               -- 答案解析
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 创建时间
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 更新时间
    delete_time     TIMESTAMP,                             -- 删除时间
    tag             BOOLEAN DEFAULT TRUE,                  -- 逻辑删除标记
    
    CONSTRAINT fk_exam_question_answers_question_id FOREIGN KEY (question_id) REFERENCES exam_questions(id),
    CONSTRAINT uk_exam_question_answers_question_id UNIQUE (question_id)
);

-- 5. 学生答题记录表 (student_exam_records)
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

-- 6. 学生答题详情表 (student_answer_details)
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

-- 7. AI教学建议表 (ai_teaching_suggestions)
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

-- 表注释
COMMENT ON TABLE exam_papers IS '试卷表';
COMMENT ON TABLE exam_questions IS '试题表';
COMMENT ON TABLE exam_question_options IS '试题选项表';
COMMENT ON TABLE exam_question_answers IS '试题答案表';
COMMENT ON TABLE student_exam_records IS '学生答题记录表';
COMMENT ON TABLE student_answer_details IS '学生答题详情表';
COMMENT ON TABLE ai_teaching_suggestions IS 'AI教学建议表';

-- 列注释
COMMENT ON COLUMN exam_papers.paper_type IS '试卷类型：chapter(章节测试)、course(课程测试)、final(期末考试)';
COMMENT ON COLUMN exam_questions.question_type IS '题目类型：single_choice(单选题)、multiple_choice(多选题)、short_answer(简答题)';
COMMENT ON COLUMN exam_question_answers.correct_answer IS '正确答案：单选题存选项标签，多选题存逗号分隔的标签，简答题存答案文本';
COMMENT ON COLUMN ai_teaching_suggestions.suggestion_type IS '建议类型：course_general(课程通用)、paper_specific(试卷特定)、student_personal(学生个人)';

-- 设置表所有者
ALTER TABLE exam_papers OWNER TO miirso;
ALTER TABLE exam_questions OWNER TO miirso;
ALTER TABLE exam_question_options OWNER TO miirso;
ALTER TABLE exam_question_answers OWNER TO miirso;
ALTER TABLE student_exam_records OWNER TO miirso;
ALTER TABLE student_answer_details OWNER TO miirso;
ALTER TABLE ai_teaching_suggestions OWNER TO miirso;

# EduAgentX 数据库表结构详细说明

---

## admin（管理员表）
| 字段名      | 类型         | 约束/说明           |
|-------------|--------------|---------------------|
| id          | char(10)     | 主键，自动生成      |
| username    | varchar(8)   | 唯一，管理员用户名  |
| password    | varchar(200) | 管理员密码          |
| create_time | timestamp    | 创建时间            |
| update_time | timestamp    | 更新时间            |
| delete_time | timestamp    | 删除时间（软删）    |
| tag         | boolean      | 逻辑删除标记        |

---

## teacher（教师表）
| 字段名      | 类型         | 约束/说明           |
|-------------|--------------|---------------------|
| id          | char(10)     | 主键，自动生成（2开头）|
| username    | varchar(10)  | 唯一                |
| real_name   | varchar(50)  | 真实姓名            |
| gender      | smallint     | 0女1男，约束        |
| phone       | char(11)     | 电话                |
| teacher_no  | varchar(10)  | 唯一，工号          |
| school      | varchar(100) | 学校                |
| college     | varchar(100) | 学院                |
| password    | varchar(100) | 密码                |
| create_time | timestamp    | 创建时间            |
| update_time | timestamp    | 更新时间            |
| delete_time | timestamp    | 删除时间            |
| tag         | boolean      | 逻辑删除标记        |

---

## student（学生表）
| 字段名      | 类型         | 约束/说明           |
|-------------|--------------|---------------------|
| id          | bigint       | 主键，自动生成      |
| student_no  | char(8)      | 唯一，学号          |
| username    | varchar(32)  | 唯一                |
| real_name   | varchar(50)  | 真实姓名            |
| gender      | smallint     | 0女1男，约束        |
| phone       | char(11)     | 电话                |
| email       | varchar(64)  | 邮箱                |
| major_code  | varchar(2)   | 专业代码            |
| class_code  | varchar(32)  | 班级代码            |
| college     | varchar(100) | 学院                |
| school      | varchar(100) | 学校                |
| password    | varchar(128) | 密码                |
| create_time | timestamp    | 创建时间            |
| update_time | timestamp    | 更新时间            |
| delete_time | timestamp    | 删除时间            |
| tag         | boolean      | 逻辑删除标记        |

---

## classes（班级表）
| 字段名      | 类型         | 约束/说明           |
|-------------|--------------|---------------------|
| id          | serial       | 主键                |
| name        | varchar(255) | 唯一，班级名        |
| major_id    | varchar(8)   | 专业ID              |
| create_time | timestamp    | 创建时间            |
| update_time | timestamp    | 更新时间            |
| tag         | boolean      | 逻辑删除标记        |

---

## subjects（学科/专业表）
| 字段名      | 类型         | 约束/说明           |
|-------------|--------------|---------------------|
| id          | serial       | 主键                |
| name        | varchar(255) | 唯一，学科名        |
| description | text         | 描述                |
| create_time | timestamp    | 创建时间            |
| update_time | timestamp    | 更新时间            |
| tag         | boolean      | 逻辑删除标记        |

---

## courses（课程表）
| 字段名            | 类型         | 约束/说明           |
|-------------------|--------------|---------------------|
| id                | varchar(8)   | 主键，业务编码      |
| subject_id        | integer      | 外键，学科ID        |
| name              | varchar(255) | 课程名              |
| description       | text         | 课程描述            |
| cover_image       | varchar(255) | 封面                |
| start_date        | date         | 开始日期            |
| end_date          | date         | 结束日期            |
| assessment_method | varchar(16)  | 考核方式            |
| type              | varchar(1)   | 课程类型            |
| create_time       | timestamp    | 创建时间            |
| update_time       | timestamp    | 更新时间            |
| tag               | boolean      | 逻辑删除标记        |

---

## chapters（课程章节表）
| 字段名      | 类型         | 约束/说明           |
|-------------|--------------|---------------------|
| id          | serial       | 主键                |
| course_id   | varchar(8)   | 外键，课程ID        |
| title       | varchar(255) | 章节标题            |
| content     | text         | 章节内容            |
| order       | integer      | 排序                |
| create_time | timestamp    | 创建时间            |
| update_time | timestamp    | 更新时间            |
| tag         | boolean      | 逻辑删除标记        |

---

## course_classes（课程-班级关联表）
| 字段名      | 类型         | 约束/说明           |
|-------------|--------------|---------------------|
| id          | serial       | 主键                |
| course_id   | varchar(8)   | 外键，课程ID        |
| class_name  | varchar(255) | 外键，班级名        |

---

## course_teachers（课程-教师关联表）
| 字段名      | 类型         | 约束/说明           |
|-------------|--------------|---------------------|
| id          | serial       | 主键                |
| course_id   | varchar(8)   | 外键，课程ID        |
| teacher_id  | char(10)     | 外键，教师ID        |

---

## enrollments（选课表）
| 字段名          | 类型         | 约束/说明           |
|-----------------|--------------|---------------------|
| id              | serial       | 主键                |
| student_id      | bigint       | 外键，学生ID        |
| course_id       | varchar(8)   | 外键，课程ID        |
| enrollment_date | timestamp    | 选课时间            |
| create_time     | timestamp    | 创建时间            |
| update_time     | timestamp    | 更新时间            |
| delete_time     | timestamp    | 删除时间            |
| tag             | boolean      | 逻辑删除标记        |

---

## student_class_relation（学生-班级关系表）
| 字段名      | 类型         | 约束/说明           |
|-------------|--------------|---------------------|
| id          | serial       | 主键                |
| student_no  | char(16)     | 外键，学生学号      |
| class_code  | varchar(16)  | 班级代码            |
| create_time | timestamp    | 创建时间            |
| update_time | timestamp    | 更新时间            |
| delete_time | timestamp    | 删除时间            |
| tag         | boolean      | 逻辑删除标记        |
| unique(student_no, class_code) |      | 组合唯一约束 |

---

## course_introductions（课程简介表）
| 字段名            | 类型         | 约束/说明           |
|-------------------|--------------|---------------------|
| id                | serial       | 主键                |
| course_id         | varchar(8)   | 课程ID              |
| introduction_text | text         | 简介文本            |
| introduction_html | text         | 富文本              |
| keywords          | varchar(500) | 关键词              |
| learning_goals    | text         | 学习目标            |
| prerequisites     | text         | 先修要求            |
| target_audience   | varchar(255) | 目标受众            |
| difficulty_level  | varchar(20)  | 难度等级            |
| estimated_hours   | integer      | 预计学时            |
| create_time       | timestamp    | 创建时间            |
| update_time       | timestamp    | 更新时间            |
| delete_time       | timestamp    | 删除时间            |
| tag               | boolean      | 逻辑删除标记        |

---

## course_outlines（课程大纲表）
| 字段名            | 类型         | 约束/说明           |
|-------------------|--------------|---------------------|
| id                | serial       | 主键                |
| course_id         | varchar(8)   | 课程ID              |
| outline_file_path | varchar(500) | 文件路径            |
| file_name         | varchar(255) | 原始文件名          |
| version           | integer      | 版本号              |
| create_time       | timestamp    | 创建时间            |
| update_time       | timestamp    | 更新时间            |
| delete_time       | timestamp    | 删除时间            |
| tag               | boolean      | 逻辑删除标记        |
| unique(course_id, version) |      | 版本唯一约束        |

---

## exam_papers（试卷表）
| 字段名            | 类型         | 约束/说明           |
|-------------------|--------------|---------------------|
| id                | serial       | 主键                |
| course_id         | varchar(8)   | 课程ID              |
| chapter_id        | integer      | 章节ID              |
| paper_name        | varchar(255) | 试卷名称            |
| paper_type        | varchar(20)  | 试卷类型            |
| total_questions   | integer      | 题数                |
| total_score       | integer      | 总分                |
| time_limit        | integer      | 限时（分钟）        |
| difficulty        | varchar(20)  | 难度                |
| status            | varchar(20)  | 状态                |
| ai_generated      | boolean      | 是否AI生成          |
| generation_prompt | text         | AI生成提示词        |
| create_time       | timestamp    | 创建时间            |
| update_time       | timestamp    | 更新时间            |
| delete_time       | timestamp    | 删除时间            |
| tag               | boolean      | 逻辑删除标记        |

---

## exam_questions（试题表）
| 字段名            | 类型         | 约束/说明           |
|-------------------|--------------|---------------------|
| id                | serial       | 主键                |
| paper_id          | integer      | 外键，试卷ID        |
| question_type     | varchar(20)  | 题型                |
| question_content  | text         | 题目内容            |
| question_order    | integer      | 顺序                |
| score             | integer      | 分值                |
| difficulty        | varchar(20)  | 难度                |
| ai_generated      | boolean      | 是否AI生成          |
| create_time       | timestamp    | 创建时间            |
| update_time       | timestamp    | 更新时间            |
| delete_time       | timestamp    | 删除时间            |
| tag               | boolean      | 逻辑删除标记        |

---

## exam_question_options（试题选项表）
| 字段名            | 类型         | 约束/说明           |
|-------------------|--------------|---------------------|
| id                | serial       | 主键                |
| question_id       | integer      | 外键，题目ID        |
| option_label      | varchar(5)   | 选项标签            |
| option_content    | text         | 选项内容            |
| option_order      | integer      | 顺序                |
| is_correct        | boolean      | 是否正确答案        |
| create_time       | timestamp    | 创建时间            |
| update_time       | timestamp    | 更新时间            |
| delete_time       | timestamp    | 删除时间            |
| tag               | boolean      | 逻辑删除标记        |

---

## exam_question_answers（试题答案表）
| 字段名            | 类型         | 约束/说明           |
|-------------------|--------------|---------------------|
| id                | serial       | 主键                |
| question_id       | integer      | 外键，题目ID        |
| correct_answer    | text         | 正确答案            |
| answer_explanation| text         | 答案解析            |
| create_time       | timestamp    | 创建时间            |
| update_time       | timestamp    | 更新时间            |
| delete_time       | timestamp    | 删除时间            |
| tag               | boolean      | 逻辑删除标记        |

---

## student_exam_records（学生答题记录表）
| 字段名            | 类型         | 约束/说明           |
|-------------------|--------------|---------------------|
| id                | serial       | 主键                |
| student_id        | bigint       | 外键，学生ID        |
| paper_id          | integer      | 外键，试卷ID        |
| start_time        | timestamp    | 开始时间            |
| submit_time       | timestamp    | 提交时间            |
| total_score       | integer      | 总分                |
| correct_count     | integer      | 正确题数            |
| wrong_count       | integer      | 错误题数            |
| accuracy_rate     | decimal(5,2) | 正确率              |
| time_spent        | integer      | 用时（分钟）        |
| status            | varchar(20)  | 状态                |
| create_time       | timestamp    | 创建时间            |
| update_time       | timestamp    | 更新时间            |
| delete_time       | timestamp    | 删除时间            |
| tag               | boolean      | 逻辑删除标记        |

---

## student_answer_details（学生答题详情表）
| 字段名            | 类型         | 约束/说明           |
|-------------------|--------------|---------------------|
| id                | serial       | 主键                |
| exam_record_id    | integer      | 外键，答题记录ID    |
| question_id       | integer      | 外键，题目ID        |
| student_answer    | text         | 学生答案            |
| is_correct        | boolean      | 是否正确            |
| score_gained      | integer      | 得分                |
| answer_time       | timestamp    | 答题时间            |
| create_time       | timestamp    | 创建时间            |
| update_time       | timestamp    | 更新时间            |
| delete_time       | timestamp    | 删除时间            |
| tag               | boolean      | 逻辑删除标记        |

---

## ai_teaching_suggestions（AI教学建议表）
| 字段名            | 类型         | 约束/说明           |
|-------------------|--------------|---------------------|
| id                | serial       | 主键                |
| course_id         | varchar(8)   | 课程ID              |
| paper_id          | integer      | 试卷ID              |
| student_id        | bigint       | 学生ID              |
| suggestion_type   | varchar(30)  | 建议类型            |
| suggestion_title  | varchar(255) | 建议标题            |
| suggestion_content| text         | 建议内容            |
| based_on_data     | text         | 基于数据            |
| ai_model          | varchar(50)  | AI模型              |
| create_time       | timestamp    | 创建时间            |
| update_time       | timestamp    | 更新时间            |
| delete_time       | timestamp    | 删除时间            |
| tag               | boolean      | 逻辑删除标记        |

---

## discussions（课程讨论区表）
| 字段名      | 类型         | 约束/说明           |
|-------------|--------------|---------------------|
| id          | serial       | 主键                |
| course_id   | varchar(8)   | 课程ID              |
| user_id     | varchar(255) | 用户ID              |
| user_role   | varchar(50)  | 用户角色            |
| parent_id   | integer      | 父评论ID            |
| content     | text         | 内容                |
| create_time | timestamp    | 创建时间            |
| update_time | timestamp    | 更新时间            |
| tag         | boolean      | 逻辑删除标记        |

---

## course_qa（课程QA问题表）
| 字段名      | 类型         | 约束/说明           |
|-------------|--------------|---------------------|
| id          | bigserial    | 主键                |
| course_id   | varchar(8)   | 课程ID              |
| student_id  | char(10)     | 学生ID              |
| title       | varchar(200) | 问题标题            |
| content     | text         | 问题内容            |
| create_time | timestamp    | 创建时间            |
| update_time | timestamp    | 更新时间            |
| status      | varchar(20)  | 状态                |

---

## course_qa_answer（课程QA回答表）
| 字段名      | 类型         | 约束/说明           |
|-------------|--------------|---------------------|
| id          | bigserial    | 主键                |
| qa_id       | bigint       | 问题ID              |
| answerer_id | char(10)     | 回答者ID            |
| answerer_type| varchar(16) | 回答者类型          |
| content     | text         | 回答内容            |
| create_time | timestamp    | 创建时间            |
| update_time | timestamp    | 更新时间            |
| status      | varchar(20)  | 状态                |

---

## dashboard_analytics（数据看板分析表）
| 字段名          | 类型         | 约束/说明           |
|-----------------|--------------|---------------------|
| id              | serial       | 主键                |
| metric_name     | varchar(255) | 指标名              |
| metric_value    | varchar(255) | 指标值              |
| metric_unit     | varchar(50)  | 单位                |
| calculation_date| date         | 统计日期            |
| scope_type      | varchar(50)  | 作用域类型          |
| scope_id        | varchar(255) | 作用域ID            |

---

## course_evaluation（课程评价表）
| 字段名          | 类型         | 约束/说明           |
|-----------------|--------------|---------------------|
| id              | serial       | 主键                |
| course_id       | varchar(8)   | 课程ID              |
| student_id      | char(10)     | 学生ID              |
| content_score   | int          | 内容评分            |
| difficulty_score| int          | 难度评分            |
| teaching_score  | int          | 教学评分            |
| harvest_score   | int          | 收获评分            |
| comment         | text         | 评价内容            |
| create_time     | timestamp    | 评价时间            |

---

## course_discussion（课程讨论表）
| 字段名      | 类型         | 约束/说明           |
|-------------|--------------|---------------------|
| id          | serial       | 主键                |
| course_id   | varchar(8)   | 课程ID              |
| user_id     | char(10)     | 用户ID              |
| user_role   | varchar(16)  | 用户角色            |
| content     | text         | 内容                |
| create_time | timestamp    | 创建时间            |


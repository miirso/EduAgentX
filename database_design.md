# EduAgentX 需求与数据库设计

## 1. 需求分析

### 1.1. 核心功能

#### 1.1.1. 课程管理
- **课程信息:**
    - 教师可以创建和管理课程，包括课程名称、简介、封面图片、开始和结束日期、考核方式等。
    - 课程号ID格式固定为：专业2位+中间5位+类型（大写字母）。
- **多教师支持:**
    - 一个课程可以由多名教师共同授课。
- **班级关联:**
    - 课程可以与一个或多个班级关联。
- **章节管理:**
    - 教师可以为课程创建和管理章节，每个章节包含Markdown格式的课程内容。
- **课件管理:**
    - 教师和管理员可以上传、查看和管理课件。
    - 学生可以查看和下载课件。
- **题目生成:**
    - 系统可以根据课程内容，利用AI模型自动生成不同类型的题目（单选、多选、判断、简答）。
- **讨论区:**
    - 每个课程都拥有一个独立的讨论区，方便师生交流。

#### 1.1.2. 学生学习
- **课程列表:**
    - 学生可以查看所有已加入的课程列表。
- **学习进度:**
    - 系统记录并展示学生在每个课程中的学习进度。
- **在线学习:**
    - 学生可以浏览课程章节内容、查看课件。
- **在线练习:**
    - 学生可以在线完成课程相关的练习题目。
- **错题本:**
    - 系统自动为学生整理错题，方便复习。
- **课程反馈:**
    - 学生可以对课程进行评价和反馈。

#### 1.1.3. 用户管理
- **角色划分:**
    - 系统包含三种角色：学生、教师、管理员。
- **信息管理:**
    - 管理员可以批量导入和管理学生、教师信息。
    - 用户可以修改自己的个人信息。

### 1.2. 扩展功能
- **学情分析:**
    - 教师可以查看学生整体的学习数据分析，包括知识点掌握情况、高频错题等。
- **AI助教:**
    - 学生可以在线向AI助教提问，获得实时的学习指导。
- **大屏概览:**
    - 管理员可以查看系统的整体运营数据，如用户活跃度、教学效率指数等。

### 1.3. API 设计

#### 1.3.1. 认证 & 用户 API
- **POST /api/auth/login**
  - **描述:** 用户登录
  - **请求体:** `{ "username": "VARCHAR(255)", "password": "VARCHAR(255)", "role": "VARCHAR(50)" }`
  - **返回:** `{ "token": "string", "userInfo": { ...userObject } }`
- **GET /api/users/me**
  - **描述:** 获取当前登录用户信息
  - **请求头:** `Authorization: Bearer <token>`
  - **返回:** `{ ...userObject }` (根据role返回admin, teacher或student对象)
- **PUT /api/users/me**
  - **描述:** 更新当前登录用户信息
  - **请求头:** `Authorization: Bearer <token>`
  - **请求体:** `{ ...fieldsToUpdate }`
  - **返回:** `{ ...updatedUserObject }`

#### 1.3.2. 课程 API
- **POST /api/courses**
  - **描述:** (教师) 创建新课程
  - **请求体:** `{ "id": "VARCHAR(8)", "subject_id": "INTEGER", "name": "VARCHAR(255)", ... }`
  - **返回:** `{ ...courseObject }`
- **GET /api/courses**
  - **描述:** 获取课程列表 (可按学科、教师等过滤)
  - **查询参数:** `?subject_id=INTEGER&teacher_id=VARCHAR(255)&keyword=string`
  - **返回:** `[ { ...courseObject } ]`
- **GET /api/courses/{id}**
  - **描述:** 获取单个课程的详细信息
  - **路径参数:** `id: VARCHAR(8)`
  - **返回:** `{ ...courseObject, teachers: [ ...teacherObject ], chapters: [ ...chapterObject ] }`
- **PUT /api/courses/{id}**
  - **描述:** (教师) 更新课程信息
  - **路径参数:** `id: VARCHAR(8)`
  - **请求体:** `{ ...fieldsToUpdate }`
  - **返回:** `{ ...updatedCourseObject }`
- **POST /api/courses/{id}/enroll**
  - **描述:** (学生) 加入课程
  - **路径参数:** `id: VARCHAR(8)`
  - **返回:** `200 OK`

#### 1.3.3. 资源 (课件) API
- **POST /api/courses/{courseId}/resources**
  - **描述:** (教师/管理员) 上传资源
  - **路径参数:** `courseId: VARCHAR(8)`
  - **请求体:** `FormData` (包含文件和资源元数据 `title`, `description`, `resource_type`, `is_shared`)
  - **返回:** `{ ...resourceObject }`
- **GET /api/courses/{courseId}/resources**
  - **描述:** 获取课程的资源列表
  - **路径参数:** `courseId: VARCHAR(8)`
  - **返回:** `[ { ...resourceObject } ]`
- **GET /api/resources/{id}/download**
  - **描述:** 下载资源文件
  - **路径参数:** `id: INTEGER`
  - **返回:** `FileStream`
- **PUT /api/resources/{id}**
  - **描述:** (教师/管理员) 更新资源信息
  - **路径参数:** `id: INTEGER`
  - **请求体:** `{ "title": "VARCHAR(255)", "description": "TEXT", ... }`
  - **返回:** `{ ...updatedResourceObject }`
- **DELETE /api/resources/{id}**
  - **描述:** (教师/管理员) 删除资源
  - **路径参数:** `id: INTEGER`
  - **返回:** `204 No Content`

#### 1.3.4. 题目 & 练习 API
- **POST /api/ai/generate-questions**
  - **描述:** (教师) AI生成题目
  - **请求体:** `{ "course_id": "VARCHAR(8)", "chapter_id": "INTEGER", "text_content": "TEXT", "question_types": ["A", "B"], "count": 5 }`
  - **返回:** `[ { ...questionObject } ]`
- **POST /api/courses/{courseId}/questions**
  - **描述:** (教师) 手动添加题目
  - **路径参数:** `courseId: VARCHAR(8)`
  - **请求体:** `{ ...questionObject }`
  - **返回:** `{ ...createdQuestionObject }`
- **GET /api/courses/{courseId}/practice**
  - **描述:** (学生) 获取练习题
  - **路径参数:** `courseId: VARCHAR(8)`
  - **返回:** `[ { ...questionObject } ]`
- **POST /api/courses/{courseId}/practice/submit**
  - **描述:** (学生) 提交练习答案
  - **路径参数:** `courseId: VARCHAR(8)`
  - **请求体:** `[ { "question_id": "VARCHAR(16)", "answer": "TEXT" } ]`
  - **返回:** `{ "score": "INTEGER", "results": [ { ... } ] }`

#### 1.3.5. 管理员 API
- **POST /api/admin/users/batch**
  - **描述:** 批量导入用户 (学生/教师)
  - **请求体:** `{ "role": "VARCHAR(50)", "users": [ { ...userObject } ] }`
  - **返回:** `{ "success_count": "INTEGER", "fail_count": "INTEGER" }`
- **GET /api/admin/dashboard/analytics**
  - **描述:** 获取大屏统计数据
  - **查询参数:** `?start_date=DATE&end_date=DATE`
  - **返回:** `{ "teacher_usage": { ... }, "student_usage": { ... }, "teaching_efficiency": { ... }, "learning_effect": { ... } }`

## 2. 数据库表设计 (PostgreSQL)

### 2.1. 管理员表 (`admin`)
*此表根据 `AdminDO.java` 设计**
| 字段名 | 数据类型 | 主键/外键 | 备注 |
|---|---|---|---|
| `id` | `VARCHAR(255)` | `PRIMARY KEY` | 主键ID |
| `username` | `VARCHAR(255)` | `UNIQUE` | 用户名 |
| `password` | `VARCHAR(255)` | | 加密后的密码 |
| `create_time` | `TIMESTAMP` | | 创建时间 |
| `update_time` | `TIMESTAMP` | | 更新时间 |
| `tag` | `BOOLEAN` | | 逻辑删除位 |

### 2.2. 教师表 (`teacher`)
*此表根据 `TeacherDO.java` 设计**
| 字段名 | 数据类型 | 主键/外键 | 备注 |
|---|---|---|---|
| `id` | `VARCHAR(255)` | `PRIMARY KEY` | 主键ID |
| `username` | `VARCHAR(255)` | `UNIQUE` | 用户名 |
| `password` | `VARCHAR(255)` | | 加密后的密码 |
| `real_name` | `VARCHAR(255)` | | 真实姓名 |
| `gender` | `INTEGER` | | 性别 (0: 女, 1: 男) |
| `phone` | `VARCHAR(20)` | | 电话号码 |
| `teacher_no` | `VARCHAR(255)` | `UNIQUE` | 教师工号 |
| `school` | `VARCHAR(255)` | | 学校 |
| `college` | `VARCHAR(255)` | | 学院 |
| `create_time` | `TIMESTAMP` | | 创建时间 |
| `update_time` | `TIMESTAMP` | | 更新时间 |
| `tag` | `BOOLEAN` | | 逻辑删除位 |

### 2.3. 学生表 (`student`)
*此表根据 `StudentDO.java` 设计**
| 字段名 | 数据类型 | 主键/外键 | 备注 |
|---|---|---|---|
| `id` | `VARCHAR(255)` | `PRIMARY KEY` | 主键ID |
| `username` | `VARCHAR(255)` | `UNIQUE` | 用户名 |
| `password` | `VARCHAR(255)` | | 加密后的密码 |
| `student_no` | `VARCHAR(255)` | `UNIQUE` | 学号 |
| `real_name` | `VARCHAR(255)` | | 真实姓名 |
| `gender` | `INTEGER` | | 性别 (0: 女, 1: 男) |
| `phone` | `VARCHAR(20)` | | 电话号码 |
| `email` | `VARCHAR(255)` | | 邮箱 |
| `major_code` | `VARCHAR(255)` | | 专业代码 |
| `class_code` | `VARCHAR(255)` | | 班级代码 |
| `college` | `VARCHAR(255)` | | 学院 |
| `school` | `VARCHAR(255)` | | 学校 |
| `create_time` | `TIMESTAMP` | | 创建时间 |
| `update_time` | `TIMESTAMP` | | 更新时间 |
| `tag` | `BOOLEAN` | | 逻辑删除位 |

### 2.4. 课程表 (`courses`)
| 字段名 | 数据类型 | 主键/外键 | 备注 |
|---|---|---|---|
| `id` | `VARCHAR(8)` | `PRIMARY KEY` | 课程号ID (专业2位+中间5位+类型) |
| `subject_id` | `INTEGER` | `FOREIGN KEY (subjects.id)` | 所属学科ID |
| `name` | `VARCHAR(255)` | | 课程名称 |
| `description` | `TEXT` | | 课程简介 (补充) |
| `cover_image` | `VARCHAR(255)` | | 课程封面图片URL (补充) |
| `start_date` | `DATE` | | 开始日期 |
| `end_date` | `DATE` | | 结束日期 |
| `assessment_method`| `VARCHAR(16)` | | 考核方式 |
| `create_time` | `TIMESTAMP` | | 创建时间 |
| `update_time` | `TIMESTAMP` | | 更新时间 |
| `tag` | `BOOLEAN` | | 逻辑删除位 |

### 2.5. 学科表 (`subjects`)
| 字段名 | 数据类型 | 主键/外键 | 备注 |
|---|---|---|--- |
| `id` | `SERIAL` | `PRIMARY KEY` | 自增主键 |
| `name` | `VARCHAR(255)`| `UNIQUE` | 学科名称 |
| `description` | `TEXT` | | 学科描述 |
| `create_time` | `TIMESTAMP` | | 创建时间 |
| `update_time` | `TIMESTAMP` | | 更新时间 |
| `tag` | `BOOLEAN` | | 逻辑删除位 |

### 2.6. 课程-教师关联表 (`course_teachers`)
| 字段名 | 数据类型 | 主键/外键 | 备注 |
|---|---|---|---|
| `id` | `SERIAL` | `PRIMARY KEY` | 自增主键 |
| `course_id` | `VARCHAR(8)` | `FOREIGN KEY (courses.id)` | 课程ID |
| `teacher_id` | `VARCHAR(255)` | `FOREIGN KEY (teacher.id)` | 教师ID |

### 2.7. 班级表 (`classes`)
| 字段名 | 数据类型 | 主键/外键 | 备注 |
|---|---|---|---|
| `id` | `SERIAL` | `PRIMARY KEY` | 自增主键 |
| `name` | `VARCHAR(255)` | | 班级名称 |
| `major_id` | `VARCHAR(8)` | | 专业ID |
| `create_time` | `TIMESTAMP` | | 创建时间 |
| `update_time` | `TIMESTAMP` | | 更新时间 |
| `tag` | `BOOLEAN` | | 逻辑删除位 |

### 2.8. 课程-班级关联表 (`course_classes`)
| 字段名 | 数据类型 | 主键/外键 | 备注 |
|---|---|---|---|
| `id` | `SERIAL` | `PRIMARY KEY` | 自增主键 |
| `course_id` | `VARCHAR(8)` | `FOREIGN KEY (courses.id)` | 课程ID |
| `class_id` | `INTEGER` | `FOREIGN KEY (classes.id)` | 班级ID |

### 2.9. 学生-课程关联表 (选课表) (`enrollments`)
| 字段名 | 数据类型 | 主键/外键 | 备注 |
|---|---|---|---|
| `id` | `SERIAL` | `PRIMARY KEY` | 自增主键 |
| `student_id` | `VARCHAR(255)` | `FOREIGN KEY (student.id)` | 学生ID |
| `course_id` | `VARCHAR(8)` | `FOREIGN KEY (courses.id)` | 课程ID |
| `enrollment_date` | `TIMESTAMP` | | 选课时间 (补充) |

### 2.10. 章节表 (`chapters`)
| 字段名 | 数据类型 | 主键/外键 | 备注 |
|---|---|---|---|
| `id` | `SERIAL` | `PRIMARY KEY` | 自增主键 |
| `course_id` | `VARCHAR(8)` | `FOREIGN KEY (courses.id)` | 课程ID |
| `title` | `VARCHAR(255)` | | 章节标题 |
| `content` | `TEXT` | | 章节内容 (Markdown) |
| `order` | `INTEGER` | | 章节顺序 |
| `create_time` | `TIMESTAMP` | | 创建时间 |
| `update_time` | `TIMESTAMP` | | 更新时间 |
| `tag` | `BOOLEAN` | | 逻辑删除位 |

### 2.11. 资源表 (`resources`)
| 字段名 | 数据类型 | 主键/外键 | 备注 |
|---|---|---|---|
| `id` | `SERIAL` | `PRIMARY KEY` | 自增主键 |
| `course_id` | `VARCHAR(8)` | `FOREIGN KEY (courses.id)` | 关联课程ID |
| `user_id` | `VARCHAR(255)` | | 上传者ID (多态关联) |
| `user_role` | `VARCHAR(50)` | | 上传者角色 ('admin', 'teacher') |
| `title` | `VARCHAR(255)` | | 资源标题 |
| `description` | `TEXT` | | 资源描述 |
| `resource_type` | `VARCHAR(50)`| | 资源类型 (e.g., 'document', 'video', 'assignment') |
| `file_name` | `VARCHAR(255)`| | 文件名 |
| `file_path` | `VARCHAR(255)`| | 文件存储路径 |
| `file_size` | `INTEGER` | | 文件大小 (Bytes) |
| `version` | `INTEGER` | | 版本号，用于编辑追溯 |
| `is_shared` | `BOOLEAN` | | 是否共享给其他教师 |
| `upload_time` | `TIMESTAMP` | | 上传时间 |
| `tag` | `BOOLEAN` | | 逻辑删除位 |

### 2.12. 题目表 (`questions`)
| 字段名 | 数据类型 | 主键/外键 | 备注 |
|---|---|---|---|
| `id` | `VARCHAR(16)` | `PRIMARY KEY` | 题目ID |
| `course_id` | `VARCHAR(8)` | `FOREIGN KEY (courses.id)` | 课程ID |
| `chapter_id` | `INTEGER` | `FOREIGN KEY (chapters.id)` | 章节ID |
| `type` | `VARCHAR(1)` | | 题目类型 (A:单选, B:多选, C:判断, D:简答) |
| `content` | `TEXT` | | 题干 |
| `options` | `JSONB` | | 选项 (对于选择题) |
| `answer` | `TEXT` | | 答案 |
| `analysis` | `TEXT` | | 题目解析 (补充) |
| `create_time` | `TIMESTAMP` | | 创建时间 |
| `update_time` | `TIMESTAMP` | | 更新时间 |
| `tag` | `BOOLEAN` | | 逻辑删除位 |

### 2.13. 讨论区表 (`discussions`)
| 字段名 | 数据类型 | 主键/外键 | 备注 |
|---|---|---|---|
| `id` | `SERIAL` | `PRIMARY KEY` | 自增主键 |
| `course_id` | `VARCHAR(8)` | `FOREIGN KEY (courses.id)` | 课程ID |
| `user_id` | `VARCHAR(255)` | | 发帖用户ID (多态关联) |
| `user_role` | `VARCHAR(50)` | | 发帖用户角色 ('student', 'teacher') |
| `parent_id` | `INTEGER` | `FOREIGN KEY (discussions.id)` | 回复的帖子ID (NULL表示主贴) |
| `content` | `TEXT` | | 内容 |
| `create_time` | `TIMESTAMP` | | 创建时间 |
| `update_time` | `TIMESTAMP` | | 更新时间 |
| `tag` | `BOOLEAN` | | 逻辑删除位 |

### 2.14. 用户活动日志表 (`user_activity_log`)
| 字段名 | 数据类型 | 主键/外键 | 备注 |
|---|---|---|---|
| `id` | `BIGSERIAL` | `PRIMARY KEY` | 自增主键 |
| `user_id` | `VARCHAR(255)` | | 操作用户ID (多态关联) |
| `user_role` | `VARCHAR(50)` | | 操作用户角色 ('admin', 'teacher', 'student') |
| `activity_type` | `VARCHAR(100)` | | 活动类型 (e.g., 'LOGIN', 'VIEW_COURSE', 'UPLOAD_RESOURCE') |
| `target_type` | `VARCHAR(100)` | | 操作对象类型 (e.g., 'course', 'resource') |
| `target_id` | `VARCHAR(255)`| | 操作对象ID |
| `details` | `JSONB` | | 额外信息 (e.g., IP地址, 设备信息) |
| `activity_time` | `TIMESTAMP` | | 活动发生时间 |

### 2.15. 仪表盘分析表 (`dashboard_analytics`)
| 字段名 | 数据类型 | 主键/外键 | 备注 |
|---|---|---|--- |
| `id` | `SERIAL` | `PRIMARY KEY` | 自增主键 |
| `metric_name` | `VARCHAR(255)`| | 指标名称 |
| `metric_value` | `VARCHAR(255)`| | 指标值 |
| `metric_unit` | `VARCHAR(50)` | | 指标单位 (e.g., '%', 'hours', 'count') |
| `calculation_date`| `DATE` | | 计算日期 |
| `scope_type` | `VARCHAR(50)` | | 范围类型 (e.g., 'system', 'course', 'teacher') |
| `scope_id` | `VARCHAR(255)` | | 范围ID (e.g., course_id, teacher_id) |

## 3. 大屏统计与指标设计

### 3.1. 活跃度统计
- **教师/学生使用次数**: 通过`user_activity_log`表，按`user_id`和`activity_time`（当日/本周）聚合统计`COUNT(id)`。
- **活跃板块**: 通过`user_activity_log`表，按`activity_type`聚合统计，找出一段时间内教师/学生使用最频繁的功能模块。例如：
    - `UPLOAD_RESOURCE` -> 课件资源区
    - `CREATE_QUESTION` -> 智能出题区
    - `POST_DISCUSSION` -> 课程讨论区
    - `TAKE_PRACTICE` -> 在线练习区

### 3.2. 教学效率指数 (Composite Score)
这是一个综合性指标，旨在量化教师的教学效率，可由以下维度加权计算得出：
- **备课自动化率**: (AI生成的教学内容 / 总教学内容) * 100%。
- **资源复用率**: (被共享或复用的资源数量 / 教师上传的总资源数) * 100%。
- **考核自动化率**: (AI自动批改的题目数 / 总题目数) * 100%。
- **平均答疑时长**: 教师在讨论区回应学生问题的平均时间。

### 3.3. 学生学习效果指数 (Composite Score)
这是一个综合性指标，旨在量化学生的学习成效，可由以下维度加权计算得出：
- **知识点掌握度**: 学生在各章节练习中的平均分。
- **学习参与度**: (学生完成的练习数 + 讨论数 + 资源查看数) / (总练习数 + 总讨论数 + 总资源数)。
- **高频错题消除率**: (已订正的错题数 / 错题总数) * 100%。
- **学习主动性**: 学生主动提问、发起讨论的频率。

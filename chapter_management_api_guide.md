# 教师章节管理API测试指南

## 概述
已完成教师章节管理的四个核心功能：
1. 分配章节 (POST /api/eduagentx/teacher/assign-chapters)
2. 删除章节 (DELETE /api/eduagentx/teacher/delete-chapter)  
3. 修改章节 (PUT /api/eduagentx/teacher/update-chapter)
4. 查询章节 (POST /api/eduagentx/teacher/query-chapters)

## API接口详情

### 1. 分配章节
**URL:** POST `/api/eduagentx/teacher/assign-chapters`

**请求示例:**
```json
{
    "courseId": "COURSE001",
    "chapters": [
        {
            "title": "第一章 Java基础",
            "content": "本章介绍Java语言的基本语法和概念",
            "order": 1
        },
        {
            "title": "第二章 面向对象编程",
            "content": "本章介绍Java面向对象编程的核心思想",
            "order": 2
        },
        {
            "title": "第三章 集合框架",
            "content": "本章介绍Java集合框架的使用",
            "order": 3
        }
    ]
}
```

**响应示例:**
```json
{
    "code": "0",
    "message": "success",
    "data": {
        "courseId": "COURSE001",
        "assignedChapterCount": 3,
        "message": "章节分配成功"
    }
}
```

### 2. 删除章节
**URL:** DELETE `/api/eduagentx/teacher/delete-chapter`

**请求示例:**
```json
{
    "courseId": "COURSE001",
    "order": 2
}
```

**响应示例:**
```json
{
    "code": "0",
    "message": "success",
    "data": {
        "courseId": "COURSE001",
        "order": 2,
        "message": "章节删除成功"
    }
}
```

### 3. 修改章节
**URL:** PUT `/api/eduagentx/teacher/update-chapter`

**请求示例:**
```json
{
    "courseId": "COURSE001",
    "originalOrder": 1,
    "title": "第一章 Java语言基础（修订版）",
    "content": "本章详细介绍Java语言的基本语法、数据类型和控制结构",
    "newOrder": 1
}
```

**响应示例:**
```json
{
    "code": "0",
    "message": "success", 
    "data": {
        "courseId": "COURSE001",
        "order": 1,
        "message": "章节修改成功"
    }
}
```

### 4. 查询章节
**URL:** POST `/api/eduagentx/teacher/query-chapters`

**请求示例:**
```json
{
    "courseId": "COURSE001"
}
```

**响应示例:**
```json
{
    "code": "0",
    "message": "success",
    "data": {
        "courseId": "COURSE001",
        "totalCount": 2,
        "chapters": [
            {
                "id": 1,
                "title": "第一章 Java语言基础（修订版）",
                "content": "本章详细介绍Java语言的基本语法、数据类型和控制结构",
                "order": 1,
                "createTime": "2025-06-23T10:30:00",
                "updateTime": "2025-06-23T11:45:00"
            },
            {
                "id": 3,
                "title": "第三章 集合框架",
                "content": "本章介绍Java集合框架的使用",
                "order": 3,
                "createTime": "2025-06-23T10:30:00",
                "updateTime": "2025-06-23T10:30:00"
            }
        ]
    }
}
```

## 权限说明
- 所有章节相关操作都需要教师登录
- 系统会校验当前登录教师是否为该课程的授课教师
- 只有课程的授课教师才能对该课程的章节进行增删改查操作

## 功能特性
1. **权限校验**: 确保只有课程教师能够操作章节
2. **参数验证**: 对请求参数进行完整性和合法性检查
3. **幂等性**: 支持重复操作，保证数据一致性
4. **异常处理**: 完善的错误信息提示和日志记录
5. **时间转换**: 正确处理Date到LocalDateTime的类型转换
6. **排序显示**: 查询章节时按order字段升序排列

## 数据库设计
章节表(chapters)包含以下字段：
- id: 主键ID (自增)
- course_id: 课程ID
- title: 章节标题
- content: 章节内容  
- "order": 章节顺序号 (PostgreSQL关键字转义)
- create_time: 创建时间
- update_time: 更新时间
- tag: 逻辑删除标记

## 测试建议
1. 先使用教师账号登录获取token
2. 创建一门课程，并为该教师分配课程权限
3. 依次测试章节的增删改查功能
4. 验证权限控制是否生效（使用非课程教师账号测试）
5. 测试边界情况（如重复的order值、不存在的章节等）

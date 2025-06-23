# 教学代理API使用指南

## 概述

本文档提供了如何使用教学代理API的详细说明。该API提供三个专门的AI代理：
- **课程规划器 (Lesson Planner)**: 从教学大纲生成结构化课程计划
- **RAG代理 (RAG Agent)**: 基于检索增强生成的上下文问答
- **测验生成器 (Quiz Generator)**: 创建练习题和评估

### 光是调试的话

直接：


```bash

# 使用uv
uv venv

uv sync


langgraph dev --allow-blocking
```

### docker安装

```bash
docker run -p 9621:9621 -v $(pwd)/data:/app/data lightrag:amd64

docker run \
  -p 8123:8000 \
  --env-file .env \
  -e LANGCHAIN_ENDPOINT="https://api.smith.langchain.com" \
  -e LANGSMITH_API_KEY="在.env里复制过来" \
  -e REDIS_URI="redis的连接字符串" \
  -e DATABASE_URI="Postgres的连接字符串" \
  teaching_agent:amd64
```

### 使用之前

先在`/documents/upload/{user_id}`端点上传相关文档，以便RAG代理可以访问这些文档进行问答。

### 创建Assistant

POST /assistants

```json
{
    "graph_id": "rag_agent", // 从rag_agent，quiz_generator或lesson_planner中选择
    "config": {
        "configurable":{
            "user_id": "用户唯一标识符"
        }
    }
}
```


### 创建Thread

POST /threads

最简单的配置就是直接空就好了

```json
{}
```

### 创建Run

#### 流式输出

POST /threads/{thread_id}/runs/stream

```json
{
    "assistant_id": "assistant_id", // assistant_id是上面创建的Assistant的ID
    "input": {
        // 看下面
    }
}
```

#### 非流式输出

## 1. 课程规划器 (Lesson Planner)

### 创建课程计划

```json
  "input": {
    "raw_syllabus": "您的教学大纲内容...",
    "num_choice_questions": 5,
    "num_short_answer_questions": 3,
    "num_true_or_false_questions": 4
  }
```

**响应示例**:
```json
{
  "final_lesson_plan": "# Python编程基础课程计划\n\n## 第一章：Python入门\n...",
  "parsed_syllabus": [
    {
      "chapter_title": "Python入门",
      "knowledge_points": [
        "Python语法和基本数据类型",
        "变量和操作符"
      ]
    }
  ],
  "chapter_results": [
    {
      "chapter_title": "Python入门",
      "content": "详细的教学内容...",
      "time_allocation": {
        "activities": [
          {
            "name": "理论讲解",
            "minutes": 30
          },
          {
            "name": "实践练习",
            "minutes": 20
          }
        ],
        "rationale": "时间分配的理由..."
      },
      "practice_exercises": {
        "multiple_choice": [...],
        "short_answer": [...],
        "true_or_false": [...]
      }
    }
  ]
}
```

## 2. RAG代理 (RAG Agent)

### 提问


**请求示例**:

```json
"input": {
  "messages": [
    {
      "role": "user",
      "content": "面向对象编程的核心原则是什么？"
    }
  ]
},

```

**响应示例**:
```json
{
  "messages": [
    {
      "role": "user",
      "content": "面向对象编程的核心原则是什么？"
    },
    {
      "role": "assistant",
      "content": "面向对象编程(OOP)的核心原则包括：\n1. 封装(Encapsulation)：将数据和方法组合在一起...\n2. 继承(Inheritance)：允许类从其他类继承属性和方法...\n3. 多态(Polymorphism)：允许不同类的对象对同一消息做出不同的响应...\n4. 抽象(Abstraction)：隐藏复杂的实现细节..."
    }
  ]
}
```

## 3. 测验生成器 (Quiz Generator)

### 生成测验


**请求示例**:

```json
"input": {
  "content": "需要生成测验的内容...",
  "num_choice_questions": 3,
  "num_short_answer_questions": 2,
  "num_true_or_false_questions": 2
},
```

**响应示例**:
```json
{
  "practice_exercises": {
    "multiple_choice": [
      {
        "question": {
          "question": "在Python中，如何定义一个函数？",
          "knowledge_points": ["函数定义", "Python语法"]
        },
        "distractors": [ // 错误选项
          "使用function关键字",
          "使用func关键字",
          "使用method关键字"
        ],
        "answer": "使用def关键字" // 正确答案
      }
    ],
    "short_answer": [
      {
        "question": {
          "question": "解释Python函数中return语句的作用。",
          "knowledge_points": ["函数返回值", "return语句"]
        },
        "reference_answer": "return语句用于从函数中返回一个值给调用者，并终止函数的执行。"
      }
    ],
    "true_or_false": [
      {
        "question": {
          "question": "Python函数必须有返回值。",
          "knowledge_points": ["函数返回值"]
        },
        "answer": false
      }
    ]
  }
}
```

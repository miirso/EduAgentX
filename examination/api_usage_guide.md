# 试题系统接口使用说明

## 接口基本信息
- **基础路径**: `/api/eduagentx/exam`
- **返回格式**: 统一使用 `Result<T>` 格式
- **示例课程**: 计算机图形学 (课程ID: 2500010B)
- **示例教师**: 王珂老师

## 1. 保存AI生成的试题

### 接口信息
- **URL**: `POST /api/eduagentx/exam/save-questions`
- **功能**: 前端传递AI生成的试题数据，后端保存到数据库
- **用途**: AI生成试题后，前端调用此接口保存

### 请求示例（计算机图形学第3章）
```json
{
  "paperId": null,
  "paperInfo": {
    "courseId": "2500010B",
    "chapterId": 3,
    "paperName": "第3章 3D图形基础与投影 - 章节测试",
    "paperType": "chapter",
    "totalQuestions": 3,
    "totalScore": 100,
    "timeLimit": 60,
    "difficulty": "medium",
    "generationPrompt": "为计算机图形学第3章生成关于3D图形基础与投影的测试题目，包含单选题、多选题和简答题"
  },
  "questions": [
    {
      "questionType": "single_choice",
      "questionText": "在计算机图形学中，将三维坐标转换为二维屏幕坐标的过程称为？",
      "questionOrder": 1,
      "score": 30,
      "difficulty": "easy",
      "options": [
        {
          "optionLabel": "A",
          "optionText": "投影变换",
          "optionOrder": 1,
          "isCorrect": true
        },
        {
          "optionLabel": "B",
          "optionText": "视口变换",
          "optionOrder": 2,
          "isCorrect": false
        },
        {
          "optionLabel": "C",
          "optionText": "模型变换",
          "optionOrder": 3,
          "isCorrect": false
        },
        {
          "optionLabel": "D",
          "optionText": "观察变换",
          "optionOrder": 4,
          "isCorrect": false
        }
      ],
      "correctAnswer": "A",
      "answerExplanation": "投影变换是将三维场景投射到二维屏幕上的过程，包括透视投影和正交投影两种方式。"
    },
    {
      "questionType": "multiple_choice",
      "questionText": "透视投影具有以下哪些特点？（多选）",
      "questionOrder": 2,
      "score": 30,
      "difficulty": "medium",
      "options": [
        {
          "optionLabel": "A",
          "optionText": "远处的物体看起来更小",
          "optionOrder": 1,
          "isCorrect": true
        },
        {
          "optionLabel": "B",
          "optionText": "具有消失点",
          "optionOrder": 2,
          "isCorrect": true
        },
        {
          "optionLabel": "C",
          "optionText": "平行线保持平行",
          "optionOrder": 3,
          "isCorrect": false
        },
        {
          "optionLabel": "D",
          "optionText": "符合人眼观察习惯",
          "optionOrder": 4,
          "isCorrect": true
        }
      ],
      "correctAnswer": "A,B,D",
      "answerExplanation": "透视投影模拟人眼观察，远处物体变小，平行线会在消失点相交，不保持平行。"
    },
    {
      "questionType": "short_answer",
      "questionText": "请简述透视投影和正交投影的主要区别，并说明它们各自的应用场景。",
      "questionOrder": 3,
      "score": 40,
      "difficulty": "hard",
      "options": [],
      "correctAnswer": "透视投影：模拟人眼观察，远处物体变小，有消失点，适用于游戏、动画等需要真实感的场景。正交投影：物体大小不随距离变化，平行线保持平行，适用于CAD、工程图等需要精确测量的场景。",
      "answerExplanation": "两种投影方式各有特点和应用场景，学生需要理解其数学原理和实际应用。"
    }
  ]
}
```

### 响应示例
```json
{
  "code": "0",
  "message": "success",
  "data": {
    "paperId": 15,
    "paperName": "第3章 3D图形基础与投影 - 章节测试",
    "questionCount": 3,
    "operation": "新增试卷",
    "operationTime": "2025-06-23T14:30:00",
    "message": "试题保存成功"
  }
}
```

## 2. 获取试卷详情

### 接口信息
- **URL**: `GET /api/eduagentx/exam/paper/{paperId}`
- **功能**: 获取试卷详情，包含所有题目和选项
- **用途**: 学生开始答题前获取试卷内容

### 请求示例
```
GET /api/eduagentx/exam/paper/15
```

### 响应示例
```json
{
  "code": "0",
  "message": "success",
  "data": {
    "id": 15,
    "courseId": "2500010B",
    "chapterId": 3,
    "paperName": "第3章 3D图形基础与投影 - 章节测试",
    "paperType": "chapter",
    "totalQuestions": 3,
    "totalScore": 100,
    "timeLimit": 60,
    "difficulty": "medium",
    "status": "active",
    "aiGenerated": true,
    "createTime": "2025-06-23T14:30:00",
    "questions": [
      {
        "id": 45,
        "questionType": "single_choice",
        "questionText": "在计算机图形学中，将三维坐标转换为二维屏幕坐标的过程称为？",
        "questionOrder": 1,
        "score": 30,
        "difficulty": "easy",
        "options": [
          {
            "id": 180,
            "optionLabel": "A",
            "optionText": "投影变换",
            "optionOrder": 1
          },
          {
            "id": 181,
            "optionLabel": "B",
            "optionText": "视口变换",
            "optionOrder": 2
          },
          {
            "id": 182,
            "optionLabel": "C",
            "optionText": "模型变换",
            "optionOrder": 3
          },
          {
            "id": 183,
            "optionLabel": "D",
            "optionText": "观察变换",
            "optionOrder": 4
          }
        ]
      }
    ]
  }
}
```

## 3. 获取课程试卷列表

### 接口信息
- **URL**: `GET /api/eduagentx/exam/papers/course/{courseId}`
- **功能**: 获取指定课程的所有试卷列表
- **用途**: 教师查看课程试卷，学生选择试卷

### 请求示例
```
GET /api/eduagentx/exam/papers/course/2500010B
```

### 响应示例
```json
{
  "code": "0",
  "message": "success",
  "data": [
    {
      "id": 10,
      "courseId": "2500010B",
      "chapterId": 1,
      "paperName": "第1章 概论 - 章节测试",
      "paperType": "chapter",
      "totalQuestions": 5,
      "totalScore": 100,
      "timeLimit": 45,
      "difficulty": "easy",
      "status": "active",
      "createTime": "2025-06-20T10:00:00"
    },
    {
      "id": 15,
      "courseId": "2500010B",
      "chapterId": 3,
      "paperName": "第3章 3D图形基础与投影 - 章节测试",
      "paperType": "chapter",
      "totalQuestions": 3,
      "totalScore": 100,
      "timeLimit": 60,
      "difficulty": "medium",
      "status": "active",
      "createTime": "2025-06-23T14:30:00"
    }
  ]
}
```

## 4. 学生开始答题

### 接口信息
- **URL**: `POST /api/eduagentx/exam/start-exam`
- **功能**: 学生开始答题，创建答题记录
- **用途**: 学生点击开始考试时调用

### 请求示例
```json
{
  "studentId": 123456789,
  "paperId": 15
}
```

### 响应示例
```json
{
  "code": "0",
  "message": "success",
  "data": {
    "examRecordId": 89,
    "paperDetail": {
      "id": 15,
      "paperName": "第3章 3D图形基础与投影 - 章节测试",
      "totalQuestions": 3,
      "totalScore": 100,
      "timeLimit": 60,
      "questions": [
        // 试卷详情...
      ]
    },
    "startTime": "2025-06-23T15:00:00",
    "message": "答题开始，祝您考试顺利！"
  }
}
```

## 5. 提交单题答案

### 接口信息
- **URL**: `POST /api/eduagentx/exam/submit-answer`
- **功能**: 学生提交单道题目的答案
- **用途**: 学生每答完一题时调用，实时判分

### 请求示例
```json
{
  "examRecordId": 89,
  "questionId": 45,
  "studentAnswer": "A"
}
```

### 响应示例
```json
{
  "code": "0",
  "message": "success",
  "data": {
    "questionId": 45,
    "isCorrect": true,
    "scoreGained": 30,
    "correctAnswer": "A",
    "answerExplanation": "投影变换是将三维场景投射到二维屏幕上的过程，包括透视投影和正交投影两种方式。",
    "message": "回答正确！"
  }
}
```

## 6. 提交整份试卷

### 接口信息
- **URL**: `POST /api/eduagentx/exam/submit-exam`
- **功能**: 学生提交整份试卷，计算总分
- **用途**: 学生答题完成后提交试卷

### 请求示例
```json
{
  "examRecordId": 89
}
```

### 响应示例
```json
{
  "code": "0",
  "message": "success",
  "data": {
    "examRecordId": 89,
    "paperId": 15,
    "paperName": "第3章 3D图形基础与投影 - 章节测试",
    "totalScore": 90,
    "fullScore": 100,
    "correctCount": 2,
    "wrongCount": 1,
    "totalQuestions": 3,
    "accuracyRate": 66.67,
    "timeSpent": 45,
    "submitTime": "2025-06-23T15:45:00",
    "message": "试卷提交成功！得分：90分"
  }
}
```

## 7. 获取题目答案（用于判分）

### 接口信息
- **URL**: `GET /api/eduagentx/exam/get-answer/{questionId}`
- **功能**: 获取题目的正确答案，用于答题判分
- **用途**: 系统内部判分使用，也可供教师查看

### 请求示例
```
GET /api/eduagentx/exam/get-answer/45
```

### 响应示例
```json
{
  "code": "0",
  "message": "success",
  "data": {
    "questionId": 45,
    "questionType": "single_choice",
    "correctAnswer": "A",
    "answerExplanation": "投影变换是将三维场景投射到二维屏幕上的过程，包括透视投影和正交投影两种方式。",
    "score": 30
  }
}
```

## 使用流程

### 教师使用流程
1. **AI生成题目** → 前端调用AI → 获得题目数据
2. **保存题目** → 调用 `save-questions` 接口 → 保存到数据库
3. **查看试卷** → 调用 `papers/course/{courseId}` → 查看课程所有试卷
4. **查看试卷详情** → 调用 `paper/{paperId}` → 查看具体试卷内容

### 学生使用流程
1. **查看可用试卷** → 调用 `papers/course/{courseId}` → 选择试卷
2. **开始答题** → 调用 `start-exam` → 创建答题记录，获取试卷
3. **逐题答题** → 调用 `submit-answer` → 每题答完立即判分
4. **提交试卷** → 调用 `submit-exam` → 完成考试，查看总分

## 注意事项

1. **答案格式**：
   - 单选题：存储选项标签，如 "A"
   - 多选题：存储逗号分隔的标签，如 "A,B,D"
   - 简答题：存储完整答案文本

2. **时间控制**：
   - `timeLimit` 为考试时间限制（分钟）
   - `timeSpent` 为实际用时（分钟）

3. **分数计算**：
   - 每题有独立分值 `score`
   - 试卷总分 `totalScore`
   - 自动计算正确率 `accuracyRate`

4. **状态管理**：
   - 试卷状态：`active`、`inactive`、`deleted`
   - 答题状态：`in_progress`、`submitted`、`graded`

## 错误处理

所有接口都会返回统一的错误格式：
```json
{
  "code": "1001",
  "message": "参数验证失败：试卷ID不能为空",
  "data": null
}
```

常见错误码：
- `1001`: 参数验证失败
- `1002`: 业务逻辑错误
- `1003`: 数据不存在
- `1004`: 操作权限不足

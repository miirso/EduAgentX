# 试题系统接口实现计划

## 实现阶段

### 第一阶段：基础CRUD接口（优先实现）

#### 1. 试题管理接口
- ✅ `POST /api/eduagentx/exam/save-questions` - 保存AI生成的试题
- ✅ `GET /api/eduagentx/exam/question/{questionId}` - 获取单个题目详情
- ✅ `GET /api/eduagentx/exam/get-answer/{questionId}` - 获取题目答案（用于判分）
- ✅ `DELETE /api/eduagentx/exam/question/{questionId}` - 删除题目

#### 2. 试卷管理接口
- ✅ `POST /api/eduagentx/exam/create-paper` - 创建试卷
- ✅ `GET /api/eduagentx/exam/paper/{paperId}` - 获取试卷详情
- ✅ `GET /api/eduagentx/exam/papers/course/{courseId}` - 获取课程试卷列表
- ✅ `GET /api/eduagentx/exam/papers/chapter/{chapterId}` - 获取章节试卷列表

#### 3. 学生答题接口
- ✅ `POST /api/eduagentx/exam/start-exam` - 开始答题
- ✅ `POST /api/eduagentx/exam/submit-answer` - 提交单题答案
- ✅ `POST /api/eduagentx/exam/submit-exam` - 提交试卷
- ✅ `GET /api/eduagentx/exam/record/{recordId}` - 获取答题记录

### 第二阶段：统计分析接口

#### 4. 统计分析接口
- ⏳ `GET /api/eduagentx/exam/statistics/course/{courseId}` - 课程整体统计
- ⏳ `GET /api/eduagentx/exam/statistics/paper/{paperId}` - 试卷统计
- ⏳ `GET /api/eduagentx/exam/statistics/student/{studentId}/course/{courseId}` - 学生统计

### 第三阶段：AI建议接口

#### 5. AI教学建议接口
- ⏳ `POST /api/eduagentx/exam/save-suggestion` - 保存AI建议
- ⏳ `GET /api/eduagentx/exam/suggestions/course/{courseId}` - 获取课程建议
- ⏳ `GET /api/eduagentx/exam/suggestions/paper/{paperId}` - 获取试卷建议

## 示例JSON数据（以计算机图形学为例）

### 1. 保存试题接口示例
```json
{
  "paperId": null,
  "paperInfo": {
    "courseId": "2500010B",
    "chapterId": 3,
    "paperName": "第3章 3D图形基础与投影 - 章节测试",
    "paperType": "chapter",
    "totalQuestions": 5,
    "totalScore": 100,
    "timeLimit": 60,
    "difficulty": "medium"
  },
  "questions": [
    {
      "questionType": "single_choice",
      "questionText": "在计算机图形学中，将三维坐标转换为二维屏幕坐标的过程称为？",
      "questionOrder": 1,
      "score": 20,
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
    }
  ]
}
```

### 2. 学生答题示例
```json
{
  "studentId": 123456,
  "paperId": 1,
  "answers": [
    {
      "questionId": 1,
      "studentAnswer": "A"
    },
    {
      "questionId": 2,
      "studentAnswer": "B,C"
    },
    {
      "questionId": 3,
      "studentAnswer": "透视投影是模拟人眼观察物体的方式，距离越远的物体看起来越小，具有消失点的特性。"
    }
  ]
}
```

### 3. 统计结果示例
```json
{
  "courseId": "2500010B",
  "courseName": "计算机图形学",
  "overallAccuracy": 75.5,
  "totalQuestions": 60,
  "totalAnswered": 540,
  "chapterStatistics": [
    {
      "chapterId": 1,
      "chapterName": "概论",
      "accuracy": 85.2,
      "questionCount": 5,
      "answerCount": 45
    },
    {
      "chapterId": 3,
      "chapterName": "3D图形基础与投影",
      "accuracy": 68.8,
      "questionCount": 8,
      "answerCount": 72
    }
  ]
}
```

### 4. AI建议示例
```json
{
  "courseId": "2500010B",
  "suggestionType": "course_general",
  "suggestionTitle": "计算机图形学课程整体学习建议",
  "suggestionContent": "根据学生答题情况分析，发现学生在3D图形变换和投影方面掌握不够牢固。建议：1. 加强线性代数基础，特别是矩阵运算；2. 增加实践练习，使用OpenGL进行编程实现；3. 通过可视化工具帮助理解投影变换过程。",
  "basedOnData": "基于45名学生的答题数据，3D变换相关题目正确率仅为68.8%，低于课程平均水平75.5%"
}
```

## 技术要点

### 1. 数据一致性
- 使用事务保证试题、选项、答案的一致性
- 答题记录和答题详情的关联完整性

### 2. 性能优化
- 统计查询使用合理的索引
- 大数据量统计考虑分页或缓存

### 3. 业务逻辑
- 单选题答案存储选项标签（如"A"）
- 多选题答案存储逗号分隔的标签（如"A,C"）
- 简答题答案存储完整文本内容

### 4. 错误处理
- 参数验证
- 业务逻辑验证
- 异常情况处理

## 下一步计划
1. 先实现第一阶段的基础CRUD接口
2. 编写单元测试和集成测试
3. 使用Apifox进行接口测试
4. 实现统计分析功能
5. 最后实现AI建议功能

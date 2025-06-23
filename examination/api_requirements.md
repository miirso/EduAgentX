# 试题系统接口需求分析

## 业务背景
- 课程：计算机图形学 (课程ID: 2500010B)
- 教师：王珂老师
- 需求：AI生成试题，学生答题，统计分析，AI教学建议

## 核心功能模块

### 1. 试题管理模块
#### 1.1 AI生成试题接口
- **接口**: `POST /api/eduagentx/exam/generate-questions`
- **功能**: 前端传递课程ID、章节ID、题目数量等参数，AI生成试题并保存到数据库
- **参数**: 课程ID、章节ID、题目类型、数量、难度等级
- **返回**: 生成的试题列表

#### 1.2 保存试题接口
- **接口**: `POST /api/eduagentx/exam/save-questions`
- **功能**: 前端传递完整的试题数据（包含题目、选项、答案），保存到数据库
- **参数**: 试卷信息、题目列表、选项列表、答案列表

#### 1.3 获取试题答案接口
- **接口**: `GET /api/eduagentx/exam/get-answer/{questionId}`
- **功能**: 根据题目ID获取正确答案，用于答题判分
- **参数**: 题目ID
- **返回**: 正确答案信息

### 2. 试卷管理模块
#### 2.1 创建试卷接口
- **接口**: `POST /api/eduagentx/exam/create-paper`
- **功能**: 创建试卷，关联题目
- **参数**: 试卷基本信息、题目ID列表

#### 2.2 查询试卷接口
- **接口**: `GET /api/eduagentx/exam/paper/{paperId}`
- **功能**: 获取试卷详情，包含所有题目
- **参数**: 试卷ID
- **返回**: 试卷信息和题目列表

#### 2.3 查询课程试卷列表
- **接口**: `GET /api/eduagentx/exam/papers/course/{courseId}`
- **功能**: 获取指定课程的所有试卷
- **参数**: 课程ID
- **返回**: 试卷列表

### 3. 学生答题模块
#### 3.1 开始答题接口
- **接口**: `POST /api/eduagentx/exam/start-exam`
- **功能**: 学生开始答题，创建答题记录
- **参数**: 学生ID、试卷ID
- **返回**: 答题记录ID、试卷内容

#### 3.2 提交答案接口
- **接口**: `POST /api/eduagentx/exam/submit-answer`
- **功能**: 学生提交单道题答案
- **参数**: 答题记录ID、题目ID、学生答案
- **返回**: 是否正确、得分

#### 3.3 提交试卷接口
- **接口**: `POST /api/eduagentx/exam/submit-exam`
- **功能**: 学生提交整份试卷
- **参数**: 答题记录ID
- **返回**: 总得分、正确率等统计信息

### 4. 统计分析模块
#### 4.1 课程试题统计接口
- **接口**: `GET /api/eduagentx/exam/statistics/course/{courseId}`
- **功能**: 统计整门课程所有试题的正确率
- **参数**: 课程ID
- **返回**: 整体正确率、各章节正确率、热点错误题目

#### 4.2 试卷统计接口
- **接口**: `GET /api/eduagentx/exam/statistics/paper/{paperId}`
- **功能**: 统计单个试卷的正确率
- **参数**: 试卷ID
- **返回**: 试卷正确率、各题目正确率、学生分数分布

#### 4.3 学生答题统计
- **接口**: `GET /api/eduagentx/exam/statistics/student/{studentId}/course/{courseId}`
- **功能**: 统计学生在某门课程中的答题情况
- **参数**: 学生ID、课程ID
- **返回**: 学生在该课程的整体表现

### 5. AI教学建议模块
#### 5.1 保存AI建议接口
- **接口**: `POST /api/eduagentx/exam/save-suggestion`
- **功能**: 前端传递AI生成的教学建议，保存到数据库
- **参数**: 课程ID、试卷ID、建议类型、建议内容
- **返回**: 保存结果

#### 5.2 获取AI建议接口
- **接口**: `GET /api/eduagentx/exam/suggestions/{type}/{targetId}`
- **功能**: 获取AI教学建议
- **参数**: 建议类型、目标ID（课程ID或试卷ID）
- **返回**: AI建议列表

## 实际测试数据
### 课程信息
- 课程ID: 2500010B
- 课程名称: 计算机图形学
- 教师: 王珂老师

### 章节信息
1. 概论 (chapter_id: 1)
2. 2D图形绘制与变换 (chapter_id: 2)
3. 3D图形基础与投影 (chapter_id: 3)
4. 光照模型与着色 (chapter_id: 4)
5. 纹理映射技术 (chapter_id: 5)
6. OpenGL编程基础 (chapter_id: 6)
7. OpenGL高级编程 (chapter_id: 7)
8. 图像处理基础 (chapter_id: 8)
9. 计算机视觉入门 (chapter_id: 9)
10. 3D建模与渲染 (chapter_id: 10)
11. 图形学应用与课程设计 (chapter_id: 11)
12. 新增测试章节 (chapter_id: 12)

## 开发优先级
1. **高优先级**: 试题管理、试卷管理、学生答题
2. **中优先级**: 统计分析
3. **低优先级**: AI教学建议

## 接口命名规范
- 统一前缀: `/api/eduagentx/exam`
- RESTful风格
- 使用标准HTTP方法
- 返回统一的Result格式

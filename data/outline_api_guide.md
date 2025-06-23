# 课程大纲管理接口文档

## 接口概览

本模块提供了完整的课程大纲文件管理功能，包括上传、查询、下载等操作。支持文件版本管理，所有接口均无需教师身份验证。

## API接口列表

### 1. 上传/更新课程大纲

**接口地址：** `POST /api/eduagentx/outline/upload`

**请求方式：** `multipart/form-data`

**请求参数：**
- `courseId` (string, 必填): 课程ID
- `outlineFile` (file, 必填): 大纲文件（支持.md, .txt, .markdown格式）
- `version` (integer, 可选): 版本号，不传则自动递增

**Apifox测试配置：**
```
URL: http://localhost:8080/api/eduagentx/outline/upload
Method: POST
Body Type: form-data

参数设置：
courseId: CS3401
outlineFile: [选择文件] 计算机图形学课程大纲.md
version: 1
```

**响应示例：**
```json
{
    "code": "200",
    "message": "操作成功",
    "data": {
        "outlineId": 1,
        "courseId": "CS3401",
        "filePath": "uploads/outlines/CS3401/CS3401_v1_20250623_220530.md",
        "version": 1,
        "operationType": "新增",
        "message": "大纲新增成功",
        "createTime": "2025-06-23T22:05:30"
    },
    "success": true
}
```

### 2. 查询课程大纲内容

**接口地址：** `POST /api/eduagentx/outline/query`

**请求方式：** `application/json`

**请求参数：**
```json
{
    "courseId": "CS3401",
    "version": 1  // 可选，不传则返回最新版本
}
```

**Apifox测试配置：**
```
URL: http://localhost:8080/api/eduagentx/outline/query
Method: POST
Body Type: raw (JSON)

请求体：
{
    "courseId": "CS3401",
    "version": 1
}
```

**响应示例：**
```json
{
    "code": "200",
    "message": "操作成功",
    "data": {
        "outlineId": 1,
        "courseId": "CS3401",
        "outlineContent": "# 计算机图形学课程大纲\n\n## 课程基本信息\n...",
        "filePath": "uploads/outlines/CS3401/CS3401_v1_20250623_220530.md",
        "fileName": "计算机图形学课程大纲.md",
        "version": 1,
        "createTime": "2025-06-23T22:05:30",
        "updateTime": "2025-06-23T22:05:30"
    },
    "success": true
}
```

### 3. 查询课程大纲（GET方式）

**接口地址：** `GET /api/eduagentx/outline/query/{courseId}`

**请求方式：** `GET`

**请求参数：**
- `courseId` (path, 必填): 课程ID
- `version` (query, 可选): 版本号

**Apifox测试配置：**
```
URL: http://localhost:8080/api/eduagentx/outline/query/CS3401?version=1
Method: GET
```

### 4. 下载课程大纲文件

**接口地址：** `GET /api/eduagentx/outline/download/{courseId}`

**请求方式：** `GET`

**请求参数：**
- `courseId` (path, 必填): 课程ID
- `version` (query, 可选): 版本号

**Apifox测试配置：**
```
URL: http://localhost:8080/api/eduagentx/outline/download/CS3401?version=1
Method: GET
```

**响应：** 直接返回文件流，浏览器会提示下载

## 功能特性

### 1. 版本管理
- 支持同一课程的多个大纲版本
- 自动版本号递增
- 可指定特定版本查询

### 2. 文件存储
- 文件存储在 `uploads/outlines/` 目录下
- 按课程ID分目录存储
- 文件名格式：`courseId_v版本号_时间戳.扩展名`

### 3. 文件验证
- 支持格式：`.md`, `.txt`, `.markdown`
- 最大文件大小：5MB
- 自动文件类型检查

### 4. 错误处理
- 完善的参数验证
- 文件操作异常处理
- 数据库事务保证数据一致性

## 测试流程

### 1. 使用Apifox测试上传

1. 创建新请求
2. 设置URL和Method
3. 选择Body类型为`form-data`
4. 添加参数：
   - `courseId`: 输入课程ID
   - `outlineFile`: 选择本地的markdown文件
   - `version`: 输入版本号（可选）
5. 发送请求

### 2. 测试查询

1. 使用上传接口返回的课程ID
2. 创建POST请求查询大纲内容
3. 或使用GET请求直接访问

### 3. 测试下载

直接在浏览器中访问下载URL，或在Apifox中测试GET请求

## 目录结构

```
uploads/
└── outlines/
    └── CS3401/
        ├── CS3401_v1_20250623_220530.md
        ├── CS3401_v2_20250623_221015.md
        └── ...
```

## 注意事项

1. **文件路径**: 确保应用有写入`uploads`目录的权限
2. **文件清理**: 更新大纲时会自动删除旧版本文件
3. **并发安全**: 使用数据库事务保证操作的原子性
4. **错误回滚**: 如果数据库操作失败，会自动清理已上传的文件

## 错误码说明

- `400`: 参数错误（课程ID为空、文件为空等）
- `500`: 服务器内部错误（文件操作失败、数据库错误等）

这个接口设计完全支持文件存储方式，您可以直接上传markdown文件进行测试！

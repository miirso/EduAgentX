# 文件上传路径修复说明

## 问题原因

原始代码使用相对路径 `uploads/outlines/` 保存文件，但在Spring Boot应用中，相对路径会指向Tomcat的临时工作目录，导致以下问题：

1. **路径不存在**: Tomcat临时目录结构复杂，uploads目录不存在
2. **权限问题**: 临时目录可能没有写入权限
3. **文件丢失**: Tomcat重启后临时目录会被清空

## 修复方案

### 1. 使用绝对路径
```java
// 使用项目根目录
String projectRoot = System.getProperty("user.dir");
String fullDir = projectRoot + "/uploads/outlines/" + courseId;
Path fullPath = Paths.get(fullDir, fileName);
```

### 2. 确保目录创建
```java
// 创建目录，包括父级目录
Files.createDirectories(Paths.get(fullDir));
```

### 3. 存储绝对路径
```java
// 返回绝对路径用于数据库存储
return fullPath.toAbsolutePath().toString();
```

## 文件存储结构

修复后的文件存储结构：
```
项目根目录/
└── uploads/
    └── outlines/
        ├── CS3401/
        │   ├── CS3401_v1_20250623_223807.md
        │   └── CS3401_v2_20250623_224015.md
        └── 2500010B/
            └── 2500010B_v1_20250623_224530.md
```

## 验证修复

1. **重新启动应用**
2. **重新测试上传接口**:
   ```
   POST /api/eduagentx/outline/upload
   courseId: 2500010B
   outlineFile: [选择文件]
   ```

3. **检查日志输出**:
   ```
   创建目录: /项目路径/uploads/outlines/2500010B
   保存文件到: /项目路径/uploads/outlines/2500010B/2500010B_v1_20250623_224530.md
   文件保存成功: /项目路径/uploads/outlines/2500010B/2500010B_v1_20250623_224530.md
   ```

## 优势

1. **路径明确**: 使用项目根目录，路径清晰可控
2. **持久化**: 文件不会因为服务重启而丢失
3. **权限保证**: 项目目录通常有读写权限
4. **便于管理**: 文件存储在项目目录下，便于备份和管理

现在重新测试应该能够成功上传文件了！

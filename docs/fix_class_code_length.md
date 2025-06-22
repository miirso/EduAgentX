# 如何修复class_code字段长度问题

## 问题描述

在批量导入学生数据并建立学生-班级关联时，出现了错误：`ERROR: value too long for type character varying(10)`。

这是因为student表中的class_code字段长度限制为10个字符，但实际班级名称（如"计算机科学与技术22-1班"）超过了这个长度。

## 解决方案

1. 执行以下SQL语句，将class_code字段的长度从10个字符增加到50个字符：

```sql
ALTER TABLE student ALTER COLUMN class_code TYPE varchar(50);
```

2. 已经修改了StudentDO实体类中的注释，添加了字段说明：

```java
/**
 * Class Code
 * 存储格式：专业名称+年级+班号，例如"计算机科学与技术22-1班"
 */
private String classCode;
```

## 如何执行SQL脚本

### 方法1：使用psql命令行

```bash
psql -h 113.44.198.34 -U miirso -d rbjdb -f src/main/resources/sql/alter_student_class_code.sql
```

然后输入密码：miirso

### 方法2：使用数据库管理工具

使用pgAdmin、DBeaver等工具连接到数据库，然后执行SQL脚本中的语句。

## 验证

执行SQL后，可以通过以下命令验证字段长度是否已经修改：

```sql
SELECT column_name, data_type, character_maximum_length 
FROM information_schema.columns 
WHERE table_name = 'student' AND column_name = 'class_code';
```

结果应该显示character_maximum_length为50。

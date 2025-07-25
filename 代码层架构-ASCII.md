# ASCII格式的代码层架构图

```
+---------------------------+     +---------------------------------+
|        前端应用           |---->|         HTTP/JSON请求          |
+---------------------------+     +---------------+----------------+
                                               |
                                               v
+---------------------------------------------------------------------------+
|                                  表现层                                    |
|  +---------------+  +----------------+  +--------------+  +-------------+  |
|  |AdminController|  |StudentController|  |TeacherController| |CourseController|  |
|  +---------------+  +----------------+  +--------------+  +-------------+  |
|                                                                           |
|  +---------------+  +------------------+                                  |
|  |ExamController |  |CourseQaController|                                  |
|  +---------------+  +------------------+                                  |
+---------------------------------------------------------------------------+
           |                |                   |                  |
           v                v                   v                  v
+------------------------------------------+    +----------------------------------------+
|                  DTO层                   |    |                 业务层                 |
|  +------------+      +-------------+     |    | +-------------+     +---------------+ |
|  |   ReqDTO   |      |   RespDTO   |     |    | |StudentService|     |StudentServiceImpl| |
|  +------------+      +-------------+     |    | +-------------+     +---------------+ |
|                                          |    |                                        |
+------------------------------------------+    | +-------------+     +---------------+ |
                                                | |TeacherService|     |TeacherServiceImpl| |
                                                | +-------------+     +---------------+ |
                                                |                                        |
                                                | +-------------+     +---------------+ |
                                                | |CourseService|     |CourseServiceImpl| |
                                                | +-------------+     +---------------+ |
                                                |                                        |
                                                | +-------------+     +---------------+ |
                                                | |ExamService  |     |ExamServiceImpl | |
                                                | +-------------+     +---------------+ |
                                                +----------------------------------------+
                                                                 |
                                                                 v
+----------------------------------------+    +----------------------------------------+
|              数据访问层               |    |                 实体层                 |
| +-------------+    +-------------+    |    | +----------+        +----------+      |
| |StudentMapper|    |TeacherMapper|    |    | |StudentDO |        |TeacherDO |      |
| +-------------+    +-------------+    |    | +----------+        +----------+      |
|                                       |    |                                        |
| +-------------+    +-------------+    |    | +----------+        +----------+      |
| |CourseMapper |    |ExamMapper   |    |    | |CourseDO  |        |ExamDO    |      |
| +-------------+    +-------------+    |    | +----------+        +----------+      |
+----------------------------------------+    +----------------------------------------+
              |                                                    ^
              |                                                    |
              v                                                    |
+----------------------------------------+                         |
|              外部系统                 |                         |
| +-------------+    +-------------+    |                         |
| | PostgreSQL  |    |Redis缓存    |<---+-------------------------+
| +-------------+    +-------------+    |
+----------------------------------------+

+----------------------------------------+
|              通用组件                 |
| +-------+  +-------+  +---------+     |
| |配置   |  |工具类 |  |异常处理 |     |
| +-------+  +-------+  +---------+     |
|                                        |
| +----------+                           |
| |拦截器    |                           |
| +----------+                           |
+----------------------------------------+
```

# 使用说明
ASCII格式的流程图可以在任何文本编辑器中查看，无需特殊工具渲染。

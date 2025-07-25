# EduAgentX 后端系统展示PPT

## 技术栈一览

| 技术类别         | 选型/框架         | 项目中的任务与亮点 |
|------------------|-------------------|-------------------|
| 后端框架         | Spring Boot       | 提供RESTful服务，自动配置，简化开发，支持分层架构 |
| ORM              | MyBatis-Plus      | 简化数据库CRUD操作，自动生成SQL，支持Lambda表达式，提升开发效率 |
| 数据库           | PostgreSQL        | 存储所有业务数据，支持复杂查询与事务，兼容性强，字段注释完整 |
| 缓存             | Redis             | 提升高频数据访问性能，支持会话与热点数据缓存 |
| 日志             | Slf4j             | 统一日志接口，便于排查问题与运维监控 |
| 依赖注入/简化代码| Lombok            | 自动生成Getter/Setter/构造器，减少样板代码，提升开发效率 |
| API风格          | RESTful           | 前后端分离，接口规范清晰，易于对接与维护 |
| 文件上传/下载    | Spring MVC Multipart | 支持课程大纲、附件等文件上传与下载，接口安全可靠 |

---

## PPT展示内容与要点

### 1. 项目背景与目标
- 面向高校师生的智能教学与考试平台，支持课程管理、在线考试、智能分析与互动。

### 2. 技术架构
- 技术选型：见上表。
- 系统整体架构：前后端分离，RESTful API，后端统一服务，数据库与缓存分层。

### 3. 主要功能模块
- 用户与权限管理（管理员、教师、学生）
- 课程与章节管理（含大纲上传下载）
- 试卷与考试（自动判分、成绩统计）
- AI教学建议（个性化学习建议）
- 课程互动：
  - 课程QA（支持一问多答，问题与多条回答分表设计）
  - 课程讨论区（聊天室）
  - 课程评价（多维度问卷）
- 数据统计与分析（成绩分布、正确率、报表等）

### 4. 设计规范与亮点
- **分层架构**：Controller-Service-Mapper-Entity分层清晰，职责单一。
- **数据库规范**：所有主外键类型统一，字段命名清晰，注释完整，支持PostgreSQL。
- **一问多答QA设计**：问题表与回答表分离，支持多用户多条回答，接口RESTful。
- **DTO与DO分离**：接口返回DTO，数据库操作用DO，保证数据安全与解耦。
- **接口规范**：所有接口均为RESTful风格，路径、方法、参数、返回体规范。
- **AI建议与统计**：AI建议表与统计表结构灵活，支持多维度扩展。
- **安全性**：登录接口返回系统id及用户基本信息，敏感信息不外泄。
- **代码精华**：
  - MyBatis-Plus简化CRUD，ServiceImpl自动注入Mapper
  - Lombok自动生成Getter/Setter/构造器，减少样板代码
  - 统一异常处理与日志记录
  - 课程QA、讨论区、评价等功能的表结构与代码实现均有详细注释

### 5. 关键接口/流程演示
- 学生答题与成绩统计接口
- AI教学建议获取接口
- 课程QA提问与多回答接口
- 课程评价提交与统计接口

### 6. 总结与展望
- 已实现智能教学、考试、互动、评价等全流程
- 后续可扩展更多AI分析、移动端支持等功能

---

# 工作

作为本项目的后端开发者，承担了系统架构设计、核心功能开发、数据库建模、接口规范制定等全流程工作，具体亮点与贡献如下：

- **系统架构设计**
  - 主导分层架构（Controller-Service-Mapper-Entity），实现高内聚低耦合，便于维护和扩展。
  - 统一技术选型，确保系统稳定性与高性能。

- **数据库设计与优化**
  - 设计并实现了规范化的数据库表结构，所有主外键类型统一，字段命名清晰，注释详尽。
  - 针对课程QA、讨论区、评价等复杂业务，采用一问多答、分表设计，兼顾扩展性与查询效率。
  - 提供PostgreSQL兼容建议，保证跨平台部署能力。

- **核心功能开发**
  - 独立完成用户、课程、章节、考试、AI建议、互动等全业务模块的接口开发。
  - 实现AI教学建议、成绩统计、错题分析等智能化功能，提升系统智能水平。
  - 课程QA、讨论区、评价等模块支持多角色、多维度互动，极大丰富了教学场景。

- **接口与安全规范**
  - 所有接口均为RESTful风格，路径、参数、返回体规范，便于前后端协作。
  - 登录接口返回系统id及用户完整信息，敏感数据安全处理，保障用户隐私。
  - DTO与DO分离，接口返回数据安全、解耦，便于维护。

- **代码质量与文档**
  - 代码注释详尽，所有DO/DTO/Service/Controller均有中文注释，便于团队协作。
  - 关键表结构、接口、设计规范均补充到md文档，方便查阅与交接。
  - 统一异常处理与日志记录，提升系统健壮性。

- **性能与扩展性**
  - 引入Redis缓存，优化高频数据访问。
  - 采用MyBatis-Plus简化CRUD，提升开发效率。
  - 预留AI与大数据分析接口，支持后续智能化扩展。

> 通过上述工作，极大提升了系统的规范性、可维护性、智能化和用户体验，为后续功能扩展和团队协作打下坚实基础。

# EduAgentX 后端代码规范与最佳实践

## 1. 错误码设计规范
- **分层管理**：所有错误码均实现`IErrorCode`接口，分为基础（`BaseErrorCode`）、管理员（`AdminErrorCode`）、教师（`TeacherErrorCode`）、学生（`StudentErrorCode`）等多级分类，便于维护和定位。
- **结构清晰**：错误码采用分段编码（如A000001为客户端错误，B000001为服务端错误），每个错误码配有明确的中文描述。
- **实际例子**：
  ```java
  // 错误码定义
  public enum BaseErrorCode implements IErrorCode {
      CLIENT_ERROR("A000001", "用户端错误"),
      SERVICE_ERROR("B000001", "系统执行出错"),
      // ...
  }
  // 使用
  throw new ClientException(BaseErrorCode.CLIENT_ERROR);
  ```

## 2. 统一异常处理
- **全局异常拦截**：所有异常通过`GlobalExceptionHandler`统一处理，保证接口返回结构一致，便于前端处理。
- **自定义异常体系**：继承`AbstractException`，区分客户端异常、服务端异常、远程调用异常。
- **实际例子**：
  ```java
  @ExceptionHandler(value = {AbstractException.class})
  public Result abstractException(HttpServletRequest request, AbstractException ex) {
      return Results.failure(ex);
  }
  ```

## 3. 鉴权与用户上下文
- **Token机制**：登录成功后生成token，存入Redis，接口请求需携带token，`LoginInterceptor`拦截校验。
- **ThreadLocal用户上下文**：通过`UserHolder`存储当前请求用户信息，避免参数层层传递。
- **实际例子**：
  ```java
  // 登录
  String token = UUID.randomUUID().toString();
  stringRedisTemplate.opsForValue().set(LOGIN_USER_KEY + token, username, 60, TimeUnit.MINUTES);
  // 拦截器
  String token = request.getHeader("authorization");
  String username = stringRedisTemplate.opsForValue().get(LOGIN_USER_KEY + token);
  UserHolder.saveUser(userInfoDTO);
  ```

## 4. 统一返回结构
- **Result/Results模式**：所有接口返回`Result<T>`对象，包含code、message、data等字段，便于前后端解耦。
- **实际例子**：
  ```java
  public Result<TeacherLoginRespDTO> login(...) {
      return Results.success(teacherLoginRespDTO);
  }
  ```

## 5. 常量与配置规范
- **集中管理**：如Redis key、token过期时间等常量统一放在`RedisCacheConstant`等类中，便于维护和修改。
- **实际例子**：
  ```java
  public static final String LOGIN_USER_KEY = "login:token:";
  public static final Long LOGIN_USER_TTL = 60L;
  ```

---

如需更详细的代码片段或针对某一模块的规范说明，可进一步补充！

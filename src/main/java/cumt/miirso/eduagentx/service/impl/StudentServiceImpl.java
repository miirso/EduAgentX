package cumt.miirso.eduagentx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cumt.miirso.eduagentx.common.constant.RedisCacheConstant;
import cumt.miirso.eduagentx.convention.errorcode.BaseErrorCode;
import cumt.miirso.eduagentx.convention.errorcode.StudentErrorCode;
import cumt.miirso.eduagentx.convention.exception.ClientException;
import cumt.miirso.eduagentx.dto.UserInfoDTO;
import cumt.miirso.eduagentx.dto.req.PasswordUpdateReqDTO;
import cumt.miirso.eduagentx.dto.req.StudentLoginReqDTO;
import cumt.miirso.eduagentx.dto.req.StudentRegisterReqDTO;
import cumt.miirso.eduagentx.dto.req.StudentUpdateReqDTO;
import cumt.miirso.eduagentx.dto.resp.StudentLoginRespDTO;
import cumt.miirso.eduagentx.dto.resp.StudentRegisterRespDTO;
import cumt.miirso.eduagentx.entity.ClassDO;
import cumt.miirso.eduagentx.entity.StudentClassRelationDO;
import cumt.miirso.eduagentx.entity.StudentDO;
import cumt.miirso.eduagentx.mapper.ClassMapper;
import cumt.miirso.eduagentx.mapper.StudentMapper;
import cumt.miirso.eduagentx.service.ClassService;
import cumt.miirso.eduagentx.service.StudentClassRelationService;
import cumt.miirso.eduagentx.service.StudentService;
import cumt.miirso.eduagentx.utils.PasswordEncoder;
import cumt.miirso.eduagentx.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.marshalling.TraceInformation;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Package cumt.miirso.eduagentx.service.impl
 * @Author miirso
 * @Date 2025/5/29 22:18
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl extends ServiceImpl<StudentMapper, StudentDO> implements StudentService {

    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final StudentClassRelationService studentClassRelationService;
    private final ClassMapper classMapper;    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentRegisterRespDTO register(StudentRegisterReqDTO studentRegisterReqDTO) {
        log.info("=== 开始执行学生注册 ===");
        log.info("注册参数: 用户名={}, 学号={}, 姓名={}, 班级代码={}", 
                studentRegisterReqDTO.getUsername(), 
                studentRegisterReqDTO.getStudentNo(),
                studentRegisterReqDTO.getRealName(),
                studentRegisterReqDTO.getClassCode());

        // 1. 检查用户名是否已存在
        log.info("步骤1: 检查用户名是否已存在");
        boolean usernameExists = lambdaQuery()
                .eq(StudentDO::getUsername, studentRegisterReqDTO.getUsername())
                .exists();
        if (usernameExists) {
            log.warn("用户名已存在: {}", studentRegisterReqDTO.getUsername());
            throw new ClientException(BaseErrorCode.USER_NAME_EXIST_ERROR);
        }

        // 2. 检查学号是否已存在
        log.info("步骤2: 检查学号是否已存在");
        boolean studentNoExists = lambdaQuery()
                .eq(StudentDO::getStudentNo, studentRegisterReqDTO.getStudentNo())
                .exists();
        if (studentNoExists) {
            log.warn("学号已存在: {}", studentRegisterReqDTO.getStudentNo());
            throw new ClientException(StudentErrorCode.STUDENT_ALREADY_EXIST);
        }

        // 3. 创建学生对象
        log.info("步骤3: 创建学生对象");
        StudentDO studentDO = getStudentDO(studentRegisterReqDTO);
        
        // 4. 保存学生信息到数据库
        log.info("步骤4: 保存学生信息到数据库");
        save(studentDO);
        log.info("学生信息保存成功, ID: {}", studentDO.getId());
          // 5. 如果有班级代码，检查并创建班级，然后添加学生班级关联记录
        if (studentDO.getClassCode() != null && !studentDO.getClassCode().isEmpty()) {
            log.info("步骤5: 处理班级关联 - 班级代码: {}", studentDO.getClassCode());
            
            try {
                // 5.1 检查classes表中是否已存在该班级
                LambdaQueryWrapper<ClassDO> classQueryWrapper = new LambdaQueryWrapper<>();
                classQueryWrapper.eq(ClassDO::getName, studentDO.getClassCode());
                ClassDO existingClass = classMapper.selectOne(classQueryWrapper);
                
                Long classId = null;
                if (existingClass == null) {
                    // 5.2 班级不存在，创建新班级
                    log.info("班级不存在，创建新班级: {}", studentDO.getClassCode());
                    ClassDO newClass = new ClassDO();
                    newClass.setName(studentDO.getClassCode());
                    
                    // 尝试从学生的专业代码推断专业ID
                    if (studentDO.getMajorCode() != null && !studentDO.getMajorCode().isEmpty()) {
                        newClass.setMajorId(studentDO.getMajorCode());
                        log.info("设置班级专业ID: {}", studentDO.getMajorCode());
                    }
                    
                    classMapper.insert(newClass);
                    classId = newClass.getId();
                    log.info("新班级创建成功: ID={}, 名称={}", classId, newClass.getName());
                } else {
                    // 5.3 班级已存在，获取班级ID
                    classId = existingClass.getId();
                    log.info("班级已存在: ID={}, 名称={}", classId, existingClass.getName());
                }
                
                // 5.4 添加学生班级关联记录
                boolean relationResult = studentClassRelationService.addRelation(
                    studentDO.getId(),
                    classId,
                    studentDO.getStudentNo(),
                    studentDO.getClassCode()
                );
                
                if (relationResult) {
                    log.info("学生班级关联添加成功: 学号={}, 班级代码={}, 班级ID={}", 
                            studentDO.getStudentNo(), studentDO.getClassCode(), classId);
                } else {
                    log.warn("学生班级关联添加失败: 学号={}, 班级代码={}, 班级ID={}", 
                            studentDO.getStudentNo(), studentDO.getClassCode(), classId);
                }
                
            } catch (Exception e) {
                log.error("处理班级关联时发生异常: 学号={}, 班级代码={}, 错误信息={}", 
                         studentDO.getStudentNo(), studentDO.getClassCode(), e.getMessage(), e);
                // 不抛出异常，避免影响学生注册主流程
            }
        } else {
            log.info("步骤5: 跳过班级关联 - 未提供班级代码");
        }
        
        // 6. 构建并返回响应DTO
        log.info("步骤6: 构建响应数据");
        StudentRegisterRespDTO respDTO = new StudentRegisterRespDTO();
        respDTO.setId(studentDO.getId().toString());
        respDTO.setUsername(studentDO.getUsername());
        respDTO.setRealName(studentDO.getRealName());
        respDTO.setStudentNo(studentDO.getStudentNo());

        log.info("=== 学生注册流程完成 ===");
        log.info("注册成功的学生信息: {}", respDTO);

        return respDTO;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentRegisterRespDTO registerWithClass(StudentRegisterReqDTO studentRegisterReqDTO, ClassDO classInfo) {
        // 1. 检查用户名是否已存在
        boolean usernameExists = lambdaQuery()
                .eq(StudentDO::getUsername, studentRegisterReqDTO.getUsername())
                .exists();
        if (usernameExists) {
            throw new ClientException(BaseErrorCode.USER_NAME_EXIST_ERROR);
        }

        // 2. 检查学号是否已存在
        boolean studentNoExists = lambdaQuery()
                .eq(StudentDO::getStudentNo, studentRegisterReqDTO.getStudentNo())
                .exists();
        if (studentNoExists) {
            throw new ClientException(StudentErrorCode.STUDENT_ALREADY_EXIST);
        }

        // 3. 创建学生对象
        StudentDO studentDO = getStudentDO(studentRegisterReqDTO);

        // 4. 保存到数据库
        save(studentDO);
        
        // 5. 处理班级关联
        ClassDO classDO = null;
        if (classInfo != null) {
            // 先检查班级是否存在
            LambdaQueryWrapper<ClassDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ClassDO::getName, classInfo.getName());
            classDO = classMapper.selectOne(queryWrapper);
            
            // 如果班级不存在，创建新班级
            if (classDO == null) {
                classDO = new ClassDO();
                classDO.setName(classInfo.getName());
                classDO.setMajorId(classInfo.getMajorId());
                classMapper.insert(classDO);
                log.info("创建新班级: {}", classDO.getName());
            }
            
            // 添加学生班级关联
            studentClassRelationService.addRelation(
                studentDO.getId(),
                classDO.getId(),
                studentDO.getStudentNo(),
                classDO.getName()
            );
            log.info("学生 [{}] 已关联到班级 [{}]", studentDO.getStudentNo(), classDO.getName());
        }
        
        // 6. 构建并返回响应DTO
        StudentRegisterRespDTO respDTO = new StudentRegisterRespDTO();
        respDTO.setId(studentDO.getId().toString());
        respDTO.setUsername(studentDO.getUsername());
        respDTO.setRealName(studentDO.getRealName());
        respDTO.setStudentNo(studentDO.getStudentNo());

        log.info("用户已成功创建并关联班级: {}", respDTO);

        return respDTO;
    }

    private static StudentDO getStudentDO(StudentRegisterReqDTO studentRegisterReqDTO) {
        StudentDO studentDO = new StudentDO();
        studentDO.setUsername(studentRegisterReqDTO.getUsername());
        studentDO.setStudentNo(studentRegisterReqDTO.getStudentNo());
        studentDO.setRealName(studentRegisterReqDTO.getRealName());
        studentDO.setGender(studentRegisterReqDTO.getGender());
        studentDO.setPhone(studentRegisterReqDTO.getPhone());
        studentDO.setEmail(studentRegisterReqDTO.getEmail());
        studentDO.setMajorCode(studentRegisterReqDTO.getMajorCode());
        studentDO.setClassCode(studentRegisterReqDTO.getClassCode());
        studentDO.setCollege(studentRegisterReqDTO.getCollege());
        studentDO.setSchool(studentRegisterReqDTO.getSchool());
        // 加密密码后存储
        if (studentRegisterReqDTO.getPassword() != null) {
            studentDO.setPassword(PasswordEncoder.encode(studentRegisterReqDTO.getPassword()));
        }
        return studentDO;
    }

    @Override
    public StudentLoginRespDTO login(StudentLoginReqDTO studentLoginReqDTO) {
        String username = studentLoginReqDTO.getUsername();
        if (username == null) {
            throw new ClientException(StudentErrorCode.STUDENT_NAME_ERROR);
        }
        // 查询学生信息
        StudentDO studentDO = lambdaQuery()
                .eq(StudentDO::getUsername, username)
                .eq(StudentDO::getTag, true)
                .one();

        if (studentDO == null) {
            throw new ClientException(StudentErrorCode.STUDENT_NULL);
        }

        // 使用PasswordEncoder验证密码
        log.info(studentDO.getPassword());
        log.info(studentLoginReqDTO.getPassword());
        log.info(PasswordEncoder.matches(studentLoginReqDTO.getPassword(), studentDO.getPassword()).toString());

        if (!PasswordEncoder.matches(studentLoginReqDTO.getPassword(), studentDO.getPassword())) {
            throw new ClientException(StudentErrorCode.STUDENT_LOGIN_ERROR);
        }        // 生成token
        String token = UUID.randomUUID().toString();
        String tokenKey = RedisCacheConstant.LOGIN_USER_KEY + token;

        // 将token存入Redis，设置60分钟过期时间
        log.error("!!!");
        log.info(tokenKey);
        log.info(username);
        // 改用StringRedisTemplate保持一致性，避免编码问题
        stringRedisTemplate.opsForValue().set(tokenKey, username, RedisCacheConstant.LOGIN_USER_TTL, TimeUnit.MINUTES);
        // 存储值为username

        // =======test=======
        log.info("存储token到Redis: {} -> {}", tokenKey, username);

        // tuser0� ??????



        UserInfoDTO userInfoDTO = new UserInfoDTO(studentDO.getId(),
                1,
                studentDO.getUsername(),
                token);
        UserHolder.saveUser(userInfoDTO);
        log.info("存储了用户:" + userInfoDTO);

        // 构建并返回登录响应
        StudentLoginRespDTO respDTO = new StudentLoginRespDTO();
        respDTO.setToken(token);

        log.info("学生用户登录成功: {}", username);

        return respDTO;
    }

    @Override
    public Boolean check(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        String tokenKey = RedisCacheConstant.LOGIN_USER_KEY + token;
        System.out.println(tokenKey);
        String username = stringRedisTemplate.opsForValue().get(tokenKey);
        return !(username == null);
    }

    /**
     * 学生退出登录
     * @param request
     * @return
     */
    @Override
    public Boolean logout(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        String tokenKey = RedisCacheConstant.LOGIN_USER_KEY + token;
        Boolean isDeleted = stringRedisTemplate.delete(tokenKey);
        return isDeleted;
    }

    /**
     * 更新学生个人资料
     * @param studentUpdateReqDTO 学生更新信息请求
     * @param request HTTP请求
     * @return
     */    @Override
    public Boolean updateProfile(StudentUpdateReqDTO studentUpdateReqDTO, HttpServletRequest request) {
        // 1. 获取当前登录用户信息
        log.info("开始处理学生updateProfile请求...");
        
        // 尝试多种方式获取token
        String token = null;
        
        // 1. 尝试从标准请求头获取
        token = request.getHeader("authorization");
        log.info("从authorization获取的token: {}", token);
        
        // 2. 如果为空，尝试从查询参数获取
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
            log.info("从查询参数获取的token: {}", token);
        }
        
        // 3. 如果为空，尝试从cookie获取
        if (token == null || token.isEmpty()) {
            jakarta.servlet.http.Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (jakarta.servlet.http.Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName()) || "authorization".equals(cookie.getName())) {
                        token = cookie.getValue();
                        log.info("从cookie获取的token: {}", token);
                        break;
                    }
                }
            }
        }
        
        if (token == null || token.isEmpty() || "undefined".equals(token)) {
            log.error("无法获取有效的token");
            throw new cumt.miirso.eduagentx.convention.exception.ClientException(
                cumt.miirso.eduagentx.convention.errorcode.StudentErrorCode.STUDENT_NOT_LOGIN);
        }
        
        String tokenKey = RedisCacheConstant.LOGIN_USER_KEY + token;
        log.info("学生token键: {}", tokenKey);
        String username = stringRedisTemplate.opsForValue().get(tokenKey);
        
        if (username == null) {
            throw new ClientException(StudentErrorCode.STUDENT_NOT_LOGIN);
        }

        // 2. 查询学生信息
        StudentDO studentDO = lambdaQuery()
                .eq(StudentDO::getUsername, username)
                .one();

        if (studentDO == null) {
            throw new ClientException(StudentErrorCode.STUDENT_NULL);
        }

        // 3. 创建学生更新对象
        StudentDO updateStudent = new StudentDO();
        boolean needUpdate = false;

        if (studentUpdateReqDTO.getRealName() != null) {
            updateStudent.setRealName(studentUpdateReqDTO.getRealName());
            needUpdate = true;
        }

        if (studentUpdateReqDTO.getGender() != null) {
            updateStudent.setGender(studentUpdateReqDTO.getGender());
            needUpdate = true;
        }

        if (studentUpdateReqDTO.getPhone() != null) {
            updateStudent.setPhone(studentUpdateReqDTO.getPhone());
            needUpdate = true;
        }

        if (studentUpdateReqDTO.getEmail() != null) {
            updateStudent.setEmail(studentUpdateReqDTO.getEmail());
            needUpdate = true;
        }

        if (studentUpdateReqDTO.getMajorCode() != null) {
            updateStudent.setMajorCode(studentUpdateReqDTO.getMajorCode());
            needUpdate = true;
        }

        if (studentUpdateReqDTO.getClassCode() != null) {
            updateStudent.setClassCode(studentUpdateReqDTO.getClassCode());
            needUpdate = true;
        }

        if (studentUpdateReqDTO.getCollege() != null) {
            updateStudent.setCollege(studentUpdateReqDTO.getCollege());
            needUpdate = true;
        }

        if (studentUpdateReqDTO.getSchool() != null) {
            updateStudent.setSchool(studentUpdateReqDTO.getSchool());
            needUpdate = true;
        }

        // 如果没有任何字段需要更新，直接返回成功
        if (!needUpdate) {
            return true;
        }

        // 4. 使用lambdaUpdate避免类型不匹配问题
        boolean success = lambdaUpdate()
                .eq(StudentDO::getId, studentDO.getId()) // 使用字符串类型的ID进行精确匹配
                .update(updateStudent);

        if (success) {
            log.info("学生个人资料更新成功: {}", username);
        } else {
            log.error("学生个人资料更新失败: {}", username);
        }

        return success;
    }

    @Override
    public Boolean updatePassword(PasswordUpdateReqDTO passwordUpdateReqDTO, HttpServletRequest request) {
        // 1. 验证新密码与确认密码是否一致
        if (!passwordUpdateReqDTO.getNewPassword().equals(passwordUpdateReqDTO.getConfirmPassword())) {
            throw new ClientException(BaseErrorCode.NEW_PASSWORD_CONFIRM_ERROR);
        }

        // 2. 获取当前登录用户信息
        // 尝试多种方式获取token
        String token = null;
        
        // 1. 尝试从标准请求头获取
        token = request.getHeader("authorization");
        log.info("从authorization获取的token: {}", token);
        
        // 2. 如果为空，尝试从查询参数获取
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
            log.info("从查询参数获取的token: {}", token);
        }
        
        // 3. 如果为空，尝试从cookie获取
        if (token == null || token.isEmpty()) {
            jakarta.servlet.http.Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (jakarta.servlet.http.Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName()) || "authorization".equals(cookie.getName())) {
                        token = cookie.getValue();
                        log.info("从cookie获取的token: {}", token);
                        break;
                    }
                }
            }
        }
        
        if (token == null || token.isEmpty() || "undefined".equals(token)) {
            log.error("无法获取有效的token");
            throw new ClientException(StudentErrorCode.STUDENT_NOT_LOGIN);
        }
        
        String tokenKey = RedisCacheConstant.LOGIN_USER_KEY + token;
        log.info("学生token键: {}", tokenKey);
        String username = stringRedisTemplate.opsForValue().get(tokenKey);

        if (username == null) {
            throw new ClientException(StudentErrorCode.STUDENT_NOT_LOGIN);
        }

        // 3. 查询学生信息
        StudentDO studentDO = lambdaQuery()
                .eq(StudentDO::getUsername, username)
                .one();

        if (studentDO == null) {
            throw new ClientException(StudentErrorCode.STUDENT_NULL);
        }

        // 4. 验证旧密码
        if (!PasswordEncoder.matches(passwordUpdateReqDTO.getOldPassword(), studentDO.getPassword())) {
            throw new ClientException(StudentErrorCode.OLD_PASSWORD_ERROR);
        }

        // 5. 更新密码
        StudentDO updateStudent = new StudentDO();
        updateStudent.setPassword(PasswordEncoder.encode(passwordUpdateReqDTO.getNewPassword()));

        // 使用lambdaUpdate避免类型不匹配问题
        boolean success = lambdaUpdate()
                .eq(StudentDO::getId, studentDO.getId()) // 使用字符串类型的ID进行精确匹配
                .update(updateStudent);

        if (success) {
            log.info("学生密码修改成功: {}", username);

            // 退出登录，使token失效，强制用户重新登录
            stringRedisTemplate.delete(tokenKey);
            UserHolder.removeUserInfoDTO();
        } else {
            log.error("学生密码修改失败: {}", username);
        }        return success;
    }
    
    /**
     * 根据用户名获取学生信息
     * @param username 用户名
     * @return 学生信息
     */
    @Override
    public StudentDO getStudentInfo(String username) {
        LambdaQueryWrapper<StudentDO> queryWrapper = Wrappers.<StudentDO>lambdaQuery()
                .eq(StudentDO::getUsername, username);
        return baseMapper.selectOne(queryWrapper);
    }
}

package cumt.miirso.eduagentx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cumt.miirso.eduagentx.common.constant.RedisCacheConstant;
import cumt.miirso.eduagentx.convention.errorcode.AdminErrorCode;
import cumt.miirso.eduagentx.convention.errorcode.BaseErrorCode;
import cumt.miirso.eduagentx.convention.errorcode.TeacherErrorCode;
import cumt.miirso.eduagentx.convention.exception.ClientException;
import cumt.miirso.eduagentx.dto.UserInfoDTO;
import cumt.miirso.eduagentx.dto.req.AdminLoginReqDTO;
import cumt.miirso.eduagentx.dto.req.AdminRegisterReqDTO;
import cumt.miirso.eduagentx.dto.resp.AdminLoginRespDTO;
import cumt.miirso.eduagentx.dto.resp.AdminRegisterRespDTO;
import cumt.miirso.eduagentx.dto.resp.ClassQueryRespDTO;
import cumt.miirso.eduagentx.dto.resp.CourseImportRespDTO;
import cumt.miirso.eduagentx.dto.resp.CourseTeacherAssignRespDTO;
import cumt.miirso.eduagentx.dto.resp.StudentInfoRespDTO;
import cumt.miirso.eduagentx.dto.req.CourseTeacherAssignReqDTO;
import cumt.miirso.eduagentx.dto.req.ClassEnrollCourseReqDTO;
import cumt.miirso.eduagentx.dto.resp.ClassEnrollCourseRespDTO;
import cumt.miirso.eduagentx.entity.AdminDO;
import cumt.miirso.eduagentx.entity.ClassDO;
import cumt.miirso.eduagentx.entity.CourseDO;
import cumt.miirso.eduagentx.entity.CourseClassDO;
import cumt.miirso.eduagentx.entity.CourseTeacherDO;
import cumt.miirso.eduagentx.entity.EnrollmentDO;
import cumt.miirso.eduagentx.entity.StudentClassRelationDO;
import cumt.miirso.eduagentx.entity.StudentDO;
import cumt.miirso.eduagentx.entity.TeacherDO;
import cumt.miirso.eduagentx.mapper.AdminMapper;
import cumt.miirso.eduagentx.mapper.ClassMapper;
import cumt.miirso.eduagentx.mapper.CourseClassMapper;
import cumt.miirso.eduagentx.mapper.CourseTeacherMapper;
import cumt.miirso.eduagentx.mapper.EnrollmentMapper;
import cumt.miirso.eduagentx.mapper.StudentMapper;
import cumt.miirso.eduagentx.service.AdminService;
import cumt.miirso.eduagentx.service.CourseService;
import cumt.miirso.eduagentx.service.StudentClassRelationService;
import cumt.miirso.eduagentx.service.StudentService;
import cumt.miirso.eduagentx.service.TeacherService;
import cumt.miirso.eduagentx.utils.PasswordEncoder;
import cumt.miirso.eduagentx.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Package cumt.miirso.eduagentx.service.impl
 * @Author miirso
 * @Date 2025/6/20
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl extends ServiceImpl<AdminMapper, AdminDO> implements AdminService {    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;    private final TeacherService teacherService;    private final StudentService studentService;
    private final StudentClassRelationService studentClassRelationService;
    private final ClassMapper classMapper;
    private final CourseService courseService;
    private final CourseTeacherMapper courseTeacherMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final CourseClassMapper courseClassMapper;
    private final StudentMapper studentMapper;
    
    @Override
    public AdminRegisterRespDTO register(AdminRegisterReqDTO adminRegisterReqDTO) {
        // 1. 检查用户名是否已存在
        boolean usernameExists = lambdaQuery()
                .eq(AdminDO::getUsername, adminRegisterReqDTO.getUsername())
                .exists();
        if (usernameExists) {
            throw new ClientException(BaseErrorCode.USER_NAME_EXIST_ERROR);
        }

        // 2. 创建管理员对象
        AdminDO adminDO = getAdminDO(adminRegisterReqDTO);

        // 3. 保存到数据库
        save(adminDO);
        
        // 4. 构建并返回响应DTO
        AdminRegisterRespDTO respDTO = new AdminRegisterRespDTO();
        respDTO.setId(adminDO.getId().toString());
        respDTO.setUsername(adminDO.getUsername());

        log.info("管理员已成功创建: {}", respDTO);

        return respDTO;
    }    private static AdminDO getAdminDO(AdminRegisterReqDTO adminRegisterReqDTO) {
        AdminDO adminDO = new AdminDO(null, adminRegisterReqDTO.getUsername(), null);
        // 加密密码后存储
        if (adminRegisterReqDTO.getPassword() != null) {
            adminDO.setPassword(PasswordEncoder.encode(adminRegisterReqDTO.getPassword()));
        }
        return adminDO;
    }
    
    @Override
    public AdminLoginRespDTO login(AdminLoginReqDTO adminLoginReqDTO) {
        String username = adminLoginReqDTO.getUsername();
        if (username == null) {
            throw new ClientException(AdminErrorCode.ADMIN_NAME_ERROR);
        }
          // 查询管理员信息
        AdminDO adminDO = lambdaQuery()
                .eq(AdminDO::getUsername, username)
                .one();
                
        if (adminDO == null) {
            throw new ClientException(AdminErrorCode.ADMIN_NULL);
        }
        
        // 使用PasswordEncoder验证密码
        if (!PasswordEncoder.matches(adminLoginReqDTO.getPassword(), adminDO.getPassword())) {
            throw new ClientException(AdminErrorCode.ADMIN_LOGIN_ERROR);
        }
          // 生成token
        String token = UUID.randomUUID().toString();
        String tokenKey = RedisCacheConstant.LOGIN_USER_KEY + token;        
        // 将token存入Redis，设置60分钟过期时间 - 使用StringRedisTemplate保持一致性
        stringRedisTemplate.opsForValue().set(tokenKey, username, RedisCacheConstant.LOGIN_USER_TTL, TimeUnit.MINUTES);
        
        // 将用户信息存入ThreadLocal，注意type为3表示管理员
        UserInfoDTO userInfoDTO = new UserInfoDTO(adminDO.getId(),
                3, 
                adminDO.getUsername(), 
                token);
        UserHolder.saveUser(userInfoDTO);
        log.info("存储了管理员用户: {}", userInfoDTO);

        // 构建并返回登录响应
        AdminLoginRespDTO respDTO = new AdminLoginRespDTO();
        respDTO.setToken(token);
        
        log.info("管理员用户登录成功: {}", username);
        
        return respDTO;
    }
    
    @Override
    public Boolean check(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        String tokenKey = RedisCacheConstant.LOGIN_USER_KEY + token;
        String username = stringRedisTemplate.opsForValue().get(tokenKey);
        return !(username == null);
    }

    @Override
    public Boolean logout(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        String tokenKey = RedisCacheConstant.LOGIN_USER_KEY + token;
        Boolean isDeleted = stringRedisTemplate.delete(tokenKey);
        return isDeleted;
    }
      @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchRegisterTeachers(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ClientException(AdminErrorCode.FILE_IS_EMPTY);
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !(originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
            throw new ClientException(AdminErrorCode.FILE_TYPE_ERROR);
        }
        
        try {
            // 解析Excel文件
            List<TeacherDO> teacherDOList = new ArrayList<>();
            
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
                
                // 第一行是列名
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    throw new ClientException(AdminErrorCode.EXCEL_FORMAT_ERROR);
                }
                
                // 从第二行开始读取数据
                int rowCount = 0;
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    
                    rowCount++;                    
                    // 按照表头定义的顺序读取：username realName gender phone teacherNo school college password
                    String username = row.getCell(0) != null ? row.getCell(0).toString().trim() : null;
                    String realName = row.getCell(1) != null ? row.getCell(1).toString().trim() : null;
                    String genderStr = row.getCell(2) != null ? row.getCell(2).toString().trim() : null;
                    String phone = row.getCell(3) != null ? row.getCell(3).toString().trim() : null;
                    String teacherNo = row.getCell(4) != null ? row.getCell(4).toString().trim() : null;
                    String school = row.getCell(5) != null ? row.getCell(5).toString().trim() : null;
                    String college = row.getCell(6) != null ? row.getCell(6).toString().trim() : null;
                    
                    // 检查必填字段
                    if (realName == null || realName.isEmpty() || 
                        teacherNo == null || teacherNo.isEmpty() || 
                        username == null || username.isEmpty()) {
                        log.warn("第{}行数据不完整，姓名、教师编号或用户名为空", rowCount + 1);
                        continue;
                    }
                    
                    // 检查用户名是否已存在
                    boolean usernameExists = teacherService.lambdaQuery()
                            .eq(TeacherDO::getUsername, username)
                            .exists();
                    if (usernameExists) {
                        log.warn("第{}行用户名已存在: {}", rowCount + 1, username);
                        continue;
                    }
                    
                    // 检查教师编号是否已存在
                    boolean teacherNoExists = teacherService.lambdaQuery()
                            .eq(TeacherDO::getTeacherNo, teacherNo)
                            .exists();
                    if (teacherNoExists) {
                        log.warn("第{}行教师编号已存在: {}", rowCount + 1, teacherNo);
                        continue;
                    }
                    
                    // 创建教师对象
                    TeacherDO teacherDO = new TeacherDO();
                    teacherDO.setRealName(realName);
                    
                    // 处理性别转换
                    Integer gender = null;
                    if ("女".equals(genderStr) || "0".equals(genderStr)) {
                        gender = 0;
                    } else if ("男".equals(genderStr) || "1".equals(genderStr)) {
                        gender = 1;
                    }
                    teacherDO.setGender(gender);
                      teacherDO.setTeacherNo(teacherNo);
                    teacherDO.setPhone(phone);
                    teacherDO.setSchool(school);
                    teacherDO.setCollege(college);
                    teacherDO.setUsername(username);
                    
                    // 设置默认密码
                    teacherDO.setPassword(PasswordEncoder.encode("123456"));
                    
                    teacherDOList.add(teacherDO);
                }
            }
            
            if (teacherDOList.isEmpty()) {
                throw new ClientException(AdminErrorCode.NO_VALID_DATA);
            }
            
            // 批量保存教师
            boolean success = teacherService.saveBatch(teacherDOList);
            
            if (!success) {
                throw new ClientException(AdminErrorCode.BATCH_SAVE_ERROR);
            }
            
            log.info("成功批量注册了 {} 名教师", teacherDOList.size());
            
            return teacherDOList.size();
            
        } catch (IOException e) {
            log.error("文件读取失败", e);
            throw new ClientException(AdminErrorCode.FILE_PARSE_ERROR);
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量注册教师失败", e);
            throw new ClientException(AdminErrorCode.BATCH_REGISTER_ERROR);
        }
    }    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchRegisterStudents(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ClientException(AdminErrorCode.FILE_IS_EMPTY);
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !(originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
            throw new ClientException(AdminErrorCode.FILE_TYPE_ERROR);
        }
        
        try {
            // 解析Excel文件
            List<StudentDO> studentDOList = new ArrayList<>();
            
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
                
                // 第一行是列名
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    throw new ClientException(AdminErrorCode.EXCEL_FORMAT_ERROR);
                }
                
                // 从第二行开始读取数据
                int rowCount = 0;
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    
                    rowCount++;
                    
                    // 按照表头定义的顺序读取：studentNo realName gender username phone email majorCode classCode college school
                    String studentNo = row.getCell(0) != null ? row.getCell(0).toString().trim() : null;
                    String realName = row.getCell(1) != null ? row.getCell(1).toString().trim() : null;
                    String genderStr = row.getCell(2) != null ? row.getCell(2).toString().trim() : null;
                    String username = row.getCell(3) != null ? row.getCell(3).toString().trim() : null;
                    String phone = row.getCell(4) != null ? row.getCell(4).toString().trim() : null;
                    String email = row.getCell(5) != null ? row.getCell(5).toString().trim() : null;
                    String majorCode = row.getCell(6) != null ? row.getCell(6).toString().trim() : null;
                    String classCode = row.getCell(7) != null ? row.getCell(7).toString().trim() : null;
                    String college = row.getCell(8) != null ? row.getCell(8).toString().trim() : null;
                    String school = row.getCell(9) != null ? row.getCell(9).toString().trim() : null;
                    String password = row.getCell(10) != null ? row.getCell(10).toString().trim() : "123456"; // 默认密码
                    
                    // 检查必填字段
                    if (studentNo == null || studentNo.isEmpty() || 
                        username == null || username.isEmpty() || 
                        realName == null || realName.isEmpty()) {
                        log.warn("第{}行数据不完整，学号、用户名或姓名为空", rowCount + 1);
                        continue;
                    }
                    
                    // 检查用户名是否已存在
                    boolean usernameExists = studentService.lambdaQuery()
                            .eq(StudentDO::getUsername, username)
                            .exists();
                    if (usernameExists) {
                        log.warn("第{}行用户名已存在: {}", rowCount + 1, username);
                        continue;
                    }
                    
                    // 检查学号是否已存在
                    boolean studentNoExists = studentService.lambdaQuery()
                            .eq(StudentDO::getStudentNo, studentNo)
                            .exists();
                    if (studentNoExists) {
                        log.warn("第{}行学号已存在: {}", rowCount + 1, studentNo);
                        continue;
                    }
                    
                    // 创建学生对象
                    StudentDO studentDO = new StudentDO();
                    studentDO.setStudentNo(studentNo);
                    studentDO.setRealName(realName);
                    
                    // 处理性别转换
                    Integer gender = null;
                    if ("女".equals(genderStr) || "0".equals(genderStr)) {
                        gender = 0;
                    } else if ("男".equals(genderStr) || "1".equals(genderStr)) {
                        gender = 1;
                    }
                    studentDO.setGender(gender);
                    
                    studentDO.setUsername(username);
                    studentDO.setPhone(phone);
                    studentDO.setEmail(email);
                    studentDO.setMajorCode(majorCode);
                    studentDO.setClassCode(classCode);
                    studentDO.setCollege(college);
                    studentDO.setSchool(school);
                    
                    // 设置密码
                    studentDO.setPassword(PasswordEncoder.encode(password));
                    
                    studentDOList.add(studentDO);
                }
            }
            
            if (studentDOList.isEmpty()) {
                throw new ClientException(AdminErrorCode.NO_VALID_DATA);
            }
            
            // 批量保存学生
            boolean success = studentService.saveBatch(studentDOList);
            
            if (!success) {
                throw new ClientException(AdminErrorCode.BATCH_SAVE_ERROR);
            }
            
            log.info("成功批量注册了 {} 名学生", studentDOList.size());
            
            return studentDOList.size();
            
        } catch (IOException e) {
            log.error("文件读取失败", e);
            throw new ClientException(AdminErrorCode.FILE_PARSE_ERROR);
        } catch (ClientException e) {
            throw e;        } catch (Exception e) {
            log.error("批量注册学生失败", e);
            throw new ClientException(AdminErrorCode.BATCH_REGISTER_ERROR);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchRegisterStudentsWithClass(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ClientException(AdminErrorCode.FILE_IS_EMPTY);
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !(originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
            throw new ClientException(AdminErrorCode.FILE_TYPE_ERROR);
        }
        
        try {
            // 从文件名中提取班级信息
            ClassDO classDO = parseClassInfoFromFilename(originalFilename);
            
            // 解析Excel文件
            List<StudentDO> studentDOList = new ArrayList<>();
            Map<Long, String> studentClassMap = new HashMap<>(); // 存储学生ID和班级信息的映射
            
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
                
                // 第一行是列名
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    throw new ClientException(AdminErrorCode.EXCEL_FORMAT_ERROR);
                }
                
                // 从第二行开始读取数据
                int rowCount = 0;
                int successCount = 0;
                
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    
                    rowCount++;
                    
                    // 按照表头定义的顺序读取数据
                    String studentNo = row.getCell(0) != null ? row.getCell(0).toString().trim() : null;
                    String realName = row.getCell(1) != null ? row.getCell(1).toString().trim() : null;
                    String genderStr = row.getCell(2) != null ? row.getCell(2).toString().trim() : null;
                    String username = row.getCell(3) != null ? row.getCell(3).toString().trim() : null;
                    String phone = row.getCell(4) != null ? row.getCell(4).toString().trim() : null;
                    String email = row.getCell(5) != null ? row.getCell(5).toString().trim() : null;
                    String majorCode = row.getCell(6) != null ? row.getCell(6).toString().trim() : null;
                    String classCodeFromExcel = row.getCell(7) != null ? row.getCell(7).toString().trim() : null;
                    String college = row.getCell(8) != null ? row.getCell(8).toString().trim() : null;
                    String school = row.getCell(9) != null ? row.getCell(9).toString().trim() : null;
                    String password = row.getCell(10) != null ? row.getCell(10).toString().trim() : "123456"; // 默认密码
                    
                    // 检查必填字段
                    if (studentNo == null || studentNo.isEmpty() || 
                        username == null || username.isEmpty() || 
                        realName == null || realName.isEmpty()) {
                        log.warn("第{}行数据不完整，学号、用户名或姓名为空", rowCount + 1);
                        continue;
                    }
                    
                    // 检查用户名是否已存在
                    boolean usernameExists = studentService.lambdaQuery()
                            .eq(StudentDO::getUsername, username)
                            .exists();
                    if (usernameExists) {
                        log.warn("第{}行用户名已存在: {}", rowCount + 1, username);
                        continue;
                    }
                    
                    // 检查学号是否已存在
                    boolean studentNoExists = studentService.lambdaQuery()
                            .eq(StudentDO::getStudentNo, studentNo)
                            .exists();
                    if (studentNoExists) {
                        log.warn("第{}行学号已存在: {}", rowCount + 1, studentNo);
                        continue;
                    }
                    
                    // 创建学生对象
                    StudentDO studentDO = new StudentDO();
                    studentDO.setStudentNo(studentNo);
                    studentDO.setRealName(realName);
                    
                    // 处理性别转换
                    Integer gender = null;
                    if ("女".equals(genderStr) || "0".equals(genderStr)) {
                        gender = 0;
                    } else if ("男".equals(genderStr) || "1".equals(genderStr)) {
                        gender = 1;
                    }
                    studentDO.setGender(gender);
                    
                    studentDO.setUsername(username);
                    studentDO.setPhone(phone);
                    studentDO.setEmail(email);
                    studentDO.setMajorCode(majorCode);
                    
                    // 优先使用Excel中的班级代码，如果没有则使用文件名解析的班级名称
                    String classCode = classCodeFromExcel;
                    if (classCode == null || classCode.isEmpty()) {
                        classCode = classDO != null ? classDO.getName() : null;
                    }
                    studentDO.setClassCode(classCode);
                    
                    studentDO.setCollege(college);
                    studentDO.setSchool(school);
                    
                    // 设置密码
                    studentDO.setPassword(PasswordEncoder.encode(password));
                    
                    // 保存学生
                    studentService.save(studentDO);
                    successCount++;
                    
                    // 处理班级关联
                    if (classDO != null) {
                        studentClassMap.put(studentDO.getId(), classDO.getName());
                    }
                }
                
                // 处理学生班级关联
                for (Map.Entry<Long, String> entry : studentClassMap.entrySet()) {
                    Long studentId = entry.getKey();
                    String className = entry.getValue();
                    
                    // 查询或创建班级
                    ClassDO existingClass = getOrCreateClass(className, classDO.getMajorId());
                    
                    // 添加学生班级关联
                    addStudentToClass(studentId, existingClass.getId());
                }
                
                log.info("成功批量注册了 {} 名学生并关联班级", successCount);
                
                return successCount;
            }
        } catch (IOException e) {
            log.error("文件读取失败", e);
            throw new ClientException(AdminErrorCode.FILE_PARSE_ERROR);
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量注册学生并关联班级失败", e);
            throw new ClientException(AdminErrorCode.BATCH_REGISTER_ERROR);
        }
    }
      /**
     * 从文件名中解析班级信息
     * 格式：专业名称_年级级_班号班_其他信息.xlsx
     * 例如：计算机科学与技术_22级_1班_20250621213131.xlsx
     */
    private ClassDO parseClassInfoFromFilename(String filename) {
        try {
            // 移除文件扩展名
            int dotIndex = filename.lastIndexOf('.');
            String nameWithoutExt = dotIndex > 0 ? filename.substring(0, dotIndex) : filename;
            
            // 按下划线分割
            String[] parts = nameWithoutExt.split("_");
            if (parts.length >= 3) {
                String majorName = parts[0]; // 专业名称
                String grade = parts[1].replace("级", ""); // 年级，去除"级"字
                String classNum = parts[2].replace("班", ""); // 班号，去除"班"字
                
                // 构建班级名称，例如：计算机科学与技术22-1班
                String className = majorName + grade + "-" + classNum + "班";
                
                // 获取或推断专业代码
                String majorId = inferMajorId(majorName);
                
                ClassDO classDO = new ClassDO();
                classDO.setName(className);
                classDO.setMajorId(majorId);
                
                log.info("从文件名 [{}] 解析出班级信息: {}", filename, classDO);
                return classDO;
            }
        } catch (Exception e) {
            log.warn("从文件名解析班级信息失败: {}", filename, e);
        }
        
        return null;
    }
    
    /**
     * 根据专业名称推断专业代码
     */
    private String inferMajorId(String majorName) {
        // 这里使用简单的逻辑，实际应用中可能需要从数据库查询
        // 以下是一个示例实现
        Map<String, String> majorNameToCode = new HashMap<>();
        majorNameToCode.put("计算机科学与技术", "46");
        majorNameToCode.put("软件工程", "50");
        majorNameToCode.put("信息安全", "48");
        majorNameToCode.put("人工智能", "20");
        
        return majorNameToCode.getOrDefault(majorName, "00");
    }
    
    /**
     * 获取或创建班级
     */
    private ClassDO getOrCreateClass(String className, String majorId) {
        // 查询班级是否存在
        LambdaQueryWrapper<ClassDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassDO::getName, className);
        ClassDO classDO = classMapper.selectOne(queryWrapper);
        
        // 如果班级不存在，创建新班级
        if (classDO == null) {
            classDO = new ClassDO();
            classDO.setName(className);
            classDO.setMajorId(majorId);
            classMapper.insert(classDO);
            log.info("创建新班级: {}", className);
        }
        
        return classDO;
    }
      @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addStudentToClass(Long studentId, Long classId) {
        try {
            // 1. 验证学生是否存在
            StudentDO student = studentService.getById(studentId);
            if (student == null) {
                log.warn("学生不存在，studentId: {}", studentId);
                throw new ClientException("学生不存在");
            }
            
            // 2. 验证班级是否存在
            LambdaQueryWrapper<ClassDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ClassDO::getId, classId);
            ClassDO classDO = classMapper.selectOne(queryWrapper);
            if (classDO == null) {
                log.warn("班级不存在，classId: {}", classId);
                throw new ClientException("班级不存在");
            }
            
            // 3. 更新学生的班级信息
            student.setClassCode(classDO.getName());  // 使用班级名称而不是ID
            boolean updateResult = studentService.updateById(student);
            
            // 4. 创建学生班级关联记录
            if (updateResult) {
                // 添加到关联表
                studentClassRelationService.addRelation(
                    studentId,
                    classId,
                    student.getStudentNo(),
                    classDO.getName()
                );
                
                log.info("成功将学生[{}]添加到班级[{}]", studentId, classId);
                return true;
            } else {
                log.error("更新学生班级信息失败，studentId: {}, classId: {}", studentId, classId);
                return false;
            }
            
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            log.error("添加学生到班级失败", e);
            throw new ClientException("添加学生到班级失败：" + e.getMessage());
        }
    }

    @Override
    public List<ClassQueryRespDTO> queryAllClasses() {
        log.info("查询所有班级信息");
        
        // 查询所有班级（tag为true的有效班级）
        LambdaQueryWrapper<ClassDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassDO::getTag, true)
                   .orderByAsc(ClassDO::getId);
        
        List<ClassDO> classList = classMapper.selectList(queryWrapper);
        List<ClassQueryRespDTO> resultList = new ArrayList<>(classList.size());
        
        // 转换为响应DTO
        for (ClassDO classDO : classList) {
            ClassQueryRespDTO respDTO = new ClassQueryRespDTO();
            respDTO.setId(classDO.getId());
            respDTO.setName(classDO.getName());
            respDTO.setMajorId(classDO.getMajorId());
            respDTO.setCreateTime(classDO.getCreateTime().toString());
              // 查询班级学生人数
            LambdaQueryWrapper<StudentClassRelationDO> countWrapper = new LambdaQueryWrapper<>();
            countWrapper.eq(StudentClassRelationDO::getClassCode, classDO.getName())
                       .eq(StudentClassRelationDO::getTag, true);
            
            Integer studentCount = Math.toIntExact(studentClassRelationService.count(countWrapper));
            respDTO.setStudentCount(studentCount);
            
            resultList.add(respDTO);
        }
        
        log.info("查询到 {} 个班级", resultList.size());
        return resultList;
    }
    
    @Override
    public List<StudentInfoRespDTO> queryStudentsByClassId(Long classId) {
        log.info("查询班级ID为 {} 的学生信息", classId);
        
        // 1. 验证班级是否存在
        ClassDO classDO = classMapper.selectById(classId);
        if (classDO == null) {
            log.warn("班级不存在，classId: {}", classId);
            throw new ClientException("班级不存在");
        }
        
        // 2. 获取班级名称
        String className = classDO.getName();
        
        // 3. 查询该班级的所有学生关联记录
        List<StudentClassRelationDO> relationList = studentClassRelationService.getByClassCode(className);
        
        // 4. 获取学生详细信息
        List<StudentInfoRespDTO> resultList = new ArrayList<>(relationList.size());
        for (StudentClassRelationDO relation : relationList) {
            // 通过学号查询学生信息
            LambdaQueryWrapper<StudentDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(StudentDO::getStudentNo, relation.getStudentNo())
                       .eq(StudentDO::getTag, true);
            
            StudentDO student = studentService.getOne(queryWrapper);
            if (student != null) {
                StudentInfoRespDTO respDTO = new StudentInfoRespDTO();
                respDTO.setId(student.getId());
                respDTO.setStudentNo(student.getStudentNo());
                respDTO.setRealName(student.getRealName());
                respDTO.setGender(student.getGender());
                respDTO.setPhone(student.getPhone());
                respDTO.setEmail(student.getEmail());
                respDTO.setMajorCode(student.getMajorCode());
                respDTO.setClassCode(student.getClassCode());
                respDTO.setCollege(student.getCollege());
                respDTO.setSchool(student.getSchool());
                respDTO.setCreateTime(student.getCreateTime().toString());
                
                resultList.add(respDTO);
            }
        }
        
        log.info("查询到班级 {} 的 {} 名学生", className, resultList.size());
        return resultList;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseImportRespDTO batchImportCourses(MultipartFile file) {
        log.info("开始批量导入课程");
        if (file.isEmpty()) {
            throw new ClientException(AdminErrorCode.FILE_IS_EMPTY);
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !(originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
            throw new ClientException(AdminErrorCode.FILE_TYPE_ERROR);
        }
        
        CourseImportRespDTO respDTO = new CourseImportRespDTO();
        int successCount = 0;
        int failCount = 0;
        int totalCount = 0;
        
        try {
            // 解析Excel文件
            List<CourseDO> courseDOList = new ArrayList<>();
            
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
                
                // 第一行是列名
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    throw new ClientException(AdminErrorCode.EXCEL_FORMAT_ERROR);
                }
                
                // 验证Excel格式
                Map<String, Integer> columnIndices = new HashMap<>();
                for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                    Cell cell = headerRow.getCell(i);
                    if (cell != null) {
                        columnIndices.put(cell.getStringCellValue().trim(), i);
                    }
                }
                
                // 必要的列
                String[] requiredColumns = {"id", "subject_id", "name", "type"};
                for (String column : requiredColumns) {
                    if (!columnIndices.containsKey(column)) {
                        throw new ClientException("Excel缺少必要的列: " + column);
                    }
                }
                
                // 从第二行开始读取数据
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    
                    totalCount++;
                    
                    try {
                        CourseDO courseDO = new CourseDO();
                        
                        // 读取必要字段
                        String id = getCellValueAsString(row, columnIndices.get("id"));
                        String name = getCellValueAsString(row, columnIndices.get("name"));
                        String subjectIdStr = getCellValueAsString(row, columnIndices.get("subject_id"));
                        String type = getCellValueAsString(row, columnIndices.get("type"));
                        
                        if (id.isEmpty() || name.isEmpty() || subjectIdStr.isEmpty() || type.isEmpty()) {
                            log.warn("第{}行数据不完整，必填字段为空", i + 1);
                            failCount++;
                            continue;
                        }
                        
                        // 验证课程编号是否已存在
                        boolean idExists = checkCourseIdExists(id);
                        if (idExists) {
                            log.warn("第{}行课程编号已存在: {}", i + 1, id);
                            failCount++;
                            continue;
                        }
                        
                        // 设置课程基本信息
                        courseDO.setId(id);
                        courseDO.setName(name);
                        courseDO.setSubjectId(Integer.parseInt(subjectIdStr));
                        courseDO.setType(type);
                        
                        // 设置可选字段
                        if (columnIndices.containsKey("description")) {
                            courseDO.setDescription(getCellValueAsString(row, columnIndices.get("description")));
                        }
                        
                        if (columnIndices.containsKey("cover_image")) {
                            courseDO.setCoverImage(getCellValueAsString(row, columnIndices.get("cover_image")));
                        }
                        
                        if (columnIndices.containsKey("assessment_method")) {
                            courseDO.setAssessmentMethod(getCellValueAsString(row, columnIndices.get("assessment_method")));
                        }
                        
                        // 处理日期字段
                        if (columnIndices.containsKey("start_date")) {
                            String startDateStr = getCellValueAsString(row, columnIndices.get("start_date"));
                            if (!startDateStr.isEmpty()) {
                                try {
                                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                                    courseDO.setStartDate(sdf.parse(startDateStr));
                                } catch (Exception e) {
                                    log.warn("第{}行开始日期格式错误: {}", i + 1, startDateStr);
                                    // 使用默认值
                                    courseDO.setStartDate(new java.util.Date());
                                }
                            }
                        }
                        
                        if (columnIndices.containsKey("end_date")) {
                            String endDateStr = getCellValueAsString(row, columnIndices.get("end_date"));
                            if (!endDateStr.isEmpty()) {
                                try {
                                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                                    courseDO.setEndDate(sdf.parse(endDateStr));
                                } catch (Exception e) {
                                    log.warn("第{}行结束日期格式错误: {}", i + 1, endDateStr);
                                    // 使用默认值
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.add(Calendar.MONTH, 4); // 默认4个月后结束
                                    courseDO.setEndDate(calendar.getTime());
                                }
                            }
                        }
                        
                        courseDOList.add(courseDO);
                        successCount++;
                        
                    } catch (Exception e) {
                        log.error("处理第{}行数据时发生错误: {}", i + 1, e.getMessage(), e);
                        failCount++;
                    }
                }
            }
            
            // 批量保存课程
            if (!courseDOList.isEmpty()) {
                boolean success = courseService.saveBatch(courseDOList);
                if (!success) {
                    throw new ClientException(AdminErrorCode.BATCH_SAVE_ERROR);
                }
                log.info("成功批量导入了 {} 个课程", courseDOList.size());
            } else {
                log.warn("没有有效的课程数据可导入");
            }
            
        } catch (IOException e) {
            log.error("读取Excel文件失败", e);
            throw new ClientException("读取Excel文件失败: " + e.getMessage());
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量导入课程失败", e);
            throw new ClientException("批量导入课程失败: " + e.getMessage());
        }
        
        respDTO.setSuccessCount(successCount);
        respDTO.setFailCount(failCount);
        respDTO.setTotalCount(totalCount);
        
        return respDTO;
    }
    
    /**
     * 获取单元格的字符串值
     */
    private String getCellValueAsString(Row row, Integer index) {
        if (index == null || row.getCell(index) == null) {
            return "";
        }
        
        Cell cell = row.getCell(index);
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return new java.text.SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
    
    /**
     * 检查课程ID是否已存在
     */
    private boolean checkCourseIdExists(String courseId) {
        return courseService.lambdaQuery()
                .eq(CourseDO::getId, courseId)
                .exists();
    }
    
    /**
     * 管理员为课程分配教师
     *
     * @param requestParam 课程教师分配请求参数
     * @return 分配结果
     */
    @Override
    @Transactional
    public CourseTeacherAssignRespDTO assignTeacherToCourse(CourseTeacherAssignReqDTO requestParam) {
        log.info("=== 开始为课程分配教师 ===");
        log.info("请求参数: 课程ID={}, 教师ID={}", requestParam.getCourseId(), requestParam.getTeacherId());
        
        // 1. 参数验证
        if (requestParam.getCourseId() == null || requestParam.getCourseId().trim().isEmpty()) {
            throw new ClientException("课程ID不能为空");
        }
        if (requestParam.getTeacherId() == null || requestParam.getTeacherId().trim().isEmpty()) {
            throw new ClientException("教师ID不能为空");
        }
        
        // 2. 验证课程是否存在
        CourseDO courseDO = courseService.lambdaQuery()
                .eq(CourseDO::getId, requestParam.getCourseId())
                .eq(CourseDO::getTag, true)
                .one();
        if (courseDO == null) {
            throw new ClientException("课程不存在或已被删除");
        }
        
        // 3. 验证教师是否存在
        TeacherDO teacherDO = teacherService.lambdaQuery()
                .eq(TeacherDO::getId, requestParam.getTeacherId())
                .eq(TeacherDO::getTag, true)
                .one();
        if (teacherDO == null) {
            throw new ClientException("教师不存在或已被删除");
        }
        
        // 4. 检查是否已经分配过该教师
        boolean alreadyAssigned = courseTeacherMapper.selectCount(
            new LambdaQueryWrapper<CourseTeacherDO>()
                .eq(CourseTeacherDO::getCourseId, requestParam.getCourseId())
                .eq(CourseTeacherDO::getTeacherId, requestParam.getTeacherId())
        ) > 0;
        
        String message;
        Long assignId;
        
        if (alreadyAssigned) {
            message = "该教师已经被分配到此课程";
            // 获取现有记录的ID
            CourseTeacherDO existingRecord = courseTeacherMapper.selectOne(
                new LambdaQueryWrapper<CourseTeacherDO>()
                    .eq(CourseTeacherDO::getCourseId, requestParam.getCourseId())
                    .eq(CourseTeacherDO::getTeacherId, requestParam.getTeacherId())
            );
            assignId = existingRecord.getId();
            log.info("教师{}已经分配到课程{}，跳过重复分配", teacherDO.getRealName(), courseDO.getName());
        } else {
            // 5. 创建课程教师关联记录
            CourseTeacherDO courseTeacherDO = new CourseTeacherDO();
            courseTeacherDO.setCourseId(requestParam.getCourseId());
            courseTeacherDO.setTeacherId(requestParam.getTeacherId());
            
            int result = courseTeacherMapper.insert(courseTeacherDO);
            if (result <= 0) {
                throw new ClientException("分配教师失败，请稍后重试");
            }
            
            assignId = courseTeacherDO.getId();
            message = "成功为课程分配教师";
            log.info("成功为课程{}分配教师{}", courseDO.getName(), teacherDO.getRealName());
        }
        
        // 6. 构建返回结果
        CourseTeacherAssignRespDTO respDTO = new CourseTeacherAssignRespDTO();
        respDTO.setId(assignId);
        respDTO.setCourseId(requestParam.getCourseId());
        respDTO.setCourseName(courseDO.getName());
        respDTO.setTeacherId(requestParam.getTeacherId());
        respDTO.setTeacherName(teacherDO.getRealName());
        respDTO.setTeacherNo(teacherDO.getTeacherNo());
        respDTO.setMessage(message);
          log.info("=== 课程教师分配完成 ===");
        log.info("分配结果: {}", respDTO);
        
        return respDTO;
    }

    /**
     * 管理员按班级名称批量添加学生到课程
     * 
     * 实现步骤：
     * 1. 验证课程是否存在
     * 2. 根据班级名称查找学生列表
     * 3. 查找已经选课的学生
     * 4. 批量插入新的选课记录
     * 5. 更新课程-班级关联
     * 6. 构建返回结果
     * 
     * @param requestParam 班级选课请求参数
     * @return 选课结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClassEnrollCourseRespDTO enrollClassToCourse(ClassEnrollCourseReqDTO requestParam) {
        log.info("=== 开始执行班级选课 ===");
        log.info("请求参数: {}", requestParam);
        
        // 1. 参数验证
        if (requestParam.getClassName() == null || requestParam.getClassName().trim().isEmpty()) {
            throw new ClientException("班级名称不能为空");
        }
        if (requestParam.getCourseId() == null || requestParam.getCourseId().trim().isEmpty()) {
            throw new ClientException("课程ID不能为空");
        }
        
        String className = requestParam.getClassName().trim();
        String courseId = requestParam.getCourseId().trim();
        
        // 2. 验证课程是否存在
        CourseDO courseDO = courseService.lambdaQuery()
                .eq(CourseDO::getId, courseId)
                .eq(CourseDO::getTag, true)
                .one();
        if (courseDO == null) {
            throw new ClientException("课程不存在或已被删除");
        }
        log.info("找到课程: {} - {}", courseDO.getId(), courseDO.getName());
        
        // 3. 根据班级名称查找学生列表 (使用classCode字段)
        List<StudentDO> classStudents = studentMapper.selectList(
                new LambdaQueryWrapper<StudentDO>()
                        .eq(StudentDO::getClassCode, className)
                        .eq(StudentDO::getTag, true)
        );
        
        if (classStudents.isEmpty()) {
            throw new ClientException("班级'" + className + "'中没有找到学生，请检查班级名称是否正确");
        }
        log.info("找到班级 '{}' 的学生数量: {}", className, classStudents.size());
        
        // 4. 查找已经选课的学生
        List<Long> studentIds = classStudents.stream().map(StudentDO::getId).toList();
        List<EnrollmentDO> existingEnrollments = enrollmentMapper.selectList(
                new LambdaQueryWrapper<EnrollmentDO>()
                        .eq(EnrollmentDO::getCourseId, courseId)
                        .in(EnrollmentDO::getStudentId, studentIds)
                        .eq(EnrollmentDO::getTag, true)
        );
        
        List<Long> alreadyEnrolledStudentIds = existingEnrollments.stream()
                .map(EnrollmentDO::getStudentId)
                .toList();
        log.info("已经选课的学生数量: {}", alreadyEnrolledStudentIds.size());
        
        // 5. 筛选需要新增选课的学生
        List<StudentDO> studentsToEnroll = classStudents.stream()
                .filter(student -> !alreadyEnrolledStudentIds.contains(student.getId()))
                .toList();
        log.info("需要新增选课的学生数量: {}", studentsToEnroll.size());
        
        // 6. 批量插入新的选课记录
        if (!studentsToEnroll.isEmpty()) {
            List<EnrollmentDO> newEnrollments = studentsToEnroll.stream()
                    .map(student -> {
                        EnrollmentDO enrollment = new EnrollmentDO();
                        enrollment.setStudentId(student.getId());
                        enrollment.setCourseId(courseId);
                        enrollment.setEnrollmentDate(java.time.LocalDateTime.now());
                        return enrollment;
                    })
                    .toList();
            
            // 批量保存
            for (EnrollmentDO enrollment : newEnrollments) {
                enrollmentMapper.insert(enrollment);
            }
            log.info("成功插入 {} 条选课记录", newEnrollments.size());
        }
        
        // 7. 更新课程-班级关联表 (根据classes表查找班级ID)
        ClassDO classDO = classMapper.selectOne(
                new LambdaQueryWrapper<ClassDO>()
                        .eq(ClassDO::getName, className)
                        .eq(ClassDO::getTag, true)
        );
        
        if (classDO != null) {
            // 检查课程-班级关联是否已存在
            CourseClassDO existingCourseClass = courseClassMapper.selectOne(
                    new LambdaQueryWrapper<CourseClassDO>()
                            .eq(CourseClassDO::getCourseId, courseId)
                            .eq(CourseClassDO::getClassId, classDO.getId())
            );
            
            if (existingCourseClass == null) {
                // 插入新的课程-班级关联
                CourseClassDO courseClassDO = new CourseClassDO();
                courseClassDO.setCourseId(courseId);
                courseClassDO.setClassId(classDO.getId());
                courseClassMapper.insert(courseClassDO);
                log.info("成功创建课程-班级关联: 课程ID={}, 班级ID={}", courseId, classDO.getId());
            } else {
                log.info("课程-班级关联已存在: 课程ID={}, 班级ID={}", courseId, classDO.getId());
            }
        } else {
            log.warn("在classes表中未找到班级 '{}', 跳过课程-班级关联更新", className);
        }
        
        // 8. 构建返回结果
        ClassEnrollCourseRespDTO respDTO = new ClassEnrollCourseRespDTO();
        respDTO.setClassName(className);
        respDTO.setCourseId(courseId);
        respDTO.setCourseName(courseDO.getName());
        respDTO.setTotalStudents(classStudents.size());
        respDTO.setEnrolledCount(studentsToEnroll.size());
        respDTO.setAlreadyEnrolledCount(alreadyEnrolledStudentIds.size());
        
        // 构建成功选课学生信息
        List<ClassEnrollCourseRespDTO.EnrolledStudentInfo> enrolledStudents = studentsToEnroll.stream()
                .map(student -> {
                    ClassEnrollCourseRespDTO.EnrolledStudentInfo info = new ClassEnrollCourseRespDTO.EnrolledStudentInfo();
                    info.setStudentId(student.getId());
                    info.setStudentName(student.getRealName());
                    info.setStudentNo(student.getStudentNo());
                    return info;
                })
                .toList();
        respDTO.setEnrolledStudents(enrolledStudents);
        
        // 构建已选课学生信息
        List<ClassEnrollCourseRespDTO.EnrolledStudentInfo> alreadyEnrolledStudents = classStudents.stream()
                .filter(student -> alreadyEnrolledStudentIds.contains(student.getId()))
                .map(student -> {
                    ClassEnrollCourseRespDTO.EnrolledStudentInfo info = new ClassEnrollCourseRespDTO.EnrolledStudentInfo();
                    info.setStudentId(student.getId());
                    info.setStudentName(student.getRealName());
                    info.setStudentNo(student.getStudentNo());
                    return info;
                })
                .toList();
        respDTO.setAlreadyEnrolledStudents(alreadyEnrolledStudents);
        
        log.info("=== 班级选课完成 ===");
        log.info("选课结果: 班级={}, 课程={}, 总学生数={}, 新增选课={}, 已选课={}", 
                className, courseDO.getName(), classStudents.size(), 
                studentsToEnroll.size(), alreadyEnrolledStudentIds.size());
        
        return respDTO;
    }
}

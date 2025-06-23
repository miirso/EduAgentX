package cumt.miirso.eduagentx.controller;

import cumt.miirso.eduagentx.convention.exception.ClientException;
import cumt.miirso.eduagentx.convention.result.Result;
import cumt.miirso.eduagentx.convention.result.Results;
import cumt.miirso.eduagentx.dto.req.AdminLoginReqDTO;
import cumt.miirso.eduagentx.dto.req.AdminRegisterReqDTO;
import cumt.miirso.eduagentx.dto.req.ClassCreateReqDTO;
import cumt.miirso.eduagentx.dto.req.ClassEnrollCourseReqDTO;
import cumt.miirso.eduagentx.dto.req.CourseTeacherAssignReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherPageQueryReqDTO;
import cumt.miirso.eduagentx.dto.resp.AdminLoginRespDTO;
import cumt.miirso.eduagentx.dto.resp.AdminRegisterRespDTO;
import cumt.miirso.eduagentx.dto.resp.ClassEnrollCourseRespDTO;
import cumt.miirso.eduagentx.dto.resp.ClassQueryRespDTO;
import cumt.miirso.eduagentx.dto.resp.CourseImportRespDTO;
import cumt.miirso.eduagentx.dto.resp.CourseQueryRespDTO;
import cumt.miirso.eduagentx.dto.resp.CourseTeacherAssignRespDTO;
import cumt.miirso.eduagentx.dto.resp.StudentInfoRespDTO;
import cumt.miirso.eduagentx.dto.resp.TeacherPageQueryRespDTO;
import cumt.miirso.eduagentx.service.AdminService;
import cumt.miirso.eduagentx.service.ClassService;
import cumt.miirso.eduagentx.service.CourseService;
import cumt.miirso.eduagentx.service.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Package cumt.miirso.eduagentx.controller
 * @Author miirso
 * @Date 2025/6/20
 */

@RestController
@RequestMapping("/api/eduagentx/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {    private final AdminService adminService;
    private final ClassService classService;
    private final CourseService courseService;
    private final TeacherService teacherService;

    /**
     * 管理员注册
     */
    @PostMapping("/register")
    public Result<AdminRegisterRespDTO> register(@RequestBody AdminRegisterReqDTO adminRegisterReqDTO) {
        AdminRegisterRespDTO adminRegisterRespDTO = adminService.register(adminRegisterReqDTO);
        return Results.success(adminRegisterRespDTO);
    }

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<AdminLoginRespDTO> login(@RequestBody AdminLoginReqDTO adminLoginReqDTO) {
        AdminLoginRespDTO adminLoginRespDTO = adminService.login(adminLoginReqDTO);
        return Results.success(adminLoginRespDTO);
    }

    /**
     * 检查管理员是否已登录
     */
    @GetMapping("/check")
    public Result<Boolean> check(HttpServletRequest request) {
        Boolean isLogin = adminService.check(request);
        return Results.success(isLogin);
    }

    /**
     * 管理员登出
     */
    @DeleteMapping("/logout")
    public Result<Boolean> logout(HttpServletRequest request) {
        Boolean isLogout = adminService.logout(request);
        return Results.success(isLogout);
    }

    /**
     * 管理员批量创建班级（通过Excel文件）
     * Excel文件格式：前两列分别为班级名称和专业ID，不包含表头
     * 
     * @param file Excel文件
     * @return 操作结果
     */
    @PostMapping("/create/class/excel")
    public Result<Void> batchCreateClassesFromExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Results.failure("400", "文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !(originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
            return Results.failure("400", "请上传Excel文件（.xlsx或.xls格式）");
        }
          try {
            // 解析Excel文件
            List<ClassCreateReqDTO> classDTOList = new ArrayList<>();
            
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
                
                // 从第一行开始读取数据（没有表头）
                int rowCount = 0;
                for (Row row : sheet) {
                    rowCount++;
                    // 读取班级名称和专业ID
                    String className = row.getCell(0) != null ? row.getCell(0).toString().trim() : null;
                    String majorId = row.getCell(1) != null ? row.getCell(1).toString().trim() : null;
                    
                    // 检查数据有效性
                    if (className == null || className.isEmpty() || majorId == null || majorId.isEmpty()) {
                        return Results.failure("400", "第" + rowCount + "行数据不完整，请检查Excel文件");
                    }
                    
                    ClassCreateReqDTO dto = new ClassCreateReqDTO();
                    dto.setName(className);
                    dto.setMajorId(majorId);
                    classDTOList.add(dto);
                }
            }
            
            if (classDTOList.isEmpty()) {
                return Results.failure("400", "Excel文件中没有有效数据");
            }
            
            // 调用Service方法进行批量创建
            try {
                classService.createBatch(classDTOList);
                // 成功后不返回数据，只返回成功状态
                return Results.success();
            } catch (RuntimeException e) {
                return Results.failure("500", e.getMessage());
            }
            
        } catch (IOException e) {
            return Results.failure("500", "文件读取失败：" + e.getMessage());
        } catch (Exception e) {
            return Results.failure("500", "创建班级失败：" + e.getMessage());
        }
    }    
    
    /**
     * 管理员批量注册教师（通过Excel文件）
     * Excel文件格式：列分别为realName, gender, teacherNo, school, college, username，包含表头
     * 
     * @param file Excel文件
     * @return 操作结果
     */
    @PostMapping("/register/teacher/excel")
    public Result<Integer> batchRegisterTeachersFromExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new Result<Integer>()
                    .setCode("400")
                    .setMessage("文件不能为空")
                    .setData(0);
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !(originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
            return new Result<Integer>()
                    .setCode("400")
                    .setMessage("请上传Excel文件（.xlsx或.xls格式）")
                    .setData(0);
        }
        
        try {
            // 调用Service方法进行批量注册
            Integer count = adminService.batchRegisterTeachers(file);
            return Results.success(count);
        } catch (ClientException e) {
            return new Result<Integer>()
                    .setCode(e.getErrorCode())
                    .setMessage(e.getErrorMessage())
                    .setData(0);
        } catch (Exception e) {
            return new Result<Integer>()
                    .setCode("500")
                    .setMessage("批量注册教师失败：" + e.getMessage())
                    .setData(0);
        }
    }

    /**
     * 管理员批量注册学生（通过Excel文件）
     * Excel文件格式：列分别为studentNo, realName, gender, username, phone, email, majorCode, classCode, college, school，包含表头
     * 
     * @param file Excel文件
     * @return 操作结果
     */
    @PostMapping("/register/student/excel")
    public Result<Integer> batchRegisterStudentsFromExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new Result<Integer>()
                    .setCode("400")
                    .setMessage("文件不能为空")
                    .setData(0);
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !(originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
            return new Result<Integer>()
                    .setCode("400")
                    .setMessage("请上传Excel文件（.xlsx或.xls格式）")
                    .setData(0);
        }
        
        try {
            // 调用Service方法进行批量注册
            Integer count = adminService.batchRegisterStudents(file);
            return Results.success(count);
        } catch (ClientException e) {
            return new Result<Integer>()
                    .setCode(e.getErrorCode())
                    .setMessage(e.getErrorMessage())
                    .setData(0);
        } catch (Exception e) {
            return new Result<Integer>()
                    .setCode("500")                    .setMessage("批量注册学生失败：" + e.getMessage())
                    .setData(0);
        }
    }
    
    /**
     * 管理员批量注册学生并自动处理班级关联（通过Excel文件）
     * Excel文件格式：列分别为studentNo, realName, gender, username, phone, email, majorCode, classCode, college, school, password，包含表头
     * 文件名格式：专业名称_年级级_其他信息.xlsx，例如：计算机科学与技术_22级_20250621213131.xlsx
     * 
     * @param file Excel文件
     * @return 操作结果
     */
    @PostMapping("/register/student/excel/with-class")
    public Result<Integer> batchRegisterStudentsWithClassFromExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new Result<Integer>()
                    .setCode("400")
                    .setMessage("文件不能为空")
                    .setData(0);
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !(originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
            return new Result<Integer>()
                    .setCode("400")
                    .setMessage("请上传Excel文件（.xlsx或.xls格式）")
                    .setData(0);
        }
        
        try {
            // 调用Service方法进行批量注册并处理班级关联
            Integer count = adminService.batchRegisterStudentsWithClass(file);
            return Results.success(count);
        } catch (ClientException e) {
            return new Result<Integer>()
                    .setCode(e.getErrorCode())
                    .setMessage(e.getErrorMessage())
                    .setData(0);
        } catch (Exception e) {
            return new Result<Integer>()
                    .setCode("500")
                    .setMessage("批量注册学生并处理班级关联失败：" + e.getMessage())
                    .setData(0);
        }
    }
    
    /**
     * 管理员将学生添加到指定班级
     * 
     * @param studentId 学生ID
     * @param classId 班级ID
     * @return 操作结果
     */
    @PostMapping("/add-student-to-class")
    public Result<Boolean> addStudentToClass(@RequestParam Long studentId, @RequestParam Long classId) {
        try {
            Boolean success = adminService.addStudentToClass(studentId, classId);
            return Results.success(success);
        } catch (Exception e) {
            return new Result<Boolean>()
                    .setCode("500")
                    .setMessage("添加学生到班级失败：" + e.getMessage())
                    .setData(false);
        }
    }
    
    /**
     * 管理员查询所有课程信息
     * 
     * @return 所有课程信息列表
     */
    @GetMapping("/courses")
    public Result<List<CourseQueryRespDTO>> listAllCourses() {
        try {
            List<CourseQueryRespDTO> courses = courseService.listAllCourses();
            return Results.success(courses);
        } catch (Exception e) {
            return new Result<List<CourseQueryRespDTO>>()
                    .setCode("500")
                    .setMessage("查询课程信息失败：" + e.getMessage())
                    .setData(null);
        }
    }
    
    /**
     * 查询所有班级信息
     */
    @GetMapping("/classes")
    public Result<List<ClassQueryRespDTO>> queryAllClasses() {
        List<ClassQueryRespDTO> classList = adminService.queryAllClasses();
        return Results.success(classList);
    }
    
    /**
     * 根据班级ID查询班级内所有学生信息
     */
    @GetMapping("/class/{classId}/students")
    public Result<List<StudentInfoRespDTO>> queryStudentsByClassId(@PathVariable("classId") Long classId) {
        List<StudentInfoRespDTO> studentList = adminService.queryStudentsByClassId(classId);
        return Results.success(studentList);
    }
    
    /**
     * 管理员批量导入课程（通过Excel文件）
     * Excel文件格式要求：
     * - 包含表头行，至少包含 id, subject_id, name, type 这些必要字段
     * - 支持的可选字段: description, cover_image, start_date, end_date, assessment_method
     * - 日期格式为 yyyy-MM-dd
     *
     * @param file 课程信息Excel文件
     * @return 导入结果
     */
    @PostMapping("/import/courses")
    public Result<CourseImportRespDTO> batchImportCoursesFromExcel(@RequestParam("file") MultipartFile file) {
        CourseImportRespDTO result = adminService.batchImportCourses(file);
        return Results.success(result);
    }
      /**
     * 管理员为课程分配教师
     * 
     * 功能说明：
     * - 管理员可以将指定的教师分配给指定的课程
     * - 如果该教师已经分配到此课程，则返回现有的分配信息
     * - 支持一个课程分配多个教师的场景
     * 
     * @param requestParam 课程教师分配请求参数，包含课程ID和教师ID
     * @return 分配结果，包含分配记录ID、课程信息、教师信息等
     */
    @PostMapping("/assign-teacher-to-course")
    public Result<CourseTeacherAssignRespDTO> assignTeacherToCourse(@RequestBody CourseTeacherAssignReqDTO requestParam) {
        log.info("管理员为课程分配教师请求: {}", requestParam);
        CourseTeacherAssignRespDTO result = adminService.assignTeacherToCourse(requestParam);
        return Results.success(result);
    }
    
    /**
     * 管理员分页查询教师信息
     * 
     * 功能说明：
     * - 支持按教师姓名、用户名、工号、性别、手机号、学校、学院等条件查询
     * - 支持分页查询和排序
     * - 支持 GET 和 POST 两种请求方式
     * 
     * @param requestParam 教师分页查询请求参数
     * @return 分页查询结果，包含教师列表和分页信息
     */
    @PostMapping("/page/teacher")
    public Result<TeacherPageQueryRespDTO> pageQueryTeachers(@RequestBody TeacherPageQueryReqDTO requestParam) {
        log.info("管理员分页查询教师请求: {}", requestParam);
        TeacherPageQueryRespDTO result = teacherService.pageQueryTeachers(requestParam);
        return Results.success(result);
    }
      /**
     * 管理员分页查询教师信息（GET方式）
     * 
     * 功能说明：
     * - 支持按教师姓名、用户名、工号、性别、手机号、学校、学院等条件查询
     * - 支持分页查询和排序
     * - 使用 GET 请求方式，参数通过 URL 查询参数传递
     * 
     * @param current 当前页码
     * @param size 每页显示条数
     * @param realName 教师姓名（模糊查询）
     * @param username 用户名（模糊查询）
     * @param teacherNo 教师工号（精确查询）
     * @param gender 性别（0：女，1：男）
     * @param phone 手机号（模糊查询）
     * @param school 学校（模糊查询）
     * @param college 学院（模糊查询）
     * @param sortBy 排序字段
     * @param sortOrder 排序方式
     * @return 分页查询结果，包含教师列表和分页信息
     */
    @GetMapping("/page/teacher")
    public Result<TeacherPageQueryRespDTO> pageQueryTeachersGet(
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String teacherNo,
            @RequestParam(required = false) Integer gender,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String school,
            @RequestParam(required = false) String college,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        
        TeacherPageQueryReqDTO requestParam = new TeacherPageQueryReqDTO();
        
        // 设置参数，如果为null则使用默认值
        requestParam.setCurrent(current != null ? current : 1);
        requestParam.setSize(size != null ? size : 10);
        requestParam.setRealName(realName);
        requestParam.setUsername(username);
        requestParam.setTeacherNo(teacherNo);
        requestParam.setGender(gender);
        requestParam.setPhone(phone);
        requestParam.setSchool(school);
        requestParam.setCollege(college);
        requestParam.setSortBy(sortBy != null ? sortBy : "create_time");
        requestParam.setSortOrder(sortOrder != null ? sortOrder : "desc");
        
        log.info("=== GET 请求收到教师分页查询 ===");
        log.info("收到教师分页查询GET请求: {}", requestParam);
        log.info("GET 参数详情: current={}, size={}, realName={}, username={}, teacherNo={}, sortBy={}, sortOrder={}", 
                requestParam.getCurrent(), requestParam.getSize(), requestParam.getRealName(),
                requestParam.getUsername(), requestParam.getTeacherNo(), requestParam.getSortBy(), requestParam.getSortOrder());
        
        TeacherPageQueryRespDTO result = teacherService.pageQueryTeachers(requestParam);
        return Results.success(result);
    }
    
    /**
     * 管理员按班级名称批量添加学生到课程
     * 
     * 功能说明：
     * - 根据班级名称查找该班级的所有学生
     * - 批量将学生添加到指定课程中
     * - 避免重复选课，已选课的学生不会重复添加
     * - 同时更新课程-班级关联表
     * - 返回详细的选课结果统计
     * 
     * @param requestParam 班级选课请求参数，包含班级名称和课程ID
     * @return 选课结果，包含成功选课学生数量、已选学生数量等详细信息
     */
    @PostMapping("/enroll-class-to-course")
    public Result<ClassEnrollCourseRespDTO> enrollClassToCourse(@RequestBody ClassEnrollCourseReqDTO requestParam) {
        log.info("管理员班级选课请求: {}", requestParam);
        ClassEnrollCourseRespDTO result = adminService.enrollClassToCourse(requestParam);
        return Results.success(result);
    }
}

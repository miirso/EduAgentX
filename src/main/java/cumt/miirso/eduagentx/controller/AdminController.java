package cumt.miirso.eduagentx.controller;

import cumt.miirso.eduagentx.convention.exception.ClientException;
import cumt.miirso.eduagentx.convention.result.Result;
import cumt.miirso.eduagentx.convention.result.Results;
import cumt.miirso.eduagentx.dto.req.AdminLoginReqDTO;
import cumt.miirso.eduagentx.dto.req.AdminRegisterReqDTO;
import cumt.miirso.eduagentx.dto.req.ClassCreateReqDTO;
import cumt.miirso.eduagentx.dto.resp.AdminLoginRespDTO;
import cumt.miirso.eduagentx.dto.resp.AdminRegisterRespDTO;
import cumt.miirso.eduagentx.dto.resp.ClassQueryRespDTO;
import cumt.miirso.eduagentx.dto.resp.CourseImportRespDTO;
import cumt.miirso.eduagentx.dto.resp.CourseQueryRespDTO;
import cumt.miirso.eduagentx.dto.resp.StudentInfoRespDTO;
import cumt.miirso.eduagentx.service.AdminService;
import cumt.miirso.eduagentx.service.ClassService;
import cumt.miirso.eduagentx.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
public class AdminController {

    private final AdminService adminService;
    private final ClassService classService;
    private final CourseService courseService;

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
    }    /**
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
}

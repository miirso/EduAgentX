package cumt.miirso.eduagentx.controller;

import cumt.miirso.eduagentx.convention.result.Result;
import cumt.miirso.eduagentx.convention.result.Results;
import cumt.miirso.eduagentx.dto.req.PasswordUpdateReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherLoginReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherRegisterReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherUpdateReqDTO;
import cumt.miirso.eduagentx.dto.resp.TeacherLoginRespDTO;
import cumt.miirso.eduagentx.dto.resp.TeacherRegisterRespDTO;
import cumt.miirso.eduagentx.entity.TeacherDO;
import cumt.miirso.eduagentx.service.TeacherService;
import cumt.miirso.eduagentx.dto.req.ClassCreateReqDTO;
import cumt.miirso.eduagentx.service.ClassService;
import cumt.miirso.eduagentx.dto.req.CourseCreateReqDTO;
import cumt.miirso.eduagentx.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Package cumt.miirso.eduagentx.controller
 * @Author miirso
 * @Date 2025/5/29 22:20
 */

@RestController
@RequestMapping("/api/eduagentx/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final ClassService classService;
    private final CourseService courseService;

    /**
     * 教师注册
     */
    @PostMapping("/register")
    public Result<TeacherRegisterRespDTO> register(@RequestBody TeacherRegisterReqDTO teacherRegisterReqDTO) {
        TeacherRegisterRespDTO teacherRegisterRespDTO = teacherService.register(teacherRegisterReqDTO);
        return Results.success(teacherRegisterRespDTO);
    }

    /**
     * 教师登录
     */
    @PostMapping("/login")
    public Result<TeacherLoginRespDTO> login(@RequestBody TeacherLoginReqDTO teacherLoginReqDTO) {
        TeacherLoginRespDTO teacherLoginRespDTO = teacherService.login(teacherLoginReqDTO);
        return Results.success(teacherLoginRespDTO);
    }

    /**
     * 检查教师是否已登录
     */
    @GetMapping("/check")
    public Result<Boolean> check(HttpServletRequest request) {
        Boolean isLogin = teacherService.check(request);
        return Results.success(isLogin);
    }

    /**
     * 教师登出
     */
    @DeleteMapping("/logout")
    public Result<Boolean> logout(HttpServletRequest request) {
        Boolean isLogout = teacherService.logout(request);
        return Results.success(isLogout);
    }

    /**
     * 教师创建班级
     * @param requestParam 创建班级请求参数
     * @return Result<Void>
     */
    @PostMapping("/create/class")
    public Result<Void> createClass(@RequestBody ClassCreateReqDTO requestParam) {
        classService.create(requestParam);
        return Results.success();
    }    /**
     * 教师创建课程
     * @param requestParam 创建课程请求参数
     * @param request HTTP请求（用于获取当前登录教师信息）
     * @return Result<Void>
     */
    @PostMapping("/create/course")
    public Result<Void> createCourse(@RequestBody CourseCreateReqDTO requestParam, HttpServletRequest request) {
        courseService.createCourse(requestParam, request);
        return Results.success();
    }    /**
     * 根据用户名获取教师信息
     * @param username 用户名
     * @return Result<TeacherDO>
     */
    @GetMapping("/info")
    public Result<TeacherDO> getTeacherInfo(@RequestParam String username) {
        TeacherDO teacherInfo = teacherService.getTeacherInfo(username);
        return Results.success(teacherInfo);
    }

    /**
     * 更新教师个人资料
     */
    @PutMapping("/profile")
    public Result<Boolean> updateProfile(@RequestBody TeacherUpdateReqDTO teacherUpdateReqDTO, HttpServletRequest request) {
        Boolean success = teacherService.updateProfile(teacherUpdateReqDTO, request);
        return Results.success(success);
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Boolean> updatePassword(@RequestBody PasswordUpdateReqDTO passwordUpdateReqDTO, HttpServletRequest request) {
        Boolean success = teacherService.updatePassword(passwordUpdateReqDTO, request);
        return Results.success(success);
    }

    /**
     * 教师为课程分配章节
     * 
     * 功能说明：
     * - 校验当前登录用户是否为该课程的教师
     * - 如果是，允许为课程分配章节信息
     * - 支持批量插入章节到数据库
     * 
     * @param requestParam 章节分配请求参数，包含课程ID和章节列表
     * @param request HTTP请求（用于获取当前登录教师信息）
     * @return 章节分配结果
     */
    @PostMapping("/assign-chapters")
    public Result<cumt.miirso.eduagentx.dto.resp.TeacherAssignChaptersRespDTO> assignChaptersToCourse(
            @RequestBody cumt.miirso.eduagentx.dto.req.TeacherAssignChaptersReqDTO requestParam, 
            HttpServletRequest request) {
        
        cumt.miirso.eduagentx.dto.resp.TeacherAssignChaptersRespDTO result = 
                teacherService.assignChaptersToCourse(requestParam, request);
        return Results.success(result);
    }

    /**
     * 教师删除课程章节
     * 
     * 功能说明：
     * - 校验当前登录用户是否为该课程的教师
     * - 根据章节顺序号删除指定章节
     * - 物理删除章节数据
     * 
     * @param requestParam 删除章节请求参数，包含课程ID和章节顺序号
     * @param request HTTP请求（用于获取当前登录教师信息）
     * @return 删除章节结果
     */
    @DeleteMapping("/delete-chapter")
    public Result<cumt.miirso.eduagentx.dto.resp.TeacherDeleteChapterRespDTO> deleteChapterFromCourse(
            @RequestBody cumt.miirso.eduagentx.dto.req.TeacherDeleteChapterReqDTO requestParam, 
            HttpServletRequest request) {
        
        cumt.miirso.eduagentx.dto.resp.TeacherDeleteChapterRespDTO result = 
                teacherService.deleteChapterFromCourse(requestParam, request);
        return Results.success(result);
    }

    /**
     * 教师修改课程章节
     * 
     * 功能说明：
     * - 校验当前登录用户是否为该课程的教师
     * - 根据原章节顺序号修改章节信息
     * - 支持修改章节标题、内容、顺序号
     * 
     * @param requestParam 修改章节请求参数，包含课程ID、原章节顺序号和新的章节信息
     * @param request HTTP请求（用于获取当前登录教师信息）
     * @return 修改章节结果
     */
    @PutMapping("/update-chapter")
    public Result<cumt.miirso.eduagentx.dto.resp.TeacherUpdateChapterRespDTO> updateChapterOfCourse(
            @RequestBody cumt.miirso.eduagentx.dto.req.TeacherUpdateChapterReqDTO requestParam, 
            HttpServletRequest request) {
        
        cumt.miirso.eduagentx.dto.resp.TeacherUpdateChapterRespDTO result = 
                teacherService.updateChapterOfCourse(requestParam, request);
        return Results.success(result);
    }

    /**
     * 教师查询课程章节列表
     * 
     * 功能说明：
     * - 校验当前登录用户是否为该课程的教师
     * - 查询指定课程的所有章节信息
     * - 按章节顺序号升序排列
     * 
     * @param requestParam 查询章节请求参数，包含课程ID
     * @param request HTTP请求（用于获取当前登录教师信息）
     * @return 章节列表结果
     */
    @PostMapping("/query-chapters")
    public Result<cumt.miirso.eduagentx.dto.resp.TeacherQueryChapterRespDTO> queryChaptersOfCourse(
            @RequestBody cumt.miirso.eduagentx.dto.req.TeacherQueryChapterReqDTO requestParam, 
            HttpServletRequest request) {
        
        cumt.miirso.eduagentx.dto.resp.TeacherQueryChapterRespDTO result = 
                teacherService.queryChaptersOfCourse(requestParam, request);
        return Results.success(result);
    }

    /**
     * 教师新增课程章节
     * 
     * 功能说明：
     * - 校验当前登录用户是否为该课程的教师
     * - 验证章节顺序号是否已被使用
     * - 创建新章节并保存到数据库
     * 
     * @param requestParam 新增章节请求参数，包含课程ID、章节标题、内容和顺序号
     * @param request HTTP请求（用于获取当前登录教师信息）
     * @return 新增章节结果
     */
    @PostMapping("/add-chapter")
    public Result<cumt.miirso.eduagentx.dto.resp.TeacherAddChapterRespDTO> addChapterToCourse(
            @RequestBody cumt.miirso.eduagentx.dto.req.TeacherAddChapterReqDTO requestParam, 
            HttpServletRequest request) {
        
        cumt.miirso.eduagentx.dto.resp.TeacherAddChapterRespDTO result = 
                teacherService.addChapterToCourse(requestParam, request);
        return Results.success(result);
    }
}

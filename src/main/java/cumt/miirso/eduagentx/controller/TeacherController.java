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
}

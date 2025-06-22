package cumt.miirso.eduagentx.controller;

import cumt.miirso.eduagentx.convention.result.Result;
import cumt.miirso.eduagentx.convention.result.Results;
import cumt.miirso.eduagentx.dto.req.PasswordUpdateReqDTO;
import cumt.miirso.eduagentx.dto.req.StudentLoginReqDTO;
import cumt.miirso.eduagentx.dto.req.StudentRegisterReqDTO;
import cumt.miirso.eduagentx.dto.req.StudentUpdateReqDTO;
import cumt.miirso.eduagentx.dto.resp.StudentLoginRespDTO;
import cumt.miirso.eduagentx.dto.resp.StudentRegisterRespDTO;
import cumt.miirso.eduagentx.entity.StudentDO;
import cumt.miirso.eduagentx.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Package cumt.miirso.eduagentx.controller
 * @Author miirso
 * @Date 2025/5/29 22:16
 */

@RestController
@RequestMapping("/api/eduagentx/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * 学生注册
     */
    @PostMapping("/register")
    public Result<StudentRegisterRespDTO> register(@RequestBody StudentRegisterReqDTO studentRegisterReqDTO) {
        StudentRegisterRespDTO studentRegisterRespDTO = studentService.register(studentRegisterReqDTO);
        return Results.success(studentRegisterRespDTO);
    }

    /**
     * 学生登录
     */
    @PostMapping("/login")
    public Result<StudentLoginRespDTO> login(@RequestBody StudentLoginReqDTO studentLoginReqDTO) {
        StudentLoginRespDTO studentLoginRespDTO = studentService.login(studentLoginReqDTO);
        return Results.success(studentLoginRespDTO);
    }

    /**
     * 检查学生是否已登录
     */
    @GetMapping("/check")
    public Result<Boolean> check(HttpServletRequest request) {
        Boolean isLogin = studentService.check(request);
        return Results.success(isLogin);
    }

    /**
     * 学生登出
     */
    @DeleteMapping("/logout")
    public Result<Boolean> logout(HttpServletRequest request) {
        Boolean isLogout = studentService.logout(request);
        return Results.success(isLogout);
    }

    /**
     * 更新学生个人资料
     */
    @PutMapping("/profile")
    public Result<Boolean> updateProfile(@RequestBody StudentUpdateReqDTO studentUpdateReqDTO, HttpServletRequest request) {
        Boolean success = studentService.updateProfile(studentUpdateReqDTO, request);
        return Results.success(success);
    }
      /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Boolean> updatePassword(@RequestBody PasswordUpdateReqDTO passwordUpdateReqDTO, HttpServletRequest request) {
        Boolean success = studentService.updatePassword(passwordUpdateReqDTO, request);
        return Results.success(success);
    }
    
    /**
     * 根据用户名获取学生信息
     * @param username 用户名
     * @return Result<StudentDO>
     */
    @GetMapping("/info")
    public Result<StudentDO> getStudentInfo(@RequestParam String username) {
        StudentDO studentInfo = studentService.getStudentInfo(username);
        return Results.success(studentInfo);
    }
}

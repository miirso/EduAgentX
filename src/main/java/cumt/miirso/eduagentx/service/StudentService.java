package cumt.miirso.eduagentx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cumt.miirso.eduagentx.dto.req.PasswordUpdateReqDTO;
import cumt.miirso.eduagentx.dto.req.StudentLoginReqDTO;
import cumt.miirso.eduagentx.dto.req.StudentRegisterReqDTO;
import cumt.miirso.eduagentx.dto.req.StudentUpdateReqDTO;
import cumt.miirso.eduagentx.dto.resp.StudentLoginRespDTO;
import cumt.miirso.eduagentx.dto.resp.StudentRegisterRespDTO;
import cumt.miirso.eduagentx.entity.ClassDO;
import cumt.miirso.eduagentx.entity.StudentDO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @Package cumt.miirso.eduagentx.service
 * @Author miirso
 * @Date 2025/5/29 22:16
 */

public interface StudentService extends IService<StudentDO> {
    StudentRegisterRespDTO register(StudentRegisterReqDTO studentRegisterReqDTO);

    StudentLoginRespDTO login(StudentLoginReqDTO studentLoginReqDTO);

    Boolean check(HttpServletRequest request);

    Boolean logout(HttpServletRequest request);
    
    /**
     * 更新学生个人资料
     *
     * @param studentUpdateReqDTO 学生更新信息请求
     * @param request HTTP请求
     * @return 是否更新成功
     */
    Boolean updateProfile(StudentUpdateReqDTO studentUpdateReqDTO, HttpServletRequest request);
      /**
     * 修改密码
     *
     * @param passwordUpdateReqDTO 密码更新请求
     * @param request HTTP请求
     * @return 是否修改成功
     */
    Boolean updatePassword(PasswordUpdateReqDTO passwordUpdateReqDTO, HttpServletRequest request);
    
    /**
     * 根据用户名获取学生信息
     *
     * @param username 用户名
     * @return 学生信息
     */
    StudentDO getStudentInfo(String username);
    
    /**
     * 注册学生并关联班级
     * 如果班级不存在，将自动创建
     *
     * @param studentRegisterReqDTO 学生注册信息
     * @param classInfo 班级信息（可选）
     * @return 注册结果
     */
    StudentRegisterRespDTO registerWithClass(StudentRegisterReqDTO studentRegisterReqDTO, ClassDO classInfo);
}

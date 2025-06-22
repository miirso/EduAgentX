package cumt.miirso.eduagentx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cumt.miirso.eduagentx.dto.req.PasswordUpdateReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherLoginReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherRegisterReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherUpdateReqDTO;
import cumt.miirso.eduagentx.dto.resp.TeacherLoginRespDTO;
import cumt.miirso.eduagentx.dto.resp.TeacherRegisterRespDTO;
import cumt.miirso.eduagentx.entity.TeacherDO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @Package cumt.miirso.eduagentx.service
 * @Author miirso
 * @Date 2025/5/29 22:17
 */

public interface TeacherService extends IService<TeacherDO> {

    /**
     * 教师注册
     *
     * @param teacherRegisterReqDTO 教师注册请求
     * @return 教师注册响应
     */
    TeacherRegisterRespDTO register(TeacherRegisterReqDTO teacherRegisterReqDTO);

    /**
     * 教师登录
     *
     * @param teacherLoginReqDTO 教师登录请求
     * @return 教师登录响应
     */
    TeacherLoginRespDTO login(TeacherLoginReqDTO teacherLoginReqDTO);

    /**
     * 检查教师是否已登录
     *
     * @param request HTTP请求
     * @return 是否已登录
     */
    Boolean check(HttpServletRequest request);    /**
     * 教师登出
     *
     * @param request HTTP请求
     * @return 是否成功登出
     */
    Boolean logout(HttpServletRequest request);
    
    /**
     * 更新教师个人资料
     *
     * @param teacherUpdateReqDTO 教师更新信息请求
     * @param request HTTP请求
     * @return 是否更新成功
     */
    Boolean updateProfile(TeacherUpdateReqDTO teacherUpdateReqDTO, HttpServletRequest request);
      /**
     * 修改密码
     *
     * @param passwordUpdateReqDTO 密码更新请求
     * @param request HTTP请求
     * @return 是否修改成功
     */
    Boolean updatePassword(PasswordUpdateReqDTO passwordUpdateReqDTO, HttpServletRequest request);
    
    /**
     * 根据用户名获取教师信息
     *
     * @param username 用户名
     * @return 教师信息
     */
    TeacherDO getTeacherInfo(String username);
}

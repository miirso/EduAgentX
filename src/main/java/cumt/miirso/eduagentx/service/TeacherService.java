package cumt.miirso.eduagentx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cumt.miirso.eduagentx.dto.req.PasswordUpdateReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherLoginReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherPageQueryReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherRegisterReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherUpdateReqDTO;
import cumt.miirso.eduagentx.dto.resp.TeacherLoginRespDTO;
import cumt.miirso.eduagentx.dto.resp.TeacherPageQueryRespDTO;
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

    /**
     * 教师分页查询
     * 
     * 支持多种查询条件：
     * - 按教师姓名模糊查询
     * - 按用户名模糊查询
     * - 按教师工号精确查询
     * - 按性别筛选
     * - 按手机号模糊查询
     * - 按学校模糊查询
     * - 按学院模糊查询
     * 
     * 支持灵活排序：
     * - 按创建时间排序（默认）
     * - 按教师姓名排序
     * - 按教师工号排序
     * - 支持升序/降序
     * 
     * @param requestParam 分页查询请求参数
     * @return 分页查询结果
     */
    TeacherPageQueryRespDTO pageQueryTeachers(TeacherPageQueryReqDTO requestParam);
}

package cumt.miirso.eduagentx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cumt.miirso.eduagentx.dto.req.AdminLoginReqDTO;
import cumt.miirso.eduagentx.dto.req.AdminRegisterReqDTO;
import cumt.miirso.eduagentx.dto.resp.AdminLoginRespDTO;
import cumt.miirso.eduagentx.dto.resp.AdminRegisterRespDTO;
import cumt.miirso.eduagentx.dto.resp.ClassQueryRespDTO;
import cumt.miirso.eduagentx.dto.resp.StudentInfoRespDTO;
import cumt.miirso.eduagentx.entity.AdminDO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Package cumt.miirso.eduagentx.service
 * @Author miirso
 * @Date 2025/6/20
 */

public interface AdminService extends IService<AdminDO> {

    /**
     * 管理员注册
     *
     * @param adminRegisterReqDTO 管理员注册请求
     * @return 管理员注册响应
     */
    AdminRegisterRespDTO register(AdminRegisterReqDTO adminRegisterReqDTO);

    /**
     * 管理员登录
     *
     * @param adminLoginReqDTO 管理员登录请求
     * @return 管理员登录响应
     */
    AdminLoginRespDTO login(AdminLoginReqDTO adminLoginReqDTO);

    /**
     * 检查管理员是否已登录
     *
     * @param request HTTP请求
     * @return 是否已登录
     */
    Boolean check(HttpServletRequest request);

    /**
     * 管理员登出
     *
     * @param request HTTP请求
     * @return 是否成功登出
     */
    Boolean logout(HttpServletRequest request);
      /**
     * 批量注册教师（通过Excel文件）
     *
     * @param file Excel文件，包含教师信息
     * @return 成功导入的教师数量
     */
    Integer batchRegisterTeachers(MultipartFile file);
      /**
     * 批量注册学生（通过Excel文件）
     *
     * @param file Excel文件，包含学生信息
     * @return 成功导入的学生数量
     */
    Integer batchRegisterStudents(MultipartFile file);
    
    /**
     * 批量注册学生并处理班级关联（通过Excel文件）
     * 支持从文件名中提取班级信息
     *
     * @param file Excel文件
     * @return 成功注册的学生数量
     */
    Integer batchRegisterStudentsWithClass(MultipartFile file);
    
    /**
     * 管理员将学生添加到指定班级
     *
     * @param studentId 学生ID
     * @param classId 班级ID
     * @return 是否添加成功
     */
    Boolean addStudentToClass(Long studentId, Long classId);

    /**
     * 查询所有班级信息
     *
     * @return 班级信息列表
     */
    List<ClassQueryRespDTO> queryAllClasses();
    
    /**
     * 根据班级ID查询班级内所有学生信息
     *
     * @param classId 班级ID
     * @return 学生信息列表
     */
    List<StudentInfoRespDTO> queryStudentsByClassId(Long classId);
    
    /**
     * 批量导入课程（通过Excel文件）
     *
     * @param file Excel文件，包含课程信息
     * @return 课程导入结果
     */
    cumt.miirso.eduagentx.dto.resp.CourseImportRespDTO batchImportCourses(org.springframework.web.multipart.MultipartFile file);
    
    /**
     * 管理员为课程分配教师
     *
     * @param requestParam 课程教师分配请求参数
     * @return 分配结果
     */
    cumt.miirso.eduagentx.dto.resp.CourseTeacherAssignRespDTO assignTeacherToCourse(cumt.miirso.eduagentx.dto.req.CourseTeacherAssignReqDTO requestParam);
    
    /**
     * 管理员按班级名称批量添加学生到课程
     * 
     * 功能说明：
     * - 根据班级名称查找该班级的所有学生
     * - 批量将学生添加到指定课程中
     * - 避免重复选课，已选课的学生不会重复添加
     * - 同时更新课程-班级关联表
     * 
     * @param requestParam 班级选课请求参数，包含班级名称和课程ID
     * @return 选课结果，包含成功选课学生数量、已选学生数量等详细信息
     */
    cumt.miirso.eduagentx.dto.resp.ClassEnrollCourseRespDTO enrollClassToCourse(cumt.miirso.eduagentx.dto.req.ClassEnrollCourseReqDTO requestParam);
}

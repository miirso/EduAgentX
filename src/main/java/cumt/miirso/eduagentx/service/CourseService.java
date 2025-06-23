package cumt.miirso.eduagentx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cumt.miirso.eduagentx.dto.req.CourseCreateReqDTO;
import cumt.miirso.eduagentx.dto.req.CoursePageQueryReqDTO;
import cumt.miirso.eduagentx.dto.resp.CourseDetailRespDTO;
import cumt.miirso.eduagentx.dto.resp.CoursePageQueryRespDTO;
import cumt.miirso.eduagentx.dto.resp.CourseQueryRespDTO;
import cumt.miirso.eduagentx.entity.CourseDO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService extends IService<CourseDO> {
    
    /**
     * 创建课程
     * @param requestParam 创建课程请求参数
     * @param request HTTP请求（用于获取当前登录教师信息）
     */
    void createCourse(CourseCreateReqDTO requestParam, HttpServletRequest request);
    
    /**
     * 查询所有课程信息
     * @return 课程信息列表
     */
    List<CourseQueryRespDTO> listAllCourses();
    
    /**
     * 分页查询课程信息
     * @param requestParam 分页查询请求参数
     * @return 分页查询结果
     */
    CoursePageQueryRespDTO pageQueryCourses(CoursePageQueryReqDTO requestParam);
    
    /**
     * 根据课程ID查询课程详细信息
     * 
     * 功能说明：
     * - 查询课程基本信息
     * - 查询分配的教师列表
     * - 查询关联的班级列表
     * - 统计选课学生数量
     * 
     * @param courseId 课程ID
     * @return 课程详细信息
     */
    CourseDetailRespDTO getCourseDetailById(String courseId);
}

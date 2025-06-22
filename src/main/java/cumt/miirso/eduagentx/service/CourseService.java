package cumt.miirso.eduagentx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cumt.miirso.eduagentx.dto.req.CourseCreateReqDTO;
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
}

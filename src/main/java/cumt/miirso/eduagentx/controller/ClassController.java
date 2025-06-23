package cumt.miirso.eduagentx.controller;

import cumt.miirso.eduagentx.convention.result.Result;
import cumt.miirso.eduagentx.convention.result.Results;
import cumt.miirso.eduagentx.dto.resp.CourseDetailRespDTO;
import cumt.miirso.eduagentx.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 班级控制器
 * 
 * @author EduAgentX
 * @date 2025/6/23
 */
@RestController
@RequestMapping("/api/eduagentx/class")
@RequiredArgsConstructor
@Slf4j
public class ClassController {

    private final CourseService courseService;

    /**
     * 根据课程ID查询课程详细信息
     * 
     * 功能说明：
     * - 查询课程基本信息（名称、描述、时间等）
     * - 查询分配给该课程的所有教师
     * - 查询选择该课程的所有班级
     * - 统计选课学生数量
     * - 提供完整的课程概览信息
     * 
     * @param courseId 课程ID
     * @return 课程详细信息，包含基本信息、教师列表、班级列表、统计数据
     */
    @GetMapping("/course/{courseId}/detail")
    public Result<CourseDetailRespDTO> getCourseDetail(@PathVariable("courseId") String courseId) {
        log.info("=== 收到查询课程详细信息请求 ===");
        log.info("课程ID: {}", courseId);
        
        try {
            CourseDetailRespDTO result = courseService.getCourseDetailById(courseId);
            
            log.info("=== 课程详细信息查询成功 ===");
            log.info("课程名称: {}, 选课学生数: {}, 分配教师数: {}, 关联班级数: {}", 
                    result.getName(), 
                    result.getEnrollmentStats().getTotalEnrollments(),
                    result.getEnrollmentStats().getTotalTeachers(),
                    result.getEnrollmentStats().getTotalClasses());
              return Results.success(result);
        } catch (RuntimeException e) {
            log.error("查询课程详细信息失败: {}", e.getMessage());
            return new Result<CourseDetailRespDTO>()
                    .setCode("404")
                    .setMessage(e.getMessage())
                    .setData(null);
        } catch (Exception e) {
            log.error("查询课程详细信息出现异常: ", e);
            return new Result<CourseDetailRespDTO>()
                    .setCode("500")
                    .setMessage("查询课程详细信息失败：" + e.getMessage())
                    .setData(null);
        }
    }
}

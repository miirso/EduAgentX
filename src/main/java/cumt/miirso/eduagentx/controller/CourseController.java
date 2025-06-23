package cumt.miirso.eduagentx.controller;

import cumt.miirso.eduagentx.convention.result.Result;
import cumt.miirso.eduagentx.convention.result.Results;
import cumt.miirso.eduagentx.dto.req.CoursePageQueryReqDTO;
import cumt.miirso.eduagentx.dto.resp.CoursePageQueryRespDTO;
import cumt.miirso.eduagentx.dto.resp.CourseQueryRespDTO;
import cumt.miirso.eduagentx.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程控制器
 * 
 * @author miirso
 * @date 2025/6/23
 */
@RestController
@RequestMapping("/api/eduagentx/course")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;    /**
     * 分页查询课程信息
     * 
     * @param requestParam 分页查询请求参数
     * @return 分页查询结果
     */    @PostMapping("/page")
    public Result<CoursePageQueryRespDTO> pageQueryCourses(@RequestBody CoursePageQueryReqDTO requestParam) {
        log.info("=== Controller 收到课程分页查询请求 ===");
        log.info("请求参数: {}", requestParam);
        log.info("参数详情: current={}, size={}, subjectId={}, sortBy={}, sortOrder={}", 
                requestParam.getCurrent(), requestParam.getSize(), requestParam.getSubjectId(),
                requestParam.getSortBy(), requestParam.getSortOrder());
        
        CoursePageQueryRespDTO result = courseService.pageQueryCourses(requestParam);
        
        log.info("=== Controller 返回结果 ===");
        log.info("返回参数: current={}, size={}, total={}, pages={}, 实际记录数={}", 
                result.getCurrent(), result.getSize(), result.getTotal(), result.getPages(),
                result.getRecords() != null ? result.getRecords().size() : 0);
        
        return Results.success(result);
    }

    /**
     * 分页查询课程信息（GET方式，支持URL参数）
     * 
     * @param current 当前页码
     * @param size 每页显示条数
     * @param name 课程名称（模糊查询）
     * @param type 课程类型
     * @param subjectId 学科ID
     * @param assessmentMethod 考核方式
     * @param sortBy 排序字段
     * @param sortOrder 排序方式
     * @return 分页查询结果
     */
    @GetMapping("/page")
    public Result<CoursePageQueryRespDTO> pageQueryCoursesGet(
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer subjectId,
            @RequestParam(required = false) String assessmentMethod,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        
        CoursePageQueryReqDTO requestParam = new CoursePageQueryReqDTO();
        
        // 设置参数，如果为null则使用默认值
        requestParam.setCurrent(current != null ? current : 1);
        requestParam.setSize(size != null ? size : 10);
        requestParam.setName(name);
        requestParam.setType(type);
        requestParam.setSubjectId(subjectId);
        requestParam.setAssessmentMethod(assessmentMethod);
        requestParam.setSortBy(sortBy != null ? sortBy : "create_time");
        requestParam.setSortOrder(sortOrder != null ? sortOrder : "desc");
        
        log.info("=== GET 请求收到课程分页查询 ===");
        log.info("收到课程分页查询GET请求: {}", requestParam);
        log.info("GET 参数详情: current={}, size={}, subjectId={}, sortBy={}, sortOrder={}", 
                requestParam.getCurrent(), requestParam.getSize(), requestParam.getSubjectId(),
                requestParam.getSortBy(), requestParam.getSortOrder());
        
        CoursePageQueryRespDTO result = courseService.pageQueryCourses(requestParam);
        return Results.success(result);
    }

    /**
     * 查询所有课程信息（原有接口保持兼容）
     * 
     * @return 所有课程信息列表
     */
    @GetMapping("/list")
    public Result<List<CourseQueryRespDTO>> listAllCourses() {
        log.info("收到查询所有课程请求");
        List<CourseQueryRespDTO> result = courseService.listAllCourses();
        return Results.success(result);
    }
}

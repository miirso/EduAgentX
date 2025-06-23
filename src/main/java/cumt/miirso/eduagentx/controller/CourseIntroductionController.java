package cumt.miirso.eduagentx.controller;

import cumt.miirso.eduagentx.convention.result.Result;
import cumt.miirso.eduagentx.convention.result.Results;
import cumt.miirso.eduagentx.dto.req.QueryIntroductionReqDTO;
import cumt.miirso.eduagentx.dto.req.SaveIntroductionReqDTO;
import cumt.miirso.eduagentx.dto.resp.QueryIntroductionRespDTO;
import cumt.miirso.eduagentx.dto.resp.SaveIntroductionRespDTO;
import cumt.miirso.eduagentx.service.CourseIntroductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 课程简介控制器
 * @author miirso
 */
@RestController
@RequestMapping("/api/eduagentx/introduction")
@RequiredArgsConstructor
public class CourseIntroductionController {

    private final CourseIntroductionService courseIntroductionService;

    /**
     * 保存或更新课程简介
     * 
     * 功能说明：
     * - 支持新增和更新课程简介
     * - 自动判断是新增还是更新操作
     * - 支持纯文本和HTML格式的简介内容
     * - 支持课程关键词、学习目标等详细信息
     * 
     * @param requestParam 简介保存请求
     * @return 保存结果
     */
    @PostMapping("/save")
    public Result<SaveIntroductionRespDTO> saveIntroduction(@RequestBody SaveIntroductionReqDTO requestParam) {
        SaveIntroductionRespDTO result = courseIntroductionService.saveOrUpdateIntroduction(requestParam);
        return Results.success(result);
    }

    /**
     * 查询课程简介（POST方式）
     * 
     * @param requestParam 查询请求参数
     * @return 课程简介信息
     */
    @PostMapping("/query")
    public Result<QueryIntroductionRespDTO> queryIntroduction(@RequestBody QueryIntroductionReqDTO requestParam) {
        QueryIntroductionRespDTO result = courseIntroductionService.queryIntroduction(requestParam);
        return Results.success(result);
    }

    /**
     * 查询课程简介（GET方式）
     * 
     * @param courseId 课程ID
     * @return 课程简介信息
     */
    @GetMapping("/query/{courseId}")
    public Result<QueryIntroductionRespDTO> queryIntroductionByGet(@PathVariable("courseId") String courseId) {
        QueryIntroductionReqDTO requestParam = new QueryIntroductionReqDTO();
        requestParam.setCourseId(courseId);
        
        QueryIntroductionRespDTO result = courseIntroductionService.queryIntroduction(requestParam);
        return Results.success(result);
    }

    /**
     * 删除课程简介
     * 
     * @param courseId 课程ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{courseId}")
    public Result<Boolean> deleteIntroduction(@PathVariable("courseId") String courseId) {
        Boolean result = courseIntroductionService.deleteIntroduction(courseId);
        return Results.success(result);
    }
}

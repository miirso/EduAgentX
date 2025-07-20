package cumt.miirso.eduagentx.controller;

import cumt.miirso.eduagentx.convention.result.Result;
import cumt.miirso.eduagentx.convention.result.Results;
import cumt.miirso.eduagentx.dto.req.QueryLessonPlanReqDTO;
import cumt.miirso.eduagentx.dto.req.UploadLessonPlanReqDTO;
import cumt.miirso.eduagentx.dto.resp.QueryLessonPlanRespDTO;
import cumt.miirso.eduagentx.dto.resp.UploadLessonPlanRespDTO;
import cumt.miirso.eduagentx.service.CourseLessonPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 课程教案控制器
 * @author miirso
 */
@RestController
@RequestMapping("/api/eduagentx/lesson-plan")
@RequiredArgsConstructor
@Slf4j
public class CourseLessonPlanController {

    private final CourseLessonPlanService courseLessonPlanService;

    /**
     * 上传/更新教案
     * 
     * 功能说明：
     * - 支持上传Markdown格式的教案文件
     * - 自动判断是新增还是更新操作
     * - 支持版本管理，未指定版本则自动递增
     * - 支持课程教案和章节教案
     * 
     * @param courseId 课程ID
     * @param chapterId 章节ID（可选，为空表示课程教案）
     * @param planFile 教案文件
     * @param planTitle 教案标题（可选）
     * @param version 版本号（可选）
     * @return 上传结果
     */
    @PostMapping("/upload")
    public Result<UploadLessonPlanRespDTO> uploadLessonPlan(
            @RequestParam("courseId") String courseId,
            @RequestParam(value = "chapterId", required = false) Integer chapterId,
            @RequestParam("planFile") MultipartFile planFile,
            @RequestParam(value = "planTitle", required = false) String planTitle,
            @RequestParam(value = "version", required = false) Integer version) {
        
        UploadLessonPlanReqDTO requestParam = new UploadLessonPlanReqDTO();
        requestParam.setCourseId(courseId);
        requestParam.setChapterId(chapterId);
        requestParam.setPlanFile(planFile);
        requestParam.setPlanTitle(planTitle);
        requestParam.setVersion(version);
        
        UploadLessonPlanRespDTO result = courseLessonPlanService.uploadOrUpdateLessonPlan(requestParam);
        return Results.success(result);
    }

    /**
     * 查询教案
     * 
     * 功能说明：
     * - 根据课程ID和章节ID查询教案文件
     * - 支持查询指定版本，不指定则返回最新版本
     * - 返回完整的教案文件内容
     * - 通用接口，任何用户都可访问
     * 
     * @param requestParam 查询教案请求参数
     * @return 教案文件内容
     */
    @PostMapping("/query")
    public Result<QueryLessonPlanRespDTO> queryLessonPlan(
            @RequestBody QueryLessonPlanReqDTO requestParam) {
        
        QueryLessonPlanRespDTO result = courseLessonPlanService.queryLessonPlan(requestParam);
        return Results.success(result);
    }

    /**
     * 根据课程ID和章节ID查询教案（GET方式，便于直接访问）
     * 
     * 功能说明：
     * - GET方式查询，便于浏览器直接访问
     * - 支持通过路径参数和查询参数指定课程ID、章节ID和版本
     * - 返回最新版本的教案内容
     * 
     * @param courseId 课程ID
     * @param chapterId 章节ID（可选）
     * @param version 版本号（可选）
     * @return 教案文件内容
     */
    @GetMapping("/query/{courseId}")
    public Result<QueryLessonPlanRespDTO> queryLessonPlanByGet(
            @PathVariable String courseId,
            @RequestParam(required = false) Integer chapterId,
            @RequestParam(required = false) Integer version) {
        
        QueryLessonPlanReqDTO requestParam = new QueryLessonPlanReqDTO();
        requestParam.setCourseId(courseId);
        requestParam.setChapterId(chapterId);
        requestParam.setVersion(version);
        
        QueryLessonPlanRespDTO result = courseLessonPlanService.queryLessonPlan(requestParam);
        return Results.success(result);
    }

    /**
     * 下载教案文件
     * 
     * 功能说明：
     * - 直接下载教案文件，适合浏览器直接访问
     * - 支持指定版本下载，不指定则下载最新版本
     * - 返回文件流，浏览器会提示下载
     * 
     * @param courseId 课程ID
     * @param chapterId 章节ID（可选）
     * @param version 版本号（可选）
     * @return 文件下载
     */
    @GetMapping("/download/{courseId}")
    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> downloadLessonPlan(
            @PathVariable String courseId,
            @RequestParam(required = false) Integer chapterId,
            @RequestParam(required = false) Integer version) {
        
        try {
            QueryLessonPlanReqDTO requestParam = new QueryLessonPlanReqDTO();
            requestParam.setCourseId(courseId);
            requestParam.setChapterId(chapterId);
            requestParam.setVersion(version);
            
            QueryLessonPlanRespDTO planData = courseLessonPlanService.queryLessonPlan(requestParam);
            
            // 创建Resource对象
            org.springframework.core.io.ByteArrayResource resource = 
                    new org.springframework.core.io.ByteArrayResource(planData.getPlanContent().getBytes());
            
            if (resource.exists() && resource.isReadable()) {
                String filename = planData.getFileName() != null ? planData.getFileName() : "lesson_plan.md";
                return org.springframework.http.ResponseEntity.ok()
                        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=\"" + filename + "\"")
                        .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "text/markdown; charset=UTF-8")
                        .body(resource);
            } else {
                throw new cumt.miirso.eduagentx.convention.exception.ClientException("文件不存在或无法读取");
            }
        } catch (Exception e) {
            throw new cumt.miirso.eduagentx.convention.exception.ClientException("文件下载失败: " + e.getMessage());
        }
    }
}

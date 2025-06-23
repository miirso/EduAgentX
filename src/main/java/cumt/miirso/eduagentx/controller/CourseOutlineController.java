package cumt.miirso.eduagentx.controller;

import cumt.miirso.eduagentx.convention.result.Result;
import cumt.miirso.eduagentx.convention.result.Results;
import cumt.miirso.eduagentx.dto.req.QueryOutlineReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherUploadOutlineReqDTO;
import cumt.miirso.eduagentx.dto.resp.QueryOutlineRespDTO;
import cumt.miirso.eduagentx.dto.resp.TeacherUploadOutlineRespDTO;
import cumt.miirso.eduagentx.service.CourseOutlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 课程大纲控制器
 * @author miirso
 */
@RestController
@RequestMapping("/api/eduagentx/outline")
@RequiredArgsConstructor
public class CourseOutlineController {

    private final CourseOutlineService courseOutlineService;

    /**
     * 教师上传/更新课程大纲
     * 
     * 功能说明：
     * - 支持上传Markdown格式的课程大纲文件
     * - 自动判断是新增还是更新操作
     * - 支持版本管理，未指定版本则自动递增
     * - 不需要教师身份验证
     * 
     * @param courseId 课程ID
     * @param outlineFile 大纲文件
     * @param version 版本号（可选）
     * @return 上传结果
     */
    @PostMapping("/upload")
    public Result<TeacherUploadOutlineRespDTO> uploadOutline(
            @RequestParam("courseId") String courseId,
            @RequestParam("outlineFile") MultipartFile outlineFile,
            @RequestParam(value = "version", required = false) Integer version) {
        
        TeacherUploadOutlineReqDTO requestParam = new TeacherUploadOutlineReqDTO();
        requestParam.setCourseId(courseId);
        requestParam.setOutlineFile(outlineFile);
        requestParam.setVersion(version);
        
        TeacherUploadOutlineRespDTO result = courseOutlineService.uploadOrUpdateOutline(requestParam);
        return Results.success(result);
    }

    /**
     * 查询课程大纲
     * 
     * 功能说明：
     * - 根据课程ID查询大纲文件
     * - 支持查询指定版本，不指定则返回最新版本
     * - 返回完整的大纲文件内容
     * - 通用接口，任何用户都可访问
     * 
     * @param requestParam 查询大纲请求参数
     * @return 大纲文件内容
     */
    @PostMapping("/query")
    public Result<QueryOutlineRespDTO> queryOutline(
            @RequestBody QueryOutlineReqDTO requestParam) {
        
        QueryOutlineRespDTO result = courseOutlineService.queryOutline(requestParam);
        return Results.success(result);
    }

    /**
     * 根据课程ID查询大纲（GET方式，便于直接访问）
     * 
     * 功能说明：
     * - GET方式查询，便于浏览器直接访问
     * - 支持通过路径参数和查询参数指定课程ID和版本
     * - 返回最新版本的大纲内容
     * 
     * @param courseId 课程ID
     * @param version 版本号（可选）
     * @return 大纲文件内容
     */
    @GetMapping("/query/{courseId}")
    public Result<QueryOutlineRespDTO> queryOutlineByGet(
            @PathVariable String courseId,
            @RequestParam(required = false) Integer version) {
        
        QueryOutlineReqDTO requestParam = new QueryOutlineReqDTO();
        requestParam.setCourseId(courseId);
        requestParam.setVersion(version);
        
        QueryOutlineRespDTO result = courseOutlineService.queryOutline(requestParam);
        return Results.success(result);
    }

    /**
     * 下载课程大纲文件
     * 
     * 功能说明：
     * - 直接下载大纲文件，适合浏览器直接访问
     * - 支持指定版本下载，不指定则下载最新版本
     * - 返回文件流，浏览器会提示下载
     * 
     * @param courseId 课程ID
     * @param version 版本号（可选）
     * @return 文件下载
     */
    @GetMapping("/download/{courseId}")
    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> downloadOutline(
            @PathVariable String courseId,
            @RequestParam(required = false) Integer version) {
        
        QueryOutlineReqDTO requestParam = new QueryOutlineReqDTO();
        requestParam.setCourseId(courseId);
        requestParam.setVersion(version);
        
        QueryOutlineRespDTO outline = courseOutlineService.queryOutline(requestParam);
        
        try {
            java.nio.file.Path filePath = java.nio.file.Paths.get(outline.getFilePath());
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return org.springframework.http.ResponseEntity.ok()
                        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=\"" + outline.getFileName() + "\"")
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

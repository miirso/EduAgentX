package cumt.miirso.eduagentx.controller;

import cumt.miirso.eduagentx.convention.result.Result;
import cumt.miirso.eduagentx.convention.result.Results;
import cumt.miirso.eduagentx.dto.req.QueryCoursewareListReqDTO;
import cumt.miirso.eduagentx.dto.req.UploadCoursewareReqDTO;
import cumt.miirso.eduagentx.dto.resp.QueryCoursewareListRespDTO;
import cumt.miirso.eduagentx.dto.resp.UploadCoursewareRespDTO;
import cumt.miirso.eduagentx.service.CourseCoursewareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 课程课件控制器
 * @author miirso
 */
@RestController
@RequestMapping("/api/eduagentx/courseware")
@RequiredArgsConstructor
@Slf4j
public class CourseCoursewareController {

    private final CourseCoursewareService courseCoursewareService;

    /**
     * 上传课件
     * 
     * 功能说明：
     * - 支持上传PPT、PDF、DOC等格式的课件文件
     * - 自动分配课件顺序
     * - 支持自定义课件标题
     * 
     * @param courseId 课程ID
     * @param coursewareFile 课件文件
     * @param coursewareTitle 课件标题（可选）
     * @param coursewareOrder 课件顺序（可选）
     * @return 上传结果
     */
    @PostMapping("/upload")
    public Result<UploadCoursewareRespDTO> uploadCourseware(
            @RequestParam("courseId") String courseId,
            @RequestParam("coursewareFile") MultipartFile coursewareFile,
            @RequestParam(value = "coursewareTitle", required = false) String coursewareTitle,
            @RequestParam(value = "coursewareOrder", required = false) Integer coursewareOrder) {
        
        UploadCoursewareReqDTO requestParam = new UploadCoursewareReqDTO();
        requestParam.setCourseId(courseId);
        requestParam.setCoursewareFile(coursewareFile);
        requestParam.setCoursewareTitle(coursewareTitle);
        requestParam.setCoursewareOrder(coursewareOrder);
        
        UploadCoursewareRespDTO result = courseCoursewareService.uploadCourseware(requestParam);
        return Results.success(result);
    }

    /**
     * 查询课程的课件列表
     * 
     * 功能说明：
     * - 根据课程ID查询所有课件
     * - 按照课件顺序排序
     * - 包含文件大小等详细信息
     * 
     * @param requestParam 查询课件列表请求参数
     * @return 课件列表
     */
    @PostMapping("/list")
    public Result<QueryCoursewareListRespDTO> queryCoursewareList(
            @RequestBody QueryCoursewareListReqDTO requestParam) {
        
        QueryCoursewareListRespDTO result = courseCoursewareService.queryCoursewareList(requestParam);
        return Results.success(result);
    }

    /**
     * 根据课程ID查询课件列表（GET方式）
     * 
     * 功能说明：
     * - GET方式查询，便于浏览器直接访问
     * - 根据课程ID查询所有课件
     * 
     * @param courseId 课程ID
     * @return 课件列表
     */
    @GetMapping("/list/{courseId}")
    public Result<QueryCoursewareListRespDTO> queryCoursewareListByGet(
            @PathVariable String courseId) {
        
        QueryCoursewareListReqDTO requestParam = new QueryCoursewareListReqDTO();
        requestParam.setCourseId(courseId);
        
        QueryCoursewareListRespDTO result = courseCoursewareService.queryCoursewareList(requestParam);
        return Results.success(result);
    }

    /**
     * 删除课件
     * 
     * 功能说明：
     * - 根据课件ID删除课件
     * - 同时删除文件和数据库记录
     * 
     * @param coursewareId 课件ID
     * @return 删除结果
     */
    @DeleteMapping("/{coursewareId}")
    public Result<Boolean> deleteCourseware(@PathVariable Integer coursewareId) {
        
        Boolean result = courseCoursewareService.deleteCourseware(coursewareId);
        return Results.success(result);
    }

    /**
     * 下载课件文件
     * 
     * 功能说明：
     * - 直接下载课件文件，适合浏览器直接访问
     * - 返回文件流，浏览器会提示下载
     * 
     * @param coursewareId 课件ID
     * @return 文件下载
     */
    @GetMapping("/download/{coursewareId}")
    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> downloadCourseware(
            @PathVariable Integer coursewareId) {
        
        try {
            // 查询课件信息
            cumt.miirso.eduagentx.entity.CourseCoursewareDO coursewareDO = 
                    courseCoursewareService.getById(coursewareId);
            
            if (coursewareDO == null || !coursewareDO.getTag()) {
                throw new cumt.miirso.eduagentx.convention.exception.ClientException("课件不存在");
            }
            
            // 读取文件
            java.nio.file.Path filePath = Paths.get(coursewareDO.getCoursewareFilePath());
            if (!Files.exists(filePath)) {
                throw new cumt.miirso.eduagentx.convention.exception.ClientException("课件文件不存在");
            }
            
            // 创建Resource对象
            org.springframework.core.io.Resource resource = 
                    new org.springframework.core.io.PathResource(filePath);
            
            if (resource.exists() && resource.isReadable()) {
                String filename = coursewareDO.getFileName();
                
                // 确定MIME类型
                String contentType;
                try {
                    contentType = Files.probeContentType(filePath);
                } catch (IOException e) {
                    contentType = "application/octet-stream";
                }
                
                return org.springframework.http.ResponseEntity.ok()
                        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=\"" + filename + "\"")
                        .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, contentType)
                        .body(resource);
            } else {
                throw new cumt.miirso.eduagentx.convention.exception.ClientException("文件不存在或无法读取");
            }
        } catch (Exception e) {
            throw new cumt.miirso.eduagentx.convention.exception.ClientException("文件下载失败: " + e.getMessage());
        }
    }
}

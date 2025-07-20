package cumt.miirso.eduagentx.utils;

import cumt.miirso.eduagentx.convention.exception.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 文件操作工具类
 * @author miirso
 */
@Slf4j
@Component
public class FileUtils {    /**
     * 允许的文件扩展名
     */
    private static final String[] ALLOWED_EXTENSIONS = {".md", ".txt", ".markdown"};

    /**
     * 允许的课件文件扩展名
     */
    private static final String[] ALLOWED_COURSEWARE_EXTENSIONS = {".ppt", ".pptx", ".pdf", ".doc", ".docx"};

    /**
     * 最大文件大小 (5MB)
     */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 最大课件文件大小 (50MB)
     */
    private static final long MAX_COURSEWARE_FILE_SIZE = 50 * 1024 * 1024;    /**
     * 保存大纲文件
     * 
     * @param file 上传的文件
     * @param courseId 课程ID
     * @param version 版本号
     * @return 文件存储路径
     */
    public String saveOutlineFile(MultipartFile file, String courseId, Integer version) {
        log.info("=== 开始保存大纲文件 ===");
        log.info("文件信息: 原文件名={}, 大小={}bytes, 课程ID={}, 版本={}", 
                file.getOriginalFilename(), file.getSize(), courseId, version);

        // 1. 文件验证
        validateFile(file);        // 2. 生成文件存储路径（使用项目根目录）
        String fileName = generateFileName(file.getOriginalFilename(), courseId, version);
        
        // 使用项目根目录
        String projectRoot = System.getProperty("user.dir");
        String fullDir = projectRoot + "/uploads/outlines/" + courseId;
        Path fullPath = Paths.get(fullDir, fileName);

        try {
            // 3. 创建目录
            Files.createDirectories(Paths.get(fullDir));
            
            log.info("创建目录: {}", fullDir);
            log.info("保存文件到: {}", fullPath.toAbsolutePath());

            // 4. 保存文件
            file.transferTo(fullPath.toFile());
            
            log.info("文件保存成功: {}", fullPath.toAbsolutePath());
            
            // 返回相对路径用于数据库存储，但实际使用绝对路径
            return fullPath.toAbsolutePath().toString();

        } catch (IOException e) {
            log.error("文件保存失败: {}", e.getMessage(), e);
            throw new ClientException("文件保存失败: " + e.getMessage());
        }
    }

    /**
     * 读取大纲文件内容
     * 
     * @param filePath 文件路径
     * @return 文件内容
     */
    public String readOutlineFile(String filePath) {
        log.info("开始读取大纲文件: {}", filePath);

        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new ClientException("文件不存在: " + filePath);
            }

            String content = Files.readString(path, StandardCharsets.UTF_8);
            log.info("文件读取成功，内容长度: {}字符", content.length());
            return content;

        } catch (IOException e) {
            log.error("文件读取失败: {}", e.getMessage(), e);
            throw new ClientException("文件读取失败: " + e.getMessage());
        }
    }

    /**
     * 删除大纲文件
     * 
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public boolean deleteOutlineFile(String filePath) {
        log.info("开始删除大纲文件: {}", filePath);

        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("文件删除成功: {}", filePath);
                return true;
            } else {
                log.warn("文件不存在，无需删除: {}", filePath);
                return true;
            }

        } catch (IOException e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查文件是否存在
     * 
     * @param filePath 文件路径
     * @return 是否存在
     */
    public boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * 获取文件大小
     * 
     * @param filePath 文件路径
     * @return 文件大小（字节）
     */
    public long getFileSize(String filePath) {
        try {
            return Files.size(Paths.get(filePath));
        } catch (IOException e) {
            log.error("获取文件大小失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 验证上传的文件
     * 
     * @param file 上传的文件
     */
    private void validateFile(MultipartFile file) {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new ClientException("上传文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ClientException("文件大小超过限制，最大允许5MB");
        }

        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new ClientException("文件名不能为空");
        }

        boolean validExtension = false;
        for (String ext : ALLOWED_EXTENSIONS) {
            if (originalFilename.toLowerCase().endsWith(ext)) {
                validExtension = true;
                break;
            }
        }

        if (!validExtension) {
            throw new ClientException("不支持的文件类型，仅支持: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }

    /**
     * 生成文件存储名称
     * 
     * @param originalFilename 原文件名
     * @param courseId 课程ID
     * @param version 版本号
     * @return 生成的文件名
     */
    private String generateFileName(String originalFilename, String courseId, Integer version) {
        // 获取文件扩展名
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        // 生成时间戳
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // 生成文件名: courseId_v版本号_时间戳.扩展名
        return String.format("%s_v%d_%s%s", courseId, version, timestamp, extension);
    }

    /**
     * 保存教案文件
     * 
     * @param file 上传的文件
     * @param courseId 课程ID
     * @param chapterId 章节ID（可为空）
     * @param version 版本号
     * @return 文件存储路径
     */
    public String saveLessonPlanFile(MultipartFile file, String courseId, Integer chapterId, Integer version) {
        log.info("=== 开始保存教案文件 ===");
        log.info("文件信息: 原文件名={}, 大小={}bytes, 课程ID={}, 章节ID={}, 版本={}", 
                file.getOriginalFilename(), file.getSize(), courseId, chapterId, version);

        // 1. 文件验证
        validateFile(file);

        // 2. 生成文件存储路径
        String fileName = generateLessonPlanFileName(file.getOriginalFilename(), courseId, chapterId, version);
        String projectRoot = System.getProperty("user.dir");
        String fullDir = projectRoot + "/uploads/lesson_plans/" + courseId;
        if (chapterId != null) {
            fullDir += "/chapter_" + chapterId;
        }
        Path fullPath = Paths.get(fullDir, fileName);

        try {
            // 3. 创建目录
            Files.createDirectories(Paths.get(fullDir));
            
            log.info("创建目录: {}", fullDir);
            log.info("保存文件到: {}", fullPath.toAbsolutePath());

            // 4. 保存文件
            file.transferTo(fullPath.toFile());
            
            log.info("教案文件保存成功: {}", fullPath.toAbsolutePath());
            
            return fullPath.toAbsolutePath().toString();

        } catch (IOException e) {
            log.error("教案文件保存失败: {}", e.getMessage(), e);
            throw new ClientException("教案文件保存失败: " + e.getMessage());
        }
    }

    /**
     * 保存课件文件
     * 
     * @param file 上传的文件
     * @param courseId 课程ID
     * @return 文件存储路径
     */
    public String saveCoursewareFile(MultipartFile file, String courseId) {
        log.info("=== 开始保存课件文件 ===");
        log.info("文件信息: 原文件名={}, 大小={}bytes, 课程ID={}", 
                file.getOriginalFilename(), file.getSize(), courseId);

        // 1. 文件验证
        validateCoursewareFile(file);

        // 2. 生成文件存储路径
        String fileName = generateCoursewareFileName(file.getOriginalFilename(), courseId);
        String projectRoot = System.getProperty("user.dir");
        String fullDir = projectRoot + "/uploads/courseware/" + courseId;
        Path fullPath = Paths.get(fullDir, fileName);

        try {
            // 3. 创建目录
            Files.createDirectories(Paths.get(fullDir));
            
            log.info("创建目录: {}", fullDir);
            log.info("保存文件到: {}", fullPath.toAbsolutePath());

            // 4. 保存文件
            file.transferTo(fullPath.toFile());
            
            log.info("课件文件保存成功: {}", fullPath.toAbsolutePath());
            
            return fullPath.toAbsolutePath().toString();

        } catch (IOException e) {
            log.error("课件文件保存失败: {}", e.getMessage(), e);
            throw new ClientException("课件文件保存失败: " + e.getMessage());
        }
    }

    /**
     * 读取教案文件内容
     * 
     * @param filePath 文件路径
     * @return 文件内容
     */
    public String readLessonPlanFile(String filePath) {
        log.info("开始读取教案文件: {}", filePath);

        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new ClientException("教案文件不存在: " + filePath);
            }

            String content = Files.readString(path, StandardCharsets.UTF_8);
            log.info("教案文件读取成功，内容长度: {}字符", content.length());
            return content;

        } catch (IOException e) {
            log.error("教案文件读取失败: {}", e.getMessage(), e);
            throw new ClientException("教案文件读取失败: " + e.getMessage());
        }
    }

    /**
     * 删除教案文件
     * 
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public boolean deleteLessonPlanFile(String filePath) {
        log.info("开始删除教案文件: {}", filePath);

        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("教案文件删除成功: {}", filePath);
                return true;
            } else {
                log.warn("教案文件不存在，无需删除: {}", filePath);
                return true;
            }

        } catch (IOException e) {
            log.error("教案文件删除失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除课件文件
     * 
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public boolean deleteCoursewareFile(String filePath) {
        log.info("开始删除课件文件: {}", filePath);

        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("课件文件删除成功: {}", filePath);
                return true;
            } else {
                log.warn("课件文件不存在，无需删除: {}", filePath);
                return true;
            }

        } catch (IOException e) {
            log.error("课件文件删除失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 验证课件文件
     * 
     * @param file 上传的文件
     */
    private void validateCoursewareFile(MultipartFile file) {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new ClientException("上传文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_COURSEWARE_FILE_SIZE) {
            throw new ClientException("文件大小超过限制，最大允许50MB");
        }

        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new ClientException("文件名不能为空");
        }

        boolean validExtension = false;
        for (String ext : ALLOWED_COURSEWARE_EXTENSIONS) {
            if (originalFilename.toLowerCase().endsWith(ext)) {
                validExtension = true;
                break;
            }
        }

        if (!validExtension) {
            throw new ClientException("不支持的文件类型，仅支持: " + String.join(", ", ALLOWED_COURSEWARE_EXTENSIONS));
        }
    }

    /**
     * 生成教案文件存储名称
     * 
     * @param originalFilename 原文件名
     * @param courseId 课程ID
     * @param chapterId 章节ID
     * @param version 版本号
     * @return 生成的文件名
     */
    private String generateLessonPlanFileName(String originalFilename, String courseId, Integer chapterId, Integer version) {
        // 获取文件扩展名
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        // 生成时间戳
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // 生成文件名
        if (chapterId != null) {
            return String.format("%s_chapter%d_v%d_%s%s", courseId, chapterId, version, timestamp, extension);
        } else {
            return String.format("%s_course_v%d_%s%s", courseId, version, timestamp, extension);
        }
    }

    /**
     * 生成课件文件存储名称
     * 
     * @param originalFilename 原文件名
     * @param courseId 课程ID
     * @return 生成的文件名
     */
    private String generateCoursewareFileName(String originalFilename, String courseId) {
        // 获取文件扩展名
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        // 生成时间戳
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // 生成文件名: courseId_时间戳.扩展名
        return String.format("%s_%s%s", courseId, timestamp, extension);
    }
}

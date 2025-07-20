package cumt.miirso.eduagentx.dto.req;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传教案请求DTO
 * @author miirso
 */
@Data
public class UploadLessonPlanReqDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 章节ID（可选，为空表示课程教案）
     */
    private Integer chapterId;

    /**
     * 教案文件
     */
    private MultipartFile planFile;

    /**
     * 教案标题（可选）
     */
    private String planTitle;

    /**
     * 版本号（可选，不传则自动递增）
     */
    private Integer version;
}

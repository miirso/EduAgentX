package cumt.miirso.eduagentx.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 上传教案响应DTO
 * @author miirso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadLessonPlanRespDTO {

    /**
     * 教案ID
     */
    private Integer planId;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 章节ID
     */
    private Integer chapterId;

    /**
     * 教案标题
     */
    private String planTitle;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 操作类型（新增/更新）
     */
    private String operationType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

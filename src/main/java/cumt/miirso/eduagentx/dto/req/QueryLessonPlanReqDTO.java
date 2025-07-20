package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * 查询教案请求DTO
 * @author miirso
 */
@Data
public class QueryLessonPlanReqDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 章节ID（可选，为空表示查询课程教案）
     */
    private Integer chapterId;

    /**
     * 版本号（可选，不传则返回最新版本）
     */
    private Integer version;
}

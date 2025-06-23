package cumt.miirso.eduagentx.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教师新增课程章节响应DTO
 * @author miirso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherAddChapterRespDTO {

    /**
     * 课程ID
     */
    private String courseId;    /**
     * 章节ID
     */
    private Integer chapterId;

    /**
     * 章节标题
     */
    private String title;

    /**
     * 章节顺序
     */
    private Integer order;

    /**
     * 操作状态描述
     */
    private String message;
}

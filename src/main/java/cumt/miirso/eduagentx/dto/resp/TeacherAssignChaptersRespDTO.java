package cumt.miirso.eduagentx.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教师为课程分配章节响应DTO
 * @author miirso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherAssignChaptersRespDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 分配的章节数量
     */
    private Integer assignedChapterCount;

    /**
     * 操作状态描述
     */
    private String message;
}

package cumt.miirso.eduagentx.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教师删除章节响应DTO
 * @author miirso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDeleteChapterRespDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 删除的章节顺序号
     */
    private Integer order;

    /**
     * 操作状态描述
     */
    private String message;
}

package cumt.miirso.eduagentx.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教师修改章节响应DTO
 * @author miirso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherUpdateChapterRespDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 修改的章节顺序号
     */
    private Integer order;

    /**
     * 操作状态描述
     */
    private String message;
}

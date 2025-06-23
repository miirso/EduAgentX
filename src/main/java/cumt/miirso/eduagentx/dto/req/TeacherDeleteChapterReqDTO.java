package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * 教师删除章节请求DTO
 * @author miirso
 */
@Data
public class TeacherDeleteChapterReqDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 章节顺序号
     */
    private Integer order;
}

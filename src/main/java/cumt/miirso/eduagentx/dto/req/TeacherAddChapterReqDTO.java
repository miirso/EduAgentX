package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * 教师新增课程章节请求DTO
 * @author miirso
 */
@Data
public class TeacherAddChapterReqDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 章节标题
     */
    private String title;

    /**
     * 章节内容
     */
    private String content;

    /**
     * 章节顺序
     */
    private Integer order;
}

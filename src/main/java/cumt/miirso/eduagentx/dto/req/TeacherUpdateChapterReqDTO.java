package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * 教师修改章节请求DTO
 * @author miirso
 */
@Data
public class TeacherUpdateChapterReqDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 原章节顺序号（用于定位要修改的章节）
     */
    private Integer originalOrder;

    /**
     * 新的章节标题
     */
    private String title;

    /**
     * 新的章节内容
     */
    private String content;

    /**
     * 新的章节顺序号
     */
    private Integer newOrder;
}

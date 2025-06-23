package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

import java.util.List;

/**
 * 教师为课程分配章节请求DTO
 * @author miirso
 */
@Data
public class TeacherAssignChaptersReqDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 章节列表
     */
    private List<ChapterInfo> chapters;

    @Data
    public static class ChapterInfo {
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
}

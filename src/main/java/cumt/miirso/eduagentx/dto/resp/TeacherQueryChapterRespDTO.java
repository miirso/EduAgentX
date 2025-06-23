package cumt.miirso.eduagentx.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教师查询课程章节响应DTO
 * @author miirso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherQueryChapterRespDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 章节列表
     */
    private List<ChapterInfo> chapters;

    /**
     * 章节总数
     */
    private Integer totalCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChapterInfo {
        /**
         * 章节ID
         */
        private Integer id;

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

        /**
         * 创建时间
         */
        private LocalDateTime createTime;

        /**
         * 更新时间
         */
        private LocalDateTime updateTime;
    }
}

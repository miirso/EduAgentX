package cumt.miirso.eduagentx.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 查询教案响应DTO
 * @author miirso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryLessonPlanRespDTO {

    /**
     * 教案ID
     */
    private Integer planId;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 章节ID
     */
    private Integer chapterId;

    /**
     * 教案内容
     */
    private String planContent;

    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 教案标题
     */
    private String planTitle;

    /**
     * 教案类型
     */
    private String planType;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

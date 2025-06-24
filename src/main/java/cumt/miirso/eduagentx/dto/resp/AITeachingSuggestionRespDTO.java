package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 * AI教学建议响应DTO
 * @author miirso
 */
@Data
public class AITeachingSuggestionRespDTO {

    /**
     * 建议ID
     */
    private Integer id;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 试卷ID
     */
    private Integer paperId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 建议类型：course_general、paper_specific、student_personal
     */
    private String suggestionType;

    /**
     * 建议标题
     */
    private String suggestionTitle;

    /**
     * 建议内容
     */
    private String suggestionContent;

    /**
     * 基于的数据分析
     */
    private String basedOnData;

    /**
     * 使用的AI模型
     */
    private String aiModel;

    /**
     * 创建时间
     */
    private Date createTime;
}

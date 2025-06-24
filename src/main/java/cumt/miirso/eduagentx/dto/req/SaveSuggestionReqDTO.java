package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * 保存AI建议请求DTO
 * @author miirso
 */
@Data
public class SaveSuggestionReqDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 试卷ID（可为空，表示整个课程的建议）
     */
    private Integer paperId;

    /**
     * 学生ID（可为空，表示针对所有学生的建议）
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
}

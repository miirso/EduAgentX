package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI教学建议实体类
 * @author miirso
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_teaching_suggestions")
public class AITeachingSuggestionDO extends BaseDO {

    /**
     * 建议ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

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

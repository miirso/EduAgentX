package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试题实体类
 * @author miirso
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_questions")
public class ExamQuestionDO extends BaseDO {

    /**
     * 题目ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 试卷ID
     */
    private Integer paperId;

    /**
     * 题目类型：single_choice、multiple_choice、short_answer
     */
    private String questionType;

    /**
     * 题目内容
     */
    private String questionText;

    /**
     * 题目在试卷中的顺序
     */
    private Integer questionOrder;

    /**
     * 题目分值
     */
    private Integer score;

    /**
     * 难度等级
     */
    private String difficulty;

    /**
     * 是否AI生成
     */
    private Boolean aiGenerated;
}

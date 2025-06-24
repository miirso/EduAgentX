package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试题答案实体类
 * @author miirso
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_question_answers")
public class ExamQuestionAnswerDO extends BaseDO {

    /**
     * 答案ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 题目ID
     */
    private Integer questionId;

    /**
     * 正确答案（单选题存选项标签，多选题存逗号分隔的标签，简答题存答案文本）
     */
    private String correctAnswer;

    /**
     * 答案解析
     */
    private String answerExplanation;
}

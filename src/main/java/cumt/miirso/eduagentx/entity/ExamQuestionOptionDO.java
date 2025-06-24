package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试题选项实体类
 * @author miirso
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_question_options")
public class ExamQuestionOptionDO extends BaseDO {

    /**
     * 选项ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 题目ID
     */
    private Integer questionId;

    /**
     * 选项标签（A、B、C、D等）
     */
    private String optionLabel;

    /**
     * 选项内容
     */
    private String optionText;

    /**
     * 选项顺序
     */
    private Integer optionOrder;

    /**
     * 是否为正确答案
     */
    private Boolean isCorrect;
}

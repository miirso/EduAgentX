package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 学生答题详情实体类
 * @author miirso
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_answer_details")
public class StudentAnswerDetailDO extends BaseDO {

    /**
     * 详情ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 答题记录ID
     */
    private Integer examRecordId;

    /**
     * 题目ID
     */
    private Integer questionId;

    /**
     * 学生答案
     */
    private String studentAnswer;

    /**
     * 是否正确
     */
    private Boolean isCorrect;

    /**
     * 得分
     */
    private Integer scoreGained;

    /**
     * 答题时间
     */
    private Date answerTime;
}

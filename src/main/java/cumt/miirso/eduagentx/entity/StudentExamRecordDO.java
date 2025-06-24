package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 学生答题记录实体类
 * @author miirso
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_exam_records")
public class StudentExamRecordDO extends BaseDO {

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 试卷ID
     */
    private Integer paperId;

    /**
     * 开始答题时间
     */
    private Date startTime;

    /**
     * 提交时间
     */
    private Date submitTime;

    /**
     * 总得分
     */
    private Integer totalScore;

    /**
     * 正确题数
     */
    private Integer correctCount;

    /**
     * 错误题数
     */
    private Integer wrongCount;

    /**
     * 正确率（百分比）
     */
    private BigDecimal accuracyRate;

    /**
     * 用时（分钟）
     */
    private Integer timeSpent;

    /**
     * 状态：in_progress、submitted、graded
     */
    private String status;
}

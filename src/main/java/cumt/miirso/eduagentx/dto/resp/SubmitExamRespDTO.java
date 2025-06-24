package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 提交试卷响应DTO
 * @author miirso
 */
@Data
public class SubmitExamRespDTO {

    /**
     * 答题记录ID
     */
    private Integer examRecordId;

    /**
     * 试卷ID
     */
    private Integer paperId;

    /**
     * 试卷名称
     */
    private String paperName;

    /**
     * 总得分
     */
    private Integer totalScore;

    /**
     * 试卷满分
     */
    private Integer fullScore;

    /**
     * 正确题数
     */
    private Integer correctCount;

    /**
     * 错误题数
     */
    private Integer wrongCount;

    /**
     * 总题数
     */
    private Integer totalQuestions;

    /**
     * 正确率（百分比）
     */
    private BigDecimal accuracyRate;

    /**
     * 用时（分钟）
     */
    private Integer timeSpent;

    /**
     * 提交时间
     */
    private Date submitTime;

    /**
     * 消息
     */
    private String message;
}

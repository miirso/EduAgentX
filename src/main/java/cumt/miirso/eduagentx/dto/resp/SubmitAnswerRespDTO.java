package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

/**
 * 提交答案响应DTO
 * @author miirso
 */
@Data
public class SubmitAnswerRespDTO {

    /**
     * 题目ID
     */
    private Integer questionId;

    /**
     * 是否正确
     */
    private Boolean isCorrect;

    /**
     * 得分
     */
    private Integer scoreGained;

    /**
     * 正确答案
     */
    private String correctAnswer;

    /**
     * 答案解析
     */
    private String answerExplanation;

    /**
     * 消息
     */
    private String message;
}

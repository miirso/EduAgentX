package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

/**
 * 获取题目答案响应DTO
 * @author miirso
 */
@Data
public class QuestionAnswerRespDTO {

    /**
     * 题目ID
     */
    private Integer questionId;

    /**
     * 题目类型
     */
    private String questionType;

    /**
     * 正确答案
     */
    private String correctAnswer;

    /**
     * 答案解析
     */
    private String answerExplanation;

    /**
     * 题目分值
     */
    private Integer score;
}

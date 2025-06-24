package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * 提交答案请求DTO
 * @author miirso
 */
@Data
public class SubmitAnswerReqDTO {

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
}

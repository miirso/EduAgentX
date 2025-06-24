package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * 提交试卷请求DTO
 * @author miirso
 */
@Data
public class SubmitExamReqDTO {

    /**
     * 答题记录ID
     */
    private Integer examRecordId;
}

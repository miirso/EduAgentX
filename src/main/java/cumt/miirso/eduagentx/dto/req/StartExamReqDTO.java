package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * 开始答题请求DTO
 * @author miirso
 */
@Data
public class StartExamReqDTO {

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 试卷ID
     */
    private Integer paperId;
}

package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 * 开始答题响应DTO
 * @author miirso
 */
@Data
public class StartExamRespDTO {

    /**
     * 答题记录ID
     */
    private Integer examRecordId;

    /**
     * 试卷详情
     */
    private ExamPaperDetailRespDTO paperDetail;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 消息
     */
    private String message;
}

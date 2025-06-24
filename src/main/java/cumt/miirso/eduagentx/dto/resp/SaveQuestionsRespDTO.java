package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 * 保存试题响应DTO
 * @author miirso
 */
@Data
public class SaveQuestionsRespDTO {

    /**
     * 试卷ID
     */
    private Integer paperId;

    /**
     * 试卷名称
     */
    private String paperName;

    /**
     * 保存的题目数量
     */
    private Integer questionCount;

    /**
     * 操作类型（新增试卷/更新试卷）
     */
    private String operation;

    /**
     * 操作时间
     */
    private Date operationTime;

    /**
     * 操作消息
     */
    private String message;
}

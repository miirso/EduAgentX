package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 * 保存课程简介响应DTO
 * @author miirso
 */
@Data
public class SaveIntroductionRespDTO {

    /**
     * 简介ID
     */
    private Integer id;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 操作类型（新增/更新）
     */
    private String operation;

    /**
     * 操作时间
     */
    private Date updateTime;

    /**
     * 操作消息
     */
    private String message;
}

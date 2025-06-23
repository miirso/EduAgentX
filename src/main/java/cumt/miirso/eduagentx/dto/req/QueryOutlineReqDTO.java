package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * 查询课程大纲请求DTO
 * @author miirso
 */
@Data
public class QueryOutlineReqDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 版本号（可选，不传则返回最新版本）
     */
    private Integer version;
}

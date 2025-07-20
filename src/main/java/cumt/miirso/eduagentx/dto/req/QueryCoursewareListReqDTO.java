package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * 查询课件列表请求DTO
 * @author miirso
 */
@Data
public class QueryCoursewareListReqDTO {

    /**
     * 课程ID
     */
    private String courseId;
}

package cumt.miirso.eduagentx.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 查询课件列表响应DTO
 * @author miirso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryCoursewareListRespDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 课件总数
     */
    private Integer totalCount;

    /**
     * 课件列表
     */
    private List<CoursewareInfoDTO> coursewareList;
}

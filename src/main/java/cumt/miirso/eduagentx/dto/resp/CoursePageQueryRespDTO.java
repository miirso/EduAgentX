package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

import java.util.List;

/**
 * 课程分页查询响应DTO
 */
@Data
public class CoursePageQueryRespDTO {

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页显示条数
     */
    private Long size;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 课程列表
     */
    private List<CourseQueryRespDTO> records;

    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;
}

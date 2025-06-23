package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * 课程分页查询请求DTO
 */
@Data
public class CoursePageQueryReqDTO {    /**
     * 当前页码（从1开始）
     */
    private Integer current;

    /**
     * 每页显示条数（默认10条，最大100条）
     */
    private Integer size;

    /**
     * 课程名称（模糊查询）
     */
    private String name;

    /**
     * 课程类型（A=必修课，B=选修课）
     */
    private String type;

    /**
     * 学科ID
     */
    private Integer subjectId;

    /**
     * 考核方式
     */
    private String assessmentMethod;    /**
     * 排序字段（create_time, start_date, end_date, name）
     */
    private String sortBy;

    /**
     * 排序方式（asc=升序, desc=降序）
     */
    private String sortOrder;
    
    @Override
    public String toString() {
        return String.format("CoursePageQueryReqDTO{current=%d, size=%d, name='%s', type='%s', subjectId=%s, assessmentMethod='%s', sortBy='%s', sortOrder='%s'}", 
                current, size, name, type, subjectId, assessmentMethod, sortBy, sortOrder);
    }
}

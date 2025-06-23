package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

import java.util.List;

/**
 * 教师分页查询响应DTO
 * 
 * @author EduAgentX
 */
@Data
public class TeacherPageQueryRespDTO {

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
     * 是否有上一页
     */
    private Boolean hasPrevious;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    /**
     * 教师记录列表
     */
    private List<TeacherQueryRespDTO> records;

    @Override
    public String toString() {
        return String.format("TeacherPageQueryRespDTO{current=%d, size=%d, total=%d, pages=%d, hasPrevious=%s, hasNext=%s, recordsSize=%d}", 
                current, size, total, pages, hasPrevious, hasNext, records != null ? records.size() : 0);
    }
}

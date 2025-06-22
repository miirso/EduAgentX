package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

/**
 * 课程批量导入响应DTO
 */
@Data
public class CourseImportRespDTO {

    /**
     * 成功导入的课程数量
     */
    private Integer successCount;
    
    /**
     * 失败的课程数量
     */
    private Integer failCount;
    
    /**
     * 总处理课程数量
     */
    private Integer totalCount;
}

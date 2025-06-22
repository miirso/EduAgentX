package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

/**
 * 班级查询响应DTO
 */
@Data
public class ClassQueryRespDTO {
    
    /**
     * 班级ID
     */
    private Long id;
    
    /**
     * 班级名称
     */
    private String name;
    
    /**
     * 专业ID
     */
    private String majorId;
    
    /**
     * 创建时间
     */
    private String createTime;
    
    /**
     * 学生人数
     */
    private Integer studentCount;
}

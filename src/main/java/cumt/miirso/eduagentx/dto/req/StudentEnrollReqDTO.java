package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * 管理员添加学生到班级的请求DTO
 * 
 * @Package cumt.miirso.eduagentx.dto.req
 * @Author miirso
 * @Date 2025/1/20
 */
@Data
public class StudentEnrollReqDTO {

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 班级ID
     */
    private Long classId;
}

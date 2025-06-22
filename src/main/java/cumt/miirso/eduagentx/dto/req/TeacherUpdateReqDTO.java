package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * @Package cumt.miirso.eduagentx.dto.req
 * @Author miirso
 * @Date 2025/6/21
 */
@Data
public class TeacherUpdateReqDTO {
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 性别（0：女，1：男）
     */
    private Integer gender;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 学校
     */
    private String school;
    
    /**
     * 学院
     */
    private String college;
}

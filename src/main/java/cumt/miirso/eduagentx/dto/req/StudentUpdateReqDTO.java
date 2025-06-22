package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * @Package cumt.miirso.eduagentx.dto.req
 * @Author miirso
 * @Date 2025/6/21
 */
@Data
public class StudentUpdateReqDTO {
    
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
     * 邮箱
     */
    private String email;
    
    /**
     * 专业代码
     */
    private String majorCode;
    
    /**
     * 班级代码
     */
    private String classCode;
    
    /**
     * 学院
     */
    private String college;
    
    /**
     * 学校
     */
    private String school;
}

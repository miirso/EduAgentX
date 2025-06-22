package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

/**
 * 学生信息查询响应DTO
 */
@Data
public class StudentInfoRespDTO {
    
    /**
     * 学生ID
     */
    private Long id;
    
    /**
     * 学号
     */
    private String studentNo;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 性别 (0: 女, 1: 男)
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
    
    /**
     * 创建时间
     */
    private String createTime;
}

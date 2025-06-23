package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;
import java.util.Date;

/**
 * 教师查询响应DTO
 * 
 * @author EduAgentX
 */
@Data
public class TeacherQueryRespDTO {

    /**
     * 教师ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别（0：女，1：男）
     */
    private Integer gender;

    /**
     * 性别描述
     */
    private String genderDesc;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 教师工号
     */
    private String teacherNo;

    /**
     * 学校
     */
    private String school;

    /**
     * 学院
     */
    private String college;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}

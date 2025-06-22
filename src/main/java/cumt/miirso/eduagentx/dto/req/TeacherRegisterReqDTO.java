package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * @Package cumt.miirso.eduagentx.dto.req
 * @Author miirso
 * @Date 2025/5/30 1:46
 */

@Data
public class TeacherRegisterReqDTO {

    /**
     * Username
     */
    private String username;

    /**
     * Real Name
     */
    private String realName;

    /**
     * Gender (0: Female, 1: Male)
     */
    private Integer gender;

    /**
     * Phone Number
     */
    private String phone;

    /**
     * Teacher Number
     */
    private String teacherNo;

    /**
     * School
     */
    private String school;

    /**
     * College
     */
    private String college;

    /**
     * Password
     */
    private String password;
}

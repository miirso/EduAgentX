package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

/**
 * @Package cumt.miirso.eduagentx.dto.resp
 * @Author miirso
 * @Date 2025/5/29 22:36
 */
@Data
public class TeacherRegisterRespDTO {

    /**
     * id
     */
    private Long id;

    /**
     * username
     */
    private String username;

    /**
     * realName
     */
    private String realName;

    /**
     * teacherNo
     */
    private String teacherNo;
}

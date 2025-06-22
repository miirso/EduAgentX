package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * @Package cumt.miirso.eduagentx.dto.req
 * @Author miirso
 * @Date 2025/5/30 1:50
 */

@Data
public class TeacherLoginReqDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}

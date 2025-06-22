package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * @Package cumt.miirso.eduagentx.dto.req
 * @Author miirso
 * @Date 2025/6/20
 */

@Data
public class AdminRegisterReqDTO {

    /**
     * Username
     */
    private String username;

    /**
     * Password
     */
    private String password;
}

package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Package cumt.miirso.eduagentx.dto.resp
 * @Author miirso
 * @Date 2025/6/20
 */

@Data
@NoArgsConstructor
public class AdminLoginRespDTO {

    /**
     * 令牌
     */
    private String token;
}

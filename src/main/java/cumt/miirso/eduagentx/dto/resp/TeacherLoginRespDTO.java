package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Package cumt.miirso.eduagentx.dto.resp
 * @Author miirso
 * @Date 2025/5/30 1:51
 */

@Data
@NoArgsConstructor
public class TeacherLoginRespDTO {

    /**
     * 令牌
     */
    private String token;
}

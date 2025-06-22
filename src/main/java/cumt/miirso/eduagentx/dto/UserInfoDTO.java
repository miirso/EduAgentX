package cumt.miirso.eduagentx.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Package com.miirso.shortlink.admin.dto.thread
 * @Author miirso
 * @Date 2024/10/11 9:32
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {

    private Long id;

    private Integer type;

    private String username;

    private String token;

}

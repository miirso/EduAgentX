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
    // 系统主键ID
    private Long id;
    // 用户名
    private String username;
    // 真实姓名
    private String realName;
    // 性别（0:女, 1:男）
    private Integer gender;
    // 手机号
    private String phone;
    // 学院
    private String college;
    // 学校
    private String school;
    // 工号
    private String teacherNo;
}

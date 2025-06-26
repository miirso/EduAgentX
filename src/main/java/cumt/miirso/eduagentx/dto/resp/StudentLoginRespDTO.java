package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

/**
 * @Package cumt.miirso.eduagentx.dto.resp
 * @Author miirso
 * @Date 2025/5/30 0:32
 */

@Data
public class StudentLoginRespDTO {
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
    // 邮箱
    private String email;
    // 专业代码
    private String majorCode;
    // 班级代码
    private String classCode;
    // 学院
    private String college;
    // 学校
    private String school;
    // 学号
    private String studentNo;
}

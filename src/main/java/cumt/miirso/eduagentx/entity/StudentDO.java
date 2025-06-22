package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Package cumt.miirso.eduagentx.entity
 * @Author miirso
 * @Date 2025/5/29 22:02
 */

@Data
@TableName("student")
public class StudentDO extends BaseDO {

    /**
     * ID (Primary Key)
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Username
     */
    private String username;


    /**
     * Student Number
     */
    private String studentNo;

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
     * Email Address
     */
    private String email;

    /**
     * Major Code
     */
    private String majorCode;    /**
     * Class Code
     * 存储格式：专业名称+年级+班号，例如"计算机科学与技术22-1班"
     */
    private String classCode;

    /**
     * College
     */
    private String college;

    /**
     * School
     */
    private String school;

    /**
     * Password
     */
    private String password;

}

package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;

/**
 * @Package cumt.miirso.eduagentx.entity
 * @Author miirso
 * @Date 2025/5/29 22:03
 */

@Data
@TableName("teacher")
public class TeacherDO extends BaseDO {

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
     * RealName
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
    private String school;    /**
     * College
     */
    private String college;

    /**
     * Password
     */
    private String password;

}

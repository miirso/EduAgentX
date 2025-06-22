package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Package cumt.miirso.eduagentx.entity
 * @Author miirso
 * @Date 2025/5/29 21:55
 */

@Data
@TableName("user")
public class UserDO {

    /**
     * id
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户类型
     * 0: 管理员
     * 1: 学生
     * 2: 教师
     */
    private Integer type;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    public UserDO() {}

}

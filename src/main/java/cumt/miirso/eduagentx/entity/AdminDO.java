package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Package cumt.miirso.eduagentx.entity
 * @Author miirso
 * @Date 2025/5/29 22:03
 */

@Data
@AllArgsConstructor
@TableName("admin")
public class AdminDO extends BaseDO {

    /**
     * ID (Primary Key)
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;



}




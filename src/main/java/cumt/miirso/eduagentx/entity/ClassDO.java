package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 班级表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("classes")
public class ClassDO extends BaseDO {    /**
     * id
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 班级名称
     */
    private String name;

    /**
     * 专业ID
     */
    private String majorId;
}

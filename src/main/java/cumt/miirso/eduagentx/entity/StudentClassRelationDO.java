package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生班级关联表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_class_relation")
public class StudentClassRelationDO extends BaseDO {
    
    /**
     * id
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 学号
     */
    private String studentNo;
    
    /**
     * 班级代码
     */
    private String classCode;
}

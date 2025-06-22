package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 课程班级关联表实体类
 */
@Data
@TableName("course_classes")
public class CourseClassDO {

    /**
     * id
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 班级ID
     */
    private Long classId;
}

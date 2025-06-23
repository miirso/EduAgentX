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
    private Long id;    /**
     * 课程ID
     */
    private String courseId;    /**
     * 班级名称 (如：计算机科学与技术22-1班)
     * 对应 student 表中的 class_code 和 classes 表中的 name
     */
    private String className;
}

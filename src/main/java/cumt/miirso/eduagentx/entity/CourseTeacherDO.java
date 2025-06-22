package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 课程教师关联表实体类
 */
@Data
@TableName("course_teachers")
public class CourseTeacherDO {

    /**
     * id
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程ID
     */
    private String courseId;    /**
     * 教师ID
     */
    private String teacherId;
}

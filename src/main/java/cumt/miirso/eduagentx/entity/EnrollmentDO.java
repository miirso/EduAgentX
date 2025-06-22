package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 学生选课表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("enrollments")
public class EnrollmentDO extends BaseDO {

    /**
     * id
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 选课日期
     */
    private LocalDateTime enrollmentDate;
}

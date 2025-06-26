package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("course_evaluation")
public class CourseEvaluationDO {
    /**
     * 评价ID，对应course_evaluation表id主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 课程ID，对应course_evaluation表course_id
     */
    private String courseId;
    /**
     * 评价学生ID，对应course_evaluation表student_id
     */
    private Long studentId;
    /**
     * 教学质量评分，对应course_evaluation表teaching_score
     */
    private Integer teachingQuality;
    /**
     * 内容丰富度评分，对应course_evaluation表content_score
     */
    private Integer contentQuality;
    /**
     * 互动性评分（如有），如无可删除
     */
    private Integer interaction;
    /**
     * 课业负担评分（如有），如无可删除
     */
    private Integer workload;
    /**
     * 收获程度评分，对应course_evaluation表harvest_score
     */
    private Integer gain;
    /**
     * 评价内容，对应course_evaluation表comment
     */
    private String comment;
    /**
     * 评价时间，对应course_evaluation表create_time
     */
    private Date createTime;
}

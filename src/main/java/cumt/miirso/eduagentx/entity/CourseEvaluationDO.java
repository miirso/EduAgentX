package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("course_evaluation")
public class CourseEvaluationDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String courseId;
    private Long studentId;
    private Integer teachingQuality;
    private Integer contentQuality;
    private Integer interaction;
    private Integer workload;
    private Integer gain;
    private String comment;
    private Date createTime;
}

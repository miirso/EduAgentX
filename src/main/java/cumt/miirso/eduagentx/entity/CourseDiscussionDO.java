package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("course_discussion")
public class CourseDiscussionDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String courseId;
    private Long userId;
    private String userRole; // student/teacher
    private String content;
    private Date createTime;
}

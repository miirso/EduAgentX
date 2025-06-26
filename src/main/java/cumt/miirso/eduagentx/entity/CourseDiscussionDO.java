package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("course_discussion")
public class CourseDiscussionDO {
    /**
     * 讨论ID，对应course_discussion表id主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 课程ID，对应course_discussion表course_id
     */
    private String courseId;
    /**
     * 发言用户ID（学生/教师），对应course_discussion表user_id
     */
    private Long userId;
    /**
     * 用户角色 student/teacher，对应course_discussion表user_role
     */
    private String userRole;
    /**
     * 讨论内容，对应course_discussion表content
     */
    private String content;
    /**
     * 发言时间，对应course_discussion表create_time
     */
    private Date createTime;
}

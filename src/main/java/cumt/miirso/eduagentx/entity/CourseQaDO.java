package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("course_qa")
public class CourseQaDO {
    /**
     * 问题ID，对应course_qa表id主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 课程ID，对应course_qa表course_id
     */
    private String courseId;
    /**
     * 提问学生ID，对应course_qa表student_id
     */
    private Long studentId;
    /**
     * 问题内容，对应course_qa表question_content
     */
    private String questionContent;
    /**
     * 是否被教师收录，1=收录，0=未收录，对应course_qa表is_collected
     */
    private Integer isCollected;
    /**
     * 问题状态，如open/solved/collected，对应course_qa表status
     */
    private String status;
    /**
     * 创建时间，对应course_qa表create_time
     */
    private Date createTime;
    /**
     * 更新时间，对应course_qa表update_time
     */
    private Date updateTime;
}

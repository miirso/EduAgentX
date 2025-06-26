package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("course_qa_answer")
public class CourseQaAnswerDO {
    /**
     * 回答ID，对应course_qa_answer表id主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 所属问题ID，对应course_qa_answer表qa_id，关联course_qa表id
     */
    private Long qaId;
    /**
     * 回答者ID（学生/教师），对应course_qa_answer表answerer_id
     */
    private Long answererId;
    /**
     * 回答者类型 student/teacher，对应course_qa_answer表answerer_type
     */
    private String answererType;
    /**
     * 回答内容，对应course_qa_answer表content
     */
    private String content;
    /**
     * 回答创建时间，对应course_qa_answer表create_time
     */
    private Date createTime;
    /**
     * 回答更新时间，对应course_qa_answer表update_time
     */
    private Date updateTime;
    /**
     * 回答状态，如active/deleted，对应course_qa_answer表status
     */
    private String status;
}

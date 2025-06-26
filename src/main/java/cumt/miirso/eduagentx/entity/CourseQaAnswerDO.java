package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("course_qa_answer")
public class CourseQaAnswerDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long qaId;
    private Long answererId;
    private String answererType; // student/teacher
    private String content;
    private Date createTime;
    private Date updateTime;
    private String status;
}

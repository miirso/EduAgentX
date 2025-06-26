package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("course_qa")
public class CourseQaDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String courseId;
    private Long studentId;
    private String questionContent;
    private String answerContent;
    private Long answerUserId;
    private Integer isCollected; // 1=收录, 0=未收录
    private String status; // open/solved/collected
    private Date createTime;
    private Date updateTime;
}

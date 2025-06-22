package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 课程序列号实体类
 */
@Data
@TableName("course_sequence")
public class CourseSequenceDO {
    
    /**
     * 学科前缀（2位）
     */
    @TableId
    private String subjectPrefix;
    
    /**
     * 当前序列号
     */
    private Integer currentSequence;
}

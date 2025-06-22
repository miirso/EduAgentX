package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("courses")
public class CourseDO extends BaseDO {

    /**
     * 课程ID
     * 主键，格式为8位字符：专业2位+中间5位+类型（大写字母）
     */
    @TableId
    private String id;

    /**
     * 学科ID
     */
    private Integer subjectId;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 课程封面图片URL
     */
    private String coverImage;

    /**
     * 开始日期
     */
    private java.util.Date startDate;

    /**
     * 结束日期
     */
    private java.util.Date endDate;    /**
     * 考核方式
     */
    private String assessmentMethod;

    /**
     * 课程类型（1位字符，用于课程ID末尾）
     */
    private String type;
}

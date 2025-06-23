package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程简介实体类
 * @author miirso
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_introductions")
public class CourseIntroductionDO extends BaseDO {

    /**
     * 简介主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 课程简介纯文本内容
     */
    private String introductionText;

    /**
     * 课程简介HTML格式内容，支持富文本编辑
     */
    private String introductionHtml;

    /**
     * 课程关键词，用逗号分隔
     */
    private String keywords;

    /**
     * 学习目标描述
     */
    private String learningGoals;

    /**
     * 先修课程或知识要求
     */
    private String prerequisites;

    /**
     * 目标受众描述
     */
    private String targetAudience;

    /**
     * 课程难度等级
     */
    private String difficultyLevel;

    /**
     * 预计学习时长（小时）
     */
    private Integer estimatedHours;
}

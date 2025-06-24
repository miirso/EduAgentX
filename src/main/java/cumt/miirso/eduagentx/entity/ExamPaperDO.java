package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试卷实体类
 * @author miirso
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_papers")
public class ExamPaperDO extends BaseDO {

    /**
     * 试卷ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 章节ID（可为空，表示整个课程的综合试卷）
     */
    private Integer chapterId;

    /**
     * 试卷名称
     */
    private String paperName;

    /**
     * 试卷类型：chapter(章节测试)、course(课程测试)、final(期末考试)
     */
    private String paperType;

    /**
     * 试卷总题数
     */
    private Integer totalQuestions;

    /**
     * 试卷总分
     */
    private Integer totalScore;

    /**
     * 考试时间限制（分钟）
     */
    private Integer timeLimit;

    /**
     * 难度等级：easy、medium、hard
     */
    private String difficulty;

    /**
     * 状态：active、inactive、deleted
     */
    private String status;

    /**
     * 是否AI生成
     */
    private Boolean aiGenerated;

    /**
     * AI生成时使用的提示词
     */
    private String generationPrompt;
}

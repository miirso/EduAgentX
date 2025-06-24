package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 试卷详情响应DTO
 * @author miirso
 */
@Data
public class ExamPaperDetailRespDTO {

    /**
     * 试卷ID
     */
    private Integer id;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 章节ID
     */
    private Integer chapterId;

    /**
     * 试卷名称
     */
    private String paperName;

    /**
     * 试卷类型
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
     * 难度等级
     */
    private String difficulty;

    /**
     * 状态
     */
    private String status;

    /**
     * 是否AI生成
     */
    private Boolean aiGenerated;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 题目列表
     */
    private List<QuestionDetail> questions;

    /**
     * 题目详情
     */
    @Data
    public static class QuestionDetail {
        /**
         * 题目ID
         */
        private Integer id;

        /**
         * 题目类型
         */
        private String questionType;

        /**
         * 题目内容
         */
        private String questionText;

        /**
         * 题目顺序
         */
        private Integer questionOrder;

        /**
         * 题目分值
         */
        private Integer score;

        /**
         * 难度等级
         */
        private String difficulty;

        /**
         * 选项列表
         */
        private List<OptionDetail> options;
    }

    /**
     * 选项详情
     */
    @Data
    public static class OptionDetail {
        /**
         * 选项ID
         */
        private Integer id;

        /**
         * 选项标签
         */
        private String optionLabel;

        /**
         * 选项内容
         */
        private String optionText;

        /**
         * 选项顺序
         */
        private Integer optionOrder;
    }
}

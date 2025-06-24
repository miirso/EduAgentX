package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

import java.util.List;

/**
 * 保存试题请求DTO
 * @author miirso
 */
@Data
public class SaveQuestionsReqDTO {

    /**
     * 试卷ID（可为空，表示新建试卷）
     */
    private Integer paperId;

    /**
     * 试卷信息（paperId为空时必填）
     */
    private PaperInfo paperInfo;

    /**
     * 题目列表
     */
    private List<QuestionInfo> questions;

    /**
     * 试卷信息
     */
    @Data
    public static class PaperInfo {
        /**
         * 课程ID
         */
        private String courseId;

        /**
         * 章节ID（可为空）
         */
        private Integer chapterId;

        /**
         * 试卷名称
         */
        private String paperName;

        /**
         * 试卷类型：chapter、course、final
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
         * AI生成提示词
         */
        private String generationPrompt;
    }

    /**
     * 题目信息
     */
    @Data
    public static class QuestionInfo {
        /**
         * 题目类型：single_choice、multiple_choice、short_answer
         */
        private String questionType;

        /**
         * 题目内容
         */
        private String questionText;

        /**
         * 题目在试卷中的顺序
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
         * 选项列表（单选题和多选题必填）
         */
        private List<OptionInfo> options;

        /**
         * 正确答案
         */
        private String correctAnswer;

        /**
         * 答案解析
         */
        private String answerExplanation;
    }

    /**
     * 选项信息
     */
    @Data
    public static class OptionInfo {
        /**
         * 选项标签（A、B、C、D等）
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

        /**
         * 是否为正确答案
         */
        private Boolean isCorrect;
    }
}

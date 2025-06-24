package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

import java.util.List;

/**
 * AI生成试题响应DTO
 * @author miirso
 */
@Data
public class GenerateQuestionsRespDTO {

    /**
     * 试卷ID
     */
    private Integer paperId;

    /**
     * 试卷名称
     */
    private String paperName;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 章节ID
     */
    private Integer chapterId;

    /**
     * 生成的题目列表
     */
    private List<QuestionDTO> questions;

    /**
     * 题目数量
     */
    private Integer questionCount;

    /**
     * 消息
     */
    private String message;

    /**
     * 试题DTO
     */
    @Data
    public static class QuestionDTO {
        /**
         * 题目ID
         */
        private Integer id;

        /**
         * 题目类型：single_choice、multiple_choice、short_answer
         */
        private String questionType;

        /**
         * 题目内容
         */
        private String questionText;

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
        private List<OptionDTO> options;

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
     * 选项DTO
     */
    @Data
    public static class OptionDTO {
        /**
         * 选项ID
         */
        private Integer id;

        /**
         * 选项标签（A、B、C、D等）
         */
        private String optionLabel;

        /**
         * 选项内容
         */
        private String optionText;

        /**
         * 是否为正确答案
         */
        private Boolean isCorrect;
    }
}

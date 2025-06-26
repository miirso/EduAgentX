package cumt.miirso.eduagentx.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考试统计响应DTO
 * @author miirso
 */
@Data
public class ExamStatisticsRespDTO {

    /**
     * 统计类型：course、paper、student
     */
    private String statisticsType;

    /**
     * 统计目标ID（课程ID、试卷ID或学生ID）
     */
    private String targetId;

    /**
     * 总体正确率
     */
    private BigDecimal overallAccuracyRate;

    /**
     * 试卷ID（仅试卷统计时有值）
     */
    private Integer paperId;

    /**
     * 试卷名称（仅试卷统计时有值）
     */
    private String paperName;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 学生ID（仅学生统计时有值）
     */
    private Long studentId;

    /**
     * 学生姓名（仅学生统计时有值）
     */
    private String studentName;

    /**
     * 参与人数
     */
    private Integer participantCount;

    /**
     * 完成人数
     */
    private Integer completedCount;

    /**
     * 考试记录数量
     */
    private Integer examCount;

    /**
     * 学生数量
     */
    private Integer studentCount;

    /**
     * 总分
     */
    private Integer totalScore;

    /**
     * 平均分
     */
    private BigDecimal averageScore;

    /**
     * 最高分
     */
    private Integer highestScore;

    /**
     * 最低分
     */
    private Integer lowestScore;

    /**
     * 及格率
     */
    private BigDecimal passRate;

    /**
     * 平均正确率
     */
    private BigDecimal averageAccuracyRate;

    /**
     * 各章节正确率（仅课程统计时有值）
     */
    private List<ChapterAccuracyDTO> chapterAccuracyList;

    /**
     * 题目正确率列表（仅试卷统计时有值）
     */
    private List<QuestionAccuracyDTO> questionAccuracyList;

    /**
     * 试卷统计列表（仅课程统计时有值）
     */
    private List<PaperStatisticsDetail> paperStatisticsList = new ArrayList<>();

    /**
     * 学生分数分布（仅试卷统计或课程统计时有值）
     */
    private Map<String, Integer> scoreDistribution = new HashMap<>();

    /**
     * 错误率最高的题目列表
     */
    private List<ProblemQuestionDTO> problemQuestionList;

    /**
     * 章节正确率DTO
     */
    @Data
    public static class ChapterAccuracyDTO {
        /**
         * 章节ID
         */
        private Integer chapterId;

        /**
         * 章节名称
         */
        private String chapterName;

        /**
         * 正确率
         */
        private BigDecimal accuracyRate;

        /**
         * 答题次数
         */
        private Integer answerCount;
    }

    /**
     * 题目正确率DTO
     */
    @Data
    @Setter
    @Getter
    @AllArgsConstructor
    public static class QuestionAccuracyDTO {
        /**
         * 题目ID
         */
        private Integer questionId;

        /**
         * 题目类型
         */
        private String questionType;

        /**
         * 题目内容（简略版）
         */
        private String questionText;

        /**
         * 正确率
         */
        private BigDecimal accuracyRate;

        /**
         * 答题次数
         */
        private Integer answerCount;
    }

    /**
     * 试卷统计详情
     */
    @Data
    public static class PaperStatisticsDetail {
        /**
         * 试卷ID
         */
        private Integer paperId;
        
        /**
         * 试卷名称
         */
        private String paperName;
        
        /**
         * 学生数量
         */
        private Integer studentCount;
        
        /**
         * 平均分
         */
        private BigDecimal averageScore;
        
        /**
         * 最高分
         */
        private Integer highestScore;
    }

    /**
     * 问题题目DTO
     */
    @Data
    public static class ProblemQuestionDTO {
        /**
         * 题目ID
         */
        private Integer questionId;

        /**
         * 题目类型
         */
        private String questionType;

        /**
         * 题目内容
         */
        private String questionText;

        /**
         * 错误率
         */
        private BigDecimal errorRate;

        /**
         * 常见错误答案
         */
        private List<String> commonWrongAnswers;

        /**
         * 正确答案
         */
        private String correctAnswer;

        /**
         * 答案解析
         */
        private String answerExplanation;
    }
}

package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 学生试卷统计数据响应DTO
 * 
 * @author miirso
 */
@Data
public class StudentExamStatisticsRespDTO {
    
    /**
     * 统计类型（student_paper）
     */
    private String statisticsType;
    
    /**
     * 目标ID（学生ID）
     */
    private String targetId;
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
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
     * 课程名称
     */
    private String courseName;
    
    /**
     * 答题记录ID
     */
    private Integer examRecordId;
    
    /**
     * 总分
     */
    private Integer totalScore;
    
    /**
     * 满分
     */
    private Integer fullScore;
    
    /**
     * 正确题数
     */
    private Integer correctCount;
    
    /**
     * 错误题数
     */
    private Integer wrongCount;
    
    /**
     * 总题数
     */
    private Integer totalQuestions;
    
    /**
     * 正确率
     */
    private BigDecimal accuracyRate;
    
    /**
     * 排名（在该试卷中的排名）
     */
    private Integer ranking;
    
    /**
     * 用时（分钟）
     */
    private Integer timeSpent;
    
    /**
     * 开始时间
     */
    private Date startTime;
    
    /**
     * 提交时间
     */
    private Date submitTime;
    
    /**
     * 题目答题详情
     */
    private List<QuestionAnswerDetail> questionDetails;
    
    /**
     * 按题型分类的正确率
     */
    private Map<String, BigDecimal> questionTypeAccuracyRates;
    
    /**
     * 按难度分类的正确率
     */
    private Map<String, BigDecimal> difficultyAccuracyRates;
    
    /**
     * 强项和弱项分析
     */
    private StrengthWeaknessAnalysis strengthWeaknessAnalysis;
    
    /**
     * 题目答题详情
     */
    @Data
    public static class QuestionAnswerDetail {
        /**
         * 题目ID
         */
        private Integer questionId;
        
        /**
         * 题目类型
         */
        private String questionType;
        
        /**
         * 题目内容（可能被截断）
         */
        private String questionText;
        
        /**
         * 题目分值
         */
        private Integer score;
        
        /**
         * 实际得分
         */
        private Integer scoreGained;
        
        /**
         * 是否正确
         */
        private Boolean isCorrect;
        
        /**
         * 学生答案
         */
        private String studentAnswer;
        
        /**
         * 正确答案
         */
        private String correctAnswer;
        
        /**
         * 答题用时（秒）
         */
        private Integer answerTime;
    }
    
    /**
     * 强项和弱项分析
     */
    @Data
    public static class StrengthWeaknessAnalysis {
        /**
         * 强项题型
         */
        private String strengthQuestionType;
        
        /**
         * 强项题型正确率
         */
        private BigDecimal strengthAccuracyRate;
        
        /**
         * 弱项题型
         */
        private String weaknessQuestionType;
        
        /**
         * 弱项题型正确率
         */
        private BigDecimal weaknessAccuracyRate;
        
        /**
         * 改进建议
         */
        private String improvementSuggestion;
    }
}

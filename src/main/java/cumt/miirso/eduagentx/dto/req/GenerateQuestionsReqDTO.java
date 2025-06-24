package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

import java.util.List;

/**
 * AI生成试题请求DTO
 * @author miirso
 */
@Data
public class GenerateQuestionsReqDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 章节ID
     */
    private Integer chapterId;

    /**
     * 题目类型列表：single_choice、multiple_choice、short_answer
     */
    private List<String> questionTypes;

    /**
     * 题目数量
     */
    private Integer count;

    /**
     * 难度等级：easy、medium、hard
     */
    private String difficulty;

    /**
     * 自定义提示词
     */
    private String customPrompt;
}

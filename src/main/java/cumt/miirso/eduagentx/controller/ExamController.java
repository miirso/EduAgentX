package cumt.miirso.eduagentx.controller;

import cumt.miirso.eduagentx.convention.result.Result;
import cumt.miirso.eduagentx.convention.result.Results;
import cumt.miirso.eduagentx.dto.req.SaveQuestionsReqDTO;
import cumt.miirso.eduagentx.dto.req.StartExamReqDTO;
import cumt.miirso.eduagentx.dto.req.SubmitAnswerReqDTO;
import cumt.miirso.eduagentx.dto.req.SubmitExamReqDTO;
import cumt.miirso.eduagentx.dto.req.SaveSuggestionReqDTO;
import cumt.miirso.eduagentx.dto.resp.ExamPaperDetailRespDTO;
import cumt.miirso.eduagentx.dto.resp.QuestionAnswerRespDTO;
import cumt.miirso.eduagentx.dto.resp.SaveQuestionsRespDTO;
import cumt.miirso.eduagentx.dto.resp.StartExamRespDTO;
import cumt.miirso.eduagentx.dto.resp.SubmitAnswerRespDTO;
import cumt.miirso.eduagentx.dto.resp.SubmitExamRespDTO;
import cumt.miirso.eduagentx.dto.resp.AITeachingSuggestionRespDTO;
import cumt.miirso.eduagentx.dto.resp.ExamStatisticsRespDTO;
import cumt.miirso.eduagentx.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考试系统控制器
 * @author miirso
 */
@RestController
@RequestMapping("/api/eduagentx/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    /**
     * 保存试题接口
     * @param requestParam 保存试题请求参数
     * @return 保存结果
     */
    @PostMapping("/save-questions")
    public Result<SaveQuestionsRespDTO> saveQuestions(@RequestBody SaveQuestionsReqDTO requestParam) {
        return Results.success(examService.saveQuestions(requestParam));
    }

    /**
     * 获取试题答案接口
     * @param questionId 题目ID
     * @return 正确答案信息
     */
    @GetMapping("/get-answer/{questionId}")
    public Result<QuestionAnswerRespDTO> getQuestionAnswer(@PathVariable Integer questionId) {
        return Results.success(examService.getQuestionAnswer(questionId));
    }

    /**
     * 获取试卷信息接口
     * @param paperId 试卷ID
     * @return 试卷信息
     */
    @GetMapping("/paper/{paperId}")
    public Result<ExamPaperDetailRespDTO> getPaper(@PathVariable Integer paperId) {
        return Results.success(examService.getPaperDetail(paperId));
    }

    /**
     * 查询课程试卷列表
     * @param courseId 课程ID
     * @return 试卷列表
     */
    @GetMapping("/papers/course/{courseId}")
    public Result<List<ExamPaperDetailRespDTO>> getCoursePapers(@PathVariable String courseId) {
        return Results.success(examService.getCoursePapers(courseId));
    }

    /**
     * 查询章节试卷列表
     * @param chapterId 章节ID
     * @return 试卷列表
     */
    @GetMapping("/papers/chapter/{chapterId}")
    public Result<List<ExamPaperDetailRespDTO>> getChapterPapers(@PathVariable Integer chapterId) {
        return Results.success(examService.getChapterPapers(chapterId));
    }

    /**
     * 学生开始答题接口
     * @param requestParam 开始答题请求参数
     * @return 答题记录ID、试卷内容
     */
    @PostMapping("/start-exam")
    public Result<StartExamRespDTO> startExam(@RequestBody StartExamReqDTO requestParam) {
        return Results.success(examService.startExam(requestParam));
    }

    /**
     * 提交答案接口
     * @param requestParam 提交答案请求参数
     * @return 是否正确、得分
     */
    @PostMapping("/submit-answer")
    public Result<SubmitAnswerRespDTO> submitAnswer(@RequestBody SubmitAnswerReqDTO requestParam) {
        return Results.success(examService.submitAnswer(requestParam));
    }

    /**
     * 提交试卷接口
     * @param requestParam 提交试卷请求参数
     * @return 总得分、正确率等统计信息
     */
    @PostMapping("/submit-exam")
    public Result<SubmitExamRespDTO> submitExam(@RequestBody SubmitExamReqDTO requestParam) {
        return Results.success(examService.submitExam(requestParam));
    }

    /**
     * 课程试题统计接口
     * @param courseId 课程ID
     * @return 整体正确率、各章节正确率、热点错误题目
     */
    @GetMapping("/statistics/course/{courseId}")
    public Result<ExamStatisticsRespDTO> getCourseStatistics(@PathVariable String courseId) {
        return Results.success(examService.getCourseStatistics(courseId));
    }

    /**
     * 试卷统计接口
     * @param paperId 试卷ID
     * @return 试卷正确率、各题目正确率、学生分数分布
     */
    @GetMapping("/statistics/paper/{paperId}")
    public Result<ExamStatisticsRespDTO> getPaperStatistics(@PathVariable Integer paperId) {
        return Results.success(examService.getPaperStatistics(paperId));
    }

    /**
     * 学生答题统计
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 学生在该课程的整体表现
     */
    @GetMapping("/statistics/student/{studentId}/course/{courseId}")
    public Result<ExamStatisticsRespDTO> getStudentStatistics(@PathVariable Long studentId, @PathVariable String courseId) {
        return Results.success(examService.getStudentStatistics(studentId, courseId));
    }

    /**
     * 保存AI建议接口
     * @param requestParam 保存AI建议请求参数
     * @return 保存结果
     */
    @PostMapping("/save-suggestion")
    public Result<Boolean> saveSuggestion(@RequestBody SaveSuggestionReqDTO requestParam) {
        return Results.success(examService.saveSuggestion(requestParam));
    }

    /**
     * 获取AI建议接口
     * @param type 建议类型
     * @param targetId 目标ID（课程ID或试卷ID）
     * @return AI建议列表
     */
    @GetMapping("/suggestions/{type}/{targetId}")
    public Result<List<AITeachingSuggestionRespDTO>> getSuggestions(@PathVariable String type, @PathVariable String targetId) {
        return Results.success(examService.getSuggestions(type, targetId));
    }

    /**
     * 删除试卷
     * @param paperId 试卷ID
     * @return 删除是否成功
     */
    @DeleteMapping("/paper/{paperId}")
    public Result<Boolean> deletePaper(@PathVariable Integer paperId) {
        return Results.success(examService.deletePaper(paperId));
    }
}

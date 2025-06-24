package cumt.miirso.eduagentx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cumt.miirso.eduagentx.dto.req.SaveQuestionsReqDTO;
import cumt.miirso.eduagentx.dto.req.SaveSuggestionReqDTO;
import cumt.miirso.eduagentx.dto.req.StartExamReqDTO;
import cumt.miirso.eduagentx.dto.req.SubmitAnswerReqDTO;
import cumt.miirso.eduagentx.dto.req.SubmitExamReqDTO;
import cumt.miirso.eduagentx.dto.resp.*;
import cumt.miirso.eduagentx.entity.ExamPaperDO;

import java.util.List;

/**
 * 试题管理服务接口
 * @author miirso
 */
public interface ExamService extends IService<ExamPaperDO> {

    /**
     * 保存AI生成的试题
     * 
     * 实现步骤：
     * 1. 验证请求参数
     * 2. 创建或获取试卷信息
     * 3. 保存题目、选项、答案
     * 4. 更新试卷统计信息
     * 5. 返回保存结果
     * 
     * @param requestParam 保存试题请求参数
     * @return 保存结果
     */
    SaveQuestionsRespDTO saveQuestions(SaveQuestionsReqDTO requestParam);

    /**
     * 获取试卷详情
     * 
     * 实现步骤：
     * 1. 查询试卷基本信息
     * 2. 查询试卷下的所有题目
     * 3. 查询题目的选项信息
     * 4. 组装返回数据
     * 
     * @param paperId 试卷ID
     * @return 试卷详情
     */
    ExamPaperDetailRespDTO getPaperDetail(Integer paperId);

    /**
     * 获取课程试卷列表
     * 
     * 实现步骤：
     * 1. 根据课程ID查询试卷列表
     * 2. 按创建时间倒序排列
     * 3. 返回试卷基本信息
     * 
     * @param courseId 课程ID
     * @return 试卷列表
     */
    List<ExamPaperDetailRespDTO> getCoursePapers(String courseId);

    /**
     * 获取章节试卷列表
     * 
     * 实现步骤：
     * 1. 根据章节ID查询试卷列表
     * 2. 返回试卷基本信息
     * 
     * @param chapterId 章节ID
     * @return 试卷列表
     */
    List<ExamPaperDetailRespDTO> getChapterPapers(Integer chapterId);

    /**
     * 学生开始答题
     * 
     * 实现步骤：
     * 1. 验证学生和试卷信息
     * 2. 检查是否已有进行中的答题记录
     * 3. 创建新的答题记录
     * 4. 返回试卷详情和答题记录ID
     * 
     * @param requestParam 开始答题请求参数
     * @return 开始答题结果
     */
    StartExamRespDTO startExam(StartExamReqDTO requestParam);

    /**
     * 提交单题答案
     * 
     * 实现步骤：
     * 1. 验证答题记录和题目
     * 2. 获取正确答案进行判分
     * 3. 保存答题详情
     * 4. 返回判分结果
     * 
     * @param requestParam 提交答案请求参数
     * @return 答题结果
     */
    SubmitAnswerRespDTO submitAnswer(SubmitAnswerReqDTO requestParam);

    /**
     * 提交整份试卷
     * 
     * 实现步骤：
     * 1. 验证答题记录
     * 2. 统计答题情况
     * 3. 计算总分和正确率
     * 4. 更新答题记录状态
     * 5. 返回统计结果
     * 
     * @param requestParam 提交试卷请求参数
     * @return 试卷提交结果
     */
    SubmitExamRespDTO submitExam(SubmitExamReqDTO requestParam);

    /**
     * 获取题目答案（用于判分）
     * 
     * 实现步骤：
     * 1. 根据题目ID查询答案
     * 2. 返回正确答案和解析
     * 
     * @param questionId 题目ID
     * @return 题目答案
     */
    QuestionAnswerRespDTO getQuestionAnswer(Integer questionId);    /**
     * 删除试卷
     * 
     * 实现步骤：
     * 1. 验证试卷ID
     * 2. 检查是否有学生答题记录
     * 3. 执行逻辑删除
     * 
     * @param paperId 试卷ID
     * @return 删除是否成功
     */
    Boolean deletePaper(Integer paperId);
    
    /**
     * 获取课程统计数据
     * 
     * 实现步骤：
     * 1. 查询课程下的所有试卷和答题记录
     * 2. 计算整体正确率
     * 3. 计算各章节正确率
     * 4. 查找错误率最高的题目
     * 
     * @param courseId 课程ID
     * @return 课程统计数据
     */
    ExamStatisticsRespDTO getCourseStatistics(String courseId);
    
    /**
     * 获取试卷统计数据
     * 
     * 实现步骤：
     * 1. 查询试卷及其所有题目
     * 2. 查询所有答题记录
     * 3. 计算整体正确率
     * 4. 计算各题目正确率
     * 5. 生成分数分布
     * 
     * @param paperId 试卷ID
     * @return 试卷统计数据
     */
    ExamStatisticsRespDTO getPaperStatistics(Integer paperId);
    
    /**
     * 获取学生统计数据
     * 
     * 实现步骤：
     * 1. 查询学生在该课程的所有答题记录
     * 2. 计算整体正确率
     * 3. 查找学生常错题目
     * 
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 学生统计数据
     */
    ExamStatisticsRespDTO getStudentStatistics(Long studentId, String courseId);
    
    /**
     * 保存AI教学建议
     * 
     * 实现步骤：
     * 1. 验证请求参数
     * 2. 保存建议到数据库
     * 
     * @param requestParam 保存建议请求参数
     * @return 保存是否成功
     */
    Boolean saveSuggestion(SaveSuggestionReqDTO requestParam);
    
    /**
     * 获取AI教学建议
     * 
     * 实现步骤：
     * 1. 根据类型和目标ID查询建议
     * 2. 返回建议列表
     * 
     * @param type 建议类型
     * @param targetId 目标ID（课程ID或试卷ID）
     * @return AI建议列表
     */
    List<AITeachingSuggestionRespDTO> getSuggestions(String type, String targetId);
}

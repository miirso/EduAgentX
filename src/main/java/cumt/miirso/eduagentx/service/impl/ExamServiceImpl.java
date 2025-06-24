package cumt.miirso.eduagentx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cumt.miirso.eduagentx.dto.req.SaveQuestionsReqDTO;
import cumt.miirso.eduagentx.dto.req.SaveSuggestionReqDTO;
import cumt.miirso.eduagentx.dto.req.StartExamReqDTO;
import cumt.miirso.eduagentx.dto.req.SubmitAnswerReqDTO;
import cumt.miirso.eduagentx.dto.req.SubmitExamReqDTO;
import cumt.miirso.eduagentx.dto.resp.*;
import cumt.miirso.eduagentx.entity.ExamPaperDO;
import cumt.miirso.eduagentx.entity.ExamQuestionAnswerDO;
import cumt.miirso.eduagentx.entity.ExamQuestionDO;
import cumt.miirso.eduagentx.entity.ExamQuestionOptionDO;
import cumt.miirso.eduagentx.entity.StudentExamRecordDO;
import cumt.miirso.eduagentx.entity.StudentAnswerDetailDO;
import cumt.miirso.eduagentx.mapper.ExamPaperMapper;
import cumt.miirso.eduagentx.mapper.ExamQuestionAnswerMapper;
import cumt.miirso.eduagentx.mapper.ExamQuestionMapper;
import cumt.miirso.eduagentx.mapper.ExamQuestionOptionMapper;
import cumt.miirso.eduagentx.mapper.StudentExamRecordMapper;
import cumt.miirso.eduagentx.mapper.StudentAnswerDetailMapper;
import cumt.miirso.eduagentx.service.ExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 试题管理服务实现类
 * 
 * @author miirso
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExamServiceImpl extends ServiceImpl<ExamPaperMapper, ExamPaperDO> implements ExamService {

    private final ExamQuestionMapper examQuestionMapper;
    private final ExamQuestionOptionMapper examQuestionOptionMapper;
    private final ExamQuestionAnswerMapper examQuestionAnswerMapper;
    private final StudentExamRecordMapper studentExamRecordMapper;
    private final StudentAnswerDetailMapper studentAnswerDetailMapper;

    /**
     * 保存试题
     * @param requestParam 保存试题请求参数
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaveQuestionsRespDTO saveQuestions(SaveQuestionsReqDTO requestParam) {
        log.info("=== 开始执行[保存试题] ===");
        log.info("参数信息: {}", requestParam);
        
        // 1. 验证请求参数
        if (requestParam.getPaperId() == null && requestParam.getPaperInfo() == null) {
            log.error("试卷ID和试卷信息不能同时为空");
            throw new IllegalArgumentException("试卷ID和试卷信息不能同时为空");
        }
        
        // 记录操作类型
        String operation;
        
        // 2. 创建或获取试卷信息
        ExamPaperDO examPaper;
        if (requestParam.getPaperId() != null) {
            // 更新现有试卷
            examPaper = this.getById(requestParam.getPaperId());
            if (examPaper == null) {
                log.error("试卷不存在，ID: {}", requestParam.getPaperId());
                throw new IllegalArgumentException("试卷不存在");
            }
            operation = "更新试卷";
        } else {
            // 创建新试卷
            examPaper = new ExamPaperDO();
            SaveQuestionsReqDTO.PaperInfo paperInfo = requestParam.getPaperInfo();
            examPaper.setCourseId(paperInfo.getCourseId());
            examPaper.setChapterId(paperInfo.getChapterId());
            examPaper.setPaperName(paperInfo.getPaperName());
            examPaper.setPaperType(paperInfo.getPaperType());
            examPaper.setTotalQuestions(paperInfo.getTotalQuestions());
            examPaper.setTotalScore(paperInfo.getTotalScore());
            examPaper.setTimeLimit(paperInfo.getTimeLimit());
            examPaper.setDifficulty(paperInfo.getDifficulty());
            examPaper.setStatus("active");
            examPaper.setAiGenerated(true);
            examPaper.setGenerationPrompt(paperInfo.getGenerationPrompt());
            this.save(examPaper);
            operation = "新增试卷";
        }
        
        // 3. 保存题目、选项、答案
        List<SaveQuestionsReqDTO.QuestionInfo> questions = requestParam.getQuestions();
        for (SaveQuestionsReqDTO.QuestionInfo question : questions) {
            // 保存题目
            ExamQuestionDO examQuestion = new ExamQuestionDO();
            examQuestion.setPaperId(examPaper.getId());
            examQuestion.setQuestionType(question.getQuestionType());
            examQuestion.setQuestionText(question.getQuestionText());
            examQuestion.setQuestionOrder(question.getQuestionOrder());
            examQuestion.setScore(question.getScore());
            examQuestion.setDifficulty(question.getDifficulty());
            examQuestion.setAiGenerated(true);
            examQuestionMapper.insert(examQuestion);
            
            // 保存选项（如果有）
            if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                for (SaveQuestionsReqDTO.OptionInfo option : question.getOptions()) {
                    ExamQuestionOptionDO examQuestionOption = new ExamQuestionOptionDO();
                    examQuestionOption.setQuestionId(examQuestion.getId());
                    examQuestionOption.setOptionLabel(option.getOptionLabel());
                    examQuestionOption.setOptionText(option.getOptionText());
                    examQuestionOption.setOptionOrder(option.getOptionOrder());
                    examQuestionOption.setIsCorrect(option.getIsCorrect());
                    examQuestionOptionMapper.insert(examQuestionOption);
                }
            }
            
            // 保存答案
            ExamQuestionAnswerDO examQuestionAnswer = new ExamQuestionAnswerDO();
            examQuestionAnswer.setQuestionId(examQuestion.getId());
            examQuestionAnswer.setCorrectAnswer(question.getCorrectAnswer());
            examQuestionAnswer.setAnswerExplanation(question.getAnswerExplanation());
            examQuestionAnswerMapper.insert(examQuestionAnswer);
        }
        
        // 4. 更新试卷统计信息（如题目数量）
        examPaper.setTotalQuestions(questions.size());
        this.updateById(examPaper);
        
        // 5. 返回保存结果
        SaveQuestionsRespDTO response = new SaveQuestionsRespDTO();
        response.setPaperId(examPaper.getId());
        response.setPaperName(examPaper.getPaperName());
        response.setQuestionCount(questions.size());
        response.setOperation(operation);
        response.setOperationTime(new Date());
        response.setMessage("试题保存成功");
        
        log.info("试题保存成功，试卷ID: {}, 题目数量: {}", examPaper.getId(), questions.size());
        log.info("=== 结束执行[保存试题] ===");
        
        return response;
    }

    /**
     * 获取试卷详情
     * @param paperId 试卷ID
     * @return
     */
    @Override
    public ExamPaperDetailRespDTO getPaperDetail(Integer paperId) {
        log.info("=== 开始执行[获取试卷详情] ===");
        log.info("试卷ID: {}", paperId);
        
        // 1. 参数验证
        if (paperId == null || paperId <= 0) {
            log.error("试卷ID无效: {}", paperId);
            throw new IllegalArgumentException("试卷ID无效");
        }
        
        // 2. 查询试卷信息
        ExamPaperDO examPaper = this.getById(paperId);
        if (examPaper == null) {
            log.error("试卷不存在，ID: {}", paperId);
            throw new IllegalArgumentException("试卷不存在");
        }
        
        // 3. 查询试卷下的所有题目
        List<ExamQuestionDO> examQuestions = examQuestionMapper.selectList(
                new LambdaQueryWrapper<ExamQuestionDO>()
                        .eq(ExamQuestionDO::getPaperId, paperId)
                        .orderByAsc(ExamQuestionDO::getQuestionOrder)
        );
        
        // 4. 组装试卷基本信息
        ExamPaperDetailRespDTO response = new ExamPaperDetailRespDTO();
        response.setId(examPaper.getId());
        response.setCourseId(examPaper.getCourseId());
        response.setChapterId(examPaper.getChapterId());
        response.setPaperName(examPaper.getPaperName());
        response.setPaperType(examPaper.getPaperType());
        response.setTotalQuestions(examPaper.getTotalQuestions());
        response.setTotalScore(examPaper.getTotalScore());
        response.setTimeLimit(examPaper.getTimeLimit());
        response.setDifficulty(examPaper.getDifficulty());
        response.setStatus(examPaper.getStatus());
        response.setAiGenerated(examPaper.getAiGenerated());
        response.setCreateTime(examPaper.getCreateTime());
        
        // 5. 组装题目和选项
        List<ExamPaperDetailRespDTO.QuestionDetail> questionDetails = examQuestions.stream().map(question -> {
            ExamPaperDetailRespDTO.QuestionDetail questionDetail = new ExamPaperDetailRespDTO.QuestionDetail();
            questionDetail.setId(question.getId());
            questionDetail.setQuestionType(question.getQuestionType());
            questionDetail.setQuestionText(question.getQuestionText());
            questionDetail.setQuestionOrder(question.getQuestionOrder());
            questionDetail.setScore(question.getScore());
            questionDetail.setDifficulty(question.getDifficulty());
            
            // 查询题目选项（单选题和多选题才有选项）
            if ("single_choice".equals(question.getQuestionType()) || "multiple_choice".equals(question.getQuestionType())) {
                List<ExamQuestionOptionDO> options = examQuestionOptionMapper.selectList(
                        new LambdaQueryWrapper<ExamQuestionOptionDO>()
                                .eq(ExamQuestionOptionDO::getQuestionId, question.getId())
                                .orderByAsc(ExamQuestionOptionDO::getOptionOrder)
                );
                
                List<ExamPaperDetailRespDTO.OptionDetail> optionDetails = options.stream().map(option -> {
                    ExamPaperDetailRespDTO.OptionDetail optionDetail = new ExamPaperDetailRespDTO.OptionDetail();
                    optionDetail.setId(option.getId());
                    optionDetail.setOptionLabel(option.getOptionLabel());
                    optionDetail.setOptionText(option.getOptionText());
                    optionDetail.setOptionOrder(option.getOptionOrder());
                    return optionDetail;
                }).toList();
                
                questionDetail.setOptions(optionDetails);
            } else {
                // 简答题没有选项，设置为空列表
                questionDetail.setOptions(List.of());
            }
            
            return questionDetail;
        }).toList();
        
        response.setQuestions(questionDetails);
        
        log.info("获取试卷详情成功，试卷ID: {}, 题目数量: {}", paperId, questionDetails.size());
        log.info("=== 结束执行[获取试卷详情] ===");
        
        return response;
    }

    /**
     * 获取课程试卷列表
     * @param courseId 课程ID
     * @return 试卷列表
     */
    @Override
    public List<ExamPaperDetailRespDTO> getCoursePapers(String courseId) {
        log.info("=== 开始执行[获取课程试卷列表] ===");
        log.info("课程ID: {}", courseId);
        
        // 1. 参数验证
        if (courseId == null || courseId.isEmpty()) {
            log.error("课程ID无效: {}", courseId);
            throw new IllegalArgumentException("课程ID无效");
        }
        
        // 2. 查询课程下的所有试卷
        List<ExamPaperDO> examPapers = this.list(
                new LambdaQueryWrapper<ExamPaperDO>()
                        .eq(ExamPaperDO::getCourseId, courseId)
                        .eq(ExamPaperDO::getStatus, "active")
                        .orderByDesc(ExamPaperDO::getCreateTime)
        );
        
        if (examPapers.isEmpty()) {
            log.info("课程没有试卷，课程ID: {}", courseId);
            return List.of();
        }
        
        // 3. 组装试卷信息，不包含题目详情
        List<ExamPaperDetailRespDTO> result = examPapers.stream().map(paper -> {
            ExamPaperDetailRespDTO paperDetail = new ExamPaperDetailRespDTO();
            paperDetail.setId(paper.getId());
            paperDetail.setCourseId(paper.getCourseId());
            paperDetail.setChapterId(paper.getChapterId());
            paperDetail.setPaperName(paper.getPaperName());
            paperDetail.setPaperType(paper.getPaperType());
            paperDetail.setTotalQuestions(paper.getTotalQuestions());
            paperDetail.setTotalScore(paper.getTotalScore());
            paperDetail.setTimeLimit(paper.getTimeLimit());
            paperDetail.setDifficulty(paper.getDifficulty());
            paperDetail.setStatus(paper.getStatus());
            paperDetail.setAiGenerated(paper.getAiGenerated());
            paperDetail.setCreateTime(paper.getCreateTime());
            // 不加载题目信息，减轻接口负担
            return paperDetail;
        }).toList();
        
        log.info("获取课程试卷列表成功，课程ID: {}, 试卷数量: {}", courseId, result.size());
        log.info("=== 结束执行[获取课程试卷列表] ===");
        
        return result;
    }

    /**
     * 获取章节试卷列表
     * @param chapterId 章节ID
     * @return 试卷列表
     */
    @Override
    public List<ExamPaperDetailRespDTO> getChapterPapers(Integer chapterId) {
        log.info("=== 开始执行[获取章节试卷列表] ===");
        log.info("章节ID: {}", chapterId);
        
        // 1. 参数验证
        if (chapterId == null || chapterId <= 0) {
            log.error("章节ID无效: {}", chapterId);
            throw new IllegalArgumentException("章节ID无效");
        }
        
        // 2. 查询章节下的所有试卷
        LambdaQueryWrapper<ExamPaperDO> queryWrapper = new LambdaQueryWrapper<ExamPaperDO>()
                .eq(ExamPaperDO::getChapterId, chapterId)
                .eq(ExamPaperDO::getStatus, "active");
                // 不再限制必须是chapter类型，允许查询所有类型的试卷
                //.eq(ExamPaperDO::getPaperType, "chapter")
        
        // 按创建时间倒序排序
        queryWrapper.orderByDesc(ExamPaperDO::getCreateTime);
        
        List<ExamPaperDO> examPapers = this.list(queryWrapper);
        
        if (examPapers.isEmpty()) {
            log.info("章节没有试卷，章节ID: {}", chapterId);
            return List.of();
        }
        
        // 3. 组装试卷信息，不包含题目详情
        List<ExamPaperDetailRespDTO> result = examPapers.stream().map(paper -> {
            ExamPaperDetailRespDTO paperDetail = new ExamPaperDetailRespDTO();
            paperDetail.setId(paper.getId());
            paperDetail.setCourseId(paper.getCourseId());
            paperDetail.setChapterId(paper.getChapterId());
            paperDetail.setPaperName(paper.getPaperName());
            paperDetail.setPaperType(paper.getPaperType());
            paperDetail.setTotalQuestions(paper.getTotalQuestions());
            paperDetail.setTotalScore(paper.getTotalScore());
            paperDetail.setTimeLimit(paper.getTimeLimit());
            paperDetail.setDifficulty(paper.getDifficulty());
            paperDetail.setStatus(paper.getStatus());
            paperDetail.setAiGenerated(paper.getAiGenerated());
            paperDetail.setCreateTime(paper.getCreateTime());
            // 不加载题目信息，减轻接口负担
            return paperDetail;
        }).toList();
        
        log.info("获取章节试卷列表成功，章节ID: {}, 试卷数量: {}", chapterId, result.size());
        log.info("=== 结束执行[获取章节试卷列表] ===");
        
        return result;
    }

    /**
     * 学生开始答题
     * 
     * @param requestParam 开始答题请求参数
     * @return 开始答题结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StartExamRespDTO startExam(StartExamReqDTO requestParam) {
        log.info("=== 开始执行[学生开始答题] ===");
        log.info("参数信息: {}", requestParam);
        
        // 1. 参数验证
        if (requestParam.getStudentId() == null || requestParam.getStudentId() <= 0) {
            log.error("学生ID无效: {}", requestParam.getStudentId());
            throw new IllegalArgumentException("学生ID无效");
        }
        
        if (requestParam.getPaperId() == null || requestParam.getPaperId() <= 0) {
            log.error("试卷ID无效: {}", requestParam.getPaperId());
            throw new IllegalArgumentException("试卷ID无效");
        }
        
        // 2. 查询试卷信息
        ExamPaperDO examPaper = this.getById(requestParam.getPaperId());
        if (examPaper == null) {
            log.error("试卷不存在，ID: {}", requestParam.getPaperId());
            throw new IllegalArgumentException("试卷不存在");
        }
        
        // 3. 检查学生是否已有进行中的答题记录
        LambdaQueryWrapper<StudentExamRecordDO> queryWrapper = new LambdaQueryWrapper<StudentExamRecordDO>()
                .eq(StudentExamRecordDO::getStudentId, requestParam.getStudentId())
                .eq(StudentExamRecordDO::getPaperId, requestParam.getPaperId())
                .eq(StudentExamRecordDO::getStatus, "in_progress");
        
        StudentExamRecordDO existingRecord = studentExamRecordMapper.selectOne(queryWrapper);
        
        // 4. 创建或复用答题记录
        StudentExamRecordDO examRecord;
        if (existingRecord != null) {
            log.info("学生已有进行中的答题记录，复用记录ID: {}", existingRecord.getId());
            examRecord = existingRecord;
        } else {
            // 创建新的答题记录
            examRecord = new StudentExamRecordDO();
            examRecord.setStudentId(requestParam.getStudentId());
            examRecord.setPaperId(requestParam.getPaperId());
            examRecord.setStartTime(new Date());
            examRecord.setStatus("in_progress");
            examRecord.setTotalScore(0);
            examRecord.setCorrectCount(0);
            examRecord.setWrongCount(0);
            examRecord.setAccuracyRate(new BigDecimal("0.00"));
            
            studentExamRecordMapper.insert(examRecord);
            log.info("创建新的答题记录，记录ID: {}", examRecord.getId());
        }
        
        // 5. 获取试卷详情（题目、选项等）
        ExamPaperDetailRespDTO paperDetail = getPaperDetail(requestParam.getPaperId());
        
        // 6. 组装返回数据
        StartExamRespDTO response = new StartExamRespDTO();
        response.setExamRecordId(examRecord.getId());
        response.setPaperDetail(paperDetail);
        response.setStartTime(examRecord.getStartTime());
        response.setMessage("开始答题成功");
        
        log.info("学生开始答题成功，学生ID: {}, 试卷ID: {}, 答题记录ID: {}", 
                requestParam.getStudentId(), requestParam.getPaperId(), examRecord.getId());
        log.info("=== 结束执行[学生开始答题] ===");
        
        return response;
    }

    /**
     * 提交单题答案
     *
     * @param requestParam 提交答案请求参数
     * @return 答题结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubmitAnswerRespDTO submitAnswer(SubmitAnswerReqDTO requestParam) {
        log.info("=== 开始执行[提交单题答案] ===");
        log.info("参数信息: {}", requestParam);
        
        // 1. 参数验证
        if (requestParam.getExamRecordId() == null || requestParam.getExamRecordId() <= 0) {
            log.error("答题记录ID无效: {}", requestParam.getExamRecordId());
            throw new IllegalArgumentException("答题记录ID无效");
        }
        
        if (requestParam.getQuestionId() == null || requestParam.getQuestionId() <= 0) {
            log.error("题目ID无效: {}", requestParam.getQuestionId());
            throw new IllegalArgumentException("题目ID无效");
        }
        
        if (requestParam.getStudentAnswer() == null) {
            log.error("学生答案为空");
            throw new IllegalArgumentException("学生答案不能为空");
        }
        
        // 2. 查询答题记录
        StudentExamRecordDO examRecord = studentExamRecordMapper.selectById(requestParam.getExamRecordId());
        if (examRecord == null) {
            log.error("答题记录不存在，ID: {}", requestParam.getExamRecordId());
            throw new IllegalArgumentException("答题记录不存在");
        }
        
        // 检查答题记录状态
        if (!"in_progress".equals(examRecord.getStatus())) {
            log.error("答题记录状态不是进行中，不能提交答案，ID: {}, 状态: {}", 
                    requestParam.getExamRecordId(), examRecord.getStatus());
            throw new IllegalArgumentException("答题已结束，不能提交答案");
        }
        
        // 3. 查询题目信息
        ExamQuestionDO examQuestion = examQuestionMapper.selectById(requestParam.getQuestionId());
        if (examQuestion == null) {
            log.error("题目不存在，ID: {}", requestParam.getQuestionId());
            throw new IllegalArgumentException("题目不存在");
        }
        
        // 确认题目属于该试卷
        if (!examQuestion.getPaperId().equals(examRecord.getPaperId())) {
            log.error("题目不属于当前答题的试卷，题目ID: {}, 试卷ID: {}, 当前答题试卷ID: {}", 
                    requestParam.getQuestionId(), examQuestion.getPaperId(), examRecord.getPaperId());
            throw new IllegalArgumentException("题目不属于当前答题的试卷");
        }
        
        // 4. 获取正确答案
        ExamQuestionAnswerDO examQuestionAnswer = examQuestionAnswerMapper.selectOne(
                new LambdaQueryWrapper<ExamQuestionAnswerDO>()
                        .eq(ExamQuestionAnswerDO::getQuestionId, requestParam.getQuestionId())
        );
        
        if (examQuestionAnswer == null) {
            log.error("题目答案不存在，题目ID: {}", requestParam.getQuestionId());
            throw new IllegalArgumentException("题目答案不存在");
        }
        
        // 5. 判断答案是否正确
        boolean isCorrect = false;
        int scoreGained = 0;
        
        switch (examQuestion.getQuestionType()) {
            case "single_choice":
            case "multiple_choice":
                // 单选题和多选题直接比较答案字符串是否相等（忽略大小写和空格）
                isCorrect = examQuestionAnswer.getCorrectAnswer().trim().equalsIgnoreCase(
                        requestParam.getStudentAnswer().trim());
                break;
            case "short_answer":
                // 简答题需要进行相似度比较或人工评分，这里简单实现为包含关键词即可
                String[] keywords = examQuestionAnswer.getCorrectAnswer().toLowerCase().split("[,，;；]");
                String studentAnswer = requestParam.getStudentAnswer().toLowerCase();
                int matchCount = 0;
                for (String keyword : keywords) {
                    if (studentAnswer.contains(keyword.trim())) {
                        matchCount++;
                    }
                }
                // 匹配度超过50%即为正确
                isCorrect = (double) matchCount / keywords.length >= 0.5;
                break;
            default:
                log.error("不支持的题目类型: {}", examQuestion.getQuestionType());
                throw new IllegalArgumentException("不支持的题目类型");
        }
        
        // 计算得分
        if (isCorrect) {
            scoreGained = examQuestion.getScore();
        }
        
        // 6. 检查是否已经提交过该题目的答案
        LambdaQueryWrapper<StudentAnswerDetailDO> queryWrapper = new LambdaQueryWrapper<StudentAnswerDetailDO>()
                .eq(StudentAnswerDetailDO::getExamRecordId, requestParam.getExamRecordId())
                .eq(StudentAnswerDetailDO::getQuestionId, requestParam.getQuestionId());
        
        StudentAnswerDetailDO existingAnswer = studentAnswerDetailMapper.selectOne(queryWrapper);
        
        // 7. 保存或更新答题详情
        StudentAnswerDetailDO answerDetail;
        if (existingAnswer != null) {
            answerDetail = existingAnswer;
            answerDetail.setStudentAnswer(requestParam.getStudentAnswer());
            answerDetail.setIsCorrect(isCorrect);
            answerDetail.setScoreGained(scoreGained);
            answerDetail.setAnswerTime(new Date());
            
            studentAnswerDetailMapper.updateById(answerDetail);
            log.info("更新答题详情，ID: {}", answerDetail.getId());
        } else {
            answerDetail = new StudentAnswerDetailDO();
            answerDetail.setExamRecordId(requestParam.getExamRecordId());
            answerDetail.setQuestionId(requestParam.getQuestionId());
            answerDetail.setStudentAnswer(requestParam.getStudentAnswer());
            answerDetail.setIsCorrect(isCorrect);
            answerDetail.setScoreGained(scoreGained);
            answerDetail.setAnswerTime(new Date());
            
            studentAnswerDetailMapper.insert(answerDetail);
            log.info("创建新的答题详情，ID: {}", answerDetail.getId());
        }
        
        // 8. 组装返回数据
        SubmitAnswerRespDTO response = new SubmitAnswerRespDTO();
        response.setQuestionId(requestParam.getQuestionId());
        response.setIsCorrect(isCorrect);
        response.setScoreGained(scoreGained);
        response.setCorrectAnswer(examQuestionAnswer.getCorrectAnswer());
        response.setAnswerExplanation(examQuestionAnswer.getAnswerExplanation());
        response.setMessage(isCorrect ? "回答正确" : "回答错误");
        
        log.info("提交答案结果 - 题目ID: {}, 是否正确: {}, 得分: {}", 
                requestParam.getQuestionId(), isCorrect, scoreGained);
        log.info("=== 结束执行[提交单题答案] ===");
        
        return response;
    }

    /**
     * 提交整份试卷
     *
     * @param requestParam 提交试卷请求参数
     * @return 试卷提交结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubmitExamRespDTO submitExam(SubmitExamReqDTO requestParam) {
        log.info("=== 开始执行[提交整份试卷] ===");
        log.info("参数信息: {}", requestParam);
        
        // 1. 参数验证
        if (requestParam.getExamRecordId() == null || requestParam.getExamRecordId() <= 0) {
            log.error("答题记录ID无效: {}", requestParam.getExamRecordId());
            throw new IllegalArgumentException("答题记录ID无效");
        }
        
        // 2. 查询答题记录
        StudentExamRecordDO examRecord = studentExamRecordMapper.selectById(requestParam.getExamRecordId());
        if (examRecord == null) {
            log.error("答题记录不存在，ID: {}", requestParam.getExamRecordId());
            throw new IllegalArgumentException("答题记录不存在");
        }
        
        // 检查答题记录状态
        if (!"in_progress".equals(examRecord.getStatus())) {
            log.error("答题记录状态不是进行中，不能提交试卷，ID: {}, 状态: {}", 
                    requestParam.getExamRecordId(), examRecord.getStatus());
            throw new IllegalArgumentException("答题已结束或已提交，不能重复提交");
        }
        
        // 3. 查询试卷信息
        ExamPaperDO examPaper = this.getById(examRecord.getPaperId());
        if (examPaper == null) {
            log.error("试卷不存在，ID: {}", examRecord.getPaperId());
            throw new IllegalArgumentException("试卷不存在");
        }
        
        // 4. 查询所有答题详情
        List<StudentAnswerDetailDO> answerDetails = studentAnswerDetailMapper.selectList(
                new LambdaQueryWrapper<StudentAnswerDetailDO>()
                        .eq(StudentAnswerDetailDO::getExamRecordId, requestParam.getExamRecordId())
        );
        
        // 5. 统计答题情况
        int correctCount = 0;
        int wrongCount = 0;
        int totalScore = 0;
        
        for (StudentAnswerDetailDO detail : answerDetails) {
            if (detail.getIsCorrect()) {
                correctCount++;
                totalScore += detail.getScoreGained();
            } else {
                wrongCount++;
            }
        }
        
        // 计算正确率
        int answeredQuestions = correctCount + wrongCount;        BigDecimal accuracyRate = BigDecimal.ZERO;
        if (answeredQuestions > 0) {
            accuracyRate = new BigDecimal(correctCount)
                    .divide(new BigDecimal(answeredQuestions), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        
        // 计算用时（分钟）
        Date now = new Date();
        long diffInMillies = now.getTime() - examRecord.getStartTime().getTime();
        int timeSpent = (int) (diffInMillies / (1000 * 60));
        
        // 6. 更新答题记录
        examRecord.setSubmitTime(now);
        examRecord.setTotalScore(totalScore);
        examRecord.setCorrectCount(correctCount);
        examRecord.setWrongCount(wrongCount);
        examRecord.setAccuracyRate(accuracyRate);
        examRecord.setTimeSpent(timeSpent);
        examRecord.setStatus("submitted"); // 修改状态为已提交
        
        studentExamRecordMapper.updateById(examRecord);
        
        // 7. 组装返回数据
        SubmitExamRespDTO response = new SubmitExamRespDTO();
        response.setExamRecordId(examRecord.getId());
        response.setPaperId(examPaper.getId());
        response.setPaperName(examPaper.getPaperName());
        response.setTotalScore(totalScore);
        response.setFullScore(examPaper.getTotalScore());
        response.setCorrectCount(correctCount);
        response.setWrongCount(wrongCount);
        response.setTotalQuestions(examPaper.getTotalQuestions());
        response.setAccuracyRate(accuracyRate);
        response.setTimeSpent(timeSpent);
        response.setSubmitTime(now);
        response.setMessage("试卷提交成功");
        
        log.info("试卷提交结果 - 答题记录ID: {}, 总分: {}, 正确题数: {}, 错误题数: {}, 正确率: {}%", 
                examRecord.getId(), totalScore, correctCount, wrongCount, accuracyRate);
        log.info("=== 结束执行[提交整份试卷] ===");
        
        return response;
    }

    /**
     * 获取题目答案
     * @param questionId 题目ID
     * @return
     */
    @Override
    public QuestionAnswerRespDTO getQuestionAnswer(Integer questionId) {
        log.info("=== 开始执行[获取题目答案] ===");
        log.info("题目ID: {}", questionId);
        
        // 1. 参数验证
        if (questionId == null || questionId <= 0) {
            log.error("题目ID无效: {}", questionId);
            throw new IllegalArgumentException("题目ID无效");
        }
        
        // 2. 查询题目信息
        ExamQuestionDO examQuestion = examQuestionMapper.selectById(questionId);
        if (examQuestion == null) {
            log.error("题目不存在，ID: {}", questionId);
            throw new IllegalArgumentException("题目不存在");
        }
        
        // 3. 查询题目答案
        ExamQuestionAnswerDO examQuestionAnswer = examQuestionAnswerMapper.selectOne(
                new LambdaQueryWrapper<ExamQuestionAnswerDO>()
                        .eq(ExamQuestionAnswerDO::getQuestionId, questionId)
        );
        
        if (examQuestionAnswer == null) {
            log.error("题目答案不存在，题目ID: {}", questionId);
            throw new IllegalArgumentException("题目答案不存在");
        }
          // 4. 组装返回数据
        QuestionAnswerRespDTO response = new QuestionAnswerRespDTO();
        response.setQuestionId(questionId);
        response.setQuestionType(examQuestion.getQuestionType());
        response.setCorrectAnswer(examQuestionAnswer.getCorrectAnswer());
        response.setAnswerExplanation(examQuestionAnswer.getAnswerExplanation());
        response.setScore(examQuestion.getScore());
        
        log.info("获取题目答案成功，题目ID: {}, 题目类型: {}", 
                questionId, examQuestion.getQuestionType());
        log.info("=== 结束执行[获取题目答案] ===");
        
        return response;
    }

    /**
     * 删除试卷
     *
     * @param paperId 试卷ID
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePaper(Integer paperId) {
        log.info("=== 开始执行[删除试卷] ===");
        log.info("试卷ID: {}", paperId);
        
        // 1. 参数验证
        if (paperId == null || paperId <= 0) {
            log.error("试卷ID无效: {}", paperId);
            throw new IllegalArgumentException("试卷ID无效");
        }
        
        // 2. 查询试卷信息
        ExamPaperDO examPaper = this.getById(paperId);
        if (examPaper == null) {
            log.error("试卷不存在，ID: {}", paperId);
            throw new IllegalArgumentException("试卷不存在");
        }
          // 3. 检查是否有学生正在答题或已经答题
        long answerRecordCount = studentExamRecordMapper.selectCount(
                new LambdaQueryWrapper<StudentExamRecordDO>()
                        .eq(StudentExamRecordDO::getPaperId, paperId)
        );
        
        if (answerRecordCount > 0) {
            log.error("试卷已有学生答题记录，不能删除，ID: {}, 记录数: {}", paperId, answerRecordCount);
            throw new IllegalArgumentException("试卷已有学生答题记录，不能删除");
        }
        
        // 4. 查询试卷下的所有题目
        List<ExamQuestionDO> questions = examQuestionMapper.selectList(
                new LambdaQueryWrapper<ExamQuestionDO>()
                        .eq(ExamQuestionDO::getPaperId, paperId)
        );
        
        // 5. 逻辑删除试卷相关数据
        // 5.1 删除题目选项和答案
        for (ExamQuestionDO question : questions) {
            // 删除选项
            examQuestionOptionMapper.delete(
                    new LambdaQueryWrapper<ExamQuestionOptionDO>()
                            .eq(ExamQuestionOptionDO::getQuestionId, question.getId())
            );
            
            // 删除答案
            examQuestionAnswerMapper.delete(
                    new LambdaQueryWrapper<ExamQuestionAnswerDO>()
                            .eq(ExamQuestionAnswerDO::getQuestionId, question.getId())
            );
        }
        
        // 5.2 删除题目
        examQuestionMapper.delete(
                new LambdaQueryWrapper<ExamQuestionDO>()
                        .eq(ExamQuestionDO::getPaperId, paperId)
        );
        
        // 5.3 修改试卷状态为已删除
        examPaper.setStatus("deleted");
        examPaper.setDeleteTime(new Date());
        boolean result = this.updateById(examPaper);
        
        if (result) {
            log.info("试卷删除成功，ID: {}", paperId);
        } else {
            log.error("试卷删除失败，ID: {}", paperId);
        }
        
        log.info("=== 结束执行[删除试卷] ===");
        
        return result;
    }  
    
    /**
     * @param courseId 课程ID
     * @return 课程统计数据
     */
    @Override
    public ExamStatisticsRespDTO getCourseStatistics(String courseId) {
        log.info("=== 开始执行[获取课程统计数据] ===");
        log.info("课程ID: {}", courseId);
        
        // 1. 参数验证
        if (courseId == null || courseId.isEmpty()) {
            log.error("课程ID无效: {}", courseId);
            throw new IllegalArgumentException("课程ID无效");
        }
        
        // 2. 查询课程下的所有试卷
        List<ExamPaperDO> examPapers = this.list(
                new LambdaQueryWrapper<ExamPaperDO>()
                        .eq(ExamPaperDO::getCourseId, courseId)
                        .eq(ExamPaperDO::getStatus, "active")
        );
        
        if (examPapers.isEmpty()) {
            log.info("课程没有试卷，课程ID: {}", courseId);
            return new ExamStatisticsRespDTO();
        }
        
        // 3. 获取试卷ID列表
        List<Integer> paperIds = examPapers.stream()
                .map(ExamPaperDO::getId)
                .toList();
        
        // 4. 查询所有答题记录
        List<StudentExamRecordDO> examRecords = studentExamRecordMapper.selectList(
                new LambdaQueryWrapper<StudentExamRecordDO>()
                        .in(StudentExamRecordDO::getPaperId, paperIds)
                        .eq(StudentExamRecordDO::getStatus, "submitted") // 只统计已提交的试卷
        );
        
        if (examRecords.isEmpty()) {
            log.info("课程没有学生答题记录，课程ID: {}", courseId);
            return new ExamStatisticsRespDTO();
        }
        
        // 5. 统计数据
        // 5.1 参与考试的学生数量（去重）
        long studentCount = examRecords.stream()
                .map(StudentExamRecordDO::getStudentId)
                .distinct()
                .count();
        
        // 5.2 答题记录总数
        int examCount = examRecords.size();
        
        // 5.3 总分、平均分、最高分、最低分
        int totalScore = examRecords.stream()
                .mapToInt(StudentExamRecordDO::getTotalScore)
                .sum();
                
        double averageScore = examRecords.stream()
                .mapToInt(StudentExamRecordDO::getTotalScore)
                .average()
                .orElse(0);
                
        int highestScore = examRecords.stream()
                .mapToInt(StudentExamRecordDO::getTotalScore)
                .max()
                .orElse(0);
                
        int lowestScore = examRecords.stream()
                .mapToInt(StudentExamRecordDO::getTotalScore)
                .min()
                .orElse(0);
        
        // 5.4 计算及格率（假设60分为及格线）
        long passCount = examRecords.stream()
                .filter(record -> {
                    // 获取试卷总分
                    ExamPaperDO paper = examPapers.stream()
                            .filter(p -> p.getId().equals(record.getPaperId()))
                            .findFirst()
                            .orElse(null);
                    
                    if (paper == null) {
                        return false;
                    }
                    
                    // 计算及格分数（60%的总分）
                    double passScore = paper.getTotalScore() * 0.6;
                    
                    // 判断是否及格
                    return record.getTotalScore() >= passScore;
                })
                .count();
        
        // 计算及格率
        BigDecimal passRate = BigDecimal.ZERO;
        if (examCount > 0) {
            passRate = new BigDecimal(passCount)
                    .divide(new BigDecimal(examCount), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        
        // 5.5 计算平均正确率
        BigDecimal averageAccuracyRate = examRecords.stream()
                .map(StudentExamRecordDO::getAccuracyRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(examCount), 2, RoundingMode.HALF_UP);        // 5.6 分数段分布（90-100, 80-89, 70-79, 60-69, <60）
        // 最终JSON中的scoreDistribution字段示例：
        // "scoreDistribution": {
        //   "90-100": 0,                       // 90-100分的人数
        //   "<60": 1,                          // 60分以下的人数
        //   "60-69": 0,                        // 60-69分的人数
        //   "70-79": 0,                        // 70-79分的人数
        //   "80-89": 0                         // 80-89分的人数
        // }
        Map<String, Integer> scoreDistribution = new HashMap<>();
        scoreDistribution.put("90-100", 0);
        scoreDistribution.put("80-89", 0);
        scoreDistribution.put("70-79", 0);
        scoreDistribution.put("60-69", 0);
        scoreDistribution.put("<60", 0);
        
        for (StudentExamRecordDO record : examRecords) {
            // 获取试卷信息
            ExamPaperDO paper = examPapers.stream()
                    .filter(p -> p.getId().equals(record.getPaperId()))
                    .findFirst()
                    .orElse(null);
            
            if (paper == null) {
                continue;
            }
            
            // 计算得分率
            double scoreRate = (double) record.getTotalScore() / paper.getTotalScore() * 100;
              // 判断分数段
            if (scoreRate >= 90) {
                scoreDistribution.put("90-100", scoreDistribution.get("90-100") + 1);
            } else if (scoreRate >= 80) {
                scoreDistribution.put("80-89", scoreDistribution.get("80-89") + 1);
            } else if (scoreRate >= 70) {
                scoreDistribution.put("70-79", scoreDistribution.get("70-79") + 1);
            } else if (scoreRate >= 60) {
                scoreDistribution.put("60-69", scoreDistribution.get("60-69") + 1);
            } else {
                scoreDistribution.put("<60", scoreDistribution.get("<60") + 1);
            }
        }
          // 6. 组装返回数据
        ExamStatisticsRespDTO response = new ExamStatisticsRespDTO();
        response.setStatisticsType("course");
        response.setTargetId(courseId);
        response.setCourseId(courseId);
        // 如果需要获取课程名称，可以添加课程服务并查询，此处暂时为空
        response.setCourseName(""); // 这里需要从课程服务中获取课程名称
        response.setExamCount(examCount);
        response.setStudentCount((int) studentCount);
        response.setTotalScore(totalScore);
        response.setAverageScore(BigDecimal.valueOf(averageScore).setScale(2, RoundingMode.HALF_UP));
        response.setHighestScore(highestScore);
        response.setLowestScore(lowestScore);
        response.setPassRate(passRate.setScale(2, RoundingMode.HALF_UP));
        response.setAverageAccuracyRate(averageAccuracyRate);
        // 总体正确率与平均正确率相同
        response.setOverallAccuracyRate(averageAccuracyRate);
        response.setScoreDistribution(scoreDistribution);
        // 初始化列表，避免NPE
        response.setChapterAccuracyList(new ArrayList<>());
        response.setProblemQuestionList(new ArrayList<>());
        
        // 7. 添加试卷统计列表
        List<ExamStatisticsRespDTO.PaperStatisticsDetail> paperStatisticsList = new ArrayList<>();
          // 7.1 章节正确率统计
        Map<Integer, ExamStatisticsRespDTO.ChapterAccuracyDTO> chapterAccuracyMap = new HashMap<>();
        
        for (ExamPaperDO paper : examPapers) {
            // 筛选该试卷的答题记录
            List<StudentExamRecordDO> paperRecords = examRecords.stream()
                    .filter(record -> record.getPaperId().equals(paper.getId()))
                    .toList();
            
            if (paperRecords.isEmpty()) {
                continue;
            }
            
            // 统计章节信息，最终JSON中的chapterAccuracyList字段示例：
            // "chapterAccuracyList": [
            //   {
            //     "chapterId": 3,                  // 章节ID
            //     "chapterName": "章节-3",          // 章节名称
            //     "accuracyRate": 20.00,           // 章节正确率（百分比）
            //     "answerCount": 1                 // 答题次数
            //   }
            // ]
            Integer chapterId = paper.getChapterId();
            if (chapterId != null) {
                ExamStatisticsRespDTO.ChapterAccuracyDTO chapterAccuracy = chapterAccuracyMap.get(chapterId);
                if (chapterAccuracy == null) {
                    chapterAccuracy = new ExamStatisticsRespDTO.ChapterAccuracyDTO();
                    chapterAccuracy.setChapterId(chapterId);
                    // 如果需要获取章节名称，可以添加章节服务并查询，此处使用占位符
                    chapterAccuracy.setChapterName("章节-" + chapterId);
                    chapterAccuracy.setAnswerCount(0);
                    chapterAccuracy.setAccuracyRate(BigDecimal.ZERO);
                    chapterAccuracyMap.put(chapterId, chapterAccuracy);
                }
                
                // 累加章节的答题记录和正确率
                int chapterAnswerCount = paperRecords.size();
                BigDecimal chapterAccuracyRateSum = paperRecords.stream()
                        .map(StudentExamRecordDO::getAccuracyRate)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                // 更新章节统计数据
                int totalAnswerCount = chapterAccuracy.getAnswerCount() + chapterAnswerCount;
                BigDecimal totalAccuracyRateSum = chapterAccuracy.getAccuracyRate()
                        .multiply(BigDecimal.valueOf(chapterAccuracy.getAnswerCount()))
                        .add(chapterAccuracyRateSum);
                
                chapterAccuracy.setAnswerCount(totalAnswerCount);
                if (totalAnswerCount > 0) {
                    chapterAccuracy.setAccuracyRate(totalAccuracyRateSum
                            .divide(BigDecimal.valueOf(totalAnswerCount), 2, RoundingMode.HALF_UP));
                }
            }
            
            // 计算试卷统计数据，最终JSON中的paperStatisticsList字段示例：
            // "paperStatisticsList": [
            //   {
            //     "paperId": 1,                    // 试卷ID
            //     "paperName": "第3章 3D图形基础与投影 - 章节测试",  // 试卷名称
            //     "studentCount": 1,               // 学生数量
            //     "averageScore": 20.00,           // 平均分
            //     "highestScore": 20               // 最高分
            //   }
            // ]
            int paperStudentCount = (int) paperRecords.stream()
                    .map(StudentExamRecordDO::getStudentId)
                    .distinct()
                    .count();
                    
            double paperAverageScore = paperRecords.stream()
                    .mapToInt(StudentExamRecordDO::getTotalScore)
                    .average()
                    .orElse(0);
                    
            int paperHighestScore = paperRecords.stream()
                    .mapToInt(StudentExamRecordDO::getTotalScore)
                    .max()
                    .orElse(0);
            
            // 组装试卷统计数据
            ExamStatisticsRespDTO.PaperStatisticsDetail paperStatistics = new ExamStatisticsRespDTO.PaperStatisticsDetail();
            paperStatistics.setPaperId(paper.getId());
            paperStatistics.setPaperName(paper.getPaperName());
            paperStatistics.setStudentCount(paperStudentCount);
            paperStatistics.setAverageScore(BigDecimal.valueOf(paperAverageScore).setScale(2, RoundingMode.HALF_UP));
            paperStatistics.setHighestScore(paperHighestScore);
            
            paperStatisticsList.add(paperStatistics);
        }
          
        // 将章节正确率Map转换为List
        List<ExamStatisticsRespDTO.ChapterAccuracyDTO> chapterAccuracyList = new ArrayList<>(chapterAccuracyMap.values());
        response.setChapterAccuracyList(chapterAccuracyList);
        
        response.setPaperStatisticsList(paperStatisticsList);
        
        // 7.2 获取学生答题详情，用于统计题目错误率
        List<StudentAnswerDetailDO> allAnswerDetails = new ArrayList<>();
        for (StudentExamRecordDO record : examRecords) {
            List<StudentAnswerDetailDO> details = studentAnswerDetailMapper.selectList(
                    new LambdaQueryWrapper<StudentAnswerDetailDO>()
                            .eq(StudentAnswerDetailDO::getExamRecordId, record.getId())
            );
            allAnswerDetails.addAll(details);
        }
        
        // 7.3 计算题目错误率
        Map<Integer, QuestionErrorStatistics> questionErrorMap = new HashMap<>();
        for (StudentAnswerDetailDO detail : allAnswerDetails) {
            QuestionErrorStatistics stats = questionErrorMap.computeIfAbsent(
                    detail.getQuestionId(), 
                    k -> new QuestionErrorStatistics()
            );
            
            stats.totalAnswers++;
            if (!detail.getIsCorrect()) {
                stats.wrongAnswers++;
            }
        }
        
        // 7.4 查找错误率最高的题目（最多5个）
        List<Map.Entry<Integer, QuestionErrorStatistics>> sortedQuestions = questionErrorMap.entrySet().stream()
                .filter(entry -> entry.getValue().totalAnswers >= 5) // 至少有5次回答
                .sorted((e1, e2) -> {
                    double errorRate1 = (double) e1.getValue().wrongAnswers / e1.getValue().totalAnswers;
                    double errorRate2 = (double) e2.getValue().wrongAnswers / e2.getValue().totalAnswers;
                    return Double.compare(errorRate2, errorRate1); // 降序排序
                })
                .limit(5)
                .toList();
          // 7.5 组装问题题目列表
        List<ExamStatisticsRespDTO.ProblemQuestionDTO> problemQuestionList = new ArrayList<>();
        for (Map.Entry<Integer, QuestionErrorStatistics> entry : sortedQuestions) {
            Integer questionId = entry.getKey();
            QuestionErrorStatistics stats = entry.getValue();
            
            // 错误率低于30%的题目不计入问题题目
            double errorRate = (double) stats.wrongAnswers / stats.totalAnswers;
            if (errorRate < 0.3) {
                continue;
            }
            
            // 查询题目信息
            ExamQuestionDO question = examQuestionMapper.selectById(questionId);
            if (question == null) {
                continue;
            }
            
            // 查询答案信息
            ExamQuestionAnswerDO answer = examQuestionAnswerMapper.selectOne(
                    new LambdaQueryWrapper<ExamQuestionAnswerDO>()
                            .eq(ExamQuestionAnswerDO::getQuestionId, questionId)
            );
            
            // 组装问题题目信息，最终JSON中的problemQuestionList字段示例：
            // "problemQuestionList": [
            //   {
            //     "questionId": 101,                  // 题目ID
            //     "questionType": "single_choice",    // 题目类型：单选题、多选题、简答题
            //     "questionText": "以下哪个不是3D图形基本元素...",  // 题目内容（可能被截断）
            //     "errorRate": 65.50,                 // 错误率（百分比）
            //     "correctAnswer": "D",               // 正确答案
            //     "answerExplanation": "解析：3D图形的基本元素...",  // 答案解析
            //     "commonWrongAnswers": []            // 常见错误答案
            //   }
            // ]
            ExamStatisticsRespDTO.ProblemQuestionDTO problemQuestion = new ExamStatisticsRespDTO.ProblemQuestionDTO();
            problemQuestion.setQuestionId(questionId);
            problemQuestion.setQuestionType(question.getQuestionType());
            // 题目内容可能很长，这里截取前50个字符
            String questionText = question.getQuestionText();
            if (questionText.length() > 50) {
                questionText = questionText.substring(0, 47) + "...";
            }
            problemQuestion.setQuestionText(questionText);
            problemQuestion.setErrorRate(BigDecimal.valueOf(errorRate).setScale(2, RoundingMode.HALF_UP));
            
            // 如果有答案信息，添加正确答案和解析
            if (answer != null) {
                problemQuestion.setCorrectAnswer(answer.getCorrectAnswer());
                problemQuestion.setAnswerExplanation(answer.getAnswerExplanation());
            }
            
            // 暂不统计常见错误答案
            problemQuestion.setCommonWrongAnswers(new ArrayList<>());
            
            problemQuestionList.add(problemQuestion);
        }
        
        response.setProblemQuestionList(problemQuestionList);
        
        log.info("获取课程统计数据成功，课程ID: {}, 学生数量: {}, 平均分: {}", 
                courseId, studentCount, response.getAverageScore());
        log.info("=== 结束执行[获取课程统计数据] ===");
        
        return response;
    }

    @Override
    public ExamStatisticsRespDTO getPaperStatistics(Integer paperId) {
        return null;
    }



    @Override
    public ExamStatisticsRespDTO getStudentStatistics(Long studentId, String courseId) {
        // 将在后续接口实现
        return null;
    }

    @Override
    public Boolean saveSuggestion(SaveSuggestionReqDTO requestParam) {
        // 将在后续接口实现
        return null;
    }

    @Override
    public List<AITeachingSuggestionRespDTO> getSuggestions(String type, String targetId) {
        // 将在后续接口实现
        return null;
    }    /**
     * 题目错误率统计辅助类
     * 用于统计每个题目的回答次数和错误次数，计算错误率
     */
    private static class QuestionErrorStatistics {
        /**
         * 总回答次数
         */
        int totalAnswers = 0;
        
        /**
         * 错误回答次数
         */
        int wrongAnswers = 0;
    }
}

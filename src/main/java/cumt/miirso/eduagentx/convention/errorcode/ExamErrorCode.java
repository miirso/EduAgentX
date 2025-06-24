package cumt.miirso.eduagentx.convention.errorcode;

/**
 * 考试系统错误码
 * @author miirso
 */
public enum ExamErrorCode implements IErrorCode {

    /**
     * 试题不存在
     */
    QUESTION_NOT_EXIST("A2000", "试题不存在"),

    /**
     * 试卷不存在
     */
    PAPER_NOT_EXIST("A2001", "试卷不存在"),

    /**
     * 答题记录不存在
     */
    EXAM_RECORD_NOT_EXIST("A2002", "答题记录不存在"),

    /**
     * 试卷已提交
     */
    PAPER_ALREADY_SUBMITTED("A2003", "试卷已提交"),

    /**
     * 试卷未开始
     */
    PAPER_NOT_STARTED("A2004", "试卷未开始"),

    /**
     * 试卷已过期
     */
    PAPER_EXPIRED("A2005", "试卷已过期"),

    /**
     * 答题时间已结束
     */
    EXAM_TIME_END("A2006", "答题时间已结束"),

    /**
     * 学生未选课
     */
    STUDENT_NOT_ENROLLED("A2007", "学生未选课"),

    /**
     * 试题选项不存在
     */
    QUESTION_OPTION_NOT_EXIST("A2008", "试题选项不存在"),    /**
     * 章节不存在
     */
    CHAPTER_NOT_EXIST("A2009", "章节不存在"),
    
    /**
     * 试题类型无效
     */
    INVALID_QUESTION_TYPE("A2010", "试题类型无效"),
    
    /**
     * 参数无效
     */
    INVALID_PARAM("A2011", "参数无效"),
    
    /**
     * 课程不存在
     */
    COURSE_NOT_EXIST("A2012", "课程不存在");

    private final String code;

    private final String message;

    ExamErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}

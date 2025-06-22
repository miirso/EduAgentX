package cumt.miirso.eduagentx.convention.errorcode;

/**
 * @Package cumt.miirso.eduagentx.convention.errorcode
 * @Author miirso
 * @Date 2025/5/30 1:45
 */

public enum TeacherErrorCode implements IErrorCode {

    TEACHER_ALREADY_EXIST("T000100", "教师已存在"),
    TEACHER_NULL("T000101", "教师不存在"),
    TEACHER_NAME_ERROR("T000102", "教师用户名错误"),
    TEACHER_LOGIN_ERROR("T000103", "教师账号或密码错误"),
    TEACHER_NOT_LOGIN("T000104", "教师未登录"),
    OLD_PASSWORD_ERROR("T000105", "旧密码错误");

    private final String code;

    private final String message;

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }

    TeacherErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

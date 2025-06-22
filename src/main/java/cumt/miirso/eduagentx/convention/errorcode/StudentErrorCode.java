package cumt.miirso.eduagentx.convention.errorcode;

import lombok.RequiredArgsConstructor;

/**
 * @Package cumt.miirso.eduagentx.convention.errorcode
 * @Author miirso
 * @Date 2025/5/29 22:50
 */

public enum StudentErrorCode implements IErrorCode {

    STUDENT_ALREADY_EXIST("S000100","学生已存在"),
    STUDENT_NULL("S000101", "学生不存在"),
    STUDENT_NAME_ERROR("S000102", "学生用户名错误"),
    STUDENT_LOGIN_ERROR("S000103", "学生账号或密码错误"),
    STUDENT_NOT_LOGIN("S000104", "学生未登录"),
    OLD_PASSWORD_ERROR("S000105", "旧密码错误");


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

    StudentErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

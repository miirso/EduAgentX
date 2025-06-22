package cumt.miirso.eduagentx.convention.errorcode;

/**
 * @Package cumt.miirso.eduagentx.convention.errorcode
 * @Author miirso
 * @Date 2025/6/20
 */

public enum AdminErrorCode implements IErrorCode {

    ADMIN_ALREADY_EXIST("A000200","管理员已存在"),
    ADMIN_NULL("A000201", "管理员不存在"),
    ADMIN_NAME_ERROR("A000202", "管理员用户名错误"),
    ADMIN_LOGIN_ERROR("A000203", "管理员账号或密码错误"),
    
    // 批量导入相关错误码
    FILE_IS_EMPTY("A000300", "上传文件不能为空"),
    FILE_TYPE_ERROR("A000301", "文件类型错误，请上传Excel文件"),
    EXCEL_FORMAT_ERROR("A000302", "Excel格式错误"),
    NO_VALID_DATA("A000303", "Excel文件中没有有效数据"),
    FILE_PARSE_ERROR("A000304", "解析Excel文件失败"),
    BATCH_SAVE_ERROR("A000305", "批量保存数据失败"),
    BATCH_REGISTER_ERROR("A000306", "批量注册失败");

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

    AdminErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

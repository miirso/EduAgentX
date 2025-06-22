package cumt.miirso.eduagentx.convention.result;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Package cumt.miirso.eduagentx.convention.result
 * @Author miirso
 * @Date 2025/5/29 22:53
 */

@Data
@Accessors(chain = true)
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 5679018624309023727L;

    /**
     * 正确返回码
     */
    public static final String SUCCESS_CODE = "0";

    /**
     * 返回码
     */
    private String code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * Json 反序列化
     */
    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }

}

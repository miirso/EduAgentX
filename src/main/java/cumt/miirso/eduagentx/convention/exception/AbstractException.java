package cumt.miirso.eduagentx.convention.exception;

import cumt.miirso.eduagentx.convention.errorcode.IErrorCode;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @Package com.miirso.shortlink.admin.common.convention.exception
 * @Author miirso
 * @Date 2024/10/10 23:56
 *
 * @see ClientException
 * @see ServiceException
 * @see RemoteException
 *
 * 抽象项目中三类异常体系：客户端异常、服务端异常以及远程服务调用异常
 *
 */

@Getter
public abstract class AbstractException extends RuntimeException {

    public final String errorCode;

    public final String errorMessage;

    public AbstractException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable);
        this.errorCode = errorCode.code();
        this.errorMessage = Optional.ofNullable(StringUtils.hasLength(message) ? message : null).orElse(errorCode.message());
    }
}

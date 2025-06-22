package cumt.miirso.eduagentx.common.constant;

/**
 * @Package com.miirso.shortlink.admin.common.constant
 * @Author miirso
 * @Date 2024/10/11 3:08
 *
 * Redis缓存常量类
 *
 */

public class RedisCacheConstant {

    public final static String LOCK_USER_REGISTER_KEY = "lock-user-register:";

    public static final String LOGIN_USER_KEY = "login:token:";

    public static final Long LOGIN_USER_TTL = 60L;

}

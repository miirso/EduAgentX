package cumt.miirso.eduagentx.utils;


import cumt.miirso.eduagentx.dto.UserInfoDTO;

/**
 * @Package com.miirso.shortlink.admin.utils
 * @Author miirso
 * @Date 2024/10/11 9:31
 */

public class UserHolder {

    private static final ThreadLocal<UserInfoDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserInfoDTO user){
        tl.set(user);
    }

    public static UserInfoDTO getUserInfoDTO(){
        return tl.get();
    }

    public static void removeUserInfoDTO(){
        tl.remove();
    }

}
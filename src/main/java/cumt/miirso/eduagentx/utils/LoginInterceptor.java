package cumt.miirso.eduagentx.utils;


import cn.hutool.core.util.StrUtil;
import cumt.miirso.eduagentx.dto.UserInfoDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

import static cumt.miirso.eduagentx.common.constant.RedisCacheConstant.LOGIN_USER_KEY;

/**
 * @Package com.miirso.shortlink.admin.utils
 * @Author miirso
 * @Date 2024/10/11 10:13
 */


@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 从redis获取用户
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            response.setStatus(401);
            return false;
        }
        String username = stringRedisTemplate.opsForValue().get(LOGIN_USER_KEY + token);
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUsername(username);
        UserHolder.saveUser(userInfoDTO);


        // 刷新token有效期
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, 1, TimeUnit.HOURS);

        // 放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}

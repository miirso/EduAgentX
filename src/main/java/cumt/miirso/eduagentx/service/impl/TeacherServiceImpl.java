package cumt.miirso.eduagentx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cumt.miirso.eduagentx.common.constant.RedisCacheConstant;
import cumt.miirso.eduagentx.convention.errorcode.BaseErrorCode;
import cumt.miirso.eduagentx.convention.errorcode.TeacherErrorCode;
import cumt.miirso.eduagentx.convention.exception.ClientException;
import cumt.miirso.eduagentx.dto.UserInfoDTO;
import cumt.miirso.eduagentx.dto.req.PasswordUpdateReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherLoginReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherRegisterReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherUpdateReqDTO;
import cumt.miirso.eduagentx.dto.resp.TeacherLoginRespDTO;
import cumt.miirso.eduagentx.dto.resp.TeacherRegisterRespDTO;
import cumt.miirso.eduagentx.entity.TeacherDO;
import cumt.miirso.eduagentx.mapper.TeacherMapper;
import cumt.miirso.eduagentx.service.TeacherService;
import cumt.miirso.eduagentx.utils.PasswordEncoder;
import cumt.miirso.eduagentx.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Package cumt.miirso.eduagentx.service.impl
 * @Author miirso
 * @Date 2025/5/29 22:19
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, TeacherDO> implements TeacherService {

    private final RedissonClient redissonClient;
    
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 教师注册
     * @param teacherRegisterReqDTO 教师注册请求
     * @return
     */
    @Override
    public TeacherRegisterRespDTO register(TeacherRegisterReqDTO teacherRegisterReqDTO) {
        // 1. 检查用户名是否已存在
        boolean usernameExists = lambdaQuery()
                .eq(TeacherDO::getUsername, teacherRegisterReqDTO.getUsername())
                .exists();
        if (usernameExists) {
            throw new ClientException(BaseErrorCode.USER_NAME_EXIST_ERROR);
        }

        // 2. 检查教师编号是否已存在
        boolean teacherNoExists = lambdaQuery()
                .eq(TeacherDO::getTeacherNo, teacherRegisterReqDTO.getTeacherNo())
                .exists();
        if (teacherNoExists) {
            throw new ClientException(TeacherErrorCode.TEACHER_ALREADY_EXIST);
        }

        // 3. 创建教师对象
        TeacherDO teacherDO = getTeacherDO(teacherRegisterReqDTO);

        // 4. 保存到数据库
        save(teacherDO);
        
        // 5. 构建并返回响应DTO
        TeacherRegisterRespDTO respDTO = new TeacherRegisterRespDTO();
        respDTO.setId(teacherDO.getId());
        respDTO.setUsername(teacherDO.getUsername());
        respDTO.setRealName(teacherDO.getRealName());
        respDTO.setTeacherNo(teacherDO.getTeacherNo());

        log.info("教师已成功创建: {}", respDTO);

        return respDTO;
    }

    private static TeacherDO getTeacherDO(TeacherRegisterReqDTO teacherRegisterReqDTO) {
        TeacherDO teacherDO = new TeacherDO();
        teacherDO.setUsername(teacherRegisterReqDTO.getUsername());
        teacherDO.setTeacherNo(teacherRegisterReqDTO.getTeacherNo());
        teacherDO.setRealName(teacherRegisterReqDTO.getRealName());
        teacherDO.setGender(teacherRegisterReqDTO.getGender());
        teacherDO.setPhone(teacherRegisterReqDTO.getPhone());
        teacherDO.setSchool(teacherRegisterReqDTO.getSchool());
        teacherDO.setCollege(teacherRegisterReqDTO.getCollege());
        // 加密密码后存储
        if (teacherRegisterReqDTO.getPassword() != null) {
            teacherDO.setPassword(PasswordEncoder.encode(teacherRegisterReqDTO.getPassword()));
        }
        return teacherDO;
    }

    /**
     * 教师登录
     * @param teacherLoginReqDTO 教师登录请求
     * @return
     */
    @Override
    public TeacherLoginRespDTO login(TeacherLoginReqDTO teacherLoginReqDTO) {
        String username = teacherLoginReqDTO.getUsername();
        if (username == null) {
            throw new ClientException(TeacherErrorCode.TEACHER_NAME_ERROR);
        }
        
        // 查询教师信息
        TeacherDO teacherDO = lambdaQuery()
                .eq(TeacherDO::getUsername, username)
                .eq(TeacherDO::getTag, true)
                .one();
                
        if (teacherDO == null) {
            throw new ClientException(TeacherErrorCode.TEACHER_NULL);
        }
        
        // 使用PasswordEncoder验证密码
        if (!PasswordEncoder.matches(teacherLoginReqDTO.getPassword(), teacherDO.getPassword())) {
            throw new ClientException(TeacherErrorCode.TEACHER_LOGIN_ERROR);
        }
          // 生成token
        String token = UUID.randomUUID().toString();
        String tokenKey = RedisCacheConstant.LOGIN_USER_KEY + token;
        
        // 将token存入Redis，设置60分钟过期时间 - 使用StringRedisTemplate保持一致性
        stringRedisTemplate.opsForValue().set(tokenKey, username, RedisCacheConstant.LOGIN_USER_TTL, TimeUnit.MINUTES);
        
        // 将用户信息存入ThreadLocal，注意type为2表示教师
        UserInfoDTO userInfoDTO = new UserInfoDTO(teacherDO.getId(),
                2, 
                teacherDO.getUsername(), 
                token);

        // ------test------
        log.info(userInfoDTO.toString());

        UserHolder.saveUser(userInfoDTO);
        log.info("存储了教师用户: {}", userInfoDTO);

        // 构建并返回登录响应
        TeacherLoginRespDTO respDTO = new TeacherLoginRespDTO();
        respDTO.setToken(token);
        
        log.info("教师用户登录成功: {}", username);
        
        return respDTO;
    }

    /**
     * 检查教师是否登录
     * @param request HTTP请求
     * @return
     */
    @Override
    public Boolean check(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        String tokenKey = RedisCacheConstant.LOGIN_USER_KEY + token;
        String username = stringRedisTemplate.opsForValue().get(tokenKey);
        return !(username == null);
    }

    /**
     * 教师登出
     * @param request HTTP请求
     * @return
     */
    @Override
    public Boolean logout(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        String tokenKey = RedisCacheConstant.LOGIN_USER_KEY + token;
        Boolean isDeleted = stringRedisTemplate.delete(tokenKey);
        return isDeleted;
    }

    /**
     * 更新教师个人资料
     * @param teacherUpdateReqDTO 教师更新信息请求
     * @param request HTTP请求
     * @return
     */
    @Override
    public Boolean updateProfile(TeacherUpdateReqDTO teacherUpdateReqDTO, HttpServletRequest request) {
        // 1. 获取当前登录用户信息
        log.info("开始处理教师updateProfile请求...");
        
        // 尝试多种方式获取token
        String token = null;
        
        // 1. 尝试从标准请求头获取
        token = request.getHeader("authorization");
        log.info("从authorization获取的token: {}", token);
        
        // 2. 如果为空，尝试从查询参数获取
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
            log.info("从查询参数获取的token: {}", token);
        }
        
        // 3. 如果为空，尝试从cookie获取
        if (token == null || token.isEmpty()) {
            jakarta.servlet.http.Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (jakarta.servlet.http.Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName()) || "authorization".equals(cookie.getName())) {
                        token = cookie.getValue();
                        log.info("从cookie获取的token: {}", token);
                        break;
                    }
                }
            }
        }
        
        if (token == null || token.isEmpty() || "undefined".equals(token)) {
            log.error("无法获取有效的token");
            throw new cumt.miirso.eduagentx.convention.exception.ClientException(
                cumt.miirso.eduagentx.convention.errorcode.TeacherErrorCode.TEACHER_NOT_LOGIN);
        }
        
        String tokenKey = RedisCacheConstant.LOGIN_USER_KEY + token;
        log.info("教师token键: {}", tokenKey);
        String username = stringRedisTemplate.opsForValue().get(tokenKey);
        
        if (username == null) {
            throw new ClientException(TeacherErrorCode.TEACHER_NOT_LOGIN);
        }
        
        // 2. 查询教师信息
        TeacherDO teacherDO = lambdaQuery()
                .eq(TeacherDO::getUsername, username)
                .one();
                
        if (teacherDO == null) {
            throw new ClientException(TeacherErrorCode.TEACHER_NULL);
        }
        
        log.info("教师ID: {}, 类型: {}", teacherDO.getId(), 
                teacherDO.getId() != null ? teacherDO.getId().getClass().getName() : "null");
        
        // 3. 使用 UpdateWrapper 更新教师信息
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<TeacherDO> updateWrapper = 
                new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        
        // 设置WHERE条件 - 使用username而不是id进行匹配，避免类型转换问题
        updateWrapper.eq("username", username);
        
        // 标记是否有字段需要更新
        boolean needUpdate = false;
        
        // 设置需要更新的字段
        if (teacherUpdateReqDTO.getRealName() != null) {
            updateWrapper.set("real_name", teacherUpdateReqDTO.getRealName());
            needUpdate = true;
        }
        
        if (teacherUpdateReqDTO.getGender() != null) {
            updateWrapper.set("gender", teacherUpdateReqDTO.getGender());
            needUpdate = true;
        }
        
        if (teacherUpdateReqDTO.getPhone() != null) {
            updateWrapper.set("phone", teacherUpdateReqDTO.getPhone());
            needUpdate = true;
        }
        
        if (teacherUpdateReqDTO.getSchool() != null) {
            updateWrapper.set("school", teacherUpdateReqDTO.getSchool());
            needUpdate = true;
        }
        
        if (teacherUpdateReqDTO.getCollege() != null) {
            updateWrapper.set("college", teacherUpdateReqDTO.getCollege());
            needUpdate = true;
        }
        
        // 如果没有任何字段需要更新，直接返回成功
        if (!needUpdate) {
            return true;
        }
        
        // 执行更新操作
        boolean success = update(null, updateWrapper);
        
        if (success) {
            log.info("教师个人资料更新成功: {}", username);
        } else {
            log.error("教师个人资料更新失败: {}", username);
        }
        
        return success;
    }

    /**
     * 更新教师密码
     * @param passwordUpdateReqDTO 密码更新请求
     * @param request HTTP请求
     * @return
     */
    @Override
    public Boolean updatePassword(PasswordUpdateReqDTO passwordUpdateReqDTO, HttpServletRequest request) {
        // 1. 验证新密码与确认密码是否一致
        if (!passwordUpdateReqDTO.getNewPassword().equals(passwordUpdateReqDTO.getConfirmPassword())) {
            throw new ClientException(BaseErrorCode.NEW_PASSWORD_CONFIRM_ERROR);
        }
        
        // 2. 获取当前登录用户信息
        // 尝试多种方式获取token
        String token = null;
        
        // 1. 尝试从标准请求头获取
        token = request.getHeader("authorization");
        log.info("从authorization获取的token: {}", token);
        
        // 2. 如果为空，尝试从查询参数获取
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
            log.info("从查询参数获取的token: {}", token);
        }
        
        // 3. 如果为空，尝试从cookie获取
        if (token == null || token.isEmpty()) {
            jakarta.servlet.http.Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (jakarta.servlet.http.Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName()) || "authorization".equals(cookie.getName())) {
                        token = cookie.getValue();
                        log.info("从cookie获取的token: {}", token);
                        break;
                    }
                }
            }
        }
        
        if (token == null || token.isEmpty() || "undefined".equals(token)) {
            log.error("无法获取有效的token");
            throw new ClientException(TeacherErrorCode.TEACHER_NOT_LOGIN);
        }
        
        String tokenKey = RedisCacheConstant.LOGIN_USER_KEY + token;
        log.info("教师token键: {}", tokenKey);
        String username = stringRedisTemplate.opsForValue().get(tokenKey);
        
        if (username == null) {
            throw new ClientException(TeacherErrorCode.TEACHER_NOT_LOGIN);
        }
        
        // 3. 查询教师信息
        TeacherDO teacherDO = lambdaQuery()
                .eq(TeacherDO::getUsername, username)
                .one();
                
        if (teacherDO == null) {
            throw new ClientException(TeacherErrorCode.TEACHER_NULL);
        }
        
        // 4. 验证旧密码
        if (!PasswordEncoder.matches(passwordUpdateReqDTO.getOldPassword(), teacherDO.getPassword())) {
            throw new ClientException(TeacherErrorCode.OLD_PASSWORD_ERROR);
        }
        
        // 5. 使用 UpdateWrapper 更新密码
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<TeacherDO> updateWrapper = 
                new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        
        // 设置WHERE条件 - 使用username而不是id进行匹配，避免类型转换问题
        updateWrapper.eq("username", username);
        
        // 设置新密码
        updateWrapper.set("password", PasswordEncoder.encode(passwordUpdateReqDTO.getNewPassword()));
        
        // 执行更新操作
        boolean success = update(null, updateWrapper);
        
        if (success) {
            log.info("教师密码修改成功: {}", username);
            
            // 退出登录，使token失效，强制用户重新登录
            stringRedisTemplate.delete(tokenKey);
            UserHolder.removeUserInfoDTO();
        } else {
            log.error("教师密码修改失败: {}", username);
        }
          return success;
    }

    /**
     * 根据用户名获取教师信息
     * @param username 用户名
     * @return 教师信息
     */
    @Override
    public TeacherDO getTeacherInfo(String username) {
        LambdaQueryWrapper<TeacherDO> queryWrapper = Wrappers.<TeacherDO>lambdaQuery()
                .eq(TeacherDO::getUsername, username);
        return baseMapper.selectOne(queryWrapper);
    }
}

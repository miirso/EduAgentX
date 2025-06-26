package cumt.miirso.eduagentx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cumt.miirso.eduagentx.common.constant.RedisCacheConstant;
import cumt.miirso.eduagentx.convention.errorcode.BaseErrorCode;
import cumt.miirso.eduagentx.convention.errorcode.TeacherErrorCode;
import cumt.miirso.eduagentx.convention.exception.ClientException;
import cumt.miirso.eduagentx.dto.UserInfoDTO;
import cumt.miirso.eduagentx.dto.req.PasswordUpdateReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherLoginReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherPageQueryReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherRegisterReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherUpdateReqDTO;
import cumt.miirso.eduagentx.dto.resp.TeacherLoginRespDTO;
import cumt.miirso.eduagentx.dto.resp.TeacherPageQueryRespDTO;
import cumt.miirso.eduagentx.dto.resp.TeacherQueryRespDTO;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
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
    
    private final cumt.miirso.eduagentx.mapper.CourseTeacherMapper courseTeacherMapper;
    
    private final cumt.miirso.eduagentx.mapper.ChapterMapper chapterMapper;

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
        respDTO.setId(teacherDO.getId());
        respDTO.setUsername(teacherDO.getUsername());
        respDTO.setRealName(teacherDO.getRealName());
        respDTO.setGender(teacherDO.getGender());
        respDTO.setPhone(teacherDO.getPhone());
        respDTO.setCollege(teacherDO.getCollege());
        respDTO.setSchool(teacherDO.getSchool());
        respDTO.setTeacherNo(teacherDO.getTeacherNo());
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

    /**
     * 教师分页查询
     * 
     * 实现步骤：
     * 1. 参数验证和默认值处理
     * 2. 构建分页对象和查询条件
     * 3. 执行分页查询
     * 4. 转换查询结果为响应DTO
     * 5. 构建分页响应信息
     * 
     * @param requestParam 分页查询请求参数
     * @return 分页查询结果
     */
    @Override
    public TeacherPageQueryRespDTO pageQueryTeachers(TeacherPageQueryReqDTO requestParam) {
        log.info("=== 开始执行教师分页查询 ===");
        log.info("原始参数: {}", requestParam);

        // 1. 参数验证和默认值处理
        if (requestParam.getCurrent() == null || requestParam.getCurrent() < 1) {
            requestParam.setCurrent(1);
        }
        if (requestParam.getSize() == null || requestParam.getSize() < 1) {
            requestParam.setSize(10);
        }
        if (requestParam.getSize() > 100) {
            requestParam.setSize(100); // 限制最大每页条数
        }

        log.info("处理后参数: 当前页={}, 每页大小={}, 教师姓名='{}', 用户名='{}', 教师工号='{}', 学院='{}'", 
                requestParam.getCurrent(), requestParam.getSize(), 
                requestParam.getRealName(), requestParam.getUsername(), requestParam.getTeacherNo(), requestParam.getCollege());

        // 2. 构建分页对象
        Page<TeacherDO> page = new Page<>(requestParam.getCurrent(), requestParam.getSize());
        log.info("构建分页对象: current={}, size={}", page.getCurrent(), page.getSize());

        // 3. 构建查询条件
        LambdaQueryWrapper<TeacherDO> queryWrapper = new LambdaQueryWrapper<>();
        
        // 教师姓名模糊查询
        if (requestParam.getRealName() != null && !requestParam.getRealName().trim().isEmpty()) {
            queryWrapper.like(TeacherDO::getRealName, requestParam.getRealName().trim());
            log.info("添加教师姓名模糊查询条件: '{}'", requestParam.getRealName().trim());
        }
        
        // 用户名模糊查询
        if (requestParam.getUsername() != null && !requestParam.getUsername().trim().isEmpty()) {
            queryWrapper.like(TeacherDO::getUsername, requestParam.getUsername().trim());
            log.info("添加用户名模糊查询条件: '{}'", requestParam.getUsername().trim());
        }
        
        // 教师工号精确查询
        if (requestParam.getTeacherNo() != null && !requestParam.getTeacherNo().trim().isEmpty()) {
            queryWrapper.eq(TeacherDO::getTeacherNo, requestParam.getTeacherNo().trim());
            log.info("添加教师工号精确查询条件: '{}'", requestParam.getTeacherNo().trim());
        }
        
        // 性别精确查询
        if (requestParam.getGender() != null) {
            queryWrapper.eq(TeacherDO::getGender, requestParam.getGender());
            log.info("添加性别查询条件: {}", requestParam.getGender());
        }
        
        // 手机号模糊查询
        if (requestParam.getPhone() != null && !requestParam.getPhone().trim().isEmpty()) {
            queryWrapper.like(TeacherDO::getPhone, requestParam.getPhone().trim());
            log.info("添加手机号模糊查询条件: '{}'", requestParam.getPhone().trim());
        }
        
        // 学校模糊查询
        if (requestParam.getSchool() != null && !requestParam.getSchool().trim().isEmpty()) {
            queryWrapper.like(TeacherDO::getSchool, requestParam.getSchool().trim());
            log.info("添加学校模糊查询条件: '{}'", requestParam.getSchool().trim());
        }
        
        // 学院模糊查询
        if (requestParam.getCollege() != null && !requestParam.getCollege().trim().isEmpty()) {
            queryWrapper.like(TeacherDO::getCollege, requestParam.getCollege().trim());
            log.info("添加学院模糊查询条件: '{}'", requestParam.getCollege().trim());
        }
        
        // 只查询有效记录
        queryWrapper.eq(TeacherDO::getTag, true);
        
        // 4. 设置排序
        String sortBy = requestParam.getSortBy();
        String sortOrder = requestParam.getSortOrder();
        
        // 设置排序的默认值
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "create_time"; // 默认按创建时间排序
        }
        if (sortOrder == null || sortOrder.trim().isEmpty()) {
            sortOrder = "desc"; // 默认降序
        }
        
        boolean isAsc = "asc".equalsIgnoreCase(sortOrder);
        
        switch (sortBy.toLowerCase()) {
            case "create_time":
                queryWrapper.orderBy(true, isAsc, TeacherDO::getCreateTime);
                break;
            case "real_name":
                queryWrapper.orderBy(true, isAsc, TeacherDO::getRealName);
                break;
            case "teacher_no":
                queryWrapper.orderBy(true, isAsc, TeacherDO::getTeacherNo);
                break;
            default:
                queryWrapper.orderByDesc(TeacherDO::getCreateTime); // 默认按创建时间降序
        }
        log.info("设置排序: {} {}", sortBy, sortOrder);

        // 执行分页查询前的检查
        log.info("=== 执行分页查询前的检查 ===");
        log.info("查询条件构建完成，准备执行分页查询");
        
        // 5. 执行分页查询
        IPage<TeacherDO> pageResult = this.page(page, queryWrapper);
        
        log.info("=== 分页查询结果 ===");
        log.info("分页查询完成: 总记录数={}, 当前页={}, 总页数={}, 实际返回记录数={}", 
                pageResult.getTotal(), pageResult.getCurrent(), pageResult.getPages(), pageResult.getRecords().size());
        
        // 检查是否正确应用了分页
        if (pageResult.getTotal() > 0 && pageResult.getRecords().size() == pageResult.getTotal()) {
            log.warn("⚠️ 警告：返回的记录数等于总记录数，分页可能未生效！");
            log.warn("预期页面大小: {}, 实际返回记录数: {}, 总记录数: {}", 
                    requestParam.getSize(), pageResult.getRecords().size(), pageResult.getTotal());
        }

        // 6. 转换查询结果为响应DTO
        List<TeacherQueryRespDTO> teacherList = pageResult.getRecords().stream()
                .map(this::convertToDTO)
                .toList();

        // 7. 构建分页响应信息
        TeacherPageQueryRespDTO respDTO = new TeacherPageQueryRespDTO();
        respDTO.setCurrent(pageResult.getCurrent());
        respDTO.setSize(pageResult.getSize());
        respDTO.setTotal(pageResult.getTotal());
        respDTO.setPages(pageResult.getPages());
        respDTO.setRecords(teacherList);
        respDTO.setHasPrevious(pageResult.getCurrent() > 1);
        respDTO.setHasNext(pageResult.getCurrent() < pageResult.getPages());

        log.info("=== 教师分页查询完成 ===");
        log.info("返回结果: 当前页={}, 每页大小={}, 总记录数={}, 实际返回记录数={}", 
                respDTO.getCurrent(), respDTO.getSize(), respDTO.getTotal(), teacherList.size());

        return respDTO;
    }

    /**
     * 将 TeacherDO 转换为 TeacherQueryRespDTO
     * 
     * @param teacherDO 教师DO对象
     * @return 教师查询响应DTO
     */
    private TeacherQueryRespDTO convertToDTO(TeacherDO teacherDO) {
        TeacherQueryRespDTO dto = new TeacherQueryRespDTO();
        dto.setId(teacherDO.getId());
        dto.setUsername(teacherDO.getUsername());
        dto.setRealName(teacherDO.getRealName());
        dto.setGender(teacherDO.getGender());
        
        // 转换性别描述
        if (teacherDO.getGender() != null) {
            dto.setGenderDesc(teacherDO.getGender() == 0 ? "女" : "男");
        }
        
        dto.setPhone(teacherDO.getPhone());
        dto.setTeacherNo(teacherDO.getTeacherNo());
        dto.setSchool(teacherDO.getSchool());
        dto.setCollege(teacherDO.getCollege());
        dto.setCreateTime(teacherDO.getCreateTime());
        dto.setUpdateTime(teacherDO.getUpdateTime());
        
        return dto;
    }

    /**
     * 教师为课程分配章节
     * 
     * @param requestParam 章节分配请求参数
     * @param request HTTP请求（用于获取当前登录教师信息）
     * @return 章节分配响应
     */
    @Override
    public cumt.miirso.eduagentx.dto.resp.TeacherAssignChaptersRespDTO assignChaptersToCourse(
            cumt.miirso.eduagentx.dto.req.TeacherAssignChaptersReqDTO requestParam, 
            HttpServletRequest request) {
        
        log.info("教师为课程分配章节开始，课程ID: {}, 章节数量: {}", 
                requestParam.getCourseId(), 
                requestParam.getChapters() != null ? requestParam.getChapters().size() : 0);
        
        // 1. 参数校验
        if (requestParam.getCourseId() == null || requestParam.getCourseId().trim().isEmpty()) {
            throw new ClientException("课程ID不能为空");
        }
        
        if (requestParam.getChapters() == null || requestParam.getChapters().isEmpty()) {
            throw new ClientException("章节列表不能为空");
        }
        
        // 2. 获取当前登录教师ID
        Long currentTeacherId = getCurrentTeacherId(request);
        log.info("当前登录教师ID: {}", currentTeacherId);
        
        // 3. 校验教师是否为该课程的教师
        boolean isTeacherOfCourse = checkTeacherCoursePermission(currentTeacherId, requestParam.getCourseId());
        if (!isTeacherOfCourse) {
            log.error("教师ID {} 不是课程 {} 的教师，无权分配章节", currentTeacherId, requestParam.getCourseId());
            throw new ClientException("您不是该课程的教师，无权分配章节");
        }
        
        // 4. 批量插入章节信息
        int assignedCount = insertChapters(requestParam.getCourseId(), requestParam.getChapters());
        
        log.info("章节分配完成，课程ID: {}, 成功分配章节数: {}", requestParam.getCourseId(), assignedCount);
        
        return cumt.miirso.eduagentx.dto.resp.TeacherAssignChaptersRespDTO.builder()
                .courseId(requestParam.getCourseId())
                .assignedChapterCount(assignedCount)
                .message("章节分配成功")
                .build();
    }

    /**
     * 获取当前登录教师ID
     */
    private Long getCurrentTeacherId(HttpServletRequest request) {
        // 获取token
        String token = getTokenFromRequest(request);
        
        if (token == null || token.isEmpty() || "undefined".equals(token)) {
            log.error("无法获取有效的token");
            throw new ClientException(TeacherErrorCode.TEACHER_NOT_LOGIN);
        }
        
        // 从Redis获取用户名
        String tokenKey = RedisCacheConstant.LOGIN_USER_KEY + token;
        String username = stringRedisTemplate.opsForValue().get(tokenKey);
        
        if (username == null) {
            throw new ClientException(TeacherErrorCode.TEACHER_NOT_LOGIN);
        }
        
        // 根据用户名查询教师信息
        TeacherDO teacher = lambdaQuery()
                .eq(TeacherDO::getUsername, username)
                .one();
          if (teacher == null) {
            throw new ClientException(TeacherErrorCode.TEACHER_NULL);
        }
        
        return teacher.getId();
    }

    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String token = null;
        
        // 1. 尝试从标准请求头获取
        token = request.getHeader("authorization");
        log.debug("从authorization获取的token: {}", token);
        
        // 2. 如果为空，尝试从查询参数获取
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
            log.debug("从查询参数获取的token: {}", token);
        }
        
        // 3. 如果为空，尝试从cookie获取
        if (token == null || token.isEmpty()) {
            jakarta.servlet.http.Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (jakarta.servlet.http.Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName()) || "authorization".equals(cookie.getName())) {
                        token = cookie.getValue();
                        log.debug("从cookie获取的token: {}", token);
                        break;
                    }
                }
            }
        }
        
        return token;
    }    /**
     * 检查教师是否为该课程的教师
     */
    private boolean checkTeacherCoursePermission(Long teacherId, String courseId) {
        // 查询course_teachers表
        LambdaQueryWrapper<cumt.miirso.eduagentx.entity.CourseTeacherDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(cumt.miirso.eduagentx.entity.CourseTeacherDO::getTeacherId, teacherId.toString())
               .eq(cumt.miirso.eduagentx.entity.CourseTeacherDO::getCourseId, courseId);
        
        long count = courseTeacherMapper.selectCount(wrapper);
        return count > 0;
    }

    /**
     * 批量插入章节信息
     */
    private int insertChapters(String courseId, List<cumt.miirso.eduagentx.dto.req.TeacherAssignChaptersReqDTO.ChapterInfo> chapters) {
        int insertedCount = 0;
        
        for (cumt.miirso.eduagentx.dto.req.TeacherAssignChaptersReqDTO.ChapterInfo chapterInfo : chapters) {
            cumt.miirso.eduagentx.entity.ChapterDO chapterDO = new cumt.miirso.eduagentx.entity.ChapterDO();
            chapterDO.setCourseId(courseId);
            chapterDO.setTitle(chapterInfo.getTitle());
            chapterDO.setContent(chapterInfo.getContent());
            chapterDO.setOrder(chapterInfo.getOrder());
            
            int result = chapterMapper.insert(chapterDO);
            if (result > 0) {
                insertedCount++;
            }
        }
        
        return insertedCount;
    }

    /**
     * 教师删除课程章节
     * 
     * @param requestParam 删除章节请求参数
     * @param request HTTP请求（用于获取当前登录教师信息）
     * @return 删除章节响应
     */
    @Override
    public cumt.miirso.eduagentx.dto.resp.TeacherDeleteChapterRespDTO deleteChapterFromCourse(
            cumt.miirso.eduagentx.dto.req.TeacherDeleteChapterReqDTO requestParam,
            HttpServletRequest request) {
        
        log.info("教师删除课程章节开始，课程ID: {}, 章节顺序: {}", 
                requestParam.getCourseId(), requestParam.getOrder());
        
        // 1. 参数校验
        if (requestParam.getCourseId() == null || requestParam.getCourseId().trim().isEmpty()) {
            throw new ClientException("课程ID不能为空");
        }
        
        if (requestParam.getOrder() == null) {
            throw new ClientException("章节顺序号不能为空");
        }
        
        // 2. 获取当前登录教师ID
        Long currentTeacherId = getCurrentTeacherId(request);
        log.info("当前登录教师ID: {}", currentTeacherId);
        
        // 3. 校验教师是否为该课程的教师
        boolean isTeacherOfCourse = checkTeacherCoursePermission(currentTeacherId, requestParam.getCourseId());
        if (!isTeacherOfCourse) {
            log.error("教师ID {} 不是课程 {} 的教师，无权删除章节", currentTeacherId, requestParam.getCourseId());
            throw new ClientException("您不是该课程的教师，无权删除章节");
        }
        
        // 4. 删除章节
        LambdaQueryWrapper<cumt.miirso.eduagentx.entity.ChapterDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(cumt.miirso.eduagentx.entity.ChapterDO::getCourseId, requestParam.getCourseId())
               .eq(cumt.miirso.eduagentx.entity.ChapterDO::getOrder, requestParam.getOrder());
        
        int deletedCount = chapterMapper.delete(wrapper);
        
        if (deletedCount == 0) {
            throw new ClientException("未找到指定的章节");
        }
        
        log.info("章节删除完成，课程ID: {}, 章节顺序: {}, 删除数量: {}", 
                requestParam.getCourseId(), requestParam.getOrder(), deletedCount);
        
        return cumt.miirso.eduagentx.dto.resp.TeacherDeleteChapterRespDTO.builder()
                .courseId(requestParam.getCourseId())
                .order(requestParam.getOrder())
                .message("章节删除成功")
                .build();
    }

    /**
     * 教师修改课程章节
     * 
     * @param requestParam 修改章节请求参数
     * @param request HTTP请求（用于获取当前登录教师信息）
     * @return 修改章节响应
     */
    @Override
    public cumt.miirso.eduagentx.dto.resp.TeacherUpdateChapterRespDTO updateChapterOfCourse(
            cumt.miirso.eduagentx.dto.req.TeacherUpdateChapterReqDTO requestParam,
            HttpServletRequest request) {
        
        log.info("教师修改课程章节开始，课程ID: {}, 原章节顺序: {}, 新章节顺序: {}", 
                requestParam.getCourseId(), requestParam.getOriginalOrder(), requestParam.getNewOrder());
        
        // 1. 参数校验
        if (requestParam.getCourseId() == null || requestParam.getCourseId().trim().isEmpty()) {
            throw new ClientException("课程ID不能为空");
        }
        
        if (requestParam.getOriginalOrder() == null) {
            throw new ClientException("原章节顺序号不能为空");
        }
        
        // 2. 获取当前登录教师ID
        Long currentTeacherId = getCurrentTeacherId(request);
        log.info("当前登录教师ID: {}", currentTeacherId);
        
        // 3. 校验教师是否为该课程的教师
        boolean isTeacherOfCourse = checkTeacherCoursePermission(currentTeacherId, requestParam.getCourseId());
        if (!isTeacherOfCourse) {
            log.error("教师ID {} 不是课程 {} 的教师，无权修改章节", currentTeacherId, requestParam.getCourseId());
            throw new ClientException("您不是该课程的教师，无权修改章节");
        }
        
        // 4. 检查原章节是否存在
        LambdaQueryWrapper<cumt.miirso.eduagentx.entity.ChapterDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(cumt.miirso.eduagentx.entity.ChapterDO::getCourseId, requestParam.getCourseId())
                   .eq(cumt.miirso.eduagentx.entity.ChapterDO::getOrder, requestParam.getOriginalOrder());
        
        cumt.miirso.eduagentx.entity.ChapterDO existingChapter = chapterMapper.selectOne(queryWrapper);
        if (existingChapter == null) {
            throw new ClientException("未找到指定的章节");
        }
        
        // 5. 如果新的顺序号与其他章节冲突，需要检查
        if (requestParam.getNewOrder() != null && !requestParam.getNewOrder().equals(requestParam.getOriginalOrder())) {
            LambdaQueryWrapper<cumt.miirso.eduagentx.entity.ChapterDO> conflictWrapper = new LambdaQueryWrapper<>();
            conflictWrapper.eq(cumt.miirso.eduagentx.entity.ChapterDO::getCourseId, requestParam.getCourseId())
                          .eq(cumt.miirso.eduagentx.entity.ChapterDO::getOrder, requestParam.getNewOrder());
            
            cumt.miirso.eduagentx.entity.ChapterDO conflictChapter = chapterMapper.selectOne(conflictWrapper);
            if (conflictChapter != null) {
                throw new ClientException("新的章节顺序号已被其他章节使用");
            }
        }
        
        // 6. 更新章节信息
        cumt.miirso.eduagentx.entity.ChapterDO updateChapter = new cumt.miirso.eduagentx.entity.ChapterDO();
        updateChapter.setId(existingChapter.getId());
        
        if (requestParam.getTitle() != null) {
            updateChapter.setTitle(requestParam.getTitle());
        }
        if (requestParam.getContent() != null) {
            updateChapter.setContent(requestParam.getContent());
        }
        if (requestParam.getNewOrder() != null) {
            updateChapter.setOrder(requestParam.getNewOrder());
        }
        
        int updatedCount = chapterMapper.updateById(updateChapter);
        
        if (updatedCount == 0) {
            throw new ClientException("章节更新失败");
        }
        
        Integer finalOrder = requestParam.getNewOrder() != null ? requestParam.getNewOrder() : requestParam.getOriginalOrder();
        
        log.info("章节修改完成，课程ID: {}, 最终章节顺序: {}", requestParam.getCourseId(), finalOrder);
        
        return cumt.miirso.eduagentx.dto.resp.TeacherUpdateChapterRespDTO.builder()
                .courseId(requestParam.getCourseId())
                .order(finalOrder)
                .message("章节修改成功")
                .build();
    }

    /**
     * 教师查询课程章节
     * 
     * @param requestParam 查询章节请求参数
     * @param request HTTP请求（用于获取当前登录教师信息）
     * @return 章节列表响应
     */
    @Override
    public cumt.miirso.eduagentx.dto.resp.TeacherQueryChapterRespDTO queryChaptersOfCourse(
            cumt.miirso.eduagentx.dto.req.TeacherQueryChapterReqDTO requestParam,
            HttpServletRequest request) {
        
        log.info("教师查询课程章节开始，课程ID: {}", requestParam.getCourseId());
        
        // 1. 参数校验
        if (requestParam.getCourseId() == null || requestParam.getCourseId().trim().isEmpty()) {
            throw new ClientException("课程ID不能为空");
        }
        
        // // 2. 获取当前登录教师ID
        // Long currentTeacherId = getCurrentTeacherId(request);
        // log.info("当前登录教师ID: {}", currentTeacherId);
        //
        // // 3. 校验教师是否为该课程的教师
        // boolean isTeacherOfCourse = checkTeacherCoursePermission(currentTeacherId, requestParam.getCourseId());
        // if (!isTeacherOfCourse) {
        //     log.error("教师ID {} 不是课程 {} 的教师，无权查询章节", currentTeacherId, requestParam.getCourseId());
        //     throw new ClientException("您不是该课程的教师，无权查询章节");
        // }
        
        // 4. 查询章节列表
        LambdaQueryWrapper<cumt.miirso.eduagentx.entity.ChapterDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(cumt.miirso.eduagentx.entity.ChapterDO::getCourseId, requestParam.getCourseId())
               .orderByAsc(cumt.miirso.eduagentx.entity.ChapterDO::getOrder);
        
        List<cumt.miirso.eduagentx.entity.ChapterDO> chapters = chapterMapper.selectList(wrapper);
          // 5. 转换为响应DTO
        List<cumt.miirso.eduagentx.dto.resp.TeacherQueryChapterRespDTO.ChapterInfo> chapterInfoList = 
                chapters.stream().map(chapter -> {
                    cumt.miirso.eduagentx.dto.resp.TeacherQueryChapterRespDTO.ChapterInfo.ChapterInfoBuilder builder = 
                        cumt.miirso.eduagentx.dto.resp.TeacherQueryChapterRespDTO.ChapterInfo.builder()
                            .id(chapter.getId())
                            .title(chapter.getTitle())
                            .content(chapter.getContent())
                            .order(chapter.getOrder());
                    
                    // 安全的时间类型转换
                    if (chapter.getCreateTime() != null) {
                        builder.createTime(dateToLocalDateTime(chapter.getCreateTime()));
                    }
                    if (chapter.getUpdateTime() != null) {
                        builder.updateTime(dateToLocalDateTime(chapter.getUpdateTime()));
                    }
                    
                    return builder.build();
                }).collect(java.util.stream.Collectors.toList());
        
        log.info("章节查询完成，课程ID: {}, 章节数量: {}", requestParam.getCourseId(), chapterInfoList.size());
        
        return cumt.miirso.eduagentx.dto.resp.TeacherQueryChapterRespDTO.builder()
                .courseId(requestParam.getCourseId())
                .chapters(chapterInfoList)
                .totalCount(chapterInfoList.size())
                .build();
    }
    
    /**
     * 将Date转换为LocalDateTime的辅助方法
     * 
     * @param date 需要转换的Date对象
     * @return LocalDateTime对象，如果输入为null则返回null
     */
    private LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 教师新增课程章节
     * 
     * @param requestParam 新增章节请求参数
     * @param request HTTP请求（用于获取当前登录教师信息）
     * @return 新增章节响应
     */
    @Override
    public cumt.miirso.eduagentx.dto.resp.TeacherAddChapterRespDTO addChapterToCourse(
            cumt.miirso.eduagentx.dto.req.TeacherAddChapterReqDTO requestParam,
            HttpServletRequest request) {
        
        log.info("教师新增课程章节开始，课程ID: {}, 章节标题: {}, 章节顺序: {}", 
                requestParam.getCourseId(), requestParam.getTitle(), requestParam.getOrder());
        
        // 1. 参数校验
        if (requestParam.getCourseId() == null || requestParam.getCourseId().trim().isEmpty()) {
            throw new ClientException("课程ID不能为空");
        }
        
        if (requestParam.getTitle() == null || requestParam.getTitle().trim().isEmpty()) {
            throw new ClientException("章节标题不能为空");
        }
        
        if (requestParam.getOrder() == null) {
            throw new ClientException("章节顺序号不能为空");
        }
        
        // 2. 获取当前登录教师ID
        Long currentTeacherId = getCurrentTeacherId(request);
        log.info("当前登录教师ID: {}", currentTeacherId);
        
        // 3. 校验教师是否为该课程的教师
        boolean isTeacherOfCourse = checkTeacherCoursePermission(currentTeacherId, requestParam.getCourseId());
        if (!isTeacherOfCourse) {
            log.error("教师ID {} 不是课程 {} 的教师，无权新增章节", currentTeacherId, requestParam.getCourseId());
            throw new ClientException("您不是该课程的教师，无权新增章节");
        }
        
        // 4. 检查章节顺序是否已存在
        LambdaQueryWrapper<cumt.miirso.eduagentx.entity.ChapterDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(cumt.miirso.eduagentx.entity.ChapterDO::getCourseId, requestParam.getCourseId())
                   .eq(cumt.miirso.eduagentx.entity.ChapterDO::getOrder, requestParam.getOrder());
        
        cumt.miirso.eduagentx.entity.ChapterDO existingChapter = chapterMapper.selectOne(queryWrapper);
        if (existingChapter != null) {
            throw new ClientException("该章节顺序号已被使用，请选择其他顺序号");
        }
        
        // 5. 创建新章节
        cumt.miirso.eduagentx.entity.ChapterDO newChapter = new cumt.miirso.eduagentx.entity.ChapterDO();
        newChapter.setCourseId(requestParam.getCourseId());
        newChapter.setTitle(requestParam.getTitle().trim());
        newChapter.setContent(requestParam.getContent());
        newChapter.setOrder(requestParam.getOrder());
        
        int insertResult = chapterMapper.insert(newChapter);
        
        if (insertResult == 0) {
            throw new ClientException("章节创建失败");
        }
        
        log.info("章节新增完成，课程ID: {}, 章节ID: {}, 章节顺序: {}", 
                requestParam.getCourseId(), newChapter.getId(), requestParam.getOrder());
        
        return cumt.miirso.eduagentx.dto.resp.TeacherAddChapterRespDTO.builder()
                .courseId(requestParam.getCourseId())
                .chapterId(newChapter.getId())
                .title(requestParam.getTitle().trim())
                .order(requestParam.getOrder())
                .message("章节新增成功")
                .build();
    }
}

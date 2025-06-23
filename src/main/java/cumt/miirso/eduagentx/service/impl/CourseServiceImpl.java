package cumt.miirso.eduagentx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cumt.miirso.eduagentx.common.constant.RedisCacheConstant;
import cumt.miirso.eduagentx.dto.req.CourseCreateReqDTO;
import cumt.miirso.eduagentx.dto.req.CoursePageQueryReqDTO;
import cumt.miirso.eduagentx.dto.resp.CourseDetailRespDTO;
import cumt.miirso.eduagentx.dto.resp.CoursePageQueryRespDTO;
import cumt.miirso.eduagentx.dto.resp.CourseQueryRespDTO;
import cumt.miirso.eduagentx.entity.CourseDO;
import cumt.miirso.eduagentx.entity.CourseClassDO;
import cumt.miirso.eduagentx.entity.CourseTeacherDO;
import cumt.miirso.eduagentx.entity.ClassDO;
import cumt.miirso.eduagentx.entity.TeacherDO;
import cumt.miirso.eduagentx.entity.EnrollmentDO;
import cumt.miirso.eduagentx.mapper.CourseMapper;
import cumt.miirso.eduagentx.mapper.CourseClassMapper;
import cumt.miirso.eduagentx.mapper.CourseTeacherMapper;
import cumt.miirso.eduagentx.mapper.ClassMapper;
import cumt.miirso.eduagentx.mapper.EnrollmentMapper;
import cumt.miirso.eduagentx.mapper.TeacherMapper;
import cumt.miirso.eduagentx.service.CourseService;
import cumt.miirso.eduagentx.service.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 课程服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, CourseDO> implements CourseService {    private final CourseTeacherMapper courseTeacherMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final TeacherService teacherService;
    private final CourseClassMapper courseClassMapper;
    private final ClassMapper classMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final TeacherMapper teacherMapper;

    /**
     * 创建课程
     * @param requestParam 创建课程请求参数
     * @param request HTTP请求（用于获取当前登录教师信息）
     */
    @Override
    @Transactional
    public void createCourse(CourseCreateReqDTO requestParam, HttpServletRequest request) {
        // 1. 获取当前登录教师ID
        String teacherId = getCurrentTeacherId(request);
        
        // 2. 创建课程记录
        CourseDO courseDO = new CourseDO();
        
        // 生成课程ID：学科ID前2位 + 5位自增序号 + 1位类型
        String courseId = generateCourseId(requestParam.getSubjectId(), requestParam.getType());
        courseDO.setId(courseId);
        
        courseDO.setSubjectId(requestParam.getSubjectId());
        courseDO.setName(requestParam.getName());
        courseDO.setDescription(requestParam.getDescription());
        courseDO.setCoverImage(requestParam.getCoverImage());
        courseDO.setStartDate(requestParam.getStartDate());
        courseDO.setEndDate(requestParam.getEndDate());
        courseDO.setAssessmentMethod(requestParam.getAssessmentMethod());
        courseDO.setType(requestParam.getType());
        
        baseMapper.insert(courseDO);
        
        // 3. 创建课程-教师关联记录
        CourseTeacherDO courseTeacherDO = new CourseTeacherDO();
        courseTeacherDO.setCourseId(courseId);
        courseTeacherDO.setTeacherId(teacherId);
        courseTeacherMapper.insert(courseTeacherDO);
        
        log.info("教师[{}]成功创建课程[{}:{}]", teacherId, courseId, requestParam.getName());
    }
      /**
     * 生成课程ID
     * @param subjectId 学科ID
     * @param type 课程类型
     * @return 8位课程ID
     */
    private String generateCourseId(Integer subjectId, String type) {
        // 获取学科ID的前2位作为前缀
        String subjectPrefix = String.format("%02d", subjectId);
        
        // 生成5位自增序号（基于当前时间戳和随机数）
        long timestamp = System.currentTimeMillis();
        int sequence = (int) ((timestamp % 100000) + Math.random() * 10000) % 100000;
        String sequenceStr = String.format("%05d", sequence);
        
        // 拼接：前2位学科ID + 5位序号 + 1位类型
        return subjectPrefix + sequenceStr + type.toUpperCase();
    }
      /**
     * 从请求中获取当前登录教师ID
     * @param request HTTP请求
     * @return 教师ID
     */
    private String getCurrentTeacherId(HttpServletRequest request) {
        // 尝试多种方式获取token
        String token = request.getHeader("authorization");

        log.info("获取到的token: {}", token);

        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
        }
        
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("用户未登录");
        }
        
        // 从Redis中获取用户名
        String userIdKey = RedisCacheConstant.LOGIN_USER_KEY + token;
        String username = stringRedisTemplate.opsForValue().get(userIdKey);

        if (username == null || username.isEmpty()) {
            throw new RuntimeException("登录已过期，请重新登录");
        }
          // 根据用户名获取教师信息
        TeacherDO teacherDO = teacherService.getTeacherInfo(username);
        if (teacherDO == null) {
            throw new RuntimeException("教师信息不存在");
        }
        
        // 返回教师的id作为teacher_id（不是teacherNo）
        return teacherDO.getId().toString();
    }
    
    /**
     * 查询所有课程信息
     * @return 课程信息列表
     */
    @Override
    public List<CourseQueryRespDTO> listAllCourses() {
        // 查询所有未删除的课程
        List<CourseDO> courseDOList = this.lambdaQuery()
                .eq(CourseDO::getTag, true)
                .list();
        
        // 转换为DTO列表
        return courseDOList.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 将CourseDO转换为CourseQueryRespDTO
     * @param courseDO 课程DO对象
     * @return 课程查询响应DTO
     */
    private CourseQueryRespDTO convertToDTO(CourseDO courseDO) {
        CourseQueryRespDTO dto = new CourseQueryRespDTO();
        dto.setId(courseDO.getId());
        dto.setSubjectId(courseDO.getSubjectId());
        dto.setName(courseDO.getName());
        dto.setDescription(courseDO.getDescription());
        dto.setCoverImage(courseDO.getCoverImage());
        dto.setStartDate(courseDO.getStartDate());
        dto.setEndDate(courseDO.getEndDate());
        dto.setAssessmentMethod(courseDO.getAssessmentMethod());
        dto.setType(courseDO.getType());
        dto.setCreateTime(courseDO.getCreateTime());
        dto.setUpdateTime(courseDO.getUpdateTime());
        return dto;    }

    /**
     * 分页查询课程信息
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
     */    @Override
    public CoursePageQueryRespDTO pageQueryCourses(CoursePageQueryReqDTO requestParam) {
        log.info("=== 开始执行课程分页查询 ===");
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

        log.info("处理后参数: 当前页={}, 每页大小={}, 课程名称='{}', 课程类型='{}', 学科ID={}", 
                requestParam.getCurrent(), requestParam.getSize(), 
                requestParam.getName(), requestParam.getType(), requestParam.getSubjectId());        // 2. 构建分页对象
        Page<CourseDO> page = new Page<>(requestParam.getCurrent(), requestParam.getSize());
        log.info("构建分页对象: current={}, size={}", page.getCurrent(), page.getSize());
        log.info("分页对象详细信息: {}", page);

        // 3. 构建查询条件
        LambdaQueryWrapper<CourseDO> queryWrapper = new LambdaQueryWrapper<>();
        
        // 课程名称模糊查询 - 修复空字符串判断
        if (requestParam.getName() != null && !requestParam.getName().trim().isEmpty()) {
            queryWrapper.like(CourseDO::getName, requestParam.getName().trim());
            log.info("添加课程名称模糊查询条件: '{}'", requestParam.getName().trim());
        }
        
        // 课程类型精确查询 - 修复空字符串判断
        if (requestParam.getType() != null && !requestParam.getType().trim().isEmpty()) {
            queryWrapper.eq(CourseDO::getType, requestParam.getType().trim());
            log.info("添加课程类型查询条件: '{}'", requestParam.getType().trim());
        }
        
        // 学科ID精确查询
        if (requestParam.getSubjectId() != null) {
            queryWrapper.eq(CourseDO::getSubjectId, requestParam.getSubjectId());
            log.info("添加学科ID查询条件: {}", requestParam.getSubjectId());
        }        
        // 考核方式模糊查询 - 修复空字符串判断
        if (requestParam.getAssessmentMethod() != null && !requestParam.getAssessmentMethod().trim().isEmpty()) {
            queryWrapper.like(CourseDO::getAssessmentMethod, requestParam.getAssessmentMethod().trim());
            log.info("添加考核方式查询条件: '{}'", requestParam.getAssessmentMethod().trim());
        }
        
        // 只查询有效记录
        queryWrapper.eq(CourseDO::getTag, true);
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
                queryWrapper.orderBy(true, isAsc, CourseDO::getCreateTime);
                break;
            case "start_date":
                queryWrapper.orderBy(true, isAsc, CourseDO::getStartDate);
                break;
            case "end_date":
                queryWrapper.orderBy(true, isAsc, CourseDO::getEndDate);
                break;
            case "name":
                queryWrapper.orderBy(true, isAsc, CourseDO::getName);
                break;
            default:
                queryWrapper.orderByDesc(CourseDO::getCreateTime); // 默认按创建时间降序
        }
        log.info("设置排序: {} {}", sortBy, sortOrder);

        // 检查是否使用了分页插件
        log.info("=== 执行分页查询前的检查 ===");
        log.info("查询条件构建完成，准备执行分页查询");
        
        // 5. 执行分页查询
        IPage<CourseDO> pageResult = this.page(page, queryWrapper);
        
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
        List<CourseQueryRespDTO> courseList = pageResult.getRecords().stream()
                .map(this::convertToDTO)
                .toList();

        // 7. 构建分页响应信息
        CoursePageQueryRespDTO respDTO = new CoursePageQueryRespDTO();
        respDTO.setCurrent(pageResult.getCurrent());
        respDTO.setSize(pageResult.getSize());
        respDTO.setTotal(pageResult.getTotal());
        respDTO.setPages(pageResult.getPages());
        respDTO.setRecords(courseList);
        respDTO.setHasPrevious(pageResult.getCurrent() > 1);
        respDTO.setHasNext(pageResult.getCurrent() < pageResult.getPages());        log.info("=== 课程分页查询完成 ===");
        log.info("返回结果: 当前页={}, 每页大小={}, 总记录数={}, 实际返回记录数={}", 
                respDTO.getCurrent(), respDTO.getSize(), respDTO.getTotal(), courseList.size());

        return respDTO;
    }

    /**
     * 根据课程ID查询课程详细信息
     * 
     * 实现步骤：
     * 1. 查询课程基本信息
     * 2. 查询分配的教师列表
     * 3. 查询关联的班级列表
     * 4. 统计选课学生数量
     * 5. 构建返回结果
     * 
     * @param courseId 课程ID
     * @return 课程详细信息
     */
    @Override
    public CourseDetailRespDTO getCourseDetailById(String courseId) {
        log.info("=== 开始查询课程详细信息 ===");
        log.info("课程ID: {}", courseId);
        
        // 1. 查询课程基本信息
        CourseDO courseDO = this.lambdaQuery()
                .eq(CourseDO::getId, courseId)
                .eq(CourseDO::getTag, true)
                .one();
        
        if (courseDO == null) {
            throw new RuntimeException("课程不存在或已被删除");
        }
        log.info("找到课程: {} - {}", courseDO.getId(), courseDO.getName());
        
        // 2. 查询分配的教师列表
        List<CourseDetailRespDTO.AssignedTeacherInfo> assignedTeachers = getAssignedTeachers(courseId);
        log.info("分配的教师数量: {}", assignedTeachers.size());
        
        // 3. 查询关联的班级列表
        List<CourseDetailRespDTO.AssignedClassInfo> assignedClasses = getAssignedClasses(courseId);
        log.info("关联的班级数量: {}", assignedClasses.size());
        
        // 4. 统计选课学生数量
        CourseDetailRespDTO.EnrollmentStatistics enrollmentStats = getEnrollmentStatistics(courseId);
        log.info("选课统计: 总学生数={}, 班级数={}, 教师数={}", 
                enrollmentStats.getTotalEnrollments(), 
                enrollmentStats.getTotalClasses(), 
                enrollmentStats.getTotalTeachers());
        
        // 5. 构建返回结果
        CourseDetailRespDTO respDTO = new CourseDetailRespDTO();
        respDTO.setId(courseDO.getId());
        respDTO.setSubjectId(courseDO.getSubjectId());        respDTO.setName(courseDO.getName());
        respDTO.setDescription(courseDO.getDescription());
        respDTO.setCoverImage(courseDO.getCoverImage());
        respDTO.setStartDate(courseDO.getStartDate() != null ? 
                courseDO.getStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null);
        respDTO.setEndDate(courseDO.getEndDate() != null ? 
                courseDO.getEndDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null);
        respDTO.setAssessmentMethod(courseDO.getAssessmentMethod());
        respDTO.setType(courseDO.getType());
        respDTO.setCreateTime(courseDO.getCreateTime() != null ? 
                courseDO.getCreateTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null);
        respDTO.setUpdateTime(courseDO.getUpdateTime() != null ? 
                courseDO.getUpdateTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null);
        respDTO.setAssignedTeachers(assignedTeachers);
        respDTO.setAssignedClasses(assignedClasses);
        respDTO.setEnrollmentStats(enrollmentStats);
        
        log.info("=== 课程详细信息查询完成 ===");
        log.info("返回结果: {}", respDTO);
        
        return respDTO;
    }
    
    /**
     * 查询课程分配的教师列表
     */
    private List<CourseDetailRespDTO.AssignedTeacherInfo> getAssignedTeachers(String courseId) {
        List<CourseTeacherDO> courseTeachers = courseTeacherMapper.selectList(
                new LambdaQueryWrapper<CourseTeacherDO>()
                        .eq(CourseTeacherDO::getCourseId, courseId)
        );
        
        return courseTeachers.stream()
                .map(ct -> {
                    TeacherDO teacher = teacherMapper.selectById(ct.getTeacherId());                    if (teacher != null && teacher.getTag()) {
                        CourseDetailRespDTO.AssignedTeacherInfo info = new CourseDetailRespDTO.AssignedTeacherInfo();
                        info.setTeacherId(teacher.getId().toString());
                        info.setTeacherName(teacher.getRealName());
                        info.setTeacherNo(teacher.getTeacherNo());
                        info.setCollege(teacher.getCollege());
                        return info;
                    }
                    return null;
                })
                .filter(info -> info != null)
                .toList();
    }
    
    /**
     * 查询课程关联的班级列表
     */
    private List<CourseDetailRespDTO.AssignedClassInfo> getAssignedClasses(String courseId) {
        List<CourseClassDO> courseClasses = courseClassMapper.selectList(
                new LambdaQueryWrapper<CourseClassDO>()
                        .eq(CourseClassDO::getCourseId, courseId)
        );
        
        return courseClasses.stream()
                .map(cc -> {
                    ClassDO classDO = classMapper.selectById(cc.getClassId());
                    if (classDO != null && classDO.getTag()) {
                        CourseDetailRespDTO.AssignedClassInfo info = new CourseDetailRespDTO.AssignedClassInfo();
                        info.setClassId(classDO.getId());
                        info.setClassName(classDO.getName());
                        info.setMajorId(classDO.getMajorId());
                        return info;
                    }
                    return null;
                })
                .filter(info -> info != null)
                .toList();
    }
    
    /**
     * 统计选课信息
     */
    private CourseDetailRespDTO.EnrollmentStatistics getEnrollmentStatistics(String courseId) {
        // 统计选课学生总数
        Long totalEnrollments = enrollmentMapper.selectCount(
                new LambdaQueryWrapper<EnrollmentDO>()
                        .eq(EnrollmentDO::getCourseId, courseId)
                        .eq(EnrollmentDO::getTag, true)
        );
        
        // 统计关联班级数
        Long totalClasses = courseClassMapper.selectCount(
                new LambdaQueryWrapper<CourseClassDO>()
                        .eq(CourseClassDO::getCourseId, courseId)
        );
        
        // 统计分配教师数
        Long totalTeachers = courseTeacherMapper.selectCount(
                new LambdaQueryWrapper<CourseTeacherDO>()
                        .eq(CourseTeacherDO::getCourseId, courseId)
        );
        
        CourseDetailRespDTO.EnrollmentStatistics stats = new CourseDetailRespDTO.EnrollmentStatistics();
        stats.setTotalEnrollments(totalEnrollments.intValue());
        stats.setTotalClasses(totalClasses.intValue());
        stats.setTotalTeachers(totalTeachers.intValue());
        
        return stats;
    }
}

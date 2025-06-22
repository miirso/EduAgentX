package cumt.miirso.eduagentx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cumt.miirso.eduagentx.common.constant.RedisCacheConstant;
import cumt.miirso.eduagentx.dto.req.CourseCreateReqDTO;
import cumt.miirso.eduagentx.dto.resp.CourseQueryRespDTO;
import cumt.miirso.eduagentx.entity.CourseDO;
import cumt.miirso.eduagentx.entity.CourseTeacherDO;
import cumt.miirso.eduagentx.entity.TeacherDO;
import cumt.miirso.eduagentx.mapper.CourseMapper;
import cumt.miirso.eduagentx.mapper.CourseTeacherMapper;
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
public class CourseServiceImpl extends ServiceImpl<CourseMapper, CourseDO> implements CourseService {

    private final CourseTeacherMapper courseTeacherMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final TeacherService teacherService;

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
        return dto;
    }
}

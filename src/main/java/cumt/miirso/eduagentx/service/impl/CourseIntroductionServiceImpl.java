package cumt.miirso.eduagentx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cumt.miirso.eduagentx.convention.exception.ClientException;
import cumt.miirso.eduagentx.dto.req.QueryIntroductionReqDTO;
import cumt.miirso.eduagentx.dto.req.SaveIntroductionReqDTO;
import cumt.miirso.eduagentx.dto.resp.QueryIntroductionRespDTO;
import cumt.miirso.eduagentx.dto.resp.SaveIntroductionRespDTO;
import cumt.miirso.eduagentx.entity.CourseIntroductionDO;
import cumt.miirso.eduagentx.mapper.CourseIntroductionMapper;
import cumt.miirso.eduagentx.service.CourseIntroductionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 课程简介服务实现类
 * @author miirso
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseIntroductionServiceImpl extends ServiceImpl<CourseIntroductionMapper, CourseIntroductionDO> implements CourseIntroductionService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaveIntroductionRespDTO saveOrUpdateIntroduction(SaveIntroductionReqDTO requestParam) {
        log.info("=== 开始执行[保存或更新课程简介] ===");
        log.info("参数信息: courseId={}, introductionText长度={}", 
                requestParam.getCourseId(), 
                requestParam.getIntroductionText() != null ? requestParam.getIntroductionText().length() : 0);

        // 1. 参数验证
        validateSaveRequest(requestParam);

        // 2. 查询是否已存在简介
        LambdaQueryWrapper<CourseIntroductionDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseIntroductionDO::getCourseId, requestParam.getCourseId())
                   .eq(CourseIntroductionDO::getTag, true);
        
        CourseIntroductionDO existingIntroduction = baseMapper.selectOne(queryWrapper);
        
        SaveIntroductionRespDTO respDTO = new SaveIntroductionRespDTO();
        respDTO.setCourseId(requestParam.getCourseId());
        respDTO.setUpdateTime(new Date());

        if (existingIntroduction != null) {
            // 3. 更新现有简介
            log.info("发现已存在的课程简介，执行更新操作，ID={}", existingIntroduction.getId());
            
            LambdaUpdateWrapper<CourseIntroductionDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(CourseIntroductionDO::getId, existingIntroduction.getId())
                        .set(CourseIntroductionDO::getIntroductionText, requestParam.getIntroductionText())
                        .set(CourseIntroductionDO::getIntroductionHtml, requestParam.getIntroductionHtml())
                        .set(CourseIntroductionDO::getKeywords, requestParam.getKeywords())
                        .set(CourseIntroductionDO::getLearningGoals, requestParam.getLearningGoals())
                        .set(CourseIntroductionDO::getPrerequisites, requestParam.getPrerequisites())
                        .set(CourseIntroductionDO::getTargetAudience, requestParam.getTargetAudience())
                        .set(CourseIntroductionDO::getDifficultyLevel, requestParam.getDifficultyLevel())
                        .set(CourseIntroductionDO::getEstimatedHours, requestParam.getEstimatedHours())
                        .set(CourseIntroductionDO::getUpdateTime, new Date());
            
            boolean updateResult = update(updateWrapper);
            if (!updateResult) {
                throw new ClientException("课程简介更新失败");
            }

            respDTO.setId(existingIntroduction.getId());
            respDTO.setOperation("更新");
            respDTO.setMessage("课程简介更新成功");
            
        } else {
            // 4. 新增简介
            log.info("未发现已存在的课程简介，执行新增操作");
            
            CourseIntroductionDO newIntroduction = new CourseIntroductionDO();
            BeanUtils.copyProperties(requestParam, newIntroduction);
            newIntroduction.setCreateTime(new Date());
            newIntroduction.setUpdateTime(new Date());
            newIntroduction.setTag(true);

            boolean saveResult = save(newIntroduction);
            if (!saveResult) {
                throw new ClientException("课程简介保存失败");
            }

            respDTO.setId(newIntroduction.getId());
            respDTO.setOperation("新增");
            respDTO.setMessage("课程简介保存成功");
        }

        log.info("=== 结束执行[保存或更新课程简介] ===");
        return respDTO;
    }

    @Override
    public QueryIntroductionRespDTO queryIntroduction(QueryIntroductionReqDTO requestParam) {
        log.info("=== 开始执行[查询课程简介] ===");
        log.info("参数信息: courseId={}", requestParam.getCourseId());

        // 1. 参数验证
        if (!StringUtils.hasText(requestParam.getCourseId())) {
            throw new ClientException("课程ID不能为空");
        }

        // 2. 查询课程简介
        LambdaQueryWrapper<CourseIntroductionDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseIntroductionDO::getCourseId, requestParam.getCourseId())
                   .eq(CourseIntroductionDO::getTag, true);
        
        CourseIntroductionDO introduction = baseMapper.selectOne(queryWrapper);
        
        if (introduction == null) {
            log.warn("未找到课程简介信息: courseId={}", requestParam.getCourseId());
            throw new ClientException("未找到该课程的简介信息");
        }

        // 3. 组装返回数据
        QueryIntroductionRespDTO respDTO = new QueryIntroductionRespDTO();
        BeanUtils.copyProperties(introduction, respDTO);

        log.info("查询课程简介成功: courseId={}, introductionId={}", requestParam.getCourseId(), introduction.getId());
        log.info("=== 结束执行[查询课程简介] ===");
        return respDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteIntroduction(String courseId) {
        log.info("=== 开始执行[删除课程简介] ===");
        log.info("参数信息: courseId={}", courseId);

        // 1. 参数验证
        if (!StringUtils.hasText(courseId)) {
            throw new ClientException("课程ID不能为空");
        }

        // 2. 执行逻辑删除
        LambdaUpdateWrapper<CourseIntroductionDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CourseIntroductionDO::getCourseId, courseId)
                    .eq(CourseIntroductionDO::getTag, true)
                    .set(CourseIntroductionDO::getTag, false)
                    .set(CourseIntroductionDO::getUpdateTime, new Date());

        boolean result = update(updateWrapper);
        
        if (result) {
            log.info("课程简介删除成功: courseId={}", courseId);
        } else {
            log.warn("课程简介删除失败或不存在: courseId={}", courseId);
        }

        log.info("=== 结束执行[删除课程简介] ===");
        return result;
    }

    /**
     * 验证保存请求参数
     * 
     * @param requestParam 请求参数
     */
    private void validateSaveRequest(SaveIntroductionReqDTO requestParam) {
        if (!StringUtils.hasText(requestParam.getCourseId())) {
            throw new ClientException("课程ID不能为空");
        }

        if (!StringUtils.hasText(requestParam.getIntroductionText())) {
            throw new ClientException("课程简介文本内容不能为空");
        }

        // 验证文本长度限制
        if (requestParam.getIntroductionText().length() > 10000) {
            throw new ClientException("课程简介文本内容过长，最大支持10000字符");
        }

        // 验证难度等级
        if (StringUtils.hasText(requestParam.getDifficultyLevel())) {
            String level = requestParam.getDifficultyLevel();
            if (!"初级".equals(level) && !"中级".equals(level) && !"高级".equals(level)) {
                throw new ClientException("难度等级只能是：初级、中级、高级");
            }
        }

        // 验证预计学习时长
        if (requestParam.getEstimatedHours() != null && requestParam.getEstimatedHours() <= 0) {
            throw new ClientException("预计学习时长必须大于0");
        }
    }
}

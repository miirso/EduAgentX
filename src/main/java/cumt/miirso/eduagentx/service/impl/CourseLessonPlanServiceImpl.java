package cumt.miirso.eduagentx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cumt.miirso.eduagentx.convention.exception.ClientException;
import cumt.miirso.eduagentx.dto.req.QueryLessonPlanReqDTO;
import cumt.miirso.eduagentx.dto.req.UploadLessonPlanReqDTO;
import cumt.miirso.eduagentx.dto.resp.QueryLessonPlanRespDTO;
import cumt.miirso.eduagentx.dto.resp.UploadLessonPlanRespDTO;
import cumt.miirso.eduagentx.entity.CourseLessonPlanDO;
import cumt.miirso.eduagentx.mapper.CourseLessonPlanMapper;
import cumt.miirso.eduagentx.service.CourseLessonPlanService;
import cumt.miirso.eduagentx.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 课程教案服务实现类
 * @author miirso
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseLessonPlanServiceImpl extends ServiceImpl<CourseLessonPlanMapper, CourseLessonPlanDO> implements CourseLessonPlanService {

    private final FileUtils fileUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UploadLessonPlanRespDTO uploadOrUpdateLessonPlan(UploadLessonPlanReqDTO requestParam) {
        log.info("=== 开始执行上传/更新教案 ===");
        log.info("课程ID: {}, 章节ID: {}, 版本: {}", 
                requestParam.getCourseId(), requestParam.getChapterId(), requestParam.getVersion());

        String courseId = requestParam.getCourseId();
        Integer chapterId = requestParam.getChapterId();
        Integer version = requestParam.getVersion();
        
        // 1. 参数验证
        if (courseId == null || courseId.trim().isEmpty()) {
            throw new ClientException("课程ID不能为空");
        }

        if (requestParam.getPlanFile() == null || requestParam.getPlanFile().isEmpty()) {
            throw new ClientException("教案文件不能为空");
        }

        // 2. 确定版本号
        if (version == null) {
            version = getNextVersion(courseId, chapterId);
            log.info("未指定版本号，自动生成版本号: {}", version);
        }

        // 3. 检查是否存在指定版本的教案
        LambdaQueryWrapper<CourseLessonPlanDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseLessonPlanDO::getCourseId, courseId)
                   .eq(CourseLessonPlanDO::getVersion, version)
                   .eq(CourseLessonPlanDO::getTag, true);
        
        if (chapterId != null) {
            queryWrapper.eq(CourseLessonPlanDO::getChapterId, chapterId);
        } else {
            queryWrapper.isNull(CourseLessonPlanDO::getChapterId);
        }
        
        CourseLessonPlanDO existingPlan = baseMapper.selectOne(queryWrapper);
        String operationType;
        CourseLessonPlanDO planDO;
        String filePath = null;
        
        try {
            // 4. 保存文件到磁盘
            filePath = fileUtils.saveLessonPlanFile(requestParam.getPlanFile(), courseId, chapterId, version);
            
            if (existingPlan != null) {
                // 更新操作：删除旧文件，保存新记录数据
                fileUtils.deleteLessonPlanFile(existingPlan.getPlanFilePath());
                
                existingPlan.setPlanFilePath(filePath);
                existingPlan.setFileName(requestParam.getPlanFile().getOriginalFilename());
                existingPlan.setPlanTitle(requestParam.getPlanTitle());
                existingPlan.setUpdateTime(new Date());
                
                int updateResult = baseMapper.updateById(existingPlan);
                if (updateResult == 0) {
                    throw new ClientException("教案更新失败");
                }
                
                operationType = "更新";
                planDO = existingPlan;
            } else {
                // 新增操作
                CourseLessonPlanDO newPlan = new CourseLessonPlanDO();
                newPlan.setCourseId(courseId);
                newPlan.setChapterId(chapterId);
                newPlan.setPlanFilePath(filePath);
                newPlan.setFileName(requestParam.getPlanFile().getOriginalFilename());
                newPlan.setPlanTitle(requestParam.getPlanTitle());
                newPlan.setPlanType(chapterId != null ? "chapter" : "course");
                newPlan.setVersion(version);
                newPlan.setCreateTime(new Date());
                newPlan.setUpdateTime(new Date());
                newPlan.setTag(true);
                
                int insertResult = baseMapper.insert(newPlan);
                if (insertResult == 0) {
                    throw new ClientException("教案创建失败");
                }
                
                operationType = "新增";
                planDO = newPlan;
            }
            
        } catch (Exception e) {
            // 如果数据库操作失败，删除已保存的文件
            if (filePath != null) {
                fileUtils.deleteLessonPlanFile(filePath);
            }
            throw e;
        }

        log.info("教案{}成功，课程ID: {}, 章节ID: {}, 版本号: {}, 教案ID: {}", 
                operationType, courseId, chapterId, version, planDO.getId());

        // 5. 构建响应结果
        return UploadLessonPlanRespDTO.builder()
                .planId(planDO.getId())
                .courseId(planDO.getCourseId())
                .chapterId(planDO.getChapterId())
                .planTitle(planDO.getPlanTitle())
                .fileName(planDO.getFileName())
                .version(planDO.getVersion())
                .operationType(operationType)
                .createTime(dateToLocalDateTime(planDO.getCreateTime()))
                .build();
    }

    @Override
    public QueryLessonPlanRespDTO queryLessonPlan(QueryLessonPlanReqDTO requestParam) {
        log.info("=== 开始执行查询教案 ===");
        log.info("课程ID: {}, 章节ID: {}, 版本: {}", 
                requestParam.getCourseId(), requestParam.getChapterId(), requestParam.getVersion());

        String courseId = requestParam.getCourseId();
        Integer chapterId = requestParam.getChapterId();
        Integer version = requestParam.getVersion();

        // 1. 参数验证
        if (courseId == null || courseId.trim().isEmpty()) {
            throw new ClientException("课程ID不能为空");
        }

        // 2. 构建查询条件
        LambdaQueryWrapper<CourseLessonPlanDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseLessonPlanDO::getCourseId, courseId)
                   .eq(CourseLessonPlanDO::getTag, true);
        
        if (chapterId != null) {
            queryWrapper.eq(CourseLessonPlanDO::getChapterId, chapterId);
        } else {
            queryWrapper.isNull(CourseLessonPlanDO::getChapterId);
        }

        if (version != null) {
            // 查询指定版本
            queryWrapper.eq(CourseLessonPlanDO::getVersion, version);
            log.info("查询指定版本的教案: {}", version);
        } else {
            // 查询最新版本
            queryWrapper.orderByDesc(CourseLessonPlanDO::getVersion);
            log.info("查询最新版本的教案");
        }

        // 3. 执行查询
        CourseLessonPlanDO planDO = baseMapper.selectOne(queryWrapper);
        
        if (planDO == null) {
            String errorMsg = chapterId != null ?
                    String.format("未找到课程[%s]章节[%d]的教案", courseId, chapterId) :
                    String.format("未找到课程[%s]的教案", courseId);
            if (version != null) {
                errorMsg += String.format("(版本: %d)", version);
            }
            log.warn(errorMsg);
            throw new ClientException(errorMsg);
        }

        log.info("找到教案记录，教案ID: {}, 版本号: {}, 文件路径: {}", 
                planDO.getId(), planDO.getVersion(), planDO.getPlanFilePath());

        // 4. 读取文件内容
        String fileContent = fileUtils.readLessonPlanFile(planDO.getPlanFilePath());

        // 5. 构建响应结果
        QueryLessonPlanRespDTO respDTO = QueryLessonPlanRespDTO.builder()
                .planId(planDO.getId())
                .courseId(planDO.getCourseId())
                .chapterId(planDO.getChapterId())
                .planContent(fileContent)
                .filePath(planDO.getPlanFilePath())
                .fileName(planDO.getFileName())
                .planTitle(planDO.getPlanTitle())
                .planType(planDO.getPlanType())
                .version(planDO.getVersion())
                .createTime(dateToLocalDateTime(planDO.getCreateTime()))
                .updateTime(dateToLocalDateTime(planDO.getUpdateTime()))
                .build();

        log.info("=== 结束执行查询教案 ===");
        return respDTO;
    }

    /**
     * 获取下一个版本号
     * 
     * @param courseId 课程ID
     * @param chapterId 章节ID
     * @return 下一个版本号
     */
    private Integer getNextVersion(String courseId, Integer chapterId) {
        LambdaQueryWrapper<CourseLessonPlanDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseLessonPlanDO::getCourseId, courseId)
                   .eq(CourseLessonPlanDO::getTag, true)
                   .orderByDesc(CourseLessonPlanDO::getVersion);
        
        if (chapterId != null) {
            queryWrapper.eq(CourseLessonPlanDO::getChapterId, chapterId);
        } else {
            queryWrapper.isNull(CourseLessonPlanDO::getChapterId);
        }

        CourseLessonPlanDO latestPlan = baseMapper.selectOne(queryWrapper);
        if (latestPlan == null) {
            return 1;
        } else {
            return latestPlan.getVersion() + 1;
        }
    }

    /**
     * Date转LocalDateTime
     * 
     * @param date Date对象
     * @return LocalDateTime对象
     */
    private LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}

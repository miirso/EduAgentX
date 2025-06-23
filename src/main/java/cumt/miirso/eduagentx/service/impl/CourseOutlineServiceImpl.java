package cumt.miirso.eduagentx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cumt.miirso.eduagentx.convention.exception.ClientException;
import cumt.miirso.eduagentx.dto.req.QueryOutlineReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherUploadOutlineReqDTO;
import cumt.miirso.eduagentx.dto.resp.QueryOutlineRespDTO;
import cumt.miirso.eduagentx.dto.resp.TeacherUploadOutlineRespDTO;
import cumt.miirso.eduagentx.entity.CourseOutlineDO;
import cumt.miirso.eduagentx.mapper.CourseOutlineMapper;
import cumt.miirso.eduagentx.service.CourseOutlineService;
import cumt.miirso.eduagentx.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 课程大纲服务实现类
 * @author miirso
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseOutlineServiceImpl extends ServiceImpl<CourseOutlineMapper, CourseOutlineDO> implements CourseOutlineService {

    private final FileUtils fileUtils;

    /**
     * 教师上传/更新课程大纲
     * 
     * 实现步骤：
     * 1. 参数校验
     * 2. 检查是否存在指定版本的大纲
     * 3. 如果未指定版本号，获取最新版本号并递增
     * 4. 保存文件到磁盘
     * 5. 执行新增或更新数据库记录
     * 6. 返回操作结果
     * 
     * @param requestParam 上传大纲请求参数
     * @return 上传大纲响应
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TeacherUploadOutlineRespDTO uploadOrUpdateOutline(TeacherUploadOutlineReqDTO requestParam) {
        log.info("=== 开始执行教师上传/更新课程大纲 ===");
        log.info("参数信息: 课程ID={}, 文件名={}, 版本号={}", 
                requestParam.getCourseId(), 
                requestParam.getOutlineFile() != null ? requestParam.getOutlineFile().getOriginalFilename() : "null", 
                requestParam.getVersion());
        
        // 1. 参数校验
        if (requestParam.getCourseId() == null || requestParam.getCourseId().trim().isEmpty()) {
            throw new ClientException("课程ID不能为空");
        }
        
        if (requestParam.getOutlineFile() == null || requestParam.getOutlineFile().isEmpty()) {
            throw new ClientException("大纲文件不能为空");
        }
        
        String courseId = requestParam.getCourseId().trim();
        String originalFileName = requestParam.getOutlineFile().getOriginalFilename();
        Integer version = requestParam.getVersion();
        
        // 2. 确定版本号
        if (version == null) {
            // 获取该课程的最新版本号并递增
            version = getNextVersion(courseId);
            log.info("未指定版本号，自动生成版本号: {}", version);
        }
        
        // 3. 检查是否存在指定版本的大纲
        LambdaQueryWrapper<CourseOutlineDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseOutlineDO::getCourseId, courseId)
                   .eq(CourseOutlineDO::getVersion, version)
                   .eq(CourseOutlineDO::getTag, true);
        
        CourseOutlineDO existingOutline = baseMapper.selectOne(queryWrapper);
          String operationType;
        CourseOutlineDO outlineDO;
        String filePath = null;
        
        try {
            // 4. 保存文件到磁盘
            filePath = fileUtils.saveOutlineFile(requestParam.getOutlineFile(), courseId, version);
            log.info("文件保存成功: {}", filePath);
            
            if (existingOutline != null) {
                // 5. 更新现有大纲
                log.info("找到现有大纲记录，执行更新操作，大纲ID: {}", existingOutline.getId());
                
                // 删除旧文件
                if (existingOutline.getOutlineFilePath() != null) {
                    fileUtils.deleteOutlineFile(existingOutline.getOutlineFilePath());
                }
                
                existingOutline.setOutlineFilePath(filePath);
                existingOutline.setFileName(originalFileName);
                
                int updateResult = baseMapper.updateById(existingOutline);
                if (updateResult == 0) {
                    throw new ClientException("大纲更新失败");
                }
                
                operationType = "更新";
                outlineDO = existingOutline;
                
            } else {
                // 6. 新增大纲
                log.info("未找到现有大纲记录，执行新增操作");
                
                CourseOutlineDO newOutline = new CourseOutlineDO();
                newOutline.setCourseId(courseId);
                newOutline.setOutlineFilePath(filePath);
                newOutline.setFileName(originalFileName);
                newOutline.setVersion(version);
                
                int insertResult = baseMapper.insert(newOutline);
                if (insertResult == 0) {
                    throw new ClientException("大纲创建失败");
                }
                
                operationType = "新增";
                outlineDO = newOutline;
            }
            
        } catch (Exception e) {
            // 如果数据库操作失败，删除已保存的文件
            if (filePath != null) {
                fileUtils.deleteOutlineFile(filePath);
            }
            throw e;
        }
        
        log.info("大纲{}成功，课程ID: {}, 版本号: {}, 大纲ID: {}", 
                operationType, courseId, version, outlineDO.getId());
        
        // 7. 构建响应结果
        TeacherUploadOutlineRespDTO respDTO = TeacherUploadOutlineRespDTO.builder()
                .outlineId(outlineDO.getId())
                .courseId(courseId)
                .filePath(outlineDO.getOutlineFilePath())
                .version(version)
                .operationType(operationType)
                .message("大纲" + operationType + "成功")
                .createTime(dateToLocalDateTime(outlineDO.getCreateTime()))
                .build();
        
        log.info("=== 结束执行教师上传/更新课程大纲 ===");
        return respDTO;
    }

    /**
     * 查询课程大纲
     * 
     * 实现步骤：
     * 1. 参数校验
     * 2. 构建查询条件
     * 3. 执行查询操作
     * 4. 读取文件内容
     * 5. 返回查询结果
     * 
     * @param requestParam 查询大纲请求参数
     * @return 大纲文件内容
     */
    @Override
    public QueryOutlineRespDTO queryOutline(QueryOutlineReqDTO requestParam) {
        log.info("=== 开始执行查询课程大纲 ===");
        log.info("参数信息: 课程ID={}, 版本号={}", requestParam.getCourseId(), requestParam.getVersion());
        
        // 1. 参数校验
        if (requestParam.getCourseId() == null || requestParam.getCourseId().trim().isEmpty()) {
            throw new ClientException("课程ID不能为空");
        }
        
        String courseId = requestParam.getCourseId().trim();
        Integer version = requestParam.getVersion();
        
        // 2. 构建查询条件
        LambdaQueryWrapper<CourseOutlineDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseOutlineDO::getCourseId, courseId)
                   .eq(CourseOutlineDO::getTag, true);
        
        if (version != null) {
            // 查询指定版本
            queryWrapper.eq(CourseOutlineDO::getVersion, version);
            log.info("查询指定版本的大纲: {}", version);
        } else {
            // 查询最新版本
            queryWrapper.orderByDesc(CourseOutlineDO::getVersion);
            log.info("查询最新版本的大纲");
        }
        
        // 3. 执行查询
        CourseOutlineDO outlineDO = baseMapper.selectOne(queryWrapper);
        
        if (outlineDO == null) {
            String errorMsg = version != null ? 
                    String.format("未找到课程[%s]版本[%d]的大纲", courseId, version) :
                    String.format("未找到课程[%s]的大纲", courseId);
            log.warn(errorMsg);
            throw new ClientException(errorMsg);
        }
        
        log.info("找到大纲记录，大纲ID: {}, 版本号: {}, 文件路径: {}", 
                outlineDO.getId(), outlineDO.getVersion(), outlineDO.getOutlineFilePath());
        
        // 4. 读取文件内容
        String fileContent = fileUtils.readOutlineFile(outlineDO.getOutlineFilePath());
        
        // 5. 构建响应结果
        QueryOutlineRespDTO respDTO = QueryOutlineRespDTO.builder()
                .outlineId(outlineDO.getId())
                .courseId(outlineDO.getCourseId())
                .outlineContent(fileContent)
                .filePath(outlineDO.getOutlineFilePath())
                .fileName(outlineDO.getFileName())
                .version(outlineDO.getVersion())
                .createTime(dateToLocalDateTime(outlineDO.getCreateTime()))
                .updateTime(dateToLocalDateTime(outlineDO.getUpdateTime()))
                .build();
        
        log.info("=== 结束执行查询课程大纲 ===");
        return respDTO;
    }

    /**
     * 获取指定课程的下一个版本号
     * 
     * @param courseId 课程ID
     * @return 下一个版本号
     */
    private Integer getNextVersion(String courseId) {
        LambdaQueryWrapper<CourseOutlineDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseOutlineDO::getCourseId, courseId)
                   .eq(CourseOutlineDO::getTag, true)
                   .orderByDesc(CourseOutlineDO::getVersion)
                   .last("LIMIT 1");
        
        CourseOutlineDO latestOutline = baseMapper.selectOne(queryWrapper);
        
        if (latestOutline == null) {
            return 1; // 第一个版本
        }
        
        return latestOutline.getVersion() + 1;
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
}

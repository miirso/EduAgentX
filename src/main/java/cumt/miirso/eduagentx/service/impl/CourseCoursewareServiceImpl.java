package cumt.miirso.eduagentx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cumt.miirso.eduagentx.convention.exception.ClientException;
import cumt.miirso.eduagentx.dto.req.QueryCoursewareListReqDTO;
import cumt.miirso.eduagentx.dto.req.UploadCoursewareReqDTO;
import cumt.miirso.eduagentx.dto.resp.CoursewareInfoDTO;
import cumt.miirso.eduagentx.dto.resp.QueryCoursewareListRespDTO;
import cumt.miirso.eduagentx.dto.resp.UploadCoursewareRespDTO;
import cumt.miirso.eduagentx.entity.CourseCoursewareDO;
import cumt.miirso.eduagentx.mapper.CourseCoursewareMapper;
import cumt.miirso.eduagentx.service.CourseCoursewareService;
import cumt.miirso.eduagentx.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程课件服务实现类
 * @author miirso
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseCoursewareServiceImpl extends ServiceImpl<CourseCoursewareMapper, CourseCoursewareDO> implements CourseCoursewareService {

    private final FileUtils fileUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UploadCoursewareRespDTO uploadCourseware(UploadCoursewareReqDTO requestParam) {
        log.info("=== 开始执行上传课件 ===");
        log.info("课程ID: {}, 课件标题: {}, 课件顺序: {}", 
                requestParam.getCourseId(), requestParam.getCoursewareTitle(), requestParam.getCoursewareOrder());

        String courseId = requestParam.getCourseId();
        
        // 1. 参数验证
        if (courseId == null || courseId.trim().isEmpty()) {
            throw new ClientException("课程ID不能为空");
        }

        if (requestParam.getCoursewareFile() == null || requestParam.getCoursewareFile().isEmpty()) {
            throw new ClientException("课件文件不能为空");
        }

        // 2. 确定课件顺序
        Integer coursewareOrder = requestParam.getCoursewareOrder();
        if (coursewareOrder == null) {
            coursewareOrder = getNextOrder(courseId);
            log.info("未指定课件顺序，自动生成顺序: {}", coursewareOrder);
        }

        String filePath = null;
        CourseCoursewareDO coursewareDO = null;
        
        try {
            // 3. 保存文件到磁盘
            filePath = fileUtils.saveCoursewareFile(requestParam.getCoursewareFile(), courseId);
            
            // 4. 保存课件记录到数据库
            coursewareDO = new CourseCoursewareDO();
            coursewareDO.setCourseId(courseId);
            coursewareDO.setCoursewareFilePath(filePath);
            coursewareDO.setFileName(requestParam.getCoursewareFile().getOriginalFilename());
            coursewareDO.setCoursewareTitle(requestParam.getCoursewareTitle());
            coursewareDO.setCoursewareOrder(coursewareOrder);
            coursewareDO.setFileSize(requestParam.getCoursewareFile().getSize());
            coursewareDO.setCreateTime(new Date());
            coursewareDO.setUpdateTime(new Date());
            coursewareDO.setTag(true);
            
            int insertResult = baseMapper.insert(coursewareDO);
            if (insertResult == 0) {
                throw new ClientException("课件创建失败");
            }
            
        } catch (Exception e) {
            // 如果数据库操作失败，删除已保存的文件
            if (filePath != null) {
                fileUtils.deleteCoursewareFile(filePath);
            }
            throw e;
        }

        log.info("课件上传成功，课程ID: {}, 课件ID: {}", courseId, coursewareDO.getId());

        // 5. 构建响应结果
        return UploadCoursewareRespDTO.builder()
                .coursewareId(coursewareDO.getId())
                .courseId(coursewareDO.getCourseId())
                .coursewareTitle(coursewareDO.getCoursewareTitle())
                .fileName(coursewareDO.getFileName())
                .coursewareOrder(coursewareDO.getCoursewareOrder())
                .fileSize(coursewareDO.getFileSize())
                .createTime(dateToLocalDateTime(coursewareDO.getCreateTime()))
                .build();
    }

    @Override
    public QueryCoursewareListRespDTO queryCoursewareList(QueryCoursewareListReqDTO requestParam) {
        log.info("=== 开始执行查询课件列表 ===");
        log.info("课程ID: {}", requestParam.getCourseId());

        String courseId = requestParam.getCourseId();

        // 1. 参数验证
        if (courseId == null || courseId.trim().isEmpty()) {
            throw new ClientException("课程ID不能为空");
        }

        // 2. 查询课件列表
        LambdaQueryWrapper<CourseCoursewareDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseCoursewareDO::getCourseId, courseId)
                   .eq(CourseCoursewareDO::getTag, true)
                   .orderByAsc(CourseCoursewareDO::getCoursewareOrder);

        List<CourseCoursewareDO> coursewareList = baseMapper.selectList(queryWrapper);

        log.info("查询到课件数量: {}", coursewareList.size());

        // 3. 转换为DTO
        List<CoursewareInfoDTO> coursewareInfoList = coursewareList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 4. 构建响应结果
        QueryCoursewareListRespDTO respDTO = QueryCoursewareListRespDTO.builder()
                .courseId(courseId)
                .totalCount(coursewareInfoList.size())
                .coursewareList(coursewareInfoList)
                .build();

        log.info("=== 结束执行查询课件列表 ===");
        return respDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCourseware(Integer coursewareId) {
        log.info("=== 开始执行删除课件 ===");
        log.info("课件ID: {}", coursewareId);

        // 1. 参数验证
        if (coursewareId == null) {
            throw new ClientException("课件ID不能为空");
        }

        // 2. 查询课件信息
        CourseCoursewareDO coursewareDO = baseMapper.selectById(coursewareId);
        if (coursewareDO == null || !coursewareDO.getTag()) {
            throw new ClientException("课件不存在");
        }

        // 3. 逻辑删除数据库记录
        coursewareDO.setTag(false);
        coursewareDO.setDeleteTime(new Date());
        coursewareDO.setUpdateTime(new Date());
        
        int updateResult = baseMapper.updateById(coursewareDO);
        if (updateResult == 0) {
            throw new ClientException("课件删除失败");
        }

        // 4. 删除物理文件
        boolean fileDeleted = fileUtils.deleteCoursewareFile(coursewareDO.getCoursewareFilePath());
        if (!fileDeleted) {
            log.warn("课件文件删除失败，但数据库记录已标记为删除: {}", coursewareDO.getCoursewareFilePath());
        }

        log.info("课件删除成功，课件ID: {}", coursewareId);
        return true;
    }

    /**
     * 获取下一个课件顺序
     * 
     * @param courseId 课程ID
     * @return 下一个顺序号
     */
    private Integer getNextOrder(String courseId) {
        LambdaQueryWrapper<CourseCoursewareDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseCoursewareDO::getCourseId, courseId)
                   .eq(CourseCoursewareDO::getTag, true)
                   .orderByDesc(CourseCoursewareDO::getCoursewareOrder);

        CourseCoursewareDO latestCourseware = baseMapper.selectOne(queryWrapper);
        if (latestCourseware == null) {
            return 1;
        } else {
            return latestCourseware.getCoursewareOrder() + 1;
        }
    }

    /**
     * 转换为DTO
     * 
     * @param coursewareDO 课件实体
     * @return 课件信息DTO
     */
    private CoursewareInfoDTO convertToDTO(CourseCoursewareDO coursewareDO) {
        return CoursewareInfoDTO.builder()
                .coursewareId(coursewareDO.getId())
                .courseId(coursewareDO.getCourseId())
                .coursewareTitle(coursewareDO.getCoursewareTitle())
                .fileName(coursewareDO.getFileName())
                .coursewareOrder(coursewareDO.getCoursewareOrder())
                .fileSize(coursewareDO.getFileSize())
                .fileSizeFormatted(formatFileSize(coursewareDO.getFileSize()))
                .createTime(dateToLocalDateTime(coursewareDO.getCreateTime()))
                .updateTime(dateToLocalDateTime(coursewareDO.getUpdateTime()))
                .build();
    }

    /**
     * 格式化文件大小
     * 
     * @param fileSize 文件大小（字节）
     * @return 格式化后的文件大小
     */
    private String formatFileSize(Long fileSize) {
        if (fileSize == null) {
            return "未知";
        }
        
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
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

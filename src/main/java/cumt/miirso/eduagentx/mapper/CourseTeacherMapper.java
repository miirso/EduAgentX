package cumt.miirso.eduagentx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cumt.miirso.eduagentx.entity.CourseTeacherDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程教师关联Mapper接口
 */
@Mapper
public interface CourseTeacherMapper extends BaseMapper<CourseTeacherDO> {
    
    /**
     * 批量插入课程教师关联
     * @param entityList 实体列表
     * @return 影响行数
     */
    int insertBatchSomeColumn(List<CourseTeacherDO> entityList);
}

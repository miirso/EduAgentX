package cumt.miirso.eduagentx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cumt.miirso.eduagentx.entity.CourseClassDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程班级关联Mapper接口
 */
@Mapper
public interface CourseClassMapper extends BaseMapper<CourseClassDO> {
}

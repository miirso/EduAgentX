package cumt.miirso.eduagentx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cumt.miirso.eduagentx.entity.StudentClassRelationDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生班级关联表Mapper接口
 */
@Mapper
public interface StudentClassRelationMapper extends BaseMapper<StudentClassRelationDO> {
}

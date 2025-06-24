package cumt.miirso.eduagentx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cumt.miirso.eduagentx.entity.StudentExamRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生答题记录Mapper接口
 * @author miirso
 */
@Mapper
public interface StudentExamRecordMapper extends BaseMapper<StudentExamRecordDO> {
}

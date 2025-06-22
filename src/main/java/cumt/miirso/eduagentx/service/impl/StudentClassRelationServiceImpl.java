package cumt.miirso.eduagentx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cumt.miirso.eduagentx.entity.StudentClassRelationDO;
import cumt.miirso.eduagentx.mapper.StudentClassRelationMapper;
import cumt.miirso.eduagentx.service.StudentClassRelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学生班级关联服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentClassRelationServiceImpl extends ServiceImpl<StudentClassRelationMapper, StudentClassRelationDO> implements StudentClassRelationService {
    
    @Override
    public boolean addRelation(Long studentId, Long classId, String studentNo, String classCode) {
        // 检查是否已存在相同的关联
        LambdaQueryWrapper<StudentClassRelationDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentClassRelationDO::getStudentNo, studentNo)
                .eq(StudentClassRelationDO::getClassCode, classCode);
        
        if (this.count(queryWrapper) > 0) {
            log.info("学生[{}]与班级[{}]的关联已存在", studentNo, classCode);
            return true; // 已存在，视为添加成功
        }
        
        // 创建新的关联
        StudentClassRelationDO relationDO = new StudentClassRelationDO();
        relationDO.setStudentNo(studentNo);
        relationDO.setClassCode(classCode);
        
        boolean result = this.save(relationDO);
        if (result) {
            log.info("成功添加学生[{}]与班级[{}]的关联", studentNo, classCode);
        } else {
            log.error("添加学生[{}]与班级[{}]的关联失败", studentNo, classCode);
        }
        
        return result;
    }
    
    @Override
    public StudentClassRelationDO getByStudentNo(String studentNo) {
        LambdaQueryWrapper<StudentClassRelationDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentClassRelationDO::getStudentNo, studentNo)
                .eq(StudentClassRelationDO::getTag, true)
                .orderByDesc(StudentClassRelationDO::getCreateTime)
                .last("LIMIT 1"); // 获取最新的一条记录
        
        return this.getOne(queryWrapper);
    }
    
    @Override
    public List<StudentClassRelationDO> getByClassCode(String classCode) {
        LambdaQueryWrapper<StudentClassRelationDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentClassRelationDO::getClassCode, classCode)
                .eq(StudentClassRelationDO::getTag, true);
        
        return this.list(queryWrapper);
    }
}

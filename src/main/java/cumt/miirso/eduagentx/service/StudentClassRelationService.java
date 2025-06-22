package cumt.miirso.eduagentx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cumt.miirso.eduagentx.entity.StudentClassRelationDO;

/**
 * 学生班级关联服务接口
 */
public interface StudentClassRelationService extends IService<StudentClassRelationDO> {
    
    /**
     * 添加学生班级关联关系
     * 
     * @param studentId 学生ID
     * @param classId 班级ID
     * @param studentNo 学号
     * @param classCode 班级代码
     * @return 是否添加成功
     */
    boolean addRelation(Long studentId, Long classId, String studentNo, String classCode);
    
    /**
     * 根据学号查询班级关联信息
     * 
     * @param studentNo 学号
     * @return 学生班级关联对象
     */
    StudentClassRelationDO getByStudentNo(String studentNo);
    
    /**
     * 根据班级代码查询该班级的所有学生关联信息
     * 
     * @param classCode 班级代码
     * @return 学生班级关联对象列表
     */
    java.util.List<StudentClassRelationDO> getByClassCode(String classCode);
}

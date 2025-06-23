package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

import java.util.List;

/**
 * 班级退课响应DTO
 * 
 * @author EduAgentX
 */
@Data
public class ClassUnenrollCourseRespDTO {

    /**
     * 班级名称
     */
    private String className;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 班级总学生数
     */
    private Integer totalStudents;

    /**
     * 成功退课学生数
     */
    private Integer unenrolledCount;

    /**
     * 没有选过课的学生数
     */
    private Integer notEnrolledCount;

    /**
     * 成功退课的学生列表
     */
    private List<UnenrolledStudentInfo> unenrolledStudents;

    /**
     * 没有选过课的学生列表
     */
    private List<UnenrolledStudentInfo> notEnrolledStudents;

    /**
     * 是否删除了课程-班级关联
     */
    private Boolean courseClassRelationDeleted;

    @Data
    public static class UnenrolledStudentInfo {
        /**
         * 学生ID
         */
        private Long studentId;

        /**
         * 学生姓名
         */
        private String studentName;

        /**
         * 学号
         */
        private String studentNo;
    }
    
    @Override
    public String toString() {
        return String.format("ClassUnenrollCourseRespDTO{className='%s', courseId='%s', courseName='%s', totalStudents=%d, unenrolledCount=%d, notEnrolledCount=%d, courseClassRelationDeleted=%s}", 
                className, courseId, courseName, totalStudents, unenrolledCount, notEnrolledCount, courseClassRelationDeleted);
    }
}

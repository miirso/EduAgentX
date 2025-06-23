package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

import java.util.List;

/**
 * 班级选课响应DTO
 * 
 * @author EduAgentX
 */
@Data
public class ClassEnrollCourseRespDTO {

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
     * 成功选课学生数
     */
    private Integer enrolledCount;

    /**
     * 已经选过课的学生数
     */
    private Integer alreadyEnrolledCount;

    /**
     * 成功选课的学生列表
     */
    private List<EnrolledStudentInfo> enrolledStudents;

    /**
     * 已经选过课的学生列表
     */
    private List<EnrolledStudentInfo> alreadyEnrolledStudents;

    @Data
    public static class EnrolledStudentInfo {
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
        return String.format("ClassEnrollCourseRespDTO{className='%s', courseId='%s', courseName='%s', totalStudents=%d, enrolledCount=%d, alreadyEnrolledCount=%d}", 
                className, courseId, courseName, totalStudents, enrolledCount, alreadyEnrolledCount);
    }
}

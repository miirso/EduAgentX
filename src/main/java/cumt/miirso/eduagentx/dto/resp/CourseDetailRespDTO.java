package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程详细信息响应DTO
 * 
 * @author EduAgentX
 */
@Data
public class CourseDetailRespDTO {

    /**
     * 课程ID
     */
    private String id;

    /**
     * 学科ID
     */
    private Integer subjectId;

    /**
     * 学科名称
     */
    private String subjectName;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 考核方式
     */
    private String assessmentMethod;

    /**
     * 课程类型
     */
    private String type;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 分配的教师列表
     */
    private List<AssignedTeacherInfo> assignedTeachers;

    /**
     * 关联的班级列表
     */
    private List<AssignedClassInfo> assignedClasses;

    /**
     * 选课学生统计
     */
    private EnrollmentStatistics enrollmentStats;

    /**
     * 分配的教师信息
     */
    @Data
    public static class AssignedTeacherInfo {
        /**
         * 教师ID
         */
        private String teacherId;

        /**
         * 教师姓名
         */
        private String teacherName;

        /**
         * 教师工号
         */
        private String teacherNo;

        /**
         * 学院
         */
        private String college;
    }

    /**
     * 关联的班级信息
     */
    @Data
    public static class AssignedClassInfo {
        /**
         * 班级ID
         */
        private Long classId;

        /**
         * 班级名称
         */
        private String className;

        /**
         * 专业ID
         */
        private String majorId;
    }

    /**
     * 选课统计信息
     */
    @Data
    public static class EnrollmentStatistics {
        /**
         * 总选课学生数
         */
        private Integer totalEnrollments;

        /**
         * 关联班级数
         */
        private Integer totalClasses;

        /**
         * 分配教师数
         */
        private Integer totalTeachers;
    }
    
    @Override
    public String toString() {
        return String.format("CourseDetailRespDTO{id='%s', name='%s', subjectId=%d, type='%s', totalEnrollments=%d}", 
                id, name, subjectId, type, 
                enrollmentStats != null ? enrollmentStats.getTotalEnrollments() : 0);
    }
}

package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

/**
 * 课程教师分配响应DTO
 * 
 * @author EduAgentX
 */
@Data
public class CourseTeacherAssignRespDTO {

    /**
     * 分配记录ID
     */
    private Long id;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 课程名称
     */
    private String courseName;

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
     * 分配状态信息
     */
    private String message;
}

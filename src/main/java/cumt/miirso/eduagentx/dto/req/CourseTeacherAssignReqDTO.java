package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * 课程教师分配请求DTO
 * 
 * @author EduAgentX
 */
@Data
public class CourseTeacherAssignReqDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 教师ID（对应teacher表的id字段）
     */
    private String teacherId;
}

package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * 教师退课请求DTO
 */
@Data
public class TeacherUnassignCourseReqDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 教师ID
     */
    private String teacherId;
}

package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

/**
 * 教师退课响应DTO
 */
@Data
public class TeacherUnassignCourseRespDTO {

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
     * 教师用户名
     */
    private String teacherUsername;

    /**
     * 是否成功删除关联
     */
    private Boolean success;

    /**
     * 删除的关联记录数量
     */
    private Integer deletedCount;

    /**
     * 操作说明
     */
    private String message;
}

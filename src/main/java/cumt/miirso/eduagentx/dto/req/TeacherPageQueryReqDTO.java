package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * 教师分页查询请求DTO
 * 
 * @author EduAgentX
 */
@Data
public class TeacherPageQueryReqDTO {

    /**
     * 当前页码（从1开始）
     */
    private Integer current;

    /**
     * 每页显示条数（默认10条，最大100条）
     */
    private Integer size;

    /**
     * 教师姓名（模糊查询）
     */
    private String realName;

    /**
     * 用户名（模糊查询）
     */
    private String username;

    /**
     * 教师工号（精确查询）
     */
    private String teacherNo;

    /**
     * 性别（0：女，1：男）
     */
    private Integer gender;

    /**
     * 手机号（模糊查询）
     */
    private String phone;

    /**
     * 学校（模糊查询）
     */
    private String school;

    /**
     * 学院（模糊查询）
     */
    private String college;

    /**
     * 排序字段（create_time, real_name, teacher_no）
     */
    private String sortBy;

    /**
     * 排序方式（asc=升序, desc=降序）
     */
    private String sortOrder;
    
    @Override
    public String toString() {
        return String.format("TeacherPageQueryReqDTO{current=%d, size=%d, realName='%s', username='%s', teacherNo='%s', gender=%s, phone='%s', school='%s', college='%s', sortBy='%s', sortOrder='%s'}", 
                current, size, realName, username, teacherNo, gender, phone, school, college, sortBy, sortOrder);
    }
}

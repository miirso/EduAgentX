package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * 班级退课请求DTO
 * 
 * @author EduAgentX
 */
@Data
public class ClassUnenrollCourseReqDTO {

    /**
     * 班级名称（例如：计算机科学与技术22-1班）
     */
    private String className;

    /**
     * 课程ID
     */
    private String courseId;
    
    @Override
    public String toString() {
        return String.format("ClassUnenrollCourseReqDTO{className='%s', courseId='%s'}", 
                className, courseId);
    }
}

package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 课程创建响应DTO
 */
@Data
public class CourseCreateRespDTO {

    /**
     * 课程ID
     */
    private String id;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 学科ID
     */
    private Integer subjectId;

    /**
     * 开始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;

    /**
     * 考核方式
     */
    private String assessmentMethod;

    /**
     * 关联的教师ID列表
     */
    private List<String> teacherIds;

    /**
     * 关联的班级ID列表
     */
    private List<Long> classIds;
}

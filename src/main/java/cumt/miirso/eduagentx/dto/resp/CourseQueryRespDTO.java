package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;
import java.util.Date;

/**
 * 课程查询响应DTO
 */
@Data
public class CourseQueryRespDTO {

    /**
     * 课程ID
     */
    private String id;

    /**
     * 学科ID
     */
    private Integer subjectId;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 课程封面图片URL
     */
    private String coverImage;

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
     * 课程类型
     */
    private String type;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}

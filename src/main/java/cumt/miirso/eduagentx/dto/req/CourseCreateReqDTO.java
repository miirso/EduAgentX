package cumt.miirso.eduagentx.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * @description: 创建课程请求
 */
@Data
public class CourseCreateReqDTO {
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
    private String coverImage;    /**
     * 开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    /**
     * 结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;/**
     * 考核方式
     */
    private String assessmentMethod;

    /**
     * 课程类型（1位字符，用于课程ID末尾）
     */
    private String type;
}

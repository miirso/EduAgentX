package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程课件实体类
 * @author miirso
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_courseware")
public class CourseCoursewareDO extends BaseDO {

    /**
     * 课件主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 课件文件存储路径
     */
    private String coursewareFilePath;

    /**
     * 课件文件名
     */
    private String fileName;

    /**
     * 课件标题
     */
    private String coursewareTitle;

    /**
     * 课件顺序
     */
    private Integer coursewareOrder;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;
}

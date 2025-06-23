package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程大纲实体类
 * @author miirso
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_outlines")
public class CourseOutlineDO extends BaseDO {

    /**
     * 大纲主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 课程ID
     */
    private String courseId;    /**
     * 大纲文件存储路径
     */
    private String outlineFilePath;

    /**
     * 大纲文件名
     */
    private String fileName;

    /**
     * 大纲版本号
     */
    private Integer version;
}

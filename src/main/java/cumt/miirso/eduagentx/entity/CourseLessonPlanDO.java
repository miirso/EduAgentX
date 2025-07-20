package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程教案实体类
 * @author miirso
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_lesson_plans")
public class CourseLessonPlanDO extends BaseDO {

    /**
     * 教案主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 章节ID（可为空，表示整个课程的教案）
     */
    private Integer chapterId;

    /**
     * 教案文件存储路径
     */
    private String planFilePath;

    /**
     * 教案文件名
     */
    private String fileName;

    /**
     * 教案标题
     */
    private String planTitle;

    /**
     * 教案类型（course=课程教案，chapter=章节教案）
     */
    private String planType;

    /**
     * 教案版本号
     */
    private Integer version;
}

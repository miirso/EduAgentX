package cumt.miirso.eduagentx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 章节实体类
 * @author miirso
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("chapters")
public class ChapterDO extends BaseDO {

    /**
     * 章节主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 章节标题
     */
    private String title;

    /**
     * 章节内容
     */
    private String content;    /**
     * 章节顺序
     */
    @TableField("\"order\"")
    private Integer order;
}

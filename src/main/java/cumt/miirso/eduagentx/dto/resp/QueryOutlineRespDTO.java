package cumt.miirso.eduagentx.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 查询课程大纲响应DTO
 * @author miirso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryOutlineRespDTO {

    /**
     * 大纲ID
     */
    private Integer outlineId;

    /**
     * 课程ID
     */
    private String courseId;    /**
     * 大纲文件内容
     */
    private String outlineContent;

    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

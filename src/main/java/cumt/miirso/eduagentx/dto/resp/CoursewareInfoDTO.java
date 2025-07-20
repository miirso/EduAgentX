package cumt.miirso.eduagentx.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 课件信息DTO
 * @author miirso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoursewareInfoDTO {

    /**
     * 课件ID
     */
    private Integer coursewareId;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 课件标题
     */
    private String coursewareTitle;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 课件顺序
     */
    private Integer coursewareOrder;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件大小（可读格式）
     */
    private String fileSizeFormatted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

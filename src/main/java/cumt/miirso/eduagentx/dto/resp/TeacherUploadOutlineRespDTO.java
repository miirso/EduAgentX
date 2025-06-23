package cumt.miirso.eduagentx.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 教师上传课程大纲响应DTO
 * @author miirso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherUploadOutlineRespDTO {

    /**
     * 大纲ID
     */
    private Integer outlineId;

    /**
     * 课程ID
     */
    private String courseId;    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 操作类型（新增/更新）
     */
    private String operationType;

    /**
     * 操作状态描述
     */
    private String message;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

package cumt.miirso.eduagentx.dto.req;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 教师上传课程大纲请求DTO
 * @author miirso
 */
@Data
public class TeacherUploadOutlineReqDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 大纲文件
     */
    private MultipartFile outlineFile;

    /**
     * 版本号（可选，不传则自动递增）
     */
    private Integer version;
}

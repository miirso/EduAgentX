package cumt.miirso.eduagentx.dto.req;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传课件请求DTO
 * @author miirso
 */
@Data
public class UploadCoursewareReqDTO {

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 课件文件
     */
    private MultipartFile coursewareFile;

    /**
     * 课件标题（可选）
     */
    private String coursewareTitle;

    /**
     * 课件顺序（可选，默认为1）
     */
    private Integer coursewareOrder;
}

package cumt.miirso.eduagentx.dto.req;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 班级批量导入请求DTO
 */
@Data
public class ClassBatchImportReqDTO {
    
    /**
     * Excel文件
     */
    private MultipartFile file;
}

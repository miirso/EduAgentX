package cumt.miirso.eduagentx.service;

import cumt.miirso.eduagentx.dto.req.ClassCreateReqDTO;
import java.util.List;

public interface ClassService {
    /**
     * 创建班级
     * @param requestParam
     */
    void create(ClassCreateReqDTO requestParam);
    
    /**
     * 批量创建班级
     * @param classDTOList 班级创建请求DTO列表
     */
    void createBatch(List<ClassCreateReqDTO> classDTOList);
}

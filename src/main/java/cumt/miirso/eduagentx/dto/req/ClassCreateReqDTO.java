package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * @description: 创建班级请求
 */
@Data
public class ClassCreateReqDTO {
    /**
     * 班级名称
     */
    private String name;

    /**
     * 专业ID
     */
    private String majorId;
}

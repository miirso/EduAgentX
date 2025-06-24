package cumt.miirso.eduagentx.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Package cumt.miirso.eduagentx.entity
 * @Author miirso
 * @Date 2025/5/29 21:54
 */

@Data
public class BaseDO {

    private Date createTime;

    private Date updateTime;
    
    /**
     * 删除时间
     */
    private Date deleteTime;

    private Boolean tag;

}

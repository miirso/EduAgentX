package cumt.miirso.eduagentx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cumt.miirso.eduagentx.dto.req.QueryIntroductionReqDTO;
import cumt.miirso.eduagentx.dto.req.SaveIntroductionReqDTO;
import cumt.miirso.eduagentx.dto.resp.QueryIntroductionRespDTO;
import cumt.miirso.eduagentx.dto.resp.SaveIntroductionRespDTO;
import cumt.miirso.eduagentx.entity.CourseIntroductionDO;

/**
 * 课程简介服务接口
 * @author miirso
 */
public interface CourseIntroductionService extends IService<CourseIntroductionDO> {

    /**
     * 保存或更新课程简介
     * 
     * 实现步骤：
     * 1. 验证课程ID有效性
     * 2. 检查课程简介是否已存在
     * 3. 执行新增或更新操作
     * 4. 返回操作结果
     * 
     * @param requestParam 保存请求参数
     * @return 保存结果
     */
    SaveIntroductionRespDTO saveOrUpdateIntroduction(SaveIntroductionReqDTO requestParam);

    /**
     * 查询课程简介
     * 
     * 实现步骤：
     * 1. 验证课程ID有效性
     * 2. 查询课程简介信息
     * 3. 组装返回数据
     * 4. 返回查询结果
     * 
     * @param requestParam 查询请求参数
     * @return 查询结果
     */
    QueryIntroductionRespDTO queryIntroduction(QueryIntroductionReqDTO requestParam);

    /**
     * 删除课程简介
     * 
     * 实现步骤：
     * 1. 验证课程ID有效性
     * 2. 执行逻辑删除操作
     * 3. 返回删除结果
     * 
     * @param courseId 课程ID
     * @return 删除是否成功
     */
    Boolean deleteIntroduction(String courseId);
}

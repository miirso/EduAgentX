package cumt.miirso.eduagentx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cumt.miirso.eduagentx.dto.req.QueryLessonPlanReqDTO;
import cumt.miirso.eduagentx.dto.req.UploadLessonPlanReqDTO;
import cumt.miirso.eduagentx.dto.resp.QueryLessonPlanRespDTO;
import cumt.miirso.eduagentx.dto.resp.UploadLessonPlanRespDTO;
import cumt.miirso.eduagentx.entity.CourseLessonPlanDO;

/**
 * 课程教案服务接口
 * @author miirso
 */
public interface CourseLessonPlanService extends IService<CourseLessonPlanDO> {

    /**
     * 上传或更新教案
     * 
     * @param requestParam 上传教案请求参数
     * @return 上传结果
     */
    UploadLessonPlanRespDTO uploadOrUpdateLessonPlan(UploadLessonPlanReqDTO requestParam);

    /**
     * 查询教案
     * 
     * @param requestParam 查询教案请求参数
     * @return 教案信息
     */
    QueryLessonPlanRespDTO queryLessonPlan(QueryLessonPlanReqDTO requestParam);
}

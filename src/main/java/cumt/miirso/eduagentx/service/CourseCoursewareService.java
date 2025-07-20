package cumt.miirso.eduagentx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cumt.miirso.eduagentx.dto.req.QueryCoursewareListReqDTO;
import cumt.miirso.eduagentx.dto.req.UploadCoursewareReqDTO;
import cumt.miirso.eduagentx.dto.resp.QueryCoursewareListRespDTO;
import cumt.miirso.eduagentx.dto.resp.UploadCoursewareRespDTO;
import cumt.miirso.eduagentx.entity.CourseCoursewareDO;

/**
 * 课程课件服务接口
 * @author miirso
 */
public interface CourseCoursewareService extends IService<CourseCoursewareDO> {

    /**
     * 上传课件
     * 
     * @param requestParam 上传课件请求参数
     * @return 上传结果
     */
    UploadCoursewareRespDTO uploadCourseware(UploadCoursewareReqDTO requestParam);

    /**
     * 查询课程的课件列表
     * 
     * @param requestParam 查询课件列表请求参数
     * @return 课件列表
     */
    QueryCoursewareListRespDTO queryCoursewareList(QueryCoursewareListReqDTO requestParam);

    /**
     * 删除课件
     * 
     * @param coursewareId 课件ID
     * @return 是否删除成功
     */
    Boolean deleteCourseware(Integer coursewareId);
}

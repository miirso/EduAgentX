package cumt.miirso.eduagentx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cumt.miirso.eduagentx.dto.req.QueryOutlineReqDTO;
import cumt.miirso.eduagentx.dto.req.TeacherUploadOutlineReqDTO;
import cumt.miirso.eduagentx.dto.resp.QueryOutlineRespDTO;
import cumt.miirso.eduagentx.dto.resp.TeacherUploadOutlineRespDTO;
import cumt.miirso.eduagentx.entity.CourseOutlineDO;

/**
 * 课程大纲服务接口
 * @author miirso
 */
public interface CourseOutlineService extends IService<CourseOutlineDO> {

    /**
     * 教师上传/更新课程大纲
     * 
     * 实现逻辑：
     * 1. 检查课程是否存在指定版本的大纲
     * 2. 如果存在则更新，不存在则新增
     * 3. 如果未指定版本号，则自动生成下一个版本号
     * 
     * @param requestParam 上传大纲请求参数
     * @return 上传大纲响应
     */
    TeacherUploadOutlineRespDTO uploadOrUpdateOutline(TeacherUploadOutlineReqDTO requestParam);

    /**
     * 查询课程大纲
     * 
     * 实现逻辑：
     * 1. 根据课程ID和版本号查询大纲
     * 2. 如果未指定版本号，则返回最新版本的大纲
     * 3. 返回大纲文件内容
     * 
     * @param requestParam 查询大纲请求参数
     * @return 大纲文件内容
     */
    QueryOutlineRespDTO queryOutline(QueryOutlineReqDTO requestParam);
}

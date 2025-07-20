package cumt.miirso.eduagentx.controller;

import cumt.miirso.eduagentx.entity.CourseQaDO;
import cumt.miirso.eduagentx.entity.CourseDiscussionDO;
import cumt.miirso.eduagentx.entity.CourseEvaluationDO;
import cumt.miirso.eduagentx.service.CourseQaService;
import cumt.miirso.eduagentx.service.CourseDiscussionService;
import cumt.miirso.eduagentx.service.CourseEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/eduagentx/course")
public class CourseEvaluationDiscussionController {
    @Autowired
    private CourseQaService courseQaService;
    @Autowired
    private CourseDiscussionService courseDiscussionService;
    @Autowired
    private CourseEvaluationService courseEvaluationService;

    // 课程QA相关接口
    @PostMapping("/qa")
    public boolean addQa(@RequestBody CourseQaDO qa) {
        return courseQaService.save(qa);
    }

    @GetMapping("/qa/{courseId}")
    public List<CourseQaDO> listQa(@PathVariable String courseId) {
        return courseQaService.lambdaQuery().eq(CourseQaDO::getCourseId, courseId).list();
    }

    // 课程讨论区相关接口
    @PostMapping("/discussion")
    public boolean addDiscussion(@RequestBody CourseDiscussionDO discussion) {
        return courseDiscussionService.save(discussion);
    }

    @GetMapping("/discussion/{courseId}")
    public List<CourseDiscussionDO> listDiscussion(@PathVariable String courseId) {
        return courseDiscussionService.lambdaQuery().eq(CourseDiscussionDO::getCourseId, courseId).list();
    }

    // 课程评价相关接口
    @PostMapping("/evaluation")
    public boolean addEvaluation(@RequestBody CourseEvaluationDO evaluation) {
        return courseEvaluationService.save(evaluation);
    }

    @GetMapping("/evaluation/{courseId}")
    public List<CourseEvaluationDO> listEvaluation(@PathVariable String courseId) {
        return courseEvaluationService.lambdaQuery().eq(CourseEvaluationDO::getCourseId, courseId).list();
    }
}

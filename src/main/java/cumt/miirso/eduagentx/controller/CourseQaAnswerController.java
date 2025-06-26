package cumt.miirso.eduagentx.controller;

import cumt.miirso.eduagentx.entity.CourseQaAnswerDO;
import cumt.miirso.eduagentx.service.CourseQaAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/courseQaAnswer")
public class CourseQaAnswerController {
    @Autowired
    private CourseQaAnswerService courseQaAnswerService;

    @PostMapping("/add")
    public boolean addAnswer(@RequestBody CourseQaAnswerDO answer) {
        return courseQaAnswerService.save(answer);
    }

    @GetMapping("/list/{qaId}")
    public List<CourseQaAnswerDO> listAnswers(@PathVariable Long qaId) {
        return courseQaAnswerService.lambdaQuery().eq(CourseQaAnswerDO::getQaId, qaId).list();
    }
}

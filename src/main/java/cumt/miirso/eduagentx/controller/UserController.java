package cumt.miirso.eduagentx.controller;

import cumt.miirso.eduagentx.service.StudentService;
import cumt.miirso.eduagentx.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Package cumt.miirso.eduagentx.controller
 * @Author miirso
 * @Date 2025/5/29 22:13
 */

@RestController
@RequestMapping("/api/eduagentx/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

}

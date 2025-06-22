package cumt.miirso.eduagentx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cumt.miirso.eduagentx.entity.UserDO;
import cumt.miirso.eduagentx.mapper.UserMapper;
import cumt.miirso.eduagentx.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.UnaryOperator;

/**
 * @Package cumt.miirso.eduagentx.service.impl
 * @Author miirso
 * @Date 2025/5/29 22:14
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO>  implements UserService {



}

package cumt.miirso.eduagentx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cumt.miirso.eduagentx.entity.AdminDO;
import cumt.miirso.eduagentx.mapper.RootMapper;
import cumt.miirso.eduagentx.service.RootService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Package cumt.miirso.eduagentx.service.impl
 * @Author miirso
 * @Date 2025/5/30 8:09
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class RootServiceImpl extends ServiceImpl<RootMapper, AdminDO> implements RootService {

    private final RedissonClient redissonClient;

    private final StringRedisTemplate stringRedisTemplate;

}

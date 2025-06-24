package cumt.miirso.eduagentx.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus配置类
 * 
 * 主要功能：
 * 1. 配置分页插件，支持分页查询
 * 2. 配置数据库类型，支持不同数据库的分页语法
 * 
 * @author EduAgentX
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 配置MyBatis-Plus插件
     * 
     * @return MybatisPlusInterceptor 插件拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
          // 添加分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.POSTGRE_SQL);
        
        // 配置分页插件参数
        paginationInterceptor.setMaxLimit(500L); // 单页分页条数限制(默认无限制)
        paginationInterceptor.setOverflow(true); // 溢出总页数后进行处理，避免返回空结果
        paginationInterceptor.setOptimizeJoin(true); // 优化JOIN查询
        
        // 添加分页插件到拦截器
        interceptor.addInnerInterceptor(paginationInterceptor);
        
        return interceptor;
    }
}

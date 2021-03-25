package com.example.demo.config;

import com.example.demo.common.AuthRealm;
import com.example.demo.filter.AuthFilter;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @ClassName ShiroConfiguration
 * @Description
 * @Author XinChunYu
 * @Date 2020/5/29 13:42
 * @Version 1.0
 **/
@Configuration
public class ShiroConfiguration {

    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ShiroConfiguration.class);

    //从配置文件里面读取是否需要启动登录认证的开关，默认true
    //@Value("${jwt.auth}")
    private boolean auth = true;

    /**
     * redis地址
     */
    @Value("${spring.redis.host}")
    private String host;

    /**
     * redis端口号
     */
    @Value("${spring.redis.port}")
    private Integer port;

    /**
     * redis密码
     */
    @Value("${spring.redis.password}")
    private String password;

    /**
     * redisManager
     *
     * @return
     */
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host+":"+port);
        redisManager.setPassword(password);
        redisManager.setDatabase(1);
        // 配置过期时间
        redisManager.setTimeout(1800);
//        redisManager.setExpire(1800);
        return redisManager;
    }

    /**
     * cacheManager
     *
     * @return
     */
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        redisCacheManager.setPrincipalIdFieldName("username");
        return redisCacheManager;
    }


    //配置拦截器
    @Bean
    public ShiroFilterFactoryBean shiroFilter(org.apache.shiro.mgt.SecurityManager securityManager) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //启用认证
        String openAuth = auth ? "auth" : "anon";

        //自定义过滤器链
        Map<String, javax.servlet.Filter> filters = new HashMap<>();
        //指定拦截器处理
        filters.put("auth", new AuthFilter());
        shiroFilterFactoryBean.setFilters(filters);
        Map<String, String> filterMap = new LinkedHashMap<>();

        //登录请求不拦截
        filterMap.put("/user/login", "anon");
        //登录页面需要用到的接口，不拦截
        filterMap.put("/user/fetchCurrentUser", "anon");
        //拦截所有接口请求，做权限判断
        filterMap.put("/**", openAuth);
//        filterMap.put("logout", "logout");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        logger.info("Shiro拦截器工厂类注入成功");
        return shiroFilterFactoryBean;
    }

    // SecurityManager 安全管理器；Shiro的核心
    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setCacheManager(cacheManager());
        securityManager.setRealm(userRealm());
        return securityManager;
    }

    //自定义身份认证realm
    @Bean
    public AuthRealm userRealm() {
        AuthRealm authRealm = new AuthRealm();
        authRealm.setAuthenticationCachingEnabled(true);
        authRealm.setAuthenticationCacheName("authenticationCache");
        return new AuthRealm();
    }

    @Bean("lifecycleBeanPostProcessor")
    //管理shiro生命周期
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    //Shiro注解支持
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(org.apache.shiro.mgt.SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager((org.apache.shiro.mgt.SecurityManager) securityManager);
        return authorizationAttributeSourceAdvisor;
    }
    /**
     *  开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator和AuthorizationAttributeSourceAdvisor)即可实现此功能
     * @return
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

}

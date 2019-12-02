package com.shiro.demo.shiro;

import com.shiro.demo.common.Const;
import com.shiro.demo.shiroCache.RedisCacheManager;
import com.shiro.demo.shiroSession.CustomSessionManager;
import com.shiro.demo.shiroSession.RedisSessionDAO;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroConfigration {

    @Autowired
    private RedisCacheManager redisCacheManager;

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager,
                                                         @Qualifier("customLoginFilter") CustomLoginFilter filter) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager);

//        LinkedHashMap<String, Filter> filtersMap = new LinkedHashMap<>();
//        filtersMap.put("loginFilter", filter);
//        bean.setFilters(filtersMap); // 注册过滤器
//
//        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
//        linkedHashMap.put("/userLogin", "loginFilter");
//        bean.setFilterChainDefinitionMap(linkedHashMap); // 注册过滤url链

        return bean;
    }

    @Bean("customLoginFilter")
    public CustomLoginFilter customLoginFilter() {
        return new CustomLoginFilter();
    }

    @Bean("securityManager")
    public DefaultWebSecurityManager defaultWebSecurityManager(@Qualifier("authorizingRealm") AuthorizingRealm realm,
                                                               @Qualifier("customSessionManager") DefaultWebSessionManager sessionManager) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(realm);
        manager.setSessionManager(sessionManager);
        manager.setCacheManager(redisCacheManager);
        return manager;
    }

    @Bean("customSessionManager")
    @Autowired
    public DefaultWebSessionManager sessionManager(RedisSessionDAO redisSessionDAO) {
        DefaultWebSessionManager sessionManager = new CustomSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO);
        sessionManager.setGlobalSessionTimeout(Const.SESSION_EXPIRE_TIME);  // 当前会话过期时间，超出改时间后会自动退出登录，单位ms

        return sessionManager;
    }

    @Bean("authorizingRealm")
    public AuthorizingRealm authorizingRealm(@Qualifier("credentialsMatcher") CredentialsMatcher matcher) {
        CustomRealm customRealm = new CustomRealm();
        customRealm.setCredentialsMatcher(matcher);

        return customRealm;
    }


    @Bean("credentialsMatcher")
    public CredentialsMatcher credentialsMatcher() {
        return new CustomCredentialsMatcher();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") SecurityManager manager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(manager);
        return advisor;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

}

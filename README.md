### springboot 集成 Apache-shiro

0. 版本信息和简介

    springboot 版本为 **2.2.1.RELEASE**，JDK 版本为 **1.8**，MySQL 数据库版本为 **5.7**。

    简介： 

    - springboot 集成 Apache-shiro 做认证和授权
    - 使用 redis 存储 session 实现共享
    - 使用 redis 作为缓存，缓存授权信息以减少数据库交互
    - 数据库使用 mybatis 并集成通用 mapper 实现数据库操作
    - redis 通过 Jedis 工具包进行操作

    注：jedis 集成和 mybatis 集成这里不作讲解，感兴趣的可以查看我写的另一个 [springboot-Jedis-demo](https://github.com/lingmacker/springboot-jedis-demo)

    使用：

    - 创建数据库名为：shiro_demo，并在application.yml 和 mybatis-generator.xml 中配置好数据用户名和密码。
    - 运行项目根目录中的 shiro_demo.sql 文件导入相应数据
    - 

1. springboot 添加 Apache-shiro 依赖

   ```xml
   <!--Apache shiro-->
   <dependency>
     <groupId>org.apache.shiro</groupId>
     <artifactId>shiro-spring-boot-web-starter</artifactId>
     <version>1.4.1</version>
   </dependency>
   ```

2. 自定义一些常量

   ```java
   package com.shiro.demo.common;
   
   public class Const {
       // 密码hash次数
       public static final int HASH_ITERATIONS = 10;
   
       // redis 中 sessionKey 的前缀
       public static final String SHIRO_SESSION_PREFIX = "shiro_session:";
   
       // Redis 中 session 超时时间，单位s
       public static final int REDIS_SESSION_EXPIRE_TIME = 600;
   
       // redis 中 cacheKey 的前缀
       public static final String SHIRO_CACHE_PREFIX = "shiro_cache:";
   
       // Redis 中 cache 超时时间，单位s
       public static final int REDIS_CACHE_EXPIRE_TIME = 600;
   
       // 一次会话超时时间，超过时间后会自动退出登录，单位ms
       public static final int SESSION_EXPIRE_TIME = 1000 * 60 * 60 * 24;
   }
   ```

   

3. 实现密码比较器

   实现密码比较器只需要重写 org.apache.shiro.authc.credential.SimpleCredentialsMatcher 类中的 doCredentialsMatchdoCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) 即可；密码采用 shiro 提供的 sha256 算法进行加密，hash 次数为 10 次。

   ```java
   package com.shiro.demo.shiro;
   
   import com.shiro.demo.common.Const;
   
   import org.apache.shiro.authc.AuthenticationInfo;
   import org.apache.shiro.authc.AuthenticationToken;
   import org.apache.shiro.authc.SaltedAuthenticationInfo;
   import org.apache.shiro.authc.UsernamePasswordToken;
   import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
   import org.apache.shiro.crypto.hash.Sha256Hash;
   
   public class CustomCredentialsMatcher extends SimpleCredentialsMatcher {
   
       @Override
       public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
           UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
           String password = new String(usernamePasswordToken.getPassword());
           String dbPassword = (String) info.getCredentials();
   
           // 盐值的获取为第二步重写的CustomReal中传过来
           String salt = new String(((SaltedAuthenticationInfo) info).getCredentialsSalt().getBytes());
         
           // 采用 shiro 提供的类对密码进行 hash，hash 次数为定义在Const 类中
           String hashedPassword = new Sha256Hash(password, salt, Const.HASH_ITERATIONS).toString();
   
           return this.equals(hashedPassword, dbPassword);
       }
   }
   ```

4. 自定义 realm 

   shiro 需要进行身份验证时，需要通过 realm 获取相应的认证和授权信息来进行验证，所以自定义 realm 可以自定义验证信息和权限信息的来源。

   实现自定义 realm 需要继承 org.apache.shiro.realm.AuthorizingRealm 类，并重写对应的认证和授权信息获取的方法即可。

   ```java
   public class CustomRealm extends AuthorizingRealm {
   
       @Autowired
       private UserService userService;
   
       @Autowired
       private UserRoleService userRoleService;
   
       @Autowired
       private UserPermissionService userPermissionService;
   
       @Autowired
       private RolePermissionService rolePermissionService;
   
       // 授权信息获取
       @Override
       protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
           SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
           User user = (User) principals.getPrimaryPrincipal();
           String username = user.getUsername();
   
           // 获取该用户的角色信息
           Set<String> roles = userRoleService.getRolesByUsername(username);
           // 获取该用户的权限信息，包括该用户特有的权限和用户角色对应的权限
           Set<String> permisseions = userPermissionService.getPermissionsByUsername(username);
           permisseions.addAll(rolePermissionService.getPermissionsByRoles(roles));
   
           info.addRoles(roles);
           info.addStringPermissions(permisseions);
   
           return info;
       }
   
       // 认证信息获取
       @Override
       protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
   
           String username = (String) token.getPrincipal();
           User user = userService.getUserByUsername(username);
   				
         	// 该返回值构造器的第三个参数就是密码hash的盐值，在密码比较器中需要获取到该盐值进行密码hash比较
           return new SimpleAuthenticationInfo(user, user.getPassword(), ByteSource.Util.bytes(user.getSalt()), this.getClass().getName());
       }
   }
   ```

   注：其中相应的 service 是从数据库中获取相应的信息的实现类，具体代码可在 github 上查看。

5. 自定义 SessionDao 实现将 session 保存到 redis

   自定义 SessionDao 需要继承 org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO 类，并重写相应的 CRUD 方法，即可将 session 保存到 redis。

   ```java
   
   @Component("redisSessionDAO")
   public class RedisSessionDAO extends EnterpriseCacheSessionDAO {
   
       @Autowired
       private JedisUtil jedisUtil;
   
       @Override
       protected Serializable doCreate(Session session) {
           Serializable sessionId = this.generateSessionId(session);
   
           this.assignSessionId(session, sessionId);
           this.saveSession(session);
   
           return sessionId;
       }
   
       @Override
       protected Session doReadSession(Serializable sessionId) {
           if (sessionId == null) {
               return null;
           }
   
           byte[] key = this.getKey(sessionId.toString());
           byte[] value = jedisUtil.get(key);
   
           return (Session) SerializationUtils.deserialize(value);
       }
   
       @Override
       protected void doUpdate(Session session) {
           this.saveSession(session);
       }
   
       @Override
       protected void doDelete(Session session) {
           if (session == null || session.getId() == null) {
               return;
           }
   
           byte[] key = this.getKey(session.getId().toString());
           jedisUtil.delete(key);
       }
   
       @Override
       public Collection<Session> getActiveSessions() {
           Set<byte[]> keys = jedisUtil.getKeys(Const.SHIRO_SESSION_PREFIX);
           Set<Session> sessions = new HashSet<>();
   
           if (CollectionUtils.isEmpty(keys)) {
               return sessions;
           }
   
           keys.forEach(key -> {
               Session session = (Session) SerializationUtils.deserialize(jedisUtil.get(key));
               sessions.add(session);
           });
   
           return sessions;
       }
   
       private void saveSession(Session session) {
           if (session != null && session.getId() != null) {
               byte[] key = this.getKey(session.getId().toString());
               byte[] value = SerializationUtils.serialize(session);
   
               jedisUtil.set(key, value);
               jedisUtil.expire(key, Const.REDIS_SESSION_EXPIRE_TIME);
           }
       }
   
       private byte[] getKey(String key) {
           return (Const.SHIRO_SESSION_PREFIX + key).getBytes();
       }
   
   }
   ```

   注：其中 JedisUtil 二次封装 Jedis 用来操作 redis，具体实现可查看 github。

6. 自定义 SessionManager 以减少程序从 redis 中读取 session 的次数

   虽然通过上面的方法将 session 信息保存到了 redis，但是实际运行时，程序会反复到 redis 中读取 session 信息，我们可以自定义 SessionManager 以将 session 保存到 request 中，减少 redis 中session的读取。

   要实现该功能，需要继承 org.apache.shiro.web.session.mgt.DefaultWebSessionManager 并重写其中的 retrieveSession() 方法，在第一次获取到了 session 后，将 session 保存到 request 中。
   
   ```java
   // 减少在redis中读取session的次数
   public class CustomSessionManager extends DefaultWebSessionManager {
   
       @Override
       protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
           Serializable sessionId = this.getSessionId(sessionKey);
           ServletRequest request = null;
   
           // 通过 WebSessionKey 获取到 ServletRequest
           if (sessionKey instanceof WebSessionKey) {
               request = ((WebSessionKey) sessionKey).getServletRequest();
           }
   
           // 尝试从 request 中获取 session 
           if (request != null && sessionId != null) {
               Session session =  (Session) request.getAttribute(sessionId.toString());
               if (session != null) {
                   return session;
               }
           }
   
           // request 没能获取到 session，则从 redis 中获取，并保存到 request 中
           Session session = super.retrieveSession(sessionKey);
           if (request != null && sessionId != null) {
               request.setAttribute(sessionId.toString(), session);
           }
           return session;
       }
   }
   ```
   
7. 自定义 Cache，将授权信息保存到 redis

   要将授权信息保存到 redis 首先需要实现 org.apache.shiro.cache.Cache 接口，重写其中的 CRUD 方法，并将自定义的 Cache 类设置到 CacheManager 中即可。

   - 首先需要自定义 Cache

     ```java
     import com.shiro.demo.common.Const;
     import com.shiro.demo.shiroSession.JedisUtil;
     import org.apache.commons.collections.CollectionUtils;
     import org.apache.shiro.cache.Cache;
     import org.apache.shiro.cache.CacheException;
     import org.springframework.beans.factory.annotation.Autowired;
     import org.springframework.stereotype.Component;
     import org.springframework.util.SerializationUtils;
     
     import java.util.Collection;
     import java.util.HashSet;
     import java.util.Set;
     
     @Component
     public class RedisCache<K, V> implements Cache<K, V> {
     
         @Autowired
         private JedisUtil jedisUtil;
     
         @Override
         public V get(K k) throws CacheException {
     //        System.out.println("从redis中获取授权数据");
     
             byte[] key = this.getKey(k);
     
             byte[] value = jedisUtil.get(key);
             if (value == null)
                 return null;
     
             return (V) SerializationUtils.deserialize(value);
         }
     
         @Override
         public V put(K k, V v) throws CacheException {
     
             if (k == null || v == null) {
                 return null;
             }
     
             byte[] key = this.getKey(k);
             byte[] value = SerializationUtils.serialize(v);
     
             jedisUtil.set(key, value);
             jedisUtil.expire(key, Const.REDIS_CACHE_EXPIRE_TIME);
     
             return v;
         }
     
         @Override
         public V remove(K k) throws CacheException {
     
             if (k == null) {
                 return null;
             }
     
             byte[] key = this.getKey(k);
             byte[] value = jedisUtil.get(key);
     
             jedisUtil.delete(key);
     
             return (V) SerializationUtils.deserialize(value);
         }
     
         @Override
         public void clear() throws CacheException {
             Set<byte[]> keys = jedisUtil.getKeys(Const.SHIRO_CACHE_PREFIX);
     
             if (CollectionUtils.isEmpty(keys)) {
                 return;
             }
     
             keys.forEach(key -> {
                 jedisUtil.delete(key);
             });
     
         }
     
         @Override
         public int size() {
             Set<byte[]> keys = jedisUtil.getKeys(Const.SHIRO_CACHE_PREFIX);
     
             return CollectionUtils.size(keys);
         }
     
         @Override
         public Set<K> keys() {
             Set<byte[]> keys = jedisUtil.getKeys(Const.SHIRO_CACHE_PREFIX);
             Set<K> vs = new HashSet<>();
     
             if (CollectionUtils.isEmpty(keys)) {
                 return vs;
             }
     
             keys.forEach(key -> {
                 Object o = SerializationUtils.deserialize(key);
                 if (o instanceof String) {
                     String string = ((String) o).replace(Const.SHIRO_CACHE_PREFIX, "");
                     vs.add((K) string);
                 } else {
                     vs.add((K) SerializationUtils.deserialize(key));
                 }
     
             });
     
             return vs;
         }
     
         @Override
         public Collection<V> values() {
             Set<byte[]> keys = jedisUtil.getKeys(Const.SHIRO_CACHE_PREFIX);
     
             Set<V> values = new HashSet<>();
             if (CollectionUtils.isEmpty(keys)) {
                 return values;
             }
     
             keys.forEach(key -> {
                 V value = (V) SerializationUtils.deserialize(jedisUtil.get(key));
                 values.add(value);
             });
     
             return values;
         }
     
         private byte[] getKey(K k) {
             if (k instanceof String) {
                 return (Const.SHIRO_CACHE_PREFIX + k).getBytes();
             }
             return SerializationUtils.serialize(k);
         }
     }
     ```

   - 接下来再自定义 CacheManager，将 Cache 类设置为自定义的 RedisCache

     ```java
     import org.apache.shiro.cache.Cache;
     import org.apache.shiro.cache.CacheException;
     import org.apache.shiro.cache.CacheManager;
     import org.springframework.beans.factory.annotation.Autowired;
     import org.springframework.stereotype.Component;
     
     import java.util.concurrent.ConcurrentHashMap;
     
     @Component("redisCacheManager")
     public class RedisCacheManager implements CacheManager {
     
         @Autowired
         RedisCache redisCache;
     
         private final ConcurrentHashMap<String, RedisCache> caches = new ConcurrentHashMap<>();
     
         @Override
         public <K, V> Cache<K, V> getCache(String s) throws CacheException {
             RedisCache cache = caches.get(s);
             if (cache != null) {
                 return cache;
             }
     
             caches.put(s, redisCache);
             return redisCache;
         }
     }
     ```

8. 最后，创建 ShiroConfigration 配置文件，将上面的自定义类配置到 shiro

   ```java
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
   
       // 自动装配自定义的 RedisCacheManager
       @Autowired  
       private RedisCacheManager redisCacheManager;
   
       // 过滤器Bean
       @Bean
       public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager manager) {
           ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
           bean.setSecurityManager(manager);
   
           return bean;
       }
   
       // 安全管理器
       @Bean("securityManager")
       public DefaultWebSecurityManager defaultWebSecurityManager(@Qualifier("authorizingRealm") AuthorizingRealm realm,
                                                                  @Qualifier("redisSessionManager") DefaultWebSessionManager sessionManager) {
           DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
           // 配置自定义的 realm，自定义认证和授权信息的来源
           manager.setRealm(realm);
           // 配置自定义的 sessionManager，将 session 信息保存到 redis
           manager.setSessionManager(sessionManager);
           // 配置自定义的 CacheManager，将授权信息保存到 redis 
           manager.setCacheManager(redisCacheManager);
           return manager;
       }
   
     
       // 创建自定义 redisSessionManager Bean，该 Bean 是为了实现将 session 保存到 redis
       @Bean("redisSessionManager")
       @Autowired  // 自动装配自定义 redisSessionDAO
       public DefaultWebSessionManager sessionManager(RedisSessionDAO redisSessionDAO) {
           DefaultWebSessionManager sessionManager = new CustomSessionManager();
           // 配置 redisSessionDao，将 session 的 CRUD 操作在 Redis 中进行
           sessionManager.setSessionDAO(redisSessionDAO);
           // 当前会话过期时间，超出改时间后会自动退出登录，单位ms
           sessionManager.setGlobalSessionTimeout(Const.SESSION_EXPIRE_TIME); 
   
           return sessionManager;
       }
   
       // 创建自定义Realm的Bean，并且将密码比较器设置到该 Realm
       @Bean("authorizingRealm")
       public AuthorizingRealm authorizingRealm(@Qualifier("credentialsMatcher") CredentialsMatcher matcher) {
           CustomRealm customRealm = new CustomRealm();
           customRealm.setCredentialsMatcher(matcher);
   
           return customRealm;
       }
   
       // 创建一个密码比较器的 Bean 
       @Bean("credentialsMatcher")
       public CredentialsMatcher credentialsMatcher() {
           return new CustomCredentialsMatcher();
       }
   
       // 用来启用启用权限注解，如 @RequiresRoles("admin") 等。
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
   ```

9. shiro 认证和授权的使用

   通过以上的配置，即可通过 SecurityUtils 获取 Subject 进行用户的认证和授权。

   ```java
   
   // 认证
   @RequestMapping("/userLogin")
   public Session login(String username, String password) {
       // 1. 通过用户名和密码创建 token
       UsernamePasswordToken token = new UsernamePasswordToken(username, password);
       // 2. 获取 subject
       Subject subject = SecurityUtils.getSubject();
       // 3. 调用 subject 的 login() 方法
       try {
           subject.login(token);
           // 登录成功
           return subject.getSession();
       } catch (AuthenticationException e) {
           // 登录失败或抛出AuthenticationException
           return null;
       }
   }
   
   // 授权
   @RequestMapping("/admin")
   @RequiresRoles("admin")  // 通过该注解，使得访问该接口必须具有 admin 的角色
   public String admin() {
     return "admin 页面";
   }
   
   @RestController
   public class PermissionsTest {
   
       @RequestMapping("/create")
       @RequiresPermissions({"user:*"})  // 该注解是使得访问该接口的用户必须拥有 user 表的全部选项
       public String create() {
           return "成功添加一条数据";
       }
   
       @RequestMapping("/read")
     	// logical = Logical.OR 则表示拥有以下两个权限之一即可
       @RequiresPermissions(value = {"user:*", "user:read"}, logical = Logical.OR)
       public String read() {
           return "成功查看该条数据";
       }
   }
   
   // 除了使用注解，也可以手动验证权限
   public String backend() {
       Subject subject = SecurityUtils.getSubject();
       subject.checkRole("admin");
       subject.checkPermission("user:*");
     
       return "这里是后台管理页面";
   }
   ```
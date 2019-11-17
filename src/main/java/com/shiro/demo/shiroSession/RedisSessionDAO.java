package com.shiro.demo.shiroSession;

import com.shiro.demo.common.Const;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


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

package com.roamtech.uc.cache.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.roamtech.uc.cache.RDCache;
import com.roamtech.uc.cache.model.Session;

@Component
public class SessionHandler implements InitializingBean {
	private RDCache cache;
	@Autowired
	@Qualifier("ucCacheManager")
	private CacheManager cacheManager;

	public int getSessionIdleTimeout() {
		return sessionIdleTimeout;
	}

	public void setSessionIdleTimeout(int sessionIdleTimeout) {
		this.sessionIdleTimeout = sessionIdleTimeout;
	}

	private int sessionIdleTimeout=86500;
	@Override
	public void afterPropertiesSet() throws Exception {
		cache  = (RDCache)cacheManager.getCache("sessionCache");
	}
	public Session save(Session session) {
		cache.put(session.getSessionId(), session, sessionIdleTimeout);
		Set nuserSessionSet = new HashSet<String>();
		Set<String>  userSessionSet = cache.get(session.getUserId(), Set.class);
		if(userSessionSet != null) {
			for (String sessionKey : userSessionSet) {
				Session lsession = cache.get(sessionKey,
						Session.class);
				if (null != lsession) {
					//nuserSessionSet.add(sessionKey);
					cache.evict(sessionKey);
				}
			}
		}
		nuserSessionSet.add(session.getSessionId());
		cache.put(session.getUserId(), nuserSessionSet);
		return cache.get(session.getSessionId(), Session.class, sessionIdleTimeout);
	}
	public void delete(Session session) {
		cache.evict(session.getSessionId());
		Set<String>  userSessionSet = cache.get(session.getUserId(), Set.class);
		if(userSessionSet != null) {
			userSessionSet.remove(session.getSessionId());
			cache.put(session.getUserId(), userSessionSet);
		}
	}
	public Session findOne(String sessionId) {
		return cache.get(sessionId, Session.class, sessionIdleTimeout);
	}
	public List<Session> findByUserId(Long userId) {
		List<Session> result = new ArrayList<Session>();
		Set<String>  userSessionSet = cache.get(userId, Set.class);
		if(userSessionSet != null) {
			for (String sessionKey : userSessionSet) {
				Session session = cache.get(sessionKey,
						Session.class);
				if(null != session) {
					result.add(session);
				}
			}
		}
		return result;
	}
}

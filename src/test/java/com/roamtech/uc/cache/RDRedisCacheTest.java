package com.roamtech.uc.cache;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.text.ParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.roamtech.uc.cache.RDRedisCache.TimeValue;





@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config.xml")
public class RDRedisCacheTest {
	@Autowired
	@Qualifier("ucCacheManager")
	private CacheManager cacheManager;
	RDCache rawkeyCache;
	RDCache cache;
	RDCounter counter;
	@Before
	public void before() {
		cache = (RDCache) cacheManager.getCache("cache");
		rawkeyCache = (RDCache) cacheManager.getCache("raw");
		rawkeyCache.setBinary(true);
		counter = cache.getHSCounter();
	}
	@After
	public void after() {
		cache.evict("zhang3");
		rawkeyCache.evict("zhang3");
		cache.evict("zhang3.version");
		cache.evict("incr1");
		cache.evict("incrAsync1");
		cache.evict("incrExpire1");
		cache.evict("decr1");
		cache.evict("decrAsync1");
	}
	@Test
	public void testPut() {
		User user = new User("zhang3",30);
		cache.put("zhang3", user);
		
		User guser = cache.get("zhang3", User.class);
		assertEquals(user,guser);
/*		rawkeyCache.put("zhang3", user);
		Bucket client = (Bucket)rawkeyCache.getNativeCache();
		User rawUser = (User)client.get("zhang3",SerializableDocument.class).content();
		assertEquals(user,rawUser);
*/		cache.evict("zhang3");
		assertEquals(null,cache.get("zhang3"));
/*		rawkeyCache.evict("zhang3");
		assertEquals(null,rawkeyCache.get("zhang3"));	*/
	}
	@Test
	public void testPutExpired() {
		User user = new User("zhang3",30);
		cache.put("zhang3.expire", user, 1);
		cache.setL1CacheRefreshTime(1000);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		User expireUser = cache.get("zhang3.expire", User.class);
		assertEquals(null,expireUser);
	}
	@Test
	public void testPutExpiredEvict() {
		User user = new User("zhang3",30);
		cache.put("zhang3.expire.evict", user, 100);
		cache.evict("zhang3.expire.evict");
		User expireUser = cache.get("zhang3.expire.evict", User.class);
		assertEquals(null,expireUser);
	}
	@Test
	public void testPutVersion() {
		User user = new User("zhang3",30);
		cache.put(new RDKey("zhang3.version"), user);
		TimeValue<Object> result = (TimeValue<Object>)cache.getL1("zhang3.version");
		User newUser = new User("zhang3.1",31);
		cache.put(new RDKey("zhang3.version", 0, result.getCas()), newUser);
		User versionUser = cache.get("zhang3.version", User.class);
		assertEquals(newUser,versionUser);
		cache.evict("zhang3.version");
		assertEquals(null,cache.get("zhang3.version"));
	}
	@Test
	public void testIncr() {
		long index = counter.incr("incr1", 1);
		assertEquals(1,index);
		index = counter.incr("incr1", 1);
		assertEquals(2,index);
		index = counter.incr("incr1", 2);
		assertEquals(4,index);
		cache.evict("incr1");
	}
	@Test
	public void testIncrAsync() {
		counter.asyncIncr("incrAsync1", 1);
		long index = counter.incr("incrAsync1", 0);
		assertEquals(1,index);
		counter.asyncIncr("incrAsync1", 1);
		counter.asyncIncr("incrAsync1", 1);
		counter.asyncIncr("incrAsync1", 1);
		index = counter.incr("incrAsync1", 0);
		assertEquals(4,index);
		cache.evict("incrAsync1");
	}
	@Test
	public void testIncrExpire() {
		long index = counter.incr("incrExpire1", 1, 1, 1);
		counter.incr("incrExpire1", 1);
		counter.incr("incrExpire1", 1);
		index = counter.incr("incrExpire1", 0);
		assertEquals(3,index);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		index = counter.incr("incrExpire1", 0);
		assertEquals(0,index);
		cache.evict("incrExpire1");
	}
	@Test
	public void testDecr() {
		counter.decr("decr1", 1, 5);
		long index = counter.decr("decr1", 1);
		assertEquals(4,index);
		index = counter.decr("decr1", 2);
		assertEquals(2,index);
		cache.evict("decr1");
	}
	@Test
	public void testDecrAsync() {
		counter.asyncDecr("decrAsync1", 1, 5);
		counter.asyncDecr("decrAsync1", 1);
		counter.asyncDecr("decrAsync1", 1);
		counter.asyncDecr("decrAsync1", 1);
		long index = counter.decr("decrAsync1", 0);
		assertEquals(2,index);
		cache.evict("decrAsync1");
	}
}

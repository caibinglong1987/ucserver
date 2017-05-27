package com.roamtech.uc.cache;

import java.util.concurrent.atomic.AtomicLong;

import redis.clients.jedis.JedisCluster;


public class RDRedisCounter implements RDCounter {
	  private final JedisCluster client;
	  private final RDRedisCache cache;
	  
	  public RDRedisCounter(RDRedisCache cache) {
		  this.cache = cache;
		  this.client = (JedisCluster) cache.getNativeCache();
	  }
	  private String getCounterKey(String key) {
		  return cache.getCacheKey(key);
	  }
	@Override
	public long incr(String key, long by) {
		return incr(key, by, 0);
	}

	@Override
	public long incr(String key, long by, long def) {
		return incr(key, by, def, 0);
	}

	@Override
	public long incr(String key, long by, long def, int exp) {
		String ckey = getCounterKey(key);
		if(def != 0) {
		if(exp ==0) {
			Long val = client.setnx(ckey, new Long(def).toString());
			if(val == 1) {
				return def;
			}
		} else {			
			client.set(ckey, new Long(def).toString(),"NX","EX",exp);
			return def;
		}
		}
		return client.incrBy(ckey,by);
		//return get(add(key,by,def,exp),"counter");
	}

	@Override
	public long decr(String key, long by) {
		return decr(key, by, 0);
	}

	@Override
	public long decr(String key, long by, long def) {
		return decr(key, by, def, 0);
	}

	@Override
	public long decr(String key, long by, long def, int exp) {
		String ckey = getCounterKey(key);
		//client.expire(ckey, exp);
		if(def != 0) {
		if(exp ==0) {
			Long val = client.setnx(ckey, new Long(def).toString());
			if(val == 1) {
				return def;
			}
		} else {			
			client.set(ckey, new Long(def).toString(),"NX","EX",exp);
			return def;
		}
		}
		return client.decrBy(ckey,by);
		//return get(add(key,-by,def,exp),"counter");
	}

	@Override
	public void asyncIncr(String key, long by) {
		asyncIncr(key, by, 0);
	}

	@Override
	public void asyncIncr(String key, long by, long def) {
		asyncIncr(key, by, def, 0);
	}

	@Override
	public void asyncIncr(String key, long by, long def, int exp) {
		incr(key,by,def,exp);
	}

	@Override
	public void asyncDecr(String key, long by) {
		asyncDecr(key, by, 0);
	}

	@Override
	public void asyncDecr(String key, long by, long def) {
		asyncDecr(key, by, def, 0);
	}

	@Override
	public void asyncDecr(String key, long by, long def, int exp) {
		decr(key,by,def,exp);
	}
}

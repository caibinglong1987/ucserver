package com.roamtech.uc.cache;

import redis.clients.jedis.JedisCluster;

public class RDRedisLock implements RDLock {
	  private final JedisCluster client;
	  private final RDRedisCache cache;
	  public static final String LOCKED = "TRUE";
	  public static final long ONE_MILLI_NANOS = 1000000L;
	    //默认超时时间（毫秒）
	  public static final long DEFAULT_TIME_OUT = 3000;
	  public static final int EXPIRE = 10;
	public RDRedisLock(RDRedisCache cache) {
		  this.cache = cache;
		  this.client = (JedisCluster) cache.getNativeCache();
	  }
	  private String getLockKey(String key) {
		  return cache.getCacheKey(key);
	  }
	@Override
	public boolean lock(String key) {
		return lock(key,DEFAULT_TIME_OUT);
	}

	@Override
	public boolean lock(String key,long timeout) {
		long nano = System.nanoTime();
        timeout *= ONE_MILLI_NANOS;
        key = getLockKey(key);
        try {
            while ((System.nanoTime() - nano) < timeout) {
                if (client.setnx(key, LOCKED) == 1) {
                	client.expire(key, EXPIRE);
                    return true;
                }
                // 短暂休眠，nano避免出现活锁
                Thread.sleep(3, 500);
            }
        } catch (Exception e) {
        }
        return false;
	}

	@Override
	public void unlock(String key, boolean locked) {
		if(locked) {
			key = getLockKey(key);
			client.del(key);
		}
	}

}

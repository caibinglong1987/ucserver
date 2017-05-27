package com.roamtech.uc.cache;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.SimpleValueWrapper;

import com.roamtech.uc.util.JSONUtils;

import redis.clients.jedis.JedisCluster;


public class RDRedisCache implements RDCache {
	private static final Logger LOG = LoggerFactory.getLogger(RDRedisCache.class);
	
	private final String name;
	private final String namespace;
	public final String database;
	private boolean binary;
	private EhCache cache;
	private RDCounter counter;
	private RDLock locker;
	private final JedisCluster client;
	private long refreshTime;
	public RDRedisCache(String database,String namespace,String name, JedisCluster client, EhCache cache, long refreshTime) {
		this.cache = cache;
		this.name = name;
		this.namespace = namespace;
		this.database = database;
		this.binary = false;
		this.client = client;
		this.refreshTime = refreshTime;
		counter = new RDRedisCounter(this);
		locker = new RDRedisLock(this); 
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getNativeCache() {
		return client;
	}

	@Override
	public ValueWrapper get(Object key) {
		Object result = getInternal(key, 0);
	    return (result != null ? new SimpleValueWrapper(result) : null);
		
	}
	  class TimeValue<T>  implements Serializable {
		  private static final long serialVersionUID = 1L;
		  private long time;
		  private final long cas;
		  private final T value;

		  /**
		   * Construct a new CASValue with the given identifer and value.
		   *
		   * @param t the last get time
		   * @param v the value
		   */
		  public TimeValue(long t, long c, T v) {
		    super();
		    time = t;
		    cas = c;
		    value = v;
		  }

		  /**
		   * Get the CAS identifier.
		   */
		  public long getTime() {
		    return time;
		  }
		  public long getCas() {
			    return cas;
		  }
		  /**
		   * Get the object value.
		   */
		  public T getValue() {
		    return value;
		  }
		  public void setTime(long time) {
			  this.time = time;
		  }
		  @Override
		  public String toString() {
		    return "{TimeValue " + time + "/" + value + "}";
		  }
		}	
	  private final Object getInternal(final Object key, int exp) {
		  String keyStr = getCacheKey(key.toString());
		  if(null == cache) {			  
			  return client.get(keyStr);
		  }
		    TimeValue<Object> result = (TimeValue<Object>) cache.get(key);
			if(null == result) {
				String record = client.get(keyStr);
		        if(record != null) {
		            cache.put(key, new TimeValue(System.currentTimeMillis(),0,record));
		        }
		        return record;
			} else if(System.currentTimeMillis() - result.getTime() > refreshTime) {
				String record = client.get(keyStr);
		        if(record != null) {
					result = new TimeValue(System.currentTimeMillis(),0,record);
					cache.put(key, result);
					if(exp != 0) {
						client.expire(keyStr, exp);
					}
		        } else {
					cache.remove(key);
					return null;
				}
				
				result.setTime(System.currentTimeMillis());
			}
		    return (result != null ? result.getValue() : null);
	  }
	@Override
	public void put(Object key, Object value) {
		  if(key instanceof RDKey) {
			  put(((RDKey)key).getKey(), value, ((RDKey)key).getExpires(), ((RDKey)key).getVersion());
		  } else {
		      put(key, value, 0);
		  }
	}

	@Override
	public void evict(Object key) {
		client.del(getCacheKey(key.toString()));
		if(cache != null) {
	    	  cache.remove(key);
	    }
	}

	@Override
	public void clear() {
		if(cache != null) {
	    	  cache.clear();
	      }
	}

	@Override
	public <T> T get(Object key, Class<T> type) {
		  return get(key, type, 0);
	}

	@Override
	public void put(Object key, Object value, int exp) {
		put(key, value, exp, 0);
	}

	@Override
	public void put(Object key, Object value, int exp, long version) {
		put(key, value, exp, 0, false);
	}

	@Override
	public void put(Object key, Object value, int exp, long version,
			boolean async) {
		String valueStr = JSONUtils.serialize(value);
		if(exp == 0) {
			client.set(getCacheKey(key.toString()),valueStr);
		} else {
			String status = client.setex(getCacheKey(key.toString()),exp, valueStr);
			LOG.info("put "+getCacheKey(key.toString())+" "+status);
		}
		if(cache != null /*&& resp != null*/) {
	    	  TimeValue<Object> obj = new TimeValue<Object>(System.currentTimeMillis(), 0/*resp.cas()*/, valueStr);
	    	  cache.put(key, obj);
		}
	}

	@Override
	public void putL1(Object key, Object value) {
		if(cache != null) {
			cache.put(key, value);
		}
	}

	@Override
	public Object getL1(Object key) {
		if(cache != null) {
			return cache.get(key);
		}
		return null;
	}

	@Override
	public void setL1CacheRefreshTime(long time) {
		this.refreshTime = refreshTime;
		if(time == 0) {
			this.cache = null;
		}
	}

	@Override
	public String getCacheKey(String key) {
		if(namespace != null && !namespace.isEmpty() && name != null && !name.isEmpty() )
			return namespace+"."+name+"."+key;
		else
			return key;
	}

	@Override
	public void setBinary(boolean binary) {
		this.binary = binary;
	}

	@Override
	public RDCounter getHSCounter() {
		return counter;
	}
	@Override
	public String getNamespace() {
		return namespace;
	}
	@Override
	public RDLock getRDLock() {
		return locker;
	}
	@Override
	public <T> T get(Object key, Class<T> type, int exp) {
		try {
	          Object result = getInternal(key, exp); 
	          result = JSONUtils.deserialize((String)result, type);
	          return (result != null ? (T) result : null);
		  } catch (Exception e) {
			  LOG.warn("cache get exception", e);
		}
		return null;  
	}
	
}

package com.roamtech.uc.cache;

import java.util.concurrent.atomic.AtomicLong;


public class RDEhCounter implements RDCounter {
	  private final EhCache nativeCache;
	  private final RDCache cache;
	  public RDEhCounter(RDCache cache) {
		  this.cache = cache;
		  nativeCache = (EhCache) cache.getNativeCache();
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
		Object indicator = nativeCache.get(getCounterKey(key));
		if(indicator == null) {
			indicator = new AtomicLong(def);
			nativeCache.put(getCounterKey(key), indicator);
			return def;
		} else {
			long result = ((AtomicLong)indicator).addAndGet(by);
			nativeCache.put(getCounterKey(key), indicator);
			return result;
		}
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
		Object indicator = nativeCache.get(getCounterKey(key));
		if(indicator == null) {
			indicator = new AtomicLong(def);
			nativeCache.put(getCounterKey(key), indicator);
			return def;
		} else {
			long result = ((AtomicLong)indicator).addAndGet(-by);
			nativeCache.put(getCounterKey(key), indicator);
			return result;
		}
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
		Object indicator = nativeCache.get(getCounterKey(key));
		if(indicator == null) {
			indicator = new AtomicLong(def);
			nativeCache.put(getCounterKey(key), indicator);
		} else {
			((AtomicLong)indicator).addAndGet(by);
			nativeCache.put(getCounterKey(key), indicator);
		}
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
		Object indicator = nativeCache.get(getCounterKey(key));
		if(indicator == null) {
			indicator = new AtomicLong(def);
			nativeCache.put(getCounterKey(key), indicator);
		} else {
			((AtomicLong)indicator).addAndGet(-by);
			nativeCache.put(getCounterKey(key), indicator);
		}
	}

}

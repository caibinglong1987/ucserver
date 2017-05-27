package com.roamtech.uc.cache;

import org.springframework.cache.support.SimpleValueWrapper;

public class RDEhCache implements RDCache {
	private final String name;
	private final String namespace;
	private boolean binary;
	private final EhCache cache;
	private RDCounter counter;
	public RDEhCache(String namespace,String name, EhCache cache) {
		this.cache = cache;
		this.name = name;
		this.namespace = namespace;
		this.binary = true;
		counter = new RDEhCounter(this);
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getNativeCache() {
		return cache;
	}

	@Override
	public ValueWrapper get(Object key) {
		Object result = key==null?null:cache.get(getCacheKey(key.toString()));
		return (result != null ? new SimpleValueWrapper(result) : null);
	}

	@Override
	public void put(Object key, Object value) {
		put(key, value,0);
	}

	@Override
	public void evict(Object key) {
		cache.remove(getCacheKey(key.toString()));
	}

	@Override
	public void clear() {
		cache.clear();
	}

	@Override
	public <T> T get(Object key, Class<T> type) {
		return key==null?(T)null:(T)cache.get(getCacheKey(key.toString()));
	}

	@Override
	public void put(Object key, Object value, int exp) {
		put(key, value, exp, 0);
	}

	@Override
	public void put(Object key, Object value, int exp, long version) {
		cache.put(getCacheKey(key.toString()), value);
	}

	@Override
	public void putL1(Object key, Object value) {
		cache.put(key, value);
	}

	@Override
	public Object getL1(Object key) {
		// TODO Auto-generated method stub
		return cache.get(key);
	}

	@Override
	public void setL1CacheRefreshTime(long time) {
		
	}

	@Override
	public String getCacheKey(String key) {
		return namespace+"."+name+"."+key;
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
	public void put(Object key, Object value, int exp, long version,
			boolean async) {
		cache.put(getCacheKey(key.toString()), value);
	}
	@Override
	public String getNamespace() {
		return namespace;
	}
	@Override
	public RDLock getRDLock() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public <T> T get(Object key, Class<T> type, int exp) {
		return get(key, type);
	}

}

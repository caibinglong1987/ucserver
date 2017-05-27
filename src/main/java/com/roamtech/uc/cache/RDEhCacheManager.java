package com.roamtech.uc.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class RDEhCacheManager implements CacheManager, InitializingBean {
	private final HashMap<String, String> config;
	private EhCacheManager ehCacheManager;
	private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>(16);

	private Set<String> cacheNames = new LinkedHashSet<String>(16);
	public RDEhCacheManager(HashMap<String,String> config, String level1CacheConfigFile, long level1CacheRefresh) {
		this.config = config;
		ehCacheManager = new EhCacheManager();
    	//ehCacheManager.setCacheManagerConfigFile(level1CacheConfigFile);
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		this.cacheMap.clear();
		this.cacheNames.clear();
	}

	protected final void addCache(Cache cache) {
		this.cacheMap.put(cache.getName(), decorateCache(cache));
		this.cacheNames.add(cache.getName());
	}

	/**
	 * Decorate the given Cache object if necessary.
	 * @param cache the Cache object to be added to this CacheManager
	 * @return the decorated Cache object to be used instead,
	 * or simply the passed-in Cache object by default
	 */
	protected Cache decorateCache(Cache cache) {
		return cache;
	}

	@Override
	public Cache getCache(String name) {
		Cache cache = this.cacheMap.get(name);
		if(null == cache) {
	    	cache = new RDEhCache(config.get("namespace"),name, (EhCache)ehCacheManager.getCache(name));
	    	addCache(cache);
		}
		return cache;
	}

	@Override
	public Collection<String> getCacheNames() {
		return Collections.unmodifiableSet(this.cacheNames);
	}

}

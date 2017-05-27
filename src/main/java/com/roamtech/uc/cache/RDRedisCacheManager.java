package com.roamtech.uc.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;


public class RDRedisCacheManager implements CacheManager, InitializingBean {
	private final HashMap<String, String> config;
	private static final EhCacheManager ehCacheManager=new EhCacheManager();
	private long refreshTime;
	private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>(32);
	private Set<String> cacheNames = new LinkedHashSet<String>(32);

	public RDRedisCacheManager(HashMap<String,String> config, String level1CacheConfigFile, long level1CacheRefresh) {
		this.config = config;
//		ehCacheManager = new EhCacheManager();
    	ehCacheManager.setCacheManagerConfigFile(level1CacheConfigFile);
    	try {
    		ehCacheManager.init();
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	refreshTime = level1CacheRefresh;

    	System.out.println(level1CacheConfigFile+" "+refreshTime);
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		this.cacheMap.clear();
		this.cacheNames.clear();
	}
	protected final void addCache(Cache cache) {
		this.cacheMap.put(cache.getName(), cache);
		this.cacheNames.add(cache.getName());
	}
	@Override
	public Cache getCache(String name) {
		Cache cache = this.cacheMap.get(name);
		if(null == cache) {
			synchronized (cacheMap) {
				cache = this.cacheMap.get(name);
				if(null == cache) {
					EhCache localCache = null;
					if(refreshTime>0) {
				    	try {
				    		localCache = (EhCache) ehCacheManager.getCache(name);
				    		System.out.println(localCache);
				    		/*if(localCache == null) {
				    			refreshTime = 0;
				    		}*/
				    	} catch (Exception e){
				    		e.printStackTrace();
				    	}
					}
					String hosts = config.get("host");
					Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
			        for (String address : hosts.split(",")) {
			        	if(address.indexOf(':')>0) {
			        		String [] hostport = address.split(":");
			        		jedisClusterNodes.add(new HostAndPort(hostport[0],Integer.parseInt(hostport[1])));
			        	} else {
			        		jedisClusterNodes.add(new HostAndPort(address,6380));
			        	}
			        }
			        JedisCluster client = new JedisCluster(jedisClusterNodes);
			    	cache = new RDRedisCache(config.get("database"),config.get("namespace"),name, client, localCache, refreshTime);
			    	addCache(cache);
				}
			}
		}
		return cache;
	}

	@Override
	public Collection<String> getCacheNames() {
		return Collections.unmodifiableSet(this.cacheNames);
	}

}

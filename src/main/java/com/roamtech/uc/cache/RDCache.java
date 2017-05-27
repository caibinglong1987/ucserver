package com.roamtech.uc.cache;


public interface RDCache extends org.springframework.cache.Cache {
	<T> T get(Object key, Class<T> type);
	<T> T get(Object key, Class<T> type, int exp);
    void put(Object key, Object value, int exp);
    void put(Object key, Object value, int exp, long version);
    void put(Object key, Object value, int exp, long version, boolean async);
    /**
     *  put key value in L1 local cache.
     * @param key
     * @param value
     */
    void putL1(Object key, Object value);
    /**
     * get object in L1 local cache.
     * @param key
     * @return
     */
    Object getL1(Object key);
    /**
     * 
     * @param time millisecond,the minimum is 1000 millisecond 
     */
    void setL1CacheRefreshTime(long time);
    String getCacheKey(String key);
    void setBinary(boolean binary);
    String getNamespace();
    RDCounter getHSCounter();
    RDLock getRDLock();
}

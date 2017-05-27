package com.roamtech.uc.cache;

public interface RDCounter {
    long incr(String key, long by);
    long incr(String key, long by, long def);
    
    /**
     * @param key 计数器名字
     * @param by  计数器步进值
     * @param def 计数器初始值
     * @param exp 计数器过期时间 ,unit is second
     * */
    long incr(String key, long by, long def, int exp);
    long decr(String key, long by);
    long decr(String key, long by, long def);
    long decr(String key, long by, long def, int exp);
    void asyncIncr(String key, long by);
    void asyncIncr(String key, long by, long def);
    void asyncIncr(String key, long by, long def, int exp);
    void asyncDecr(String key, long by);
    void asyncDecr(String key, long by, long def);
    void asyncDecr(String key, long by, long def, int exp);
}

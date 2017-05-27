package com.roamtech.uc.cache;

public interface RDLock {
	boolean lock(String key);
	boolean lock(String key,long timeout);
	void unlock(String key, boolean locked);
}

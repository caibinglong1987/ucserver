package com.roamtech.uc.cache;

public class RDKey {
	private Object key;
	private int expires;
	private long version;
	public RDKey(Object key){
		init(key, 0, (long)0);
	}
	public RDKey(Object key, int expires){
		init(key, expires, (long)0);
	}
	public RDKey(Object key, int expires, long version){
		init(key, expires, version);
	}
	private void init(Object key, int expires, long version) {
		this.key = key;
		this.expires = expires;
		this.version = version;
	}
	public Object getKey() {
		return key;
	}
	public void setKey(Object key) {
		this.key = key;
	}
	public int getExpires() {
		return expires;
	}
	public void setExpires(int expires) {
		this.expires = expires;
	}
	public long getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
}

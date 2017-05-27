package com.roamtech.uc.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class HKUnicomDelPackRequest implements Serializable {
	private String authKey;
	private String imsi;
	private String packCode;
	private String expTime;
	private String packOrderSn;

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getPackCode() {
		return packCode;
	}

	public void setPackCode(String packCode) {
		this.packCode = packCode;
	}

	public String getExpTime() {
		return expTime;
	}

	public void setExpTime(String expTime) {
		this.expTime = expTime;
	}

	public String getPackOrderSn() {
		return packOrderSn;
	}

	public void setPackOrderSn(String packOrderSn) {
		this.packOrderSn = packOrderSn;
	}
}

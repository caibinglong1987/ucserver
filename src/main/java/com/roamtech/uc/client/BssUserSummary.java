package com.roamtech.uc.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BssUserSummary implements Serializable {
	private String imsi;
	private String msisdn;
	private String userIdentifier;
	private String email;
	private String notificationUuid;
	private String registered;

	private Boolean actived;

	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getUserIdentifier() {
		return userIdentifier;
	}
	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNotificationUuid() {
		return notificationUuid;
	}
	public void setNotificationUuid(String notificationUuid) {
		this.notificationUuid = notificationUuid;
	}
	public String getRegistered() {
		return registered;
	}
	public void setRegistered(String registered) {
		this.registered = registered;
	}

	public void setActived(Boolean actived) {
		this.actived = actived;
	}

	public Boolean getActived() {
		return actived;
	}

}

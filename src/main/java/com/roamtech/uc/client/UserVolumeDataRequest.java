package com.roamtech.uc.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserVolumeDataRequest implements Serializable {
	private String userIdentifier;
	private Boolean surfPassVolume;//True – only surf pass volume will be returned, False – all user data volume will be returned (default value = false)
	public UserVolumeDataRequest(String userIdentifier) {
		this(userIdentifier, null);
	}
	public UserVolumeDataRequest(String userIdentifier,Boolean surfPassVolume) {
		setUserIdentifier(userIdentifier);
		setSurfPassVolume(surfPassVolume);
	}
	public String getUserIdentifier() {
		return userIdentifier;
	}
	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}
	public Boolean getSurfPassVolume() {
		return surfPassVolume;
	}
	public void setSurfPassVolume(Boolean surfPassVolume) {
		this.surfPassVolume = surfPassVolume;
	}

}

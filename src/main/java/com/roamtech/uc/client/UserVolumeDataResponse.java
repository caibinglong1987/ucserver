package com.roamtech.uc.client;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class UserVolumeDataResponse implements Serializable {
	private String userIdentifier;
	private List<UserVolumeData> entries;
	public String getUserIdentifier() {
		return userIdentifier;
	}
	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}
	public List<UserVolumeData> getEntries() {
		return entries;
	}
	public void setEntries(List<UserVolumeData> entries) {
		this.entries = entries;
	}
	
}

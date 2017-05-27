package com.roamtech.uc.client;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class UserSessionLogData implements Serializable {
	private String userIdentifier;
	private List<Session> entries;
	public String getUserIdentifier() {
		return userIdentifier;
	}
	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}
	public List<Session> getEntries() {
		return entries;
	}
	public void setEntries(List<Session> entries) {
		this.entries = entries;
	}
	
}

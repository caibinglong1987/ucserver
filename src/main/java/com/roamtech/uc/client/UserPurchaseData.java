package com.roamtech.uc.client;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class UserPurchaseData implements Serializable {
	private String userIdentifier;
	private List<UserPurchase> entries;
	public String getUserIdentifier() {
		return userIdentifier;
	}
	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}
	public List<UserPurchase> getEntries() {
		return entries;
	}
	public void setEntries(List<UserPurchase> entries) {
		this.entries = entries;
	}
	
}

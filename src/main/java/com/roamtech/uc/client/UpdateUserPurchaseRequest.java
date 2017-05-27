package com.roamtech.uc.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UpdateUserPurchaseRequest implements Serializable {
	private String userIdentifier;
	private Boolean recurring;
	private String userPurchaseId;
	public String getUserIdentifier() {
		return userIdentifier;
	}
	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}
	public Boolean getRecurring() {
		return recurring;
	}
	public void setRecurring(Boolean recurring) {
		this.recurring = recurring;
	}
	public String getUserPurchaseId() {
		return userPurchaseId;
	}
	public void setUserPurchaseId(String userPurchaseId) {
		this.userPurchaseId = userPurchaseId;
	}
	
}

package com.roamtech.uc.client;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class TransactionData implements Serializable {
	private String userIdentifier;
	private List<Transaction> entries;
	public String getUserIdentifier() {
		return userIdentifier;
	}
	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}
	public List<Transaction> getEntries() {
		return entries;
	}
	public void setEntries(List<Transaction> entries) {
		this.entries = entries;
	}
	
}

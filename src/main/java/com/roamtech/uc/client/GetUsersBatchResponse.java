package com.roamtech.uc.client;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
class GetUsersBatchResponse implements Serializable {
	private String restrictedOperation;
	private List<String> nonExistingUserIdentifiers;
	private List<BssUser> users;
	public String getRestrictedOperation() {
		return restrictedOperation;
	}
	public void setRestrictedOperation(String restrictedOperation) {
		this.restrictedOperation = restrictedOperation;
	}
	public List<String> getNonExistingUserIdentifiers() {
		return nonExistingUserIdentifiers;
	}
	public void setNonExistingUserIdentifiers(List<String> nonExistingUserIdentifiers) {
		this.nonExistingUserIdentifiers = nonExistingUserIdentifiers;
	}
	public List<BssUser> getUsers() {
		return users;
	}
	public void setUsers(List<BssUser> users) {
		this.users = users;
	}

};

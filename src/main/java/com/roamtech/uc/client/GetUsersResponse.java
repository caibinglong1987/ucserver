package com.roamtech.uc.client;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
class GetUsersResponse implements Serializable {
	private String restrictedOperation;
	private Integer size;
	private List<BssUserSummary> users;
	public String getRestrictedOperation() {
		return restrictedOperation;
	}
	public void setRestrictedOperation(String restrictedOperation) {
		this.restrictedOperation = restrictedOperation;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public List<BssUserSummary> getUsers() {
		return users;
	}
	public void setUsers(List<BssUserSummary> users) {
		this.users = users;
	}
};

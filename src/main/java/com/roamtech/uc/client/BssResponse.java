package com.roamtech.uc.client;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class BssResponse implements Serializable {
	private Integer errorCode;
	private String errorMessage;
	private String errorName;
	private String restrictedOperation;
	private String batchJobToken;
	private List<String> nonExistingUserIdentifiers;
	private Integer size;
	private Object result;
	
	
	public Integer getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getErrorName() {
		return errorName;
	}
	public void setErrorName(String errorName) {
		this.errorName = errorName;
	}
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
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	public String getBatchJobToken() {
		return batchJobToken;
	}
	public void setBatchJobToken(String batchJobToken) {
		this.batchJobToken = batchJobToken;
	}
	public List<String> getNonExistingUserIdentifiers() {
		return nonExistingUserIdentifiers;
	}
	public void setNonExistingUserIdentifiers(List<String> nonExistingUserIdentifiers) {
		this.nonExistingUserIdentifiers = nonExistingUserIdentifiers;
	}
	
	
}

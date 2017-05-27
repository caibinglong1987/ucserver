package com.roamtech.uc.client;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class BatchJobStatus implements Serializable {
	private Integer submittedEntryCount;
	private Integer processedEntryCount;
	private List<Object> successEntries;
	private List<Object> errorEntries;
	public Integer getSubmittedEntryCount() {
		return submittedEntryCount;
	}
	public void setSubmittedEntryCount(Integer submittedEntryCount) {
		this.submittedEntryCount = submittedEntryCount;
	}
	public Integer getProcessedEntryCount() {
		return processedEntryCount;
	}
	public void setProcessedEntryCount(Integer processedEntryCount) {
		this.processedEntryCount = processedEntryCount;
	}
	public List<Object> getSuccessEntries() {
		return successEntries;
	}
	public void setSuccessEntries(List<Object> successEntries) {
		this.successEntries = successEntries;
	}
	public List<Object> getErrorEntries() {
		return errorEntries;
	}
	public void setErrorEntries(List<Object> errorEntries) {
		this.errorEntries = errorEntries;
	}
	
}

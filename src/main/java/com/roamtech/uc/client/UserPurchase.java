package com.roamtech.uc.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserPurchase implements Serializable {
	private String activationDate;
	private String created;
	private Boolean enabled;
	private String expirationDate;
	private Boolean expired;
	private Long id;
	private Boolean networkActivated;
	private Integer paymentRetryCount;
	private Integer priority;
	private Boolean recurring;
	private Integer recurringCounter;
	private Long startVolume;
	private Boolean suspended;
	private Long throttlingStartVolume;
	private Long throttlingVolume;
	private String transactionId;
	private String type;
	private Long typeEntryId;
	private String typeEntryName;
	private String userPurchaseType;
	private Long volume;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getTypeEntryId() {
		return typeEntryId;
	}
	public void setTypeEntryId(Long typeEntryId) {
		this.typeEntryId = typeEntryId;
	}
	public String getTypeEntryName() {
		return typeEntryName;
	}
	public void setTypeEntryName(String typeEntryName) {
		this.typeEntryName = typeEntryName;
	}
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getUserPurchaseType() {
		return userPurchaseType;
	}
	public void setUserPurchaseType(String userPurchaseType) {
		this.userPurchaseType = userPurchaseType;
	}
	public Boolean getRecurring() {
		return recurring;
	}
	public void setRecurring(Boolean recurring) {
		this.recurring = recurring;
	}
	public Integer getRecurringCounter() {
		return recurringCounter;
	}
	public void setRecurringCounter(Integer recurringCounter) {
		this.recurringCounter = recurringCounter;
	}
	public String getActivationDate() {
		return activationDate;
	}
	public void setActivationDate(String activationDate) {
		this.activationDate = activationDate;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public Boolean getExpired() {
		return expired;
	}
	public void setExpired(Boolean expired) {
		this.expired = expired;
	}
	public Boolean getNetworkActivated() {
		return networkActivated;
	}
	public void setNetworkActivated(Boolean networkActivated) {
		this.networkActivated = networkActivated;
	}
	public Integer getPaymentRetryCount() {
		return paymentRetryCount;
	}
	public void setPaymentRetryCount(Integer paymentRetryCount) {
		this.paymentRetryCount = paymentRetryCount;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public Long getStartVolume() {
		return startVolume;
	}
	public void setStartVolume(Long startVolume) {
		this.startVolume = startVolume;
	}
	public Boolean getSuspended() {
		return suspended;
	}
	public void setSuspended(Boolean suspended) {
		this.suspended = suspended;
	}
	public Long getThrottlingStartVolume() {
		return throttlingStartVolume;
	}
	public void setThrottlingStartVolume(Long throttlingStartVolume) {
		this.throttlingStartVolume = throttlingStartVolume;
	}
	public Long getThrottlingVolume() {
		return throttlingVolume;
	}
	public void setThrottlingVolume(Long throttlingVolume) {
		this.throttlingVolume = throttlingVolume;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getVolume() {
		return volume;
	}
	public void setVolume(Long volume) {
		this.volume = volume;
	}
	
}

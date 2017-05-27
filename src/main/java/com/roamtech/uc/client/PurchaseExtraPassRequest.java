package com.roamtech.uc.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PurchaseExtraPassRequest implements Serializable {
	private String userIdentifier;
	private Integer extraPassId;
	private String address;
	private String streetNo;
	private String city;
	private String state;
	private String zip;
	private String externalTransactionId;
	private String recurring;//True – user purchase will be recurring (will have its volume reset on billing cycle), false – user purchase will not be recurring
	private String expirationDate;
	private String volume;
	private String throttlingVolume;
	public String getUserIdentifier() {
		return userIdentifier;
	}
	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}
	public Integer getExtraPassId() {
		return extraPassId;
	}
	public void setExtraPassId(Integer extraPassId) {
		this.extraPassId = extraPassId;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getStreetNo() {
		return streetNo;
	}
	public void setStreetNo(String streetNo) {
		this.streetNo = streetNo;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getExternalTransactionId() {
		return externalTransactionId;
	}
	public void setExternalTransactionId(String externalTransactionId) {
		this.externalTransactionId = externalTransactionId;
	}
	public String getRecurring() {
		return recurring;
	}
	public void setRecurring(String recurring) {
		this.recurring = recurring;
	}
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getThrottlingVolume() {
		return throttlingVolume;
	}
	public void setThrottlingVolume(String throttlingVolume) {
		this.throttlingVolume = throttlingVolume;
	}
	
}

package com.roamtech.uc.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BssUser implements Serializable {
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String address;
	private String streetNo;
	private String city;
	private String state;
	private String zip;
	private String msisdn;
	private String imsi;
	private String birthday;//Birth date (format: yyyy-MM-dd)
	private String accountValid;//Account expiration date (format: yyyy-MM-ddhh:mm)
	private String client; //client name
	private String volume; //User account volume left for surfing – amount of volume left to be used on current network
	private String accountType;//Account type name
	private String startVolume;//User account initial volume – amount of volume, initially available for end-user for surfing on current network
	private String nextAccount;//Next billing cycle account type name
	private String simStatus;//Sim status defines user status, e.g. enabled, disabled
	private Boolean payAgreement;//True - user has pay agreement attached
	private String network;//User home network pmn value
	private Boolean visitNetwork;//True - user is roaming (user is not allowed to surf in current network)
	private String currentNetwork;//User current network pmn value
	private String verificationStatus;//User advanced registration state
	private String personalId;
	private String device;
	private String redirectUrl;//Redirect url to be used if user is not allowed to surf
	private String userIdentifier;//User identifier
	private String accountId;//User account id
	private String timeZone;//Time zone (format: time zone code, e.g. Etc/GMT+12)
	private Boolean stopped;//True – user is suspended, false – user is not suspended
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getAccountValid() {
		return accountValid;
	}
	public void setAccountValid(String accountValid) {
		this.accountValid = accountValid;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getStartVolume() {
		return startVolume;
	}
	public void setStartVolume(String startVolume) {
		this.startVolume = startVolume;
	}
	public String getNextAccount() {
		return nextAccount;
	}
	public void setNextAccount(String nextAccount) {
		this.nextAccount = nextAccount;
	}
	public String getSimStatus() {
		return simStatus;
	}
	public void setSimStatus(String simStatus) {
		this.simStatus = simStatus;
	}
	public Boolean getPayAgreement() {
		return payAgreement;
	}
	public void setPayAgreement(Boolean payAgreement) {
		this.payAgreement = payAgreement;
	}
	public String getNetwork() {
		return network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
	public Boolean getVisitNetwork() {
		return visitNetwork;
	}
	public void setVisitNetwork(Boolean visitNetwork) {
		this.visitNetwork = visitNetwork;
	}
	public String getCurrentNetwork() {
		return currentNetwork;
	}
	public void setCurrentNetwork(String currentNetwork) {
		this.currentNetwork = currentNetwork;
	}
	public String getVerificationStatus() {
		return verificationStatus;
	}
	public void setVerificationStatus(String verificationStatus) {
		this.verificationStatus = verificationStatus;
	}
	public String getPersonalId() {
		return personalId;
	}
	public void setPersonalId(String personalId) {
		this.personalId = personalId;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getRedirectUrl() {
		return redirectUrl;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	public String getUserIdentifier() {
		return userIdentifier;
	}
	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public Boolean getStopped() {
		return stopped;
	}
	public void setStopped(Boolean stopped) {
		this.stopped = stopped;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}

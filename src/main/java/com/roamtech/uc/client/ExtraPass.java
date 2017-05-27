package com.roamtech.uc.client;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class ExtraPass implements Serializable {
	private String id;
	private String volume;
	private String currency;
	private String price;
	private String description;
	private String validityTerm;//Surf pass validity term in days. If 30 provided - validity term is one month, independently, how many days there are in month
	private String validityTermUnit;//Day – validity term in days, Week – validity term in weeks, Month – validity term in months
	private List<Country> countries;
	private List<Country> surfingCountries;
	private String resetValidityTerm;//Volume (both high speed and throttling) reset validity term –data will be reset by defined interval
	private String resetValidityTermUnit;//Day – reset validity term in days, Week – reset validity term in weeks, Month – reset validity term in months
	private Boolean customExpirationDateEnabled;//True – custom expiration date can be set on surf pass purchase, false – standard expiration date calculation logic is used
	private Boolean customVolumeEnabled;//True – custom volume can be set on surf pass purchase, false – standard surf pass volume will be used
	private Boolean customThrottlingVolumeEnabled;//True – custom throttling volume can be set on surf extra purchase, false – standard extra pass throttling volume will be used
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getValidityTerm() {
		return validityTerm;
	}
	public void setValidityTerm(String validityTerm) {
		this.validityTerm = validityTerm;
	}
	public List<Country> getCountries() {
		return countries;
	}
	public void setCountries(List<Country> countries) {
		this.countries = countries;
	}
	public List<Country> getSurfingCountries() {
		return surfingCountries;
	}
	public void setSurfingCountries(List<Country> surfingCountries) {
		this.surfingCountries = surfingCountries;
	}

	public String getValidityTermUnit() {
		return validityTermUnit;
	}
	public void setValidityTermUnit(String validityTermUnit) {
		this.validityTermUnit = validityTermUnit;
	}
	public String getResetValidityTerm() {
		return resetValidityTerm;
	}
	public void setResetValidityTerm(String resetValidityTerm) {
		this.resetValidityTerm = resetValidityTerm;
	}
	public String getResetValidityTermUnit() {
		return resetValidityTermUnit;
	}
	public void setResetValidityTermUnit(String resetValidityTermUnit) {
		this.resetValidityTermUnit = resetValidityTermUnit;
	}
	public Boolean getCustomThrottlingVolumeEnabled() {
		return customThrottlingVolumeEnabled;
	}
	public void setCustomThrottlingVolumeEnabled(Boolean customThrottlingVolumeEnabled) {
		this.customThrottlingVolumeEnabled = customThrottlingVolumeEnabled;
	}
	public Boolean getCustomExpirationDateEnabled() {
		return customExpirationDateEnabled;
	}
	public void setCustomExpirationDateEnabled(Boolean customExpirationDateEnabled) {
		this.customExpirationDateEnabled = customExpirationDateEnabled;
	}
	public Boolean getCustomVolumeEnabled() {
		return customVolumeEnabled;
	}
	public void setCustomVolumeEnabled(Boolean customVolumeEnabled) {
		this.customVolumeEnabled = customVolumeEnabled;
	}

}

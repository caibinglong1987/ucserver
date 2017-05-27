package com.roamtech.uc.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserVolumeData implements Serializable {
	private String country;
	private Long volume;
	private Long startVolume;
	private String expirationDate;
	private String countryIsoCode;
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Long getVolume() {
		return volume;
	}
	public void setVolume(Long volume) {
		this.volume = volume;
	}
	public Long getStartVolume() {
		return startVolume;
	}
	public void setStartVolume(Long startVolume) {
		this.startVolume = startVolume;
	}
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getCountryIsoCode() {
		return countryIsoCode;
	}
	public void setCountryIsoCode(String countryIsoCode) {
		this.countryIsoCode = countryIsoCode;
	}
	
}

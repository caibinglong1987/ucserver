package com.roamtech.uc.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Country implements Serializable {
	private Integer id;
	private String continentIsoCode;
	private String name;
	private String isocode;
	private String mccmnc;
	private String pmn;
	private Boolean enabled;
	private String providerTitle;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getContinentIsoCode() {
		return continentIsoCode;
	}
	public void setContinentIsoCode(String continentIsoCode) {
		this.continentIsoCode = continentIsoCode;
	}
	public String getProviderTitle() {
		return providerTitle;
	}
	public void setProviderTitle(String providerTitle) {
		this.providerTitle = providerTitle;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIsocode() {
		return isocode;
	}
	public void setIsocode(String isocode) {
		this.isocode = isocode;
	}
	public String getMccmnc() {
		return mccmnc;
	}
	public void setMccmnc(String mccmnc) {
		this.mccmnc = mccmnc;
	}
	public String getPmn() {
		return pmn;
	}
	public void setPmn(String pmn) {
		this.pmn = pmn;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
}

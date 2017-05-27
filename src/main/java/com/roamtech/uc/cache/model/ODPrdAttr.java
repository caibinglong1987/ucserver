package com.roamtech.uc.cache.model;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class ODPrdAttr implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String STARTTIME_TAG="effect_datetime";
	public static final String ENDTIME_TAG="failure_datetime";
	public static final String SIMID_TAG="simid";
	public static final String AREANAME_TAG="areaname";
	public static final String PHONE_TAG="phone";
	public static final String GENEVOUCHER_TAG="genevoucher";
	public static final String CALLDURATION_TAG="call_duration";	
	@JSONField(name = "name")
	private String name;
	@JSONField(name = "varname")
	private String varname;	
	@JSONField(name = "value")
	private String value;
	@JSONField(name = "price")
	private String price;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVarname() {
		return varname;
	}
	public void setVarname(String varname) {
		this.varname = varname;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	

}

package com.roamtech.uc.cache.model;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class ODAttr implements Serializable {
	private static final long serialVersionUID = 1L;
	@JSONField(name = "effect_datetime", format="yyyy-MM-dd HH:mm:ss")
	private Date startTime;
	@JSONField(name = "failure_datetime", format="yyyy-MM-dd HH:mm:ss")
	private Date endTime;	
	@JSONField(name = "call_duration")
	private Long callDuration;
	@JSONField(name = "quantity_per_unit")
	private Integer quantityPerUnit;
	@JSONField(name = "areaname")
	private String areaname;
	@JSONField(name = "simid")
	private String simid;
	@JSONField(name = "phone")
	private String phone;
	@JSONField(name = "genevoucher")
	private Boolean genEvoucher;
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Long getCallDuration() {
		return callDuration;
	}
	public void setCallDuration(Long callDuration) {
		this.callDuration = callDuration;
	}
	public Integer getQuantityPerUnit() {
		return quantityPerUnit;
	}
	public void setQuantityPerUnit(Integer quantityPerUnit) {
		this.quantityPerUnit = quantityPerUnit;
	}
	public String getAreaname() {
		return areaname;
	}
	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}
	public String getSimid() {
		return simid;
	}
	public void setSimid(String simid) {
		this.simid = simid;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Boolean getGenEvoucher() {
		return genEvoucher;
	}

    public void setGenEvoucher(boolean genEvoucher) {
        this.genEvoucher = genEvoucher;
    }
}

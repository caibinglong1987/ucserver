package com.roamtech.uc.cache.model;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class VoiceNumber implements Serializable {
	private static final long serialVersionUID = 1L;
	@JSONField(name = "effect_datetime",format="yyyy-MM-dd HH:mm:ss")
	private Date startTime;
	@JSONField(name = "failure_datetime",format="yyyy-MM-dd HH:mm:ss")
	private Date endTime;	
	@JSONField(name = "remaindertime")
	private Double remainder;//天数
	@JSONField(name = "phone")
	private String phone;
	@JSONField(name = "userid")
	private Long userid;
	@JSONField(name = "subid")
	private Integer subid;
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
	public Double getRemainder() {
		return remainder;
	}
	public void setRemainder(Double remainder) {
		this.remainder = remainder;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public Integer getSubid() {
		return subid;
	}
	public void setSubid(Integer subid) {
		this.subid = subid;
	}

}

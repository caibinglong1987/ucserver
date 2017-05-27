package com.roamtech.uc.opensips.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.alibaba.fastjson.annotation.JSONField;

@Entity
@XmlRootElement
@Table(name = "missed_calls")
public class MissedCalls implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@Column(name = "method")
	private String method;
	
	@Column(name = "from_tag")
	@JSONField(name = "from_tag")
	private String fromTag;

	@Column(name = "to_tag")
	@JSONField(name = "to_tag")
	private String toTag;
	
	@Column(name = "callid")
	@JSONField(name = "callid")
	private String callid;
	
	@Column(name = "sip_code")
	@JSONField(name = "sip_code")
	private String sipCode;
	
	@Column(name = "sip_reason")
	@JSONField(name = "sip_reason")
	private String sipReason;
	
	@Column(name = "time")
	private Date time;
	
	@Column(name = "setuptime")
	private Integer setuptime;

	@Column(name = "created")
	private Date created;
	
	@Column(name = "caller")
	private String caller;
	
	@Column(name = "callee")
	private String callee;

	@Column(name = "message")
	private String message;

	@Column(name = "userid")
	private Long userid;
	
	@Column(name = "realdest")
	private String realdest;
	
	@Column(name = "direction")
	private Boolean direction;
	
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getFromTag() {
		return fromTag;
	}

	public void setFromTag(String fromTag) {
		this.fromTag = fromTag;
	}

	public String getToTag() {
		return toTag;
	}

	public void setToTag(String toTag) {
		this.toTag = toTag;
	}

	public String getCallid() {
		return callid;
	}

	public void setCallid(String callid) {
		this.callid = callid;
	}

	public String getSipCode() {
		return sipCode;
	}

	public void setSipCode(String sipCode) {
		this.sipCode = sipCode;
	}

	public String getSipReason() {
		return sipReason;
	}

	public void setSipReason(String sipReason) {
		this.sipReason = sipReason;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Integer getSetuptime() {
		return setuptime;
	}

	public void setSetuptime(Integer setuptime) {
		this.setuptime = setuptime;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getCallee() {
		return callee;
	}

	public void setCallee(String callee) {
		this.callee = callee;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getRealdest() {
		return realdest;
	}

	public void setRealdest(String realdest) {
		this.realdest = realdest;
	}

	public Boolean getDirection() {
		return direction;
	}

	public void setDirection(Boolean direction) {
		this.direction = direction;
	}
	
}

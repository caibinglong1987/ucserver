package com.roamtech.uc.model;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@Entity
@XmlRootElement
@Table(name = "mdr")
public class MsgDetailRecord implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	@JSONField(name = "id")
	private Long id;
	@Column(name = "caller")
	@JSONField(name = "caller")
	private String caller;
	@Column(name = "callee")
	@JSONField(name = "callee")
	private String callee;
	@Column(name = "callid")
	@JSONField(name = "callid")
	private String callid;
	@Column(name = "time")
	@JSONField(name = "time")
	private Date time;
	@Column(name = "message")
	private String message;
	@Column(name = "direction")
	@JSONField(name = "direction")
	private Boolean direction; /*true origin call, false incoming call*/
	@Column(name = "userid")
	@JSONField(name = "userid")
	private Long userId;
	@Column(name = "sip_code")
	@JSONField(name = "sip_code")
	private String sipCode;	
	@Column(name = "sip_reason")
	@JSONField(name = "sip_reason")
	private String sipReason;
	@Column(name = "relayphone")
	private String relayphone;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getCallid() {
		return callid;
	}
	public void setCallid(String callid) {
		this.callid = callid;
	}
	public Boolean getDirection() {
		return direction;
	}
	public void setDirection(Boolean direction) {
		this.direction = direction;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRelayphone() {
		return relayphone;
	}
	public void setRelayphone(String relayphone) {
		this.relayphone = relayphone;
	}
	
	
}

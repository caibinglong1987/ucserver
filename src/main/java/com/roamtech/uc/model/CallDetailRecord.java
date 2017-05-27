package com.roamtech.uc.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import com.alibaba.fastjson.annotation.JSONField;

@Entity
@XmlRootElement
@Table(name = "cdr")
public class CallDetailRecord implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
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
	@Column(name = "createtime")
	@JSONField(name = "createtime")
	private Date createTime;
	@Column(name = "setuptime")
	private Integer setuptime;
	@Column(name = "starttime")
	@JSONField(name = "starttime")
	private Date startTime;
	@Column(name = "endtime")
	@JSONField(name = "endtime")
	private Date endTime;
	@Column(name = "duration")
	@JSONField(name = "duration")
	private Integer duration;
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
	@Column(name = "myroambox")
	private Boolean myroambox;
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
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
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
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Integer getSetuptime() {
		return setuptime;
	}
	public void setSetuptime(Integer setuptime) {
		this.setuptime = setuptime;
	}
	public String getRelayphone() {
		return relayphone;
	}
	public void setRelayphone(String relayphone) {
		this.relayphone = relayphone;
	}

	public Boolean getMyroambox() {
		return myroambox;
	}

	public void setMyroambox(Boolean myroambox) {
		this.myroambox = myroambox;
	}
}

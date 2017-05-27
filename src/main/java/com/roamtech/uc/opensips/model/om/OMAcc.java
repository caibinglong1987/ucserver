package com.roamtech.uc.opensips.model.om;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@Entity
@XmlRootElement
@Table(name = "acc")
public class OMAcc implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JSONField(name = "id")
    @Column(name = "id")
    private Long id;

    @Column(name = "method")
    private String method;

    @JSONField(name = "from_tag")
    @Column(name = "from_tag")
    private String fromTag;

    @JSONField(name = "to_tag")
    @Column(name = "to_tag")
    private String toTag;

    @JSONField(name = "callid")
    @Column(name = "callid")
    private String callId;

    @JSONField(name = "sip_code")
    @Column(name = "sip_code")
    private String sipCode;

    @JSONField(name = "sip_reason")
    @Column(name = "sip_reason")
    private String sipReason;

    @JSONField(name = "time",format = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "time")
    private Date time;

    @JSONField(name = "duration")
    @Column(name = "duration")
    private Integer duration;

    @JSONField(name = "setuptime")
    @Column(name = "setuptime")
    private Integer setupTime;

    @JSONField(name = "created",format = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created")
    private Date created;

    @Column(name = "message")
    private String message;

    @Column(name = "caller")
    private String caller;

    @Column(name = "callee")
    private String callee;

    @JSONField(name = "ms_duration")
    @Column(name = "ms_duration")
    private Integer durationTime;

    @JSONField(name = "userid")
    @Column(name = "userid")
    private Long userId;

    @Transient
    @JSONField(name = "userinfo")
    private String userinfo;

    @Column(name = "realdest")
    private String realdest;

    @Column(name = "direction")
    private Boolean direction;

    @Column(name = "status")
    private int status;

    @JSONField(name = "tosip")
    @Column(name = "tosip")
    private int toSip = 1;

    @JSONField(name = "callee_status")
    @Column(name = "callee_status")
    private int calleeStatus;


    public int getCalleeStatus() {
        return calleeStatus;
    }

    public void setCalleeStatus(int calleeStatus) {
        this.calleeStatus = calleeStatus;
    }

    public int getToSip() {
        return toSip;
    }

    public void setToSip(int toSip) {
        this.toSip = toSip;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(Integer setupTime) {
        this.setupTime = setupTime;
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

    public Integer getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(Integer durationTime) {
        this.durationTime = durationTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(String userinfo) {
        this.userinfo = userinfo;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}

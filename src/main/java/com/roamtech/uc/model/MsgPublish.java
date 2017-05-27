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
@Table(name = "msgpublish")
public class MsgPublish implements Serializable {
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
	@Column(name = "type")
	private Integer type;
	@Column(name = "message")
	private String message;
	@Column(name = "createtime")
	@JSONField(name = "createtime")
	private Date createTime;
	@Column(name = "publishtime")
	@JSONField(name = "publishtime")
	private Date publishTime;
	@Column(name = "callername")
	@JSONField(name = "callername")
	private String callerName;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public String getCallerName() {
		return callerName;
	}

	public void setCallerName(String callerName) {
		this.callerName = callerName;
	}
}

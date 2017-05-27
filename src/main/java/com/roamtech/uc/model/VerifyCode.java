package com.roamtech.uc.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import com.alibaba.fastjson.annotation.JSONField;

@Entity
@XmlRootElement
@Table(name = "verifycode",uniqueConstraints = @UniqueConstraint(columnNames = {
		"checkid"}))
public class VerifyCode implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "checkid")
	@Column(name = "checkid")
	private Long checkId;
	
	@Column(name = "createtime")
	private Long createTime;
	
	@Column(name = "userid")
	private Long userId;
	
	@Column(name = "phone")
	private String phone;	

	@Column(name = "email")
	private String email;	
	
	@Column(name = "code")
	private String code;	
	
	@Column(name = "expired")
	private Long expired;
	
	@Column(name = "status")
	private Integer status;
	
	@PrePersist
	public void updateTimeStamps() {
		if (createTime == null) {
			createTime = new Date().getTime();
			expired = createTime + 300*1000;
		}
	}
	public Long getCheckId() {
		return checkId;
	}

	public void setCheckId(Long checkId) {
		this.checkId = checkId;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getExpired() {
		return expired;
	}

	public void setExpired(Long expired) {
		this.expired = expired;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}	
}

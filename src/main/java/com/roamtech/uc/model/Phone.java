package com.roamtech.uc.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Entity
@XmlRootElement
@Table(name = "phonelist", uniqueConstraints = @UniqueConstraint(columnNames = {
		"phone", "tenantid", "phone_type"}))
public class Phone implements Serializable {
	public static final int TYPE_PHONE = 1;
	public static final int TYPE_WX = 2;
	public static final int TYPE_QQ = 3;
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;

	@Column(name = "phone")
	private String phone;

	@Column(name = "userid")
	@JSONField(name = "userid", serialize = false)
	private Long userId;
	
	@Column(name = "verified")
	private Boolean verified;

	@Column(name = "tenantid")
	@JSONField(name = "tenantid", serialize = false)
	private Long tenantId;

	@Column(name = "phone_type")
	@JSONField(name = "phone_type")
	private Integer phoneType;

	@Override
	public int hashCode() {
		return (phone == null) ? super.hashCode() : phone
				.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		Phone entity = (Phone) other;
		if (phone != null && entity.phone != null && tenantId != null && entity.tenantId != null
				&& phoneType != null && entity.phoneType != null) {
			return phone.equals(entity.phone) && tenantId.equals(entity.tenantId) && phoneType.equals(entity.phoneType);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this,
				ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public Integer getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(Integer phoneType) {
		this.phoneType = phoneType;
	}

	@PrePersist
	public void defaultTenantIdAndType() {
		if (tenantId == null) {
			tenantId = User.ROAM_TENANT_ID;
		}
		if (phoneType == null) {
			phoneType = TYPE_PHONE;
		}
	}
}

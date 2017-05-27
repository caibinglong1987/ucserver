package com.roamtech.uc.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import com.alibaba.fastjson.annotation.JSONField;

@Entity
@XmlRootElement
@Table(name = "shipping")
public class Shipping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)	
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@JSONField(name = "code")
	@Column(name = "code")
	private String code;

	@Column(name = "name")
	@JSONField(name = "name")
	private String name;

	@Column(name = "description")
	@JSONField(name = "description")
	private String description;

	@Column(name = "insure")
	@JSONField(name = "insure")
	private String insure;//保价费用

	@JSONField(name = "support_cod")
	@Column(name = "support_cod")
	private Boolean supportCod;//是否支持货到付款，1、支持;0、不支持

	@JSONField(name = "enabled")
	@Column(name = "enabled")
	private Boolean enabled;//配送方式是否禁用，1、可用；0、禁用

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInsure() {
		return insure;
	}

	public void setInsure(String insure) {
		this.insure = insure;
	}

	public Boolean getSupportCod() {
		return supportCod;
	}

	public void setSupportCod(Boolean supportCod) {
		this.supportCod = supportCod;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}


}

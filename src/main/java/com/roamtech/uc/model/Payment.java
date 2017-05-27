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
@Table(name = "payment")
public class Payment implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int WAPALIPAY_ID=2;
	public static final int MOBILEALIPAY_ID=4;
	public static final int WXPAY_ID=5;
	public static final int XC_PAY_ID = 6; //信程支付
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

	@Column(name = "fee")
	@JSONField(name = "fee")
	private Double fee;//支付费用

	@Column(name = "sort")
	@JSONField(name = "sort")
	private Integer sort;

	@JSONField(name = "enabled")
	@Column(name = "enabled")
	private Boolean enabled;//支付方式是否禁用，1、可用；0、禁用

	@JSONField(name = "terminaltype")
	@Column(name = "terminaltype")
	private Integer terminalType;//终端类型，1、mobile；2、PC

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

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Integer getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(Integer terminalType) {
		this.terminalType = terminalType;
	}

}

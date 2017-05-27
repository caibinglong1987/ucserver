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
@Table(name = "areacode")
public class AreaCode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)	
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@Column(name = "areacode")
	@JSONField(name = "areacode")
	private String areacode;

	@Column(name = "areaname")
	@JSONField(name = "areaname")
	private String areaname;

	@Column(name = "nationalcode")
	@JSONField(name = "nationalcode")
	private String nationalcode;

	@Column(name = "moneycode")
	@JSONField(name = "moneycode")
	private String moneycode;
	
	@JSONField(name = "timediff")
	@Column(name = "timediff")
	private Long timediff;
	
	@JSONField(name = "groupid")
	@Column(name = "groupid")
	private Long groupid;
	
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getAreacode() {
		return areacode;
	}

	public void setAreacode(String areacode) {
		this.areacode = areacode;
	}

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public String getNationalcode() {
		return nationalcode;
	}

	public void setNationalcode(String nationalcode) {
		this.nationalcode = nationalcode;
	}

	public String getMoneycode() {
		return moneycode;
	}

	public void setMoneycode(String moneycode) {
		this.moneycode = moneycode;
	}

	public Long getTimediff() {
		return timediff;
	}

	public void setTimediff(Long timediff) {
		this.timediff = timediff;
	}

	public Long getGroupid() {
		return groupid;
	}

	public void setGroupid(Long groupid) {
		this.groupid = groupid;
	}


}

package com.roamtech.uc.model;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@Entity
@XmlRootElement
@Table(name = "datacard_area")
public class DataCardArea implements Serializable {
	private static final long serialVersionUID = 1L;	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;

	@Column(name = "datacard_type")
	private Integer datacardType;

	@Column(name = "areaname")
	private String areaname;

	@Column(name = "nationalcode")
	@JSONField(name = "nationalcode")
	private String nationalcode;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Integer getDatacardType() {
		return datacardType;
	}

	public void setDatacardType(Integer datacardType) {
		this.datacardType = datacardType;
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
}


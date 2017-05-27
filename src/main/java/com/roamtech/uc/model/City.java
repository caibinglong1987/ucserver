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
@Table(name = "city")
public class City implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)	
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@JSONField(name = "pid")
	@Column(name = "pid")
	private Long pid;

	@Column(name = "name")
	@JSONField(name = "name")
	private String name;

	@Column(name = "type")
	@JSONField(name = "type")
	private Integer type;//0-国家，1-省，2-市(地级)，3-区(县级)

	public final static int COUNTRY_TYPE = 0;
	public final static int PROVINCE_TYPE = 1;
	public final static int CITY_TYPE = 2;
	public final static int DISTRICT_TYPE = 3;	
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}

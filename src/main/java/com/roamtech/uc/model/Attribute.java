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
@Table(name = "attribute")
public class Attribute implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@Column(name = "typeid")
	@JSONField(name = "typeid")
	private Long typeid;

		
	@Column(name = "name")
	@JSONField(name = "name")
	private String name;
	
	@Column(name = "varname")
	@JSONField(name = "varname")
	private String varname;	
	
	@Column(name = "input_type")
	@JSONField(name = "input_type")
	private Boolean inputType;

	@Column(name = "type")
	@JSONField(name = "type")
	private String type;		
	
	@Column(name = "values")
	@JSONField(name = "values")
	private String values;
	
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Long getTypeid() {
		return typeid;
	}

	public void setTypeid(Long typeid) {
		this.typeid = typeid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVarname() {
		return varname;
	}

	public void setVarname(String varname) {
		this.varname = varname;
	}	
	
	public Boolean getInputType() {
		return inputType;
	}

	public void setInputType(Boolean inputType) {
		this.inputType = inputType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}

}

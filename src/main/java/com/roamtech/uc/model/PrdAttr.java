package com.roamtech.uc.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import com.alibaba.fastjson.annotation.JSONField;

@Entity
@XmlRootElement
@Table(name = "prd_attr")
public class PrdAttr implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@Column(name = "productid")
	@JSONField(name = "productid")
	private Long productid;

		
//	@Column(name = "attrid")
//	@JSONField(name = "attrid")
//	private Long attrid;
	
	@Column(name = "value")
	@JSONField(name = "value")
	private String value;

	@Column(name = "price")
	@JSONField(name = "price")
	private String price;		

	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "attrid")	
	private Attribute attr;	
	
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Long getProductid() {
		return productid;
	}

	public void setProductid(Long productid) {
		this.productid = productid;
	}
/*
	public Long getAttrid() {
		return attrid;
	}

	public void setAttrid(Long attrid) {
		this.attrid = attrid;
	}
*/
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	
	public Attribute getAttr() {
		return attr;
	}

	public void setAttr(Attribute attr) {
		this.attr = attr;
	}

}

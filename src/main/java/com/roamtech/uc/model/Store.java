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
@Table(name = "store",uniqueConstraints = @UniqueConstraint(columnNames = {
		"name"}))
public class Store implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@Column(name = "userid")
	@JSONField(name = "userid")
	private Long userid;

		
	@Column(name = "name")
	@JSONField(name = "name")
	private String name;//店铺名称
	
	@Column(name = "logo")
	@JSONField(name = "logo")
	private String logo;/*店铺图片*/	

	@Column(name = "intro")
	@JSONField(name = "intro")
	private String intro;	//简介

	@Column(name = "location")
	@JSONField(name = "location")
	private String location;	//联系地址

	@Column(name = "source")
	@JSONField(name = "source")
	private String source; //主要货源
	
	@Column(name = "description")
	@JSONField(name = "description")
	private String description;

	@Column(name = "invoice_content")
	@JSONField(name = "invoice_content")
	private String invoice_content;	// 发票内容

	
	@Column(name = "createtime")
	@JSONField(name = "createtime")
	private Date createtime;		
	
	@Column(name = "modifytime")
	@JSONField(name = "modifytime")
	private Date modifytime;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInvoice_content() {
		return invoice_content;
	}

	public void setInvoice_content(String invoice_content) {
		this.invoice_content = invoice_content;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getModifytime() {
		return modifytime;
	}

	public void setModifytime(Date modifytime) {
		this.modifytime = modifytime;
	}

}

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
@Table(name = "module",uniqueConstraints = @UniqueConstraint(columnNames = {
		"code"}))
public class Module implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@Column(name = "pid")
	@JSONField(name = "pid")
	private Long pid;
	
	@Column(name = "code")
	@JSONField(name = "code")
	private String code;//模块编号
	
	@Column(name = "type")
	@JSONField(name = "type")
	private String type;/*模块类型：SYSTEM,GROUP,MODULE,FUNCTION*/	

	@Column(name = "url")
	@JSONField(name = "url")
	private String url;
	
	@Column(name = "image")
	@JSONField(name = "image")
	private String image;
	
	@Column(name = "description")
	@JSONField(name = "description")
	private String description;

	@Column(name = "expanded")
	@JSONField(name = "expanded")
	private Boolean expanded;	
	
	@Column(name = "sort")
	@JSONField(name = "sort")
	private Integer sort;		

	@Column(name = "createtime")
	@JSONField(name = "createtime")
	private Date createtime;		
	
	@Column(name = "modifytime")
	@JSONField(name = "modifytime")
	private Date modifytime;

	public Long getId() {
		return Id;
	}

	public void setId(Long Id) {
		this.Id = Id;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getExpanded() {
		return expanded;
	}

	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
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

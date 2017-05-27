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
@Table(name = "prd_category")
public class PrdCategory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@Column(name = "pid")
	@JSONField(name = "pid")
	private Long pid;

	@Column(name = "path")
	@JSONField(name = "path")
	private String path;//树的路径,只能三级
	
	@Column(name = "name")
	@JSONField(name = "name")
	private String name;//产品类别(流量包、语音包)
	
	@Column(name = "logo")
	@JSONField(name = "logo")
	private String logo;/*类目图片*/	
	
	@Column(name = "description")
	@JSONField(name = "description")
	private String description;	
	
	@Column(name = "sort")
	@JSONField(name = "sort")
	private Integer sort;		

	@Column(name = "createtime")
	@JSONField(name = "createtime",format="yyyy-MM-dd HH:mm:ss")
	private Date createtime;		
	
	@Column(name = "modifytime")
	@JSONField(name = "modifytime",format="yyyy-MM-dd HH:mm:ss")
	private Date modifytime;

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

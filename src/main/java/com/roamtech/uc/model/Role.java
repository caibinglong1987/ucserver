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
@Table(name = "role",uniqueConstraints = @UniqueConstraint(columnNames = {
		"name"}))
public class Role implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "roleid")
	@Column(name = "roleid")
	private Long roleId;
	
	@Column(name = "pid")
	@JSONField(name = "pid")
	private Long pid;
	
	@Column(name = "name")
	@JSONField(name = "name")
	private String name;//角色名
	
	@Column(name = "type")
	@JSONField(name = "type")
	private String type;/*角色类型：ROLE_ADMIN,ROLE_USER*/	
	
	@Column(name = "description")
	@JSONField(name = "description")
	private String description;

	@Column(name = "leaf")
	@JSONField(name = "leaf")
	private Boolean leaf;	
	
	@Column(name = "sort")
	@JSONField(name = "sort")
	private Integer sort;		

	@Column(name = "createtime")
	@JSONField(name = "createtime")
	private Date createtime;		
	
	@Column(name = "modifytime")
	@JSONField(name = "modifytime")
	private Date modifytime;

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getLeaf() {
		return leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
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

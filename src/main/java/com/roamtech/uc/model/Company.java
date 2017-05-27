package com.roamtech.uc.model;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Entity
@XmlRootElement
@Table(name = "company")
public class Company implements Serializable {
	private static final long serialVersionUID = 1L;

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

	@Column(name = "comptype")
	@JSONField(name = "comptype")
	private Integer comptype;//公司类型：1、快递，2、支付，3、社交，4、旅游，5、金融，6、电子商务
	
	@JSONField(name = "enabled")
	@Column(name = "enabled")
	private Boolean enabled;//公司是否禁用，1、可用；0、禁用

	@Column(name = "servergroup")
	@JSONField(name = "servergroup")
	private Integer serverGroup;

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

	public Integer getComptype() {
		return comptype;
	}

	public void setComptype(Integer comptype) {
		this.comptype = comptype;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Integer getServerGroup() {
		return serverGroup;
	}

	public void setServerGroup(Integer serverGroup) {
		this.serverGroup = serverGroup;
	}
}

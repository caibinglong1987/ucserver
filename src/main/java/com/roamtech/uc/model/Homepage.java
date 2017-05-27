package com.roamtech.uc.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@Table(name = "homepage")
public class Homepage implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int APP_HOMEPAGE=1;
	public static final int MALL_HOMEPAGE=2;
	@Id
	@Column(name = "id")
	private Long Id;

	// 1:APP首页;2:商城首页
	@Column(name = "type")
	private Integer type;
	
	// location(首页布局位置，'banners','products','travalroutes','headlines','hotactivities')
	@Column(name = "location")
	private String location;	
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "logo")
	private String logo;
	
	@Column(name = "url")
	private String url;
	
	@Column(name = "url_title")
	@JSONField(name = "url_title")
	private String urlTitle;
	
	@Column(name = "content")
	private String content;
	
	@Column(name = "sort")
	private Integer sort;

	@Column(name = "disabled")
	private Boolean disabled;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlTitle() {
		return urlTitle;
	}

	public void setUrlTitle(String urlTitle) {
		this.urlTitle = urlTitle;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
}

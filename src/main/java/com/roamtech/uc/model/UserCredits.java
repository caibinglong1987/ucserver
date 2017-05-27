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
@Table(name = "user_credits")
public class UserCredits implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	@JSONField(name = "name")
	private String name;
	
	@Column(name = "logo")
	@JSONField(name = "logo")
	private String logo;
	
	@Column(name = "min_points")
	@JSONField(name = "min_points")
	private Long minPoints;	
	
	@Column(name = "max_points")
	@JSONField(name = "max_points")
	private Long maxPoints;	

	@Column(name = "birthday")
	@JSONField(name = "birthday")
	private Date birthday;	
	
	@Column(name = "discount")
	@JSONField(name = "discount")
	private Integer discount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getMinPoints() {
		return minPoints;
	}

	public void setMinPoints(Long minPoints) {
		this.minPoints = minPoints;
	}

	public Long getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(Long maxPoints) {
		this.maxPoints = maxPoints;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}	
	
}

package com.roamtech.uc.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import com.alibaba.fastjson.annotation.JSONField;

@Entity
@XmlRootElement
@Table(name = "user_address")
public class UserAddress implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long id;
	
	@Column(name = "userid")
	@JSONField(name = "userid")
	private Long userId;
	
	@Column(name = "consignee")
	@JSONField(name = "consignee")
	private String consignee;
	
	@Column(name = "email")
	@JSONField(name = "email")
	private String email;	
	
	@Column(name = "country")
	@JSONField(name = "country")
	private Integer country;

	@Column(name = "province")
	@JSONField(name = "province")
	private Integer province;	
	
	@Column(name = "city")
	@JSONField(name = "city")
	private Integer city;
	
	@Column(name = "district")
	@JSONField(name = "district")
	private Integer district;	
	
	@Column(name = "address")
	@JSONField(name = "address")
	private String address;	
	
	@Column(name = "zipcode")
	@JSONField(name = "zipcode")
	private String zipcode;	
	
	@Column(name = "mobile")
	@JSONField(name = "mobile")
	private String mobile;
	
	@Column(name = "status")
	@JSONField(name = "status")
	private Integer status;
	
	public static final int NORMAL_STATUS=0;
	public static final int DELETED_STATUS=1;
	
	@Transient
	private List<City> cities;
	
	@Transient
	private Boolean defaultaddr;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getCountry() {
		return country;
	}

	public void setCountry(Integer country) {
		this.country = country;
	}

	public Integer getProvince() {
		return province;
	}

	public void setProvince(Integer province) {
		this.province = province;
	}

	public Integer getCity() {
		return city;
	}

	public void setCity(Integer city) {
		this.city = city;
	}

	public Integer getDistrict() {
		return district;
	}

	public void setDistrict(Integer district) {
		this.district = district;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public List<City> getCities() {
		return cities;
	}

	public void setCities(List<City> cities) {
		this.cities = cities;
	}
	
	public Boolean getDefaultaddr() {
		return defaultaddr;
	}

	public void setDefaultaddr(Boolean defaultaddr) {
		this.defaultaddr = defaultaddr;
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}

package com.roamtech.uc.cache.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;

public class ServicePackage implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String STARTTIME_TAG="effect_datetime";
	public static final String ENDTIME_TAG="failure_datetime";
	public static final String SIMID_TAG="simid";
	@JSONField(name = "name")
	private String name;
	@JSONField(name = "logo")
	private String logo;	
	@JSONField(name = "type")
	private Integer type;//1 全球上网卡，2 流量套餐， 3 语音套餐
	@JSONField(name = "productid")
	private Long productId;
	@JSONField(name = "orderid")
	private Long orderId;
	@JSONField(name = "orderdetailid")
	private Long orderdetailId;
	@JSONField(name = "effect_datetime",format="yyyy-MM-dd HH:mm:ss")
	private Date startTime;
	@JSONField(name = "failure_datetime",format="yyyy-MM-dd HH:mm:ss")
	private Date endTime;
	@JSONField(name = "remainder")
	private Double remainder;
	@JSONField(name = "areaname")
	private String areaname;
	@JSONField(name = "simid")
	private String simid;
	@JSONField(name = "phone")
	private String phone;
	private Integer quantity;
	Set<String> simids;
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
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Long getOrderdetailId() {
		return orderdetailId;
	}
	public void setOrderdetailId(Long orderdetailId) {
		this.orderdetailId = orderdetailId;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Double getRemainder() {
		return remainder;
	}
	public void setRemainder(Double remainder) {
		this.remainder = remainder;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getAreaname() {
		return areaname;
	}
	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}
	public String getSimid() {
		return simid;
	}
	public void setSimid(String simid) {
		this.simid = simid;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Set<String> getSimids() {
		return simids;
	}
	public void setSimids(Set<String> simids) {
		this.simids = simids;
	}
}

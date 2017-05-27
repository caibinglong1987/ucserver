package com.roamtech.uc.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;


import com.alibaba.fastjson.annotation.JSONField;

@Entity
@XmlRootElement
@Table(name = "datacard_traffic")
public class DataCardTraffic implements Serializable {
	private static final long serialVersionUID = 1L;	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;

	@Column(name = "datacardid")
	private Long datacardid;

	@Column(name = "orderid")
	private Long orderid;	
	
	@Column(name = "orderdetailid")
	private Long orderdetailid;	
	
	@Column(name = "iccid")
	private String iccid;

	@Column(name = "imsi")
	private String imsi;

	@Column(name = "effect_datetime")
	@JSONField(name = "effect_datetime",format="yyyy-MM-dd HH:mm:ss")
	private Date effectDatetime;		
	
	@Column(name = "failure_datetime")
	@JSONField(name = "failure_datetime",format="yyyy-MM-dd HH:mm:ss")
	private Date failureDatetime;
	
	@Column(name = "areaname")
	private String areaname;

	@Column(name = "userid")
	@JSONField(name = "userid")
	private Long userId;

	@Column(name = "status")
	@JSONField(name = "status")
	private Integer status;//订单状态。0，未确认；1，已确认；2，已取消；

	@Column(name = "purchaseid")
	@JSONField(name = "purchaseid")
	private String purchaseId;

	@Transient
	@JSONField(serialize=false)
	private DataCard datacard;

	@Transient
	private Integer type;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Long getDatacardid() {
		return datacardid;
	}

	public void setDatacardid(Long datacardid) {
		this.datacardid = datacardid;
	}

	public Long getOrderid() {
		return orderid;
	}

	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}

	public Long getOrderdetailid() {
		return orderdetailid;
	}

	public void setOrderdetailid(Long orderdetailid) {
		this.orderdetailid = orderdetailid;
	}

	public String getIccid() {
		return iccid;
	}

	public void setIccid(String iccid) {
		this.iccid = iccid;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public Date getEffectDatetime() {
		return effectDatetime;
	}

	public void setEffectDatetime(Date effectDatetime) {
		this.effectDatetime = effectDatetime;
	}

	public Date getFailureDatetime() {
		return failureDatetime;
	}

	public void setFailureDatetime(Date failureDatetime) {
		this.failureDatetime = failureDatetime;
	}

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public DataCard getDatacard() {
		return datacard;
	}

	public void setDatacard(DataCard datacard) {
		this.datacard = datacard;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getPurchaseId() {
		return purchaseId;
	}

	public void setPurchaseId(String purchaseId) {
		this.purchaseId = purchaseId;
	}
}


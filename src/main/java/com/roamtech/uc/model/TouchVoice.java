package com.roamtech.uc.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;


import com.alibaba.fastjson.annotation.JSONField;

@Entity
@XmlRootElement
@Table(name = "touch_voice")
public class TouchVoice implements Serializable {
	private static final long serialVersionUID = 1L;	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;

	@Column(name = "touchdevid")
	private Long touchdevid;

	@Column(name = "orderid")
	private Long orderid;	
	
	@Column(name = "orderdetailid")
	private Long orderdetailid;	

	@Column(name = "touchchansid")
	private Long touchchansid;	

	@Column(name = "phone")
	private String phone;
	
	@Column(name = "effect_datetime")
	@JSONField(name = "effect_datetime",format="yyyy-MM-dd HH:mm:ss")
	private Date effectDatetime;		
	
	@Column(name = "failure_datetime")
	@JSONField(name = "failure_datetime",format="yyyy-MM-dd HH:mm:ss")
	private Date failureDatetime;
	
	@Column(name = "userid")
	@JSONField(name = "userid")
	private Long userId;

	@Column(name = "sgroup")
	private Integer sgroup;	
	
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Long getTouchdevid() {
		return touchdevid;
	}

	public void setTouchdevid(Long touchdevid) {
		this.touchdevid = touchdevid;
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

	public Long getTouchchansid() {
		return touchchansid;
	}

	public void setTouchchansid(Long touchchansid) {
		this.touchchansid = touchchansid;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getSgroup() {
		return sgroup;
	}

	public void setSgroup(Integer sgroup) {
		this.sgroup = sgroup;
	}	

}


package com.roamtech.uc.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
@Table(name = "user_verify")
public class EVoucherVerify implements Serializable {
	public static final int EVOUCHER_VALID=1;
	public static final int EVOUCHER_USED=2;
	public static final int EVOUCHER_INVALID=3;
	public static final int EVOUCHER_NOEXIST=4;
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long id;
	
	@Column(name = "evoucherid")
	@JSONField(name = "evoucherid")
	private Long evoucherid;
	
	@Column(name = "evoucher_sn")
	@JSONField(name = "evoucher_sn")
	private Long sn;

	@Column(name = "evoucher_name")
	@JSONField(name = "evoucher_name")
	private String evoucherName;
	
	@Column(name = "action_userid")
	@JSONField(name = "action_userid")
	private Long actionUserid;
	
	@Column(name = "userid")
	@JSONField(name = "userid")
	private Long userid;
	
	@Column(name = "orderid")
	@JSONField(name = "orderid")
	private Long orderid;

	
	@Column(name = "status")
	@JSONField(name = "status")
	private Integer status;
	
	@Column(name = "createtime")
	@JSONField(name = "createtime",format="yyyy-MM-dd HH:mm:ss")
	private Date createtime;		

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEvoucherid() {
		return evoucherid;
	}

	public void setEvoucherid(Long evoucherid) {
		this.evoucherid = evoucherid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Long getSn() {
		return sn;
	}

	public void setSn(Long sn) {
		this.sn = sn;
	}

	public String getEvoucherName() {
		return evoucherName;
	}

	public void setEvoucherName(String evoucherName) {
		this.evoucherName = evoucherName;
	}

	public Long getActionUserid() {
		return actionUserid;
	}

	public void setActionUserid(Long actionUserid) {
		this.actionUserid = actionUserid;
	}

	public Long getOrderid() {
		return orderid;
	}

	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

}

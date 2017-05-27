package com.roamtech.uc.model;

import java.io.Serializable;
import java.util.Date;

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
@Table(name = "prd_discount")
public class PrdDiscount implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@Column(name = "productid")
	@JSONField(name = "productid")
	private Long productid;

		
	@Column(name = "quantity")
	@JSONField(name = "quantity")
	private Integer quantity;//折扣条件(对应商品单价的倍数)
	
	@Column(name = "discount")
	@JSONField(name = "discount")
	private Integer discount;

	@Column(name = "effect_datetime")
	@JSONField(name = "effect_datetime",format="yyyy-MM-dd HH:mm:ss")
	private Date effectDatetime;		
	
	@Column(name = "failure_datetime")
	@JSONField(name = "failure_datetime",format="yyyy-MM-dd HH:mm:ss")
	private Date failureDatetime;
	
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Long getProductid() {
		return productid;
	}

	public void setProductid(Long productid) {
		this.productid = productid;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
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


}

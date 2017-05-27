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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.roamtech.uc.cache.model.ODPrdAttr;

@Entity
@XmlRootElement
@Table(name = "cart",uniqueConstraints = @UniqueConstraint(columnNames = {
		"sessionid"}))
public class Cart implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@Column(name = "userid")
	@JSONField(name = "userid")
	private Long userid;

		
	@Column(name = "sessionid")
	@JSONField(name = "sessionid")
	private String sessionid;//登录的会话id,用户退出对应的购物车所有记录都删除
	
	@Column(name = "productid")
	@JSONField(name = "productid")
	private Long productid;
	
	@Column(name = "unit_price")
	@JSONField(name = "unit_price")
	private Double price;
	
	@Column(name = "quantity_per_unit")
	@JSONField(name = "quantity_per_unit")
	private Integer quantityPerUnit;
	
	@Column(name = "quantity")
	@JSONField(name = "quantity")
	private Integer quantity;

	@Column(name = "discount")
	@JSONField(name = "discount")
	private Double discount;

	@Column(name = "attrs")
	@JSONField(serialize = false)
	private String attrs;
	
	@Transient
	private List<ODPrdAttr> odprdattrs;
	
/*	@Column(name = "effect_datetime")
	@JSONField(name = "effect_datetime")
	private Date effectDatetime;
	
	@Column(name = "failure_datetime")
	@JSONField(name = "failure_datetime")
	private Date failureDatetime;

	@Column(name = "simid")
	@JSONField(name = "simid")
	private Long simId;*/
	@Transient
	private List<DataCardTraffic> dcts;
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public Long getProductid() {
		return productid;
	}

	public void setProductid(Long productid) {
		this.productid = productid;
	}

	public Integer getQuantityPerUnit() {
		return quantityPerUnit;
	}

	public void setQuantityPerUnit(Integer quantityPerUnit) {
		this.quantityPerUnit = quantityPerUnit;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

/*	public Date getEffectDatetime() {
		return effectDatetime;
	}

	public void setEffectDatetime(Date effectDatetime) {
		this.effectDatetime = effectDatetime;
	}*/

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

/*	public Date getFailureDatetime() {
		return failureDatetime;
	}

	public void setFailureDatetime(Date failureDatetime) {
		this.failureDatetime = failureDatetime;
	}

	public Long getSimId() {
		return simId;
	}

	public void setSimId(Long simId) {
		this.simId = simId;
	}*/
	public String getAttrs() {
		return attrs;
	}

	public void setAttrs(String attrs) {
		this.attrs = attrs;
	}
	
	public List<ODPrdAttr> getOdprdattrs() {
		if(odprdattrs == null && StringUtils.isNotBlank(attrs)) {
			odprdattrs = (List<ODPrdAttr>) JSON.parseObject(attrs,new TypeReference<List<ODPrdAttr>>(){});
		}
		return odprdattrs;
	}

	public void setOdprdattrs(List<ODPrdAttr> odprdattrs) {
		this.odprdattrs = odprdattrs;
		setAttrs(JSON.toJSONString(odprdattrs));
	}

	public List<DataCardTraffic> getDcts() {
		return dcts;
	}

	public void setDcts(List<DataCardTraffic> dcts) {
		this.dcts = dcts;
	}
}

package com.roamtech.uc.model.om;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.roamtech.uc.cache.model.ODPrdAttr;
import com.roamtech.uc.model.DataCard;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@XmlRootElement
@Table(name = "orderdetail")
public class OrderDetailJoin implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	//@Column(name = "orderid")
	@Transient
	@JSONField(name = "orderid")
	private String orderid;

	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "orderid")
	@JSONField(name = "order")
	@NotFound(action = NotFoundAction.IGNORE)
	private OMOrder prdOrder;

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

	@Column(name = "discount")// 梯度价格优惠
	@JSONField(name = "discount")
	private Double discount;
	
	@Column(name = "voucher_amount")// 优惠券的价格优惠，放到这里的都是不限品类的优惠券优惠价格
	@JSONField(name = "voucher_amount")
	private Double voucherAmount;

	@Column(name = "status")
	@JSONField(name = "status")
	private Integer status;//订单状态。0，未确认；1，已确认；2，已取消；3，退货中；4，退款中；5，关闭;6,已退货；7，已退款
	public static final int ORDERDETAIL_RETURNED = 6;
	public static final int ORDERDETAIL_REFUNDED = 7;
	@Column(name = "attrs")
	@JSONField(serialize = false)
	private String attrs;
	
	@Transient
	private List<ODPrdAttr> odprdattrs;	

	@Column(name = "userid")
	@JSONField(name = "userid")
	private Long userId;
	
	@Column(name = "effect_datetime")
	@JSONField(name = "effect_datetime",format="yyyy-MM-dd HH:mm:ss")
	private Date effectDatetime;		
	
	@Column(name = "failure_datetime")
	@JSONField(name = "failure_datetime",format="yyyy-MM-dd HH:mm:ss")
	private Date failureDatetime;
	
	@Column(name = "areaname")
	private String areaname;
	
	@Column(name = "phone")
	private String phone;

	@Column(name = "call_duration")
	@JSONField(name = "call_duration")
	private Integer callDuration;

	@Column(name = "simids")
	@JSONField(serialize=false)
	private String simids;

	@Column(name = "source")
	private String source;
	
	@Transient
	@JSONField(name = "simids")
	private Set<String> simidlist;

	@Transient
	@JSONField(name = "datacards")
	private List<DataCard> dcs;


	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public Long getProductid() {
		return productid;
	}

	public void setProductid(Long productid) {
		this.productid = productid;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
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

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getVoucherAmount() {
		return voucherAmount;
	}

	public void setVoucherAmount(Double voucherAmount) {
		this.voucherAmount = voucherAmount;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

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
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public Integer getCallDuration() {
		return callDuration;
	}

	public void setCallDuration(Integer callDuration) {
		this.callDuration = callDuration;
	}
	
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSimids() {
		return simids;
	}

	public void setSimids(String simids) {
		this.simids = simids;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	public Set<String> getSimidlist() {
		if(simidlist == null && StringUtils.isNotBlank(simids)) {
			simidlist = (Set<String>) JSON.parseObject(simids,new TypeReference<Set<String>>(){});
		} else {
			simidlist = new HashSet<String>();
		}
		return simidlist;
	}

	public void setSimidlist(Set<String> simidlist) {
		this.simidlist = simidlist;
		setSimids(JSON.toJSONString(simidlist));
	}

	public void setOdprdattrs(List<ODPrdAttr> odprdattrs) {
		this.odprdattrs = odprdattrs;
		setAttrs(JSON.toJSONString(odprdattrs));
	}

	public List<DataCard> getDcs() {
		return dcs;
	}

	public void setDcs(List<DataCard> dcs) {
		this.dcs = dcs;
	}

	public OMOrder getPrdOrder() {
		return prdOrder;
	}

	public void setPrdOrder(OMOrder prdOrder) {
		this.prdOrder = prdOrder;
	}
}

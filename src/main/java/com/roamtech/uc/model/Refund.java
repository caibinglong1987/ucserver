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
@Table(name = "refund")
public class Refund implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@Column(name = "userid")
	@JSONField(name = "userid")
	private Long userid;

	@Column(name = "orderid")
	@JSONField(name = "orderid")
	private Long orderid;

	@Column(name = "orderdetailid")
	@JSONField(name = "orderdetailid")
	private Long orderDetailId;

	@Column(name = "money")
	@JSONField(name = "money")
	private Double money;		

	@Column(name = "payvoucher")
	@JSONField(name = "payvoucher")
	private String payVoucher;

	@Column(name = "shipping_name")
	@JSONField(name = "shipping_name")
	private String shippingName;

	@Column(name = "invoice_no")
	@JSONField(name = "invoice_no")
	private String invoiceNo;
	
	@Column(name = "refund_type")
	@JSONField(name = "refund_type")
	private Integer refundType;
	
	@Column(name = "createtime")
	@JSONField(name = "createtime")
	private Date createtime;

	@Column(name = "shippingtime")
	@JSONField(name = "shippingtime")
	private Date shippingTime;

	@Column(name = "receivedtime")
	@JSONField(name = "receivedtime")
	private Date receivedTime;

	@Column(name = "refundingtime")
	@JSONField(name = "refundingtime")
	private Date refundingTime;

	@Column(name = "refundtime")
	@JSONField(name = "refundtime")
	private Date refundTime;

	@Column(name = "confirmtime")
	@JSONField(name = "confirmtime")
	private Date confirmTime;

	@Column(name = "status")
	private Integer status;
	public static final int REFUND_STATUS_INIT = 0; //未确认
	public static final int REFUND_STATUS_CONFIRMED = 1;//已确认
	public static final int REFUND_STATUS_CANCELLED = 2;//无效
	public static final int REFUND_STATUS_RETURNING = 3;//退款中
	public static final int REFUND_STATUS_REFUNDING = 4;//已退款

	@Column(name = "shipping_status")
	@JSONField(name = "shipping_status")
	private Integer shippingStatus;
	public static final int REFUND_SHIPPING_NOTRECEIVED = 0; //未收到货
	public static final int REFUND_SHIPPING_SHIPPED = 1;//已发送退货
	public static final int REFUND_SHIPPING_RECEIVED = 2;//收到退货

	@Column(name = "reasonid")
	@JSONField(name = "reasonid")
	private Integer reasonId;

	@Column(name = "reason")
	@JSONField(name = "reason")
	private String reason;

	@Column(name = "is_real")
	@JSONField(name = "is_real")
	private Boolean real;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Long getOrderid() {
		return orderid;
	}

	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Integer getRefundType() {
		return refundType;
	}

	public void setRefundType(Integer refundType) {
		this.refundType = refundType;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(Long orderDetailId) {
		this.orderDetailId = orderDetailId;
	}

	public String getPayVoucher() {
		return payVoucher;
	}

	public void setPayVoucher(String payVoucher) {
		this.payVoucher = payVoucher;
	}

	public String getShippingName() {
		return shippingName;
	}

	public void setShippingName(String shippingName) {
		this.shippingName = shippingName;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}


	public Date getShippingTime() {
		return shippingTime;
	}

	public void setShippingTime(Date shippingTime) {
		this.shippingTime = shippingTime;
	}

	public Date getReceivedTime() {
		return receivedTime;
	}

	public void setReceivedTime(Date receivedTime) {
		this.receivedTime = receivedTime;
	}

	public Date getRefundingTime() {
		return refundingTime;
	}

	public void setRefundingTime(Date refundingTime) {
		this.refundingTime = refundingTime;
	}

	public Date getRefundTime() {
		return refundTime;
	}

	public void setRefundTime(Date refundTime) {
		this.refundTime = refundTime;
	}

	public Date getConfirmTime() {
		return confirmTime;
	}

	public void setConfirmTime(Date confirmTime) {
		this.confirmTime = confirmTime;
	}

	public Integer getShippingStatus() {
		return shippingStatus;
	}

	public void setShippingStatus(Integer shippingStatus) {
		this.shippingStatus = shippingStatus;
	}

	public Integer getReasonId() {
		return reasonId;
	}

	public void setReasonId(Integer reasonId) {
		this.reasonId = reasonId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Boolean getReal() {
		return real;
	}

	public void setReal(Boolean real) {
		this.real = real;
	}
}

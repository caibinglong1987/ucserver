package com.roamtech.uc.model.om;

import com.alibaba.fastjson.annotation.JSONField;
import com.roamtech.uc.model.OrderDetail;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@XmlRootElement
@Table(name = "prd_order")
public class OMOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @JSONField(name = "id")
    @Column(name = "id")
    private String id;

    @Column(name = "userid")
    @JSONField(name = "userid")
    private Long userid;

    @Column(name = "ship_address")
    @JSONField(name = "ship_address")
    private Long shipAddress;

    @Column(name = "shipping_status")
    @JSONField(name = "shipping_status")
    private Integer shippingStatus;//商品配送情况，0，未发货；1，已发货；2，已收货；3，备货中
    public static final int SHIPPING_STATUS_INIT = 0;
    public static final int SHIPPING_STATUS_SHIPPED = 1;
    public static final int SHIPPING_STATUS_RECEIVED = 2;
    public static final int SHIPPING_STATUS_PREPARE = 3;
    @Column(name = "pay_status")
    @JSONField(name = "pay_status")
    private Integer payStatus;//支付状态；0，未付款；1，付款中；2，已付款 3,已退款
    public static final int PAY_STATUS_INIT = 0;
    public static final int PAY_STATUS_PAYING = 1;
    public static final int PAY_STATUS_PAYED = 2;
    public static final int PAY_STATUS_REFUNDED = 3;
    @Column(name = "order_status")
    @JSONField(name = "order_status")
    private Integer orderStatus;//订单状态。0，未确认；1，已确认；2，已取消；3，退货中；4，退款中；5，关闭
    public static final int ORDER_STATUS_INIT = 0;
    public static final int ORDER_STATUS_CONFIRMED = 1;//买家确认收货
    public static final int ORDER_STATUS_CANCELLED = 2;
    public static final int ORDER_STATUS_RETURNING = 3;
    public static final int ORDER_STATUS_REFUNDING = 4;
    public static final int ORDER_STATUS_CLOSED = 5;
    @Column(name = "shipping_id")
    @JSONField(name = "shipping_id")
    private Integer shippingId;//用户选择的配送方式id

    @Column(name = "payid")
    @JSONField(name = "payid")
    private Integer payId;//支付方式

    @Column(name = "payvoucher")
    @JSONField(name = "payvoucher")
    private String payvoucher;//支付凭据编号

    @Column(name = "shipping_fee")
    @JSONField(name = "shipping_fee")
    private Double shippingFee;//配送费用

    @Column(name = "invoice_no")
    @JSONField(name = "invoice_no")
    private String invoiceNo;//发货单号

    @Column(name = "price")
    @JSONField(name = "price")
    private Double price;

    @Column(name = "discount") // 阶梯价格优惠
    @JSONField(name = "discount")
    private Double discount;

    @Column(name = "voucher_amount")// 优惠券的价格优惠，放到这里的都是不限品类的优惠券优惠价格
    @JSONField(name = "voucher_amount")
    private Double voucherAmount;

    @Column(name = "payable_amount")
    @JSONField(name = "payable_amount")
    private Double payableAmount;

    @Column(name = "tax_fee")
    @JSONField(name = "tax_fee")
    private Double taxFee;

    @Column(name = "receipt")
    @JSONField(name = "receipt")
    private Boolean receipt;

    @Column(name = "inv_payee")
    @JSONField(name = "inv_payee")
    private String invPayee;//发票抬头

    @Column(name = "inv_content")
    @JSONField(name = "inv_content")
    private String invContent;//发票内容，选择店家的invoice_content

    @Column(name = "obtainvoucher")
    @JSONField(name = "obtainvoucher")
    private Boolean obtainvoucher;

    @Column(name = "outletid")
    @JSONField(name = "outletid")
    private Long outletid;

    @Column(name = "obtaintime")
    @JSONField(name = "obtaintime",format="yyyy-MM-dd HH:mm:ss")
    private Date obtaintime;

    @Column(name = "paidmoney")
    @JSONField(name = "paidmoney")
    private Double paidmoney;

    @Column(name = "createtime")
    @JSONField(name = "createtime",format="yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    @Column(name = "confirmtime")
    @JSONField(name = "confirmtime",format="yyyy-MM-dd HH:mm:ss")
    private Date confirmtime;

    @Column(name = "paytime")
    @JSONField(name = "paytime",format="yyyy-MM-dd HH:mm:ss")
    private Date paytime;

    @Column(name = "shippingtime")
    @JSONField(name = "shippingtime",format="yyyy-MM-dd HH:mm:ss")
    private Date shippingtime;

    @Transient
    @JSONField(name = "orderdetails")
    private List<OrderDetail> orderDetails;

    @Column(name = "tenantid")
    @JSONField(name = "tenantid")
    private Long tenantId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Long getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(Long shipAddress) {
        this.shipAddress = shipAddress;
    }

    public Integer getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(Integer shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getShippingId() {
        return shippingId;
    }

    public void setShippingId(Integer shippingId) {
        this.shippingId = shippingId;
    }

    public Integer getPayId() {
        return payId;
    }

    public void setPayId(Integer payId) {
        this.payId = payId;
    }

    public String getPayvoucher() {
        return payvoucher;
    }

    public void setPayvoucher(String payvoucher) {
        this.payvoucher = payvoucher;
    }

    public Double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(Double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

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

    public Double getVoucherAmount() {
        return voucherAmount;
    }

    public void setVoucherAmount(Double voucherAmount) {
        this.voucherAmount = voucherAmount;
    }

    public Double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(Double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public Double getTaxFee() {
        return taxFee;
    }

    public void setTaxFee(Double taxFee) {
        this.taxFee = taxFee;
    }

    public Boolean getReceipt() {
        return receipt;
    }

    public void setReceipt(Boolean receipt) {
        this.receipt = receipt;
    }

    public String getInvPayee() {
        return invPayee;
    }

    public void setInvPayee(String invPayee) {
        this.invPayee = invPayee;
    }

    public String getInvContent() {
        return invContent;
    }

    public void setInvContent(String invContent) {
        this.invContent = invContent;
    }

    public Boolean getObtainvoucher() {
        return obtainvoucher;
    }

    public void setObtainvoucher(Boolean obtainvoucher) {
        this.obtainvoucher = obtainvoucher;
    }

    public Long getOutletid() {
        return outletid;
    }

    public void setOutletid(Long outletid) {
        this.outletid = outletid;
    }

    public Date getObtaintime() {
        return obtaintime;
    }

    public void setObtaintime(Date obtaintime) {
        this.obtaintime = obtaintime;
    }

    public Double getPaidmoney() {
        return paidmoney;
    }

    public void setPaidmoney(Double paidmoney) {
        this.paidmoney = paidmoney;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getConfirmtime() {
        return confirmtime;
    }

    public void setConfirmtime(Date confirmtime) {
        this.confirmtime = confirmtime;
    }

    public Date getPaytime() {
        return paytime;
    }

    public void setPaytime(Date paytime) {
        this.paytime = paytime;
    }

    public Date getShippingtime() {
        return shippingtime;
    }

    public void setShippingtime(Date shippingtime) {
        this.shippingtime = shippingtime;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}

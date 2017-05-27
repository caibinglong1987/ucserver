package com.roamtech.uc.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;

import com.alibaba.fastjson.annotation.JSONField;

@Entity
@XmlRootElement
@Table(name = "product",uniqueConstraints = @UniqueConstraint(columnNames = {
		"name"}))
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@Column(name = "storeid")
	@JSONField(name = "storeid")
	private Long storeid;

	@Column(name = "brandid")
	@JSONField(name = "brandid")
	private Long brandid;
	
	@Column(name = "categoryid")
	@JSONField(name = "categoryid")
	private Long categoryid;

	@Column(name = "typeid")
	@JSONField(name = "typeid")
	private Long typeid;
	
	@Column(name = "name")
	@JSONField(name = "name")
	private String name;//商品的标题

	@Column(name = "subname")
	@JSONField(name = "subname")
	private String subname;//商品的副标题
	
	@Column(name = "image")
	@JSONField(name = "image")
	private String image;/*商品的图片*/	
	
	@Column(name = "description")
	@JSONField(name = "description")
	private String description;

	@Column(name = "unit_price")
	@JSONField(name = "unit_price")
	private Double unitPrice;	

	@Column(name = "quantity_per_unit")
	@JSONField(name = "quantity_per_unit")
	private Integer quantityPerUnit;
	
	@Column(name = "validity")
	private Integer validity;

	@Column(name = "stock_number")
	@JSONField(name = "stock_number")
	private Integer stockNumber;		

	@Column(name = "sell_count")
	@JSONField(name = "sell_count")
	private Long sellCount;

	@Column(name = "groupid")
	@JSONField(name = "groupid")
	private Long groupId;

	@Column(name = "is_real")
	@JSONField(name = "is_real")
	private Boolean real;

	@Column(name = "is_delete")
	@JSONField(name = "is_delete")
	private Boolean delete;

	@Column(name = "is_package")
	@JSONField(name = "is_package")
	private Boolean bpackage;
	
	@Column(name = "createtime")
	@JSONField(name = "createtime",format="yyyy-MM-dd HH:mm:ss")
	private Date createtime;		
	
	@Column(name = "modifytime")
	@JSONField(name = "modifytime",format="yyyy-MM-dd HH:mm:ss")
	private Date modifytime;

	@Column(name = "deletetime")
	@JSONField(name = "deletetime",format="yyyy-MM-dd HH:mm:ss")
	private Date deletetime;

	@Column(name = "sale_state")
	@JSONField(name = "sale_state")
	private Integer saleState;
	
	//@OneToMany(cascade = { CascadeType.ALL },fetch=FetchType.EAGER)
	//@JoinColumn(name="packageid")
	@Transient
	private List<PrdPackage> packages;
	
	//@OneToMany(cascade = { CascadeType.ALL },fetch=FetchType.EAGER)
	//@JoinColumn(name="productid")
	@JSONField(name = "prdattrs")
	@Transient
	private List<PrdAttr> prdAttrs;
	
	@JoinColumn(name="instructions")
	@JSONField(name = "instructions")
	private String instructions;
	
	@JoinColumn(name="detail_image")
	@JSONField(name = "detail_image")
	private String detailImage;

	@JoinColumn(name="is_shipfree")
	@JSONField(name = "is_shipfree")
	private Boolean shipfree;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public Long getStoreid() {
		return storeid;
	}

	public void setStoreid(Long storeid) {
		this.storeid = storeid;
	}

	public Long getBrandid() {
		return brandid;
	}

	public void setBrandid(Long brandid) {
		this.brandid = brandid;
	}

	public Long getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(Long categoryid) {
		this.categoryid = categoryid;
	}
	
	public Long getTypeid() {
		return typeid;
	}

	public void setTypeid(Long typeid) {
		this.typeid = typeid;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubname() {
		return subname;
	}

	public void setSubname(String subname) {
		this.subname = subname;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getQuantityPerUnit() {
		return quantityPerUnit;
	}

	public void setQuantityPerUnit(Integer quantityPerUnit) {
		this.quantityPerUnit = quantityPerUnit;
	}

	public Integer getValidity() {
		return validity;
	}

	public void setValidity(Integer validity) {
		this.validity = validity;
	}
	
	public Integer getStockNumber() {
		return stockNumber;
	}

	public void setStockNumber(Integer stockNumber) {
		this.stockNumber = stockNumber;
	}

	public Long getSellCount() {
		return sellCount;
	}

	public void setSellCount(Long sellCount) {
		this.sellCount = sellCount;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Boolean getReal() {
		return real;
	}

	public void setReal(Boolean real) {
		this.real = real;
	}

	public Boolean getDelete() {
		return delete;
	}

	public void setDelete(Boolean delete) {
		this.delete = delete;
	}
	
	public Boolean getBpackage() {
		return bpackage;
	}

	public void setBpackage(Boolean bpackage) {
		this.bpackage = bpackage;
	}
	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getModifytime() {
		return modifytime;
	}

	public void setModifytime(Date modifytime) {
		this.modifytime = modifytime;
	}

	public Date getDeletetime() {
		return deletetime;
	}

	public void setDeletetime(Date deletetime) {
		this.deletetime = deletetime;
	}

	public Integer getSaleState() {
		return saleState;
	}

	public void setSaleState(Integer saleState) {
		this.saleState = saleState;
	}
	
	public List<PrdPackage> getPackages() {
		return packages;
	}

	public void setPackages(List<PrdPackage> packages) {
		this.packages = packages;
	}

	public List<PrdAttr> getPrdAttrs() {
		return prdAttrs;
	}

	public void setPrdAttrs(List<PrdAttr> prdAttrs) {
		this.prdAttrs = prdAttrs;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public String getDetailImage() {
		return detailImage;
	}

	public void setDetailImage(String detailImage) {
		this.detailImage = detailImage;
	}

	public Boolean getShipfree() {
		return shipfree;
	}

	public void setShipfree(Boolean shipfree) {
		this.shipfree = shipfree;
	}

}

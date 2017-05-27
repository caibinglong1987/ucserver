package com.roamtech.uc.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import com.alibaba.fastjson.annotation.JSONField;

@Entity
@XmlRootElement
@Table(name = "prd_package")
public class PrdPackage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@JSONField(name = "packageid")
	@Column(name = "packageid")
	private Long packageid;
	
	@Column(name = "productid")
	@JSONField(name = "productid")
	private Long productid;
	
/*	@ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH }, optional = true)   
    @JoinColumn(name="packageid")//这里设置JoinColum设置了外键的名字，并且PrdPackage是关系维护端 
	private Product product;
*/
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}
	
	public Long getPackageid() {
		return packageid;
	}

	public void setPackageid(Long packageid) {
		this.packageid = packageid;
	}

	public Long getProductid() {
		return productid;
	}

	public void setProductid(Long productid) {
		this.productid = productid;
	}

	
}

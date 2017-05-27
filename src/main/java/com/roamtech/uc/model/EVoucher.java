package com.roamtech.uc.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import com.alibaba.fastjson.annotation.JSONField;

@Entity
@XmlRootElement
@Table(name = "evoucher")
public class EVoucher implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int EVOUCHER_DELIVERY=1;
	public static final int EVOUCHER_RECHARGE=2;
	public static final int EVOUCHER_DISCOUNT=3;
	public static final int EVOUCHER_MEMBER=4;
	public static final int EVOUCHER_MXJX=5;		// 满X天减X天
	public static final int EVOUCHER_MEMBERID=7;
	public static final String OFFLINECOUPON_LOCATION="offlinecoupon";
	public static final String ONLINECOUPON_LOCATION="onlinecoupon";
	public static final String EVOUCHER_LOCATION="evoucher";
	public static final String MEMBER_LOCATION="member";
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	@JSONField(name = "name")
	private String name;

	@Column(name = "description")
	@JSONField(name = "description")
	private String description;
	
	@Column(name = "money")
	@JSONField(name = "money")
	private Double money;	
	
	@Column(name = "min_amount")
	@JSONField(name = "min_amount")
	private Double minamount;
	
	@Column(name = "max_amount")
	@JSONField(name = "max_amount")
	private Double maxamount;	
	
	@Column(name = "effect_datetime")
	@JSONField(name = "effect_datetime",format="yyyy-MM-dd HH:mm:ss")
	private Date effectDatetime;		
	
	@Column(name = "failure_datetime")
	@JSONField(name = "failure_datetime",format="yyyy-MM-dd HH:mm:ss")
	private Date failureDatetime;
	
	@Column(name = "background")
	@JSONField(name = "background")
	private String background;

	@Column(name = "logo")
	@JSONField(name = "logo")
	private String logo;

	@Column(name = "image")
	@JSONField(name = "image")
	private String image;

	@Column(name = "repeatable")
	@JSONField(name = "repeatable")
	private Boolean repeat;

	@Column(name = "type")
	@JSONField(name = "type")
	private Integer type;

	@Column(name = "createtime")
	@JSONField(name = "createtime",format="yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	@Column(name = "location")
	@JSONField(name = "location")
	private String location;

	@OneToMany(cascade = { CascadeType.ALL },fetch=FetchType.EAGER)
	@JoinColumn(name="evoucherid")
	@JSONField(name = "prdevouchers")
	private List<PrdEVoucher> prdEVouchers;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Double getMinamount() {
		return minamount;
	}

	public void setMinamount(Double minamount) {
		this.minamount = minamount;
	}

	public Double getMaxamount() {
		return maxamount;
	}

	public void setMaxamount(Double maxamount) {
		this.maxamount = maxamount;
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

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public Boolean getRepeat() {
		return repeat;
	}

	public void setRepeat(Boolean repeat) {
		this.repeat = repeat==null?false:repeat;
	}
	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<PrdEVoucher> getPrdEVouchers() {
		return prdEVouchers;
	}

	public void setPrdEVouchers(List<PrdEVoucher> prdEVouchers) {
		this.prdEVouchers = prdEVouchers;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}

package com.roamtech.uc.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import com.alibaba.fastjson.annotation.JSONField;

@Entity
@XmlRootElement
@Table(name = "user_evoucher")
public class UserEVoucher implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long id;
	
	@Column(name = "evoucherid")
	@JSONField(name = "evoucherid")
	private Long evoucherid;

	@Column(name = "userid")
	@JSONField(name = "userid")
	private Long userid;	
	
	@Column(name = "evoucher_sn")
	@JSONField(name = "evoucher_sn")
	private Long sn;
	
	@Column(name = "used_datetime")
	@JSONField(name = "used_datetime",format="yyyy-MM-dd HH:mm:ss")
	private Date usedDatetime;		
	
	@Column(name = "orderid")
	@JSONField(name = "orderid")
	private Long orderid;
	
	@Column(name = "image")
	@JSONField(name = "image")
	private String image;	
	
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

	@Transient
	@JSONField(name = "logo")
	private String logo;

	@Transient
	@JSONField(serialize=false)
	private String background;

	@Column(name = "repeatable")
	@JSONField(name = "repeatable")
	private Boolean repeatable;

	@Column(name = "createtime")
	@JSONField(name = "createtime",format="yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	@Transient
	@JSONField(name = "location")
	private String location;

	@Transient
	@JSONField(name = "showdetail")
	private Boolean showdetail;

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

	public Date getUsedDatetime() {
		return usedDatetime;
	}

	public void setUsedDatetime(Date date) {
		this.usedDatetime = date;
	}

	public Long getOrderid() {
		return orderid;
	}

	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
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

	public Boolean getRepeatable() {
		return repeatable;
	}

	public void setRepeatable(Boolean repeatable) {
		this.repeatable = repeatable;
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

	public Boolean getShowdetail() {
		return showdetail;
	}

	public void setShowdetail(Boolean showdetail) {
		this.showdetail = showdetail;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public static UserEVoucher createUserEVoucher(EVoucher ev) {
		UserEVoucher uev = new UserEVoucher();
		uev.setName(ev.getName());
		uev.setEffectDatetime(ev.getEffectDatetime());
		uev.setFailureDatetime(ev.getFailureDatetime());
		uev.setBackground(ev.getBackground());
		//uev.setImage(ev.getImage());
		uev.setLogo(ev.getLogo());
		uev.setUserid(-1L);
		uev.setRepeatable(ev.getRepeat());
		uev.setEvoucherid(ev.getId());
		uev.setDescription(ev.getDescription());
		uev.setMoney(ev.getMoney());
		uev.setMinamount(ev.getMinamount());
		uev.setMaxamount(ev.getMaxamount());
		return uev;
	}
}

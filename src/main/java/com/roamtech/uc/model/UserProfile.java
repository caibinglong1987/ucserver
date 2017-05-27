package com.roamtech.uc.model;

import java.io.Serializable;
import java.sql.Date;

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
@Table(name = "user_profile",uniqueConstraints = @UniqueConstraint(columnNames = {
		"userid"}))
public class UserProfile implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long id;
	
	@Column(name = "userid")
	@JSONField(name = "userid")
	private Long userId;
	
	@Column(name = "money")
	@JSONField(name = "money")
	private Double money;
	
	@Column(name = "money_type")
	@JSONField(name = "money_type")
	private Long moneyType;	
	
	@Column(name = "sex")
	@JSONField(name = "sex")
	private Integer sex;

	@Column(name = "birthday")
	@JSONField(name = "birthday")
	private Date birthday;	
	
	@Column(name = "pay_points")
	@JSONField(name = "pay_points")
	private Integer payPoints;
	
	@Column(name = "rank_points")
	@JSONField(name = "rank_points")
	private Integer rankPoints;	
	
	@Column(name = "rank_id")
	@JSONField(name = "rank_id")
	private Integer rankId;	
	
	@Column(name = "address_id")
	@JSONField(name = "address_id")
	private Long addressId;	
	
	@Column(name = "visit_count")
	@JSONField(name = "visit_count")
	private Long visitCount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Long getMoneyType() {
		return moneyType;
	}

	public void setMoneyType(Long moneyType) {
		this.moneyType = moneyType;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Integer getPayPoints() {
		return payPoints;
	}

	public void setPayPoints(Integer payPoints) {
		this.payPoints = payPoints;
	}

	public Integer getRankPoints() {
		return rankPoints;
	}

	public void setRankPoints(Integer rankPoints) {
		this.rankPoints = rankPoints;
	}

	public Integer getRankId() {
		return rankId;
	}

	public void setRankId(Integer rankId) {
		this.rankId = rankId;
	}

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public Long getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(Long visitCount) {
		this.visitCount = visitCount;
	}	
}

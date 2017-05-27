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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.alibaba.fastjson.annotation.JSONField;

@Entity
@XmlRootElement
@Table(name = "datacard",uniqueConstraints = @UniqueConstraint(columnNames = {
		"iccid"}))
public class DataCard implements Serializable {
	private static final long serialVersionUID = 1L;	
	@Id
//	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@Column(name = "iccid")
	private String iccid;
	
	@Column(name = "imsi")
	private String imsi;

	@Column(name = "userid")
	@JSONField(name = "userid")
	private Long userId;
	
	@Column(name = "ki")
	@JSONField(name = "ki")
	private String ki;
	
	@Column(name = "createtime")
	@JSONField(name = "createtime",format="yyyy-MM-dd HH:mm:ss")
	private Date createtime;
	
	@Column(name = "distributor")
	private Long distributor;//合作伙伴公司ID
	public static final long ROAMDATA_COMPID=1;
	@Column(name = "user_identifier")
	@JSONField(name = "user_identifier")
	private String userIdentifier;

	@Column(name = "type")
	private Integer type;
	public static final int EUROPE_DATACARD=1;/*欧洲dashi卡*/
	public static final int HK_DATACARD=2;/*香港联通卡*/
	public static final int EUROPEHK_DATACARD=3;/*双ki卡*/

	@Column(name = "imsi_hk")
	@JSONField(name = "imsi_hk")
	private String imsihk;

	@Column(name = "ki_hk")
	@JSONField(name = "ki_hk")
	private String kihk;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getIccid() {
		return iccid;
	}

	public void setIccid(String iccid) {
		this.iccid = iccid;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}	

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getKi() {
		return ki;
	}

	public void setKi(String ki) {
		this.ki = ki;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}	
	
	public Long getDistributor() {
		return distributor;
	}

	public void setDistributor(Long distributor) {
		this.distributor = distributor;
	}

	public String getUserIdentifier() {
		return userIdentifier;
	}

	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Boolean isHKDataCard() {
		return type == HK_DATACARD;
	}

	public String getImsihk() {
		return imsihk;
	}

	public void setImsihk(String imsihk) {
		this.imsihk = imsihk;
	}

	public String getKihk() {
		return kihk;
	}

	public void setKihk(String kihk) {
		this.kihk = kihk;
	}
}


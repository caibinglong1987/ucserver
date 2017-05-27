package com.roamtech.uc.model;

import java.io.Serializable;
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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.alibaba.fastjson.annotation.JSONField;

@Entity
@XmlRootElement
@Table(name = "touchdev",uniqueConstraints = @UniqueConstraint(columnNames = {
		"devid"}))
public class Touch implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_SGROUP = 0;
	public static final int DEVTYPE_FIXEDNUMBER = 1;
	public static final int DEVTYPE_RANDOMNUMBER = 2;
	public static final int DEVTYPE_ROAMBOX = 3;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@Column(name = "devid")
	private String devid;

	@Column(name = "userid")
	@JSONField(name = "userid")
	private Long userId;
	
	@Column(name = "phone")
	private String phone;	
	
	@Column(name = "host")
	private String host;	
	
	@Column(name = "wifi_ssid")
	@JSONField(name = "wifi_ssid")
	private String wifiSsid;

	@Column(name = "wifi_password")
	@JSONField(name = "wifi_password")
	private String wifiPassword;
	
	@Column(name = "domain")
	private String domain;	
	
	@Column(name = "devtype")
	private Integer devtype;	
	
	@Column(name = "sgroup")
	private Integer sgroup;	
	
	@Column(name = "verified")
	private Boolean verified;	
	
	@Transient
	@JSONField(name = "touchchans")
	private List<TouchChans> touchChans;

	@Column(name = "lan_ssid")
	@JSONField(name = "lan_ssid")
	private String lanSsid;

	@Column(name = "lan_password")
	@JSONField(name = "lan_password")
	private String lanPassword;

	@Column(name = "lan_ip")
	@JSONField(name = "lan_ip")
	private String lanIp;

	@Column(name = "wan_type")
	@JSONField(name = "wan_type")
	private String wanType;

	@Column(name = "wan_proto")
	@JSONField(name = "wan_proto")
	private String wanProto;

	@Column(name = "wan_ip")
	@JSONField(name = "wan_ip")
	private String wanIp;

	@Column(name = "transport")
	private String transport;

	@Override
	public int hashCode() {
		return (devid == null) ? super.hashCode() : devid
				.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		Touch entity = (Touch) other;
		if (devid == null && entity.devid == null) {
			return super.equals(entity);
		}
		if ((devid != null && entity.devid == null)
				|| (devid == null && entity.devid != null)) {
			return false;
		}
		return devid.equals(entity.devid);
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this,
				ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

	public String getDevid() {
		return devid;
	}

	public void setDevid(String devid) {
		this.devid = devid;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getWifiSsid() {
		return wifiSsid;
	}

	public void setWifiSsid(String wifiSsid) {
		this.wifiSsid = wifiSsid;
	}

	public String getWifiPassword() {
		return wifiPassword;
	}

	public void setWifiPassword(String wifiPassword) {
		this.wifiPassword = wifiPassword;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Integer getDevtype() {
		return devtype;
	}

	public void setDevtype(Integer devtype) {
		this.devtype = devtype;
	}

	public Integer getSgroup() {
		return sgroup;
	}

	public void setSgroup(Integer sgroup) {
		this.sgroup = sgroup;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	public List<TouchChans> getTouchChans() {
		return touchChans;
	}

	public void setTouchChans(List<TouchChans> touchChans) {
		this.touchChans = touchChans;
	}

	public String getLanSsid() {
		return lanSsid;
	}

	public void setLanSsid(String lanSsid) {
		this.lanSsid = lanSsid;
	}

	public String getLanPassword() {
		return lanPassword;
	}

	public void setLanPassword(String lanPassword) {
		this.lanPassword = lanPassword;
	}

	public String getLanIp() {
		return lanIp;
	}

	public void setLanIp(String lanIp) {
		this.lanIp = lanIp;
	}

	public String getWanType() {
		return wanType;
	}

	public void setWanType(String wanType) {
		this.wanType = wanType;
	}

	public String getWanProto() {
		return wanProto;
	}

	public void setWanProto(String wanProto) {
		this.wanProto = wanProto;
	}

	public String getWanIp() {
		return wanIp;
	}

	public void setWanIp(String wanIp) {
		this.wanIp = wanIp;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}
}


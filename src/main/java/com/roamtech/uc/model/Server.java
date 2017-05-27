package com.roamtech.uc.model;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Entity
@XmlRootElement
@Table(name = "server")
public class Server implements Serializable {
	private static final long serialVersionUID = 1L;	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@Column(name = "hostname")
	@JSONField(name = "hostname")
	private String hostname;
	
	@Column(name = "hostip")
	@JSONField(name = "hostip")
	private String hostip;

	@Column(name = "servicename")
	@JSONField(name = "servicename")
	private String serviceName;

	@Column(name = "servicecode")
	@JSONField(name = "servicecode")
	private String serviceCode;

	@Column(name = "protocol")
	@JSONField(name = "protocol")
	private String protocol;

	@Column(name = "serviceport")
	@JSONField(name = "serviceport")
	private Integer servicePort;

	@Column(name = "group")
	@JSONField(name = "group")
	private Integer group;


	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getHostip() {
		return hostip;
	}

	public void setHostip(String hostip) {
		this.hostip = hostip;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Integer getServicePort() {
		return servicePort;
	}

	public void setServicePort(Integer servicePort) {
		this.servicePort = servicePort;
	}

	public Integer getGroup() {
		return group;
	}

	public void setGroup(Integer group) {
		this.group = group;
	}
}


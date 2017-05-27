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
@Table(name = "prd_type")
public class PrdType implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final long PRD_DATACARD = 1L;
	public static final long PRD_DATATRAFFIC = 2L;
	public static final long PRD_VOICE = 3L;
	public static final long PRD_DATAVOICE = 4L;
	public static final long PRD_VOICENUMBER = 5L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JSONField(name = "id")
	@Column(name = "id")
	private Long Id;
	
	@Column(name = "name")
	@JSONField(name = "name")
	private String name;

	@Column(name = "enabled")
	@JSONField(name = "enabled")
	private Boolean enabled;		
	
	
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public static Boolean isDataOrVoice(Long type) {
		return (type == PRD_DATATRAFFIC || type == PRD_VOICE || type == PRD_DATAVOICE);
	}
	public static Boolean isDataOrVoiceNumber(Long type) {
		return (type == PRD_DATATRAFFIC || type == PRD_VOICENUMBER || type == PRD_DATAVOICE);
	}
	public static Boolean isVoiceNumber(Long type) {
		return (type == PRD_VOICENUMBER);
	}
	public static Boolean isVoiceType(Long type) {
		return (type == PRD_VOICE || type == PRD_DATAVOICE);
	}
	public static Boolean isDataType(Long type) {
		return (type == PRD_DATATRAFFIC || type == PRD_DATAVOICE);
	}
}

package com.roamtech.uc.cache.model;

import java.io.Serializable;


public class FixedPhone implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String phone;
	private Long touchdevid;	
	private Long touchchansid;
	private Integer sgroup;
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Long getTouchdevid() {
		return touchdevid;
	}

	public void setTouchdevid(Long touchdevid) {
		this.touchdevid = touchdevid;
	}

	public Long getTouchchansid() {
		return touchchansid;
	}

	public void setTouchchansid(Long touchchansid) {
		this.touchchansid = touchchansid;
	}

	public Integer getSgroup() {
		return sgroup;
	}

	public void setSgroup(Integer sgroup) {
		this.sgroup = sgroup;
	}	

}

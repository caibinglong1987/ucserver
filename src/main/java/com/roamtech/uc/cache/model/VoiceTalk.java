package com.roamtech.uc.cache.model;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class VoiceTalk implements Serializable {
	private static final long serialVersionUID = 1L;
	@JSONField(name = "totaltime")
	private Long totaltime;//分钟
	@JSONField(name = "usedtime")
	private Long usedtime;	
	@JSONField(name = "remaindertime")
	private Long remaindertime;
	public Long getTotaltime() {
		return totaltime;
	}
	public void setTotaltime(Long totaltime) {
		this.totaltime = totaltime;
	}
	public Long getUsedtime() {
		return usedtime;
	}
	public void setUsedtime(Long usedtime) {
		this.usedtime = usedtime;
	}
	public Long getRemaindertime() {
		return remaindertime;
	}
	public void setRemaindertime(Long remaindertime) {
		this.remaindertime = remaindertime;
	}
}

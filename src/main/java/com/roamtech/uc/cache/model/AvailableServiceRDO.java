package com.roamtech.uc.cache.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class AvailableServiceRDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@JSONField(name = "servicepackages")
	private List<ServicePackage> servicePackages;
	@JSONField(name = "voiceavailable")
	private VoiceTalk voiceTalk;
	@JSONField(name = "voicenumber")
	private VoiceNumber voiceNumber;
	public List<ServicePackage> getServicePackages() {
		return servicePackages;
	}
	public void setServicePackages(List<ServicePackage> servicePackages) {
		this.servicePackages = servicePackages;
	}
	public VoiceTalk getVoiceTalk() {
		return voiceTalk;
	}
	public void setVoiceTalk(VoiceTalk voiceTalk) {
		this.voiceTalk = voiceTalk;
	}
	public VoiceNumber getVoiceNumber() {
		return voiceNumber;
	}
	public void setVoiceNumber(VoiceNumber voiceNumber) {
		this.voiceNumber = voiceNumber;
	}


}

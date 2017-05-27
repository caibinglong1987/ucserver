package com.roamtech.uc.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.roamtech.uc.cache.handler.TouchVoiceHandler;
import com.roamtech.uc.client.BssApis;

@Component("ucScheduler")
public class UCScheduler {
	@Autowired
	TouchVoiceHandler tvHandler;
	@Autowired
	BssApis bssApis;
	public void loadFixedPhone() {
		tvHandler.loadFixedPhone();
	}
	
	public void expireTouchVoice() {
		tvHandler.findAll();
	}
	
	public void bssLoadData() {
		bssApis.loadData();
	}
}

package com.roamtech.uc.client;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class HKUnicomResponse implements Serializable {
	private String retCode;
	private String retMesg;
	private String acceptSn;
	private static final String SUCCESS_CODE="000000";
	private static final String DUPLICATEORDER_CODE="300018";
	public Boolean isSuccess() {
		if(retCode == null) return false;
		return retCode.equals(SUCCESS_CODE)||retCode.equals(DUPLICATEORDER_CODE);
	}
	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public String getRetMesg() {
		return retMesg;
	}

	public void setRetMesg(String retMesg) {
		this.retMesg = retMesg;
	}

	public String getAcceptSn() {
		return acceptSn;
	}

	public void setAcceptSn(String acceptSn) {
		this.acceptSn = acceptSn;
	}
}

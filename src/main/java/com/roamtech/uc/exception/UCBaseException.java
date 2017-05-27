package com.roamtech.uc.exception;

import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCResponse;

@SuppressWarnings("serial")
public class UCBaseException extends Exception {
	private UCResponse error = new UCResponse();

	public UCResponse getError() {
		return error;
	}

	public void setError(UCResponse error) {
		this.error = error;
	}
	public UCBaseException(int errorNo, String errorInfo) {
		this.error.setErrorNo(errorNo);
		this.error.setErrorInfo(errorInfo);
	}
	public void setError(int error) {
		this.error.setErrorNo(error);
		this.error.setErrorInfo(ErrorCode.getErrorInfo(error));
	}
}

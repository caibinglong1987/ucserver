package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.cache.model.VoiceNumber;
import com.roamtech.uc.handler.services.wrapper.AbstractRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;


public class TouchVoicePkgGetService extends AbstractService {
	private class TouchVoicePkgGetRequest extends AbstractRequest {
		private String devid;
		private String host;

		public TouchVoicePkgGetRequest(HttpServletRequest request) {
			super(request);
			devid = getParameter("devid");
			host = getParameter("host");
		}

		@Override
		public boolean validate() {
			return (StringUtils.isNotBlank(devid) && StringUtils.isNotBlank(host));
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		TouchVoicePkgGetRequest tvpkgGetReq = new TouchVoicePkgGetRequest(request);
		int status = ErrorCode.SUCCESS;
		List<VoiceNumber> voicePkgs = null;
		if(tvpkgGetReq.validate()) {
			voicePkgs = ucService.getVoicePackagesByTouch(tvpkgGetReq.devid,tvpkgGetReq.host);
			if(voicePkgs == null) {
				status = ErrorCode.ERR_TOUCHID_NOEXIST;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status));
		ucResp.addAttribute("voicepkgs", voicePkgs);
		postProcess(baseRequest, response, ucResp);
	}

}

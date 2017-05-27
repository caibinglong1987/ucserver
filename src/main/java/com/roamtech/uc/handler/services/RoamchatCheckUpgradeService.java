package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.AbstractRequest;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.AppReleased;


public class RoamchatCheckUpgradeService extends AbstractService {
	private class RoamchatCheckUpgradeRequest extends UCRequest {
		private Integer type;
		private Integer version;

		public RoamchatCheckUpgradeRequest(HttpServletRequest request) {
			super(request);
			
			String type = getParameter("type");
			this.type = Integer.parseInt(type);
		
			String version = getParameter("version");
			this.version = 0;
			if(StringUtils.isNotBlank(version)) {
				try {
					this.version = Integer.parseInt(version);					
				} catch (NumberFormatException e) {
					this.version = ucService.convertVersion(version);
				}
			}
		}

		@Override
		public boolean validate() {
			return (super.validate() && type != null /*&& !devid.isEmpty() && version != 0*/);
		}

	}

	
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		RoamchatCheckUpgradeRequest roamchatCheckReq = new RoamchatCheckUpgradeRequest(request);
		int status = ErrorCode.SUCCESS;
		AppReleased app = null;
		if(roamchatCheckReq.validate()) {
			if(ucService.isSessionValid(roamchatCheckReq.getSessionId())) {
					app = ucService.checkRoamchatUpgrade(roamchatCheckReq.type, roamchatCheckReq.version);
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status));
		ucResp.addAttribute("needed", app != null);
		if(app != null) {
			ucResp.addAttribute("version", app.getVersion());
			ucResp.addAttribute("version_name", app.getVersionName());
			ucResp.addAttribute("upgrade_time", (new Date()).getTime());
			ucResp.addAttribute("release_time", app.getReleaseTime());
			ucResp.addAttribute("description", app.getDescription());
			ucResp.addAttribute("url", app.getUrl());
			ucResp.addAttribute("file_size", app.getFileSize());
			ucResp.addAttribute("status", app.getStatus());
			Boolean forceUpgrade = app.getSupportedMinVersion() != null && app.getSupportedMinVersion()>roamchatCheckReq.version;
			ucResp.addAttribute("force_upgrade", forceUpgrade);
		}
		postProcess(baseRequest, response, ucResp);
	}

}

package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.AbstractRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.AppReleased;


public class TouchCheckUpgradeService extends AbstractService {
	private class TouchCheckUpgradeRequest extends AbstractRequest {
		private String devid;
		private Integer version;
		private Integer type;

		public TouchCheckUpgradeRequest(HttpServletRequest request) {
			super(request);
			devid = getParameter("devid");
			String version = getParameter("version");
			this.version = 0;
			if(StringUtils.isNotBlank(version)) {
				this.version = Integer.parseInt(version);
			}
			this.type = AppReleased.AppType.TouchApp.mValue;
			String temp = getParameter("type");
			if(StringUtils.isNotBlank(temp)) {
				this.type = Integer.parseInt(temp);
			}
		}

		@Override
		public boolean validate() {
			return (devid != null && !devid.isEmpty() /*&& version != 0*/);
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		TouchCheckUpgradeRequest touchCheckReq = new TouchCheckUpgradeRequest(request);
		int status = ErrorCode.SUCCESS;
		AppReleased app = null;
		if(touchCheckReq.validate()) {
			//if(ucService.findTouch(touchCheckReq.devid) != null) {
					app = ucService.checkTouchUpgrade(touchCheckReq.type,touchCheckReq.version);
			/*} else {
				status = ErrorCode.ERR_TOUCHID_NOEXIST;
			}*/	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status));
		ucResp.addAttribute("needed", app != null);
		if(app != null) {
			ucResp.addAttribute("version", app.getVersion());
			ucResp.addAttribute("upgrade_time", (new Date()).getTime());
			ucResp.addAttribute("url", app.getUrl());
		}
		postProcess(baseRequest, response, ucResp);
	}

}

package com.roamtech.uc.handler.om.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Touch;
import com.roamtech.uc.model.User;

public class TouchDevSetService extends AbstractService {
	private class TouchSetRequest extends UCRequest {
		private Long devid;
		private String hostname;
		private String host;
		private String domain;
		private String mobile;
		private String wifi_ssid;
		private String wifi_password;
		private Integer devtype;
		private Integer sgroup;
		private Long userId;


		public TouchSetRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("touchid");
			devid = null;
			if(StringUtils.isNotBlank(temp)) {
				devid = Long.valueOf(temp);
			}
			hostname = getParameter("hostname");
			host = getParameter("host");
			domain = getParameter("domain");
			mobile = getParameter("phone");
			wifi_ssid = getParameter("wifi_ssid");
			wifi_password = getParameter("wifi_password");
			devtype = 3;
			temp = getParameter("devtype");
			if(StringUtils.isNotBlank(temp)) {
				devtype = Integer.valueOf(temp);
			}
			devtype = 3;

			sgroup = 0;temp = getParameter("sgroup");
			if(StringUtils.isNotBlank(temp)) {
				sgroup = Integer.valueOf(temp);
			}
			userId = 0L;
			temp = getParameter("userid");
			if(StringUtils.isNotBlank(temp)) {
				userId = Long.valueOf(temp);
			}
		}

		@Override
		public boolean validate() {
			return (/*super.validate() &&*/ StringUtils.isNotBlank(host) && StringUtils.isNotBlank(hostname));
		}
		public Touch getTouch() {
			Touch touch = new Touch();
			touch.setId(devid);
			touch.setDevid(hostname);
			touch.setDomain(domain);
			touch.setHost(host);
			touch.setPhone(mobile);
			touch.setWifiSsid(wifi_ssid);
			touch.setWifiPassword(wifi_password);
			touch.setUserId(userId);
			touch.setDevtype(devtype);
			touch.setSgroup(sgroup);
			touch.setLanIp(getParameter("lan_ip"));
			touch.setLanSsid(getParameter("lan_ssid"));
			touch.setLanPassword(getParameter("lan_password"));
			touch.setWanType(getParameter("wan_type"));
			touch.setWanProto(getParameter("wan_proto"));
			touch.setWanIp(getParameter("wan_ip"));
			String transport = getParameter("transport");
			if(StringUtils.isBlank(transport)) {
				transport = "udp";
			}
			touch.setTransport(transport);
			return touch;
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		TouchSetRequest touchSetReq = new TouchSetRequest(request);
		int status = ErrorCode.SUCCESS;
		Touch touch = null;
		if(touchSetReq.validate()) {
			Integer usertype = omService.checkSessionValid(touchSetReq.getSessionId());
			if(usertype==User.ADMIN_USER) {
				touch = touchSetReq.getTouch();
				touch.setVerified(true);
				touch = omService.setTouch(touch);
			} else {
				touch = touchSetReq.getTouch();
				touch.setVerified(false);
				touch = omService.setTouch(touch);
				//status = ErrorCode.ERR_SESSIONID_INVALID;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), touchSetReq.getUserid(),touchSetReq.getSessionId());
		ucResp.addAttribute("touch", touch);
		postProcess(baseRequest, response, ucResp);
	}

}

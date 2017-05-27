package com.roamtech.uc.handler.om.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.GuestRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.City;
import com.roamtech.uc.model.Touch;

public class TouchDevGetsService extends AbstractService {
	private class  TouchDevGetsRequest extends GuestRequest {
		private Integer devtype;
		private String devid;
		private String phone;
		private Integer pageindex;
		private Integer pagesize;
		 TouchDevGetsRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("devtype");
			devtype = 0;
			if(temp != null) {
				devtype = Integer.parseInt(temp);
			}
			temp = getParameter("pageindex");
			pageindex = 0;
			if(temp != null) {
				pageindex = Integer.parseInt(temp);
			}
			temp = getParameter("pagesize");
			pagesize = 10;
			if(temp != null) {
				pagesize = Integer.parseInt(temp);
			}
			devid = getParameter("devid");
			phone = getParameter("phone");
		}
		@Override
		public boolean validate() {
			return (super.validate() && (StringUtils.isNotBlank(devid) || StringUtils.isNotBlank(phone)||devtype != 0));
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		TouchDevGetsRequest touchGetsReq = new TouchDevGetsRequest(request);
		int status = ErrorCode.SUCCESS;
		List<Touch> touchs = null;
		if(touchGetsReq.validate()) {
			touchs = omService.getTouchs(touchGetsReq.devid,touchGetsReq.phone, touchGetsReq.devtype, touchGetsReq.pageindex, touchGetsReq.pagesize);
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), touchGetsReq.getUserid(),touchGetsReq.getSessionId());
		ucResp.addAttribute("touchs", touchs);
		postProcess(baseRequest, response, ucResp);
	}
}

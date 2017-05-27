package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.City;
import com.roamtech.uc.model.Outlet;
import com.roamtech.uc.model.Phone;

public class OutletGetsService extends AbstractService {
	private class OutletGetsRequest extends UCRequest {
		private Integer city;
		private Integer province;
		OutletGetsRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("city");
			city = 0;
			if(StringUtils.isNotBlank(temp)) {
				city = Integer.parseInt(temp);
			}
			temp = getParameter("province");
			province = 0;
			if(StringUtils.isNotBlank(temp)) {
				province = Integer.parseInt(temp);
			}
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		OutletGetsRequest outletGetsReq = new OutletGetsRequest(request);
		int status = ErrorCode.SUCCESS;
		List<Outlet> outlets = null;
		if(outletGetsReq.validate()) {
			if(ucService.isSessionValid(outletGetsReq.getSessionId())) {
				outlets = ucService.getOutlets(outletGetsReq.province, outletGetsReq.city);
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), outletGetsReq.getUserid(),outletGetsReq.getSessionId());
		ucResp.addAttribute("outlets", outlets);
		postProcess(baseRequest, response, ucResp);
	}
}

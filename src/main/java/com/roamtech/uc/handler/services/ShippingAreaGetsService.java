package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.GuestRequest;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.ShippingArea;

public class ShippingAreaGetsService extends AbstractService {
	private class ShippingAreaGetsRequest extends GuestRequest {
		private Integer shippingId;
		ShippingAreaGetsRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("shippingid");
			shippingId = 0;
			if(StringUtils.isNotBlank(temp)) {
				shippingId = Integer.parseInt(temp);
			}
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		ShippingAreaGetsRequest shippingAreaGetsReq = new ShippingAreaGetsRequest(request);
		int status = ErrorCode.SUCCESS;
		List<ShippingArea> shippingAreas = null;
		if(shippingAreaGetsReq.validate()) {
			if(ucService.isSessionValid(shippingAreaGetsReq.getSessionId())) {
				shippingAreas = ucService.getShippingAreas(shippingAreaGetsReq.shippingId);
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), shippingAreaGetsReq.getUserid(),shippingAreaGetsReq.getSessionId());
		ucResp.addAttribute("shippingareas", shippingAreas);
		postProcess(baseRequest, response, ucResp);
	}

}

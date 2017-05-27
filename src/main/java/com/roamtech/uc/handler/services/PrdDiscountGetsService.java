package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.GuestRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.PrdDiscount;

public class PrdDiscountGetsService extends AbstractService {
	private class DiscountGetsRequest extends GuestRequest {
		private Long productid;
		DiscountGetsRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("productid");
			productid = -1L;			
			if(StringUtils.isNotBlank(temp)) {
				productid = Long.parseLong(temp);
			}
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		DiscountGetsRequest discountGetsReq = new DiscountGetsRequest(request);
		int status = ErrorCode.SUCCESS;
		List<PrdDiscount> discounts = null;
		if(discountGetsReq.validate()) {
			discounts = ucService.getPrdDiscounts(discountGetsReq.productid);
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), discountGetsReq.getUserid(),discountGetsReq.getSessionId());
		ucResp.addAttribute("prddiscounts", discounts);
		postProcess(baseRequest, response, ucResp);
	}
}

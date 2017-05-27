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

import com.roamtech.uc.model.PrdUnitPrice;

public class PrdUnitPriceGetsService extends AbstractService {
	private class PriceGetsRequest extends GuestRequest {
		private Long productid;
		PriceGetsRequest(HttpServletRequest request) {
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
		PriceGetsRequest priceGetsReq = new PriceGetsRequest(request);
		int status = ErrorCode.SUCCESS;
		List<PrdUnitPrice> prices = null;
		if(priceGetsReq.validate()) {
			prices = ucService.getPrdUnitPrices(priceGetsReq.productid);
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), priceGetsReq.getUserid(),priceGetsReq.getSessionId());
		ucResp.addAttribute("prdunitprices", prices);
		postProcess(baseRequest, response, ucResp);
	}
}

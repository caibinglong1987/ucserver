package com.roamtech.uc.handler.om.services;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.PrdUnitPrice;
import com.roamtech.uc.model.Touch;
import com.roamtech.uc.model.User;

public class PrdUnitPriceSetService extends AbstractService {
	private class PriceSetRequest extends UCRequest {
		private Long unitpriceid;
		private String startTime;
		private String endTime;
		private Long productid;
		private Double price;

		public PriceSetRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("unitpriceid");
			unitpriceid = null;
			if(temp != null) {
				unitpriceid = Long.valueOf(temp);
			}
			temp = getParameter("price");
			if(temp != null) {
				price = Double.valueOf(temp);
			}
			startTime = getParameter("effect_datetime");
			endTime = getParameter("failure_datetime");
			temp = getParameter("productid");
			productid = 0L;
			if(temp != null) {
				productid = Long.valueOf(temp);
			}
		}

		@Override
		public boolean validate() {
			return (super.validate() && productid != 0L && startTime != null && !startTime.isEmpty());
		}
		public PrdUnitPrice getPrdUnitPrice() {
			PrdUnitPrice unitPrice = new PrdUnitPrice();
			unitPrice.setId(unitpriceid);

			try {
				unitPrice.setEffectDatetime(sdf.parse(startTime));
				if(endTime != null) {
					unitPrice.setFailureDatetime(sdf.parse(endTime));
				}
			} catch (ParseException e) {
				
			}
			unitPrice.setProductid(productid);
			unitPrice.setPrice(price);
			return unitPrice;
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		PriceSetRequest priceSetReq = new PriceSetRequest(request);
		int status = ErrorCode.SUCCESS;
		PrdUnitPrice price = null;
		if(priceSetReq.validate()) {
			Integer usertype = omService.checkSessionValid(priceSetReq.getSessionId());
			if(usertype == -1) {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			} else if(usertype == User.ADMIN_USER || usertype == User.OPERATOR_USER) {
				price = omService.setPrdUnitPrice(priceSetReq.getPrdUnitPrice());
			} else {
				status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), priceSetReq.getUserid(),priceSetReq.getSessionId());
		ucResp.addAttribute("prdunitprice", price);
		postProcess(baseRequest, response, ucResp);
	}

}

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
import com.roamtech.uc.model.PrdDiscount;
import com.roamtech.uc.model.PrdUnitPrice;
import com.roamtech.uc.model.Touch;
import com.roamtech.uc.model.User;

public class PrdDiscountSetService extends AbstractService {
	private class DiscountSetRequest extends UCRequest {
		private Long discountid;
		private String startTime;
		private String endTime;
		private Long productid;
		private Integer quantity;
		private Integer discount;

		public DiscountSetRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("discountid");
			discountid = null;
			if(temp != null) {
				discountid = Long.valueOf(temp);
			}
			temp = getParameter("quantity");
			if(temp != null) {
				quantity = Integer.valueOf(temp);
			}
			temp = getParameter("discount");
			if(temp != null) {
				discount = Integer.valueOf(temp);
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
		public PrdDiscount getPrdDiscount() {
			PrdDiscount prdDiscount = new PrdDiscount();
			prdDiscount.setId(discountid);

			try {
				prdDiscount.setEffectDatetime(sdf.parse(startTime));
				if(endTime != null) {
					prdDiscount.setFailureDatetime(sdf.parse(endTime));
				}
			} catch (ParseException e) {
				
			}
			prdDiscount.setProductid(productid);
			prdDiscount.setQuantity(quantity);
			prdDiscount.setDiscount(discount);
			return prdDiscount;
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		DiscountSetRequest priceSetReq = new DiscountSetRequest(request);
		int status = ErrorCode.SUCCESS;
		PrdDiscount discount = null;
		if(priceSetReq.validate()) {
			Integer usertype = omService.checkSessionValid(priceSetReq.getSessionId());
			if(usertype == -1) {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			} else if(usertype == User.ADMIN_USER || usertype == User.OPERATOR_USER) {
				discount = omService.setPrdDiscount(priceSetReq.getPrdDiscount());
			} else {
				status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), priceSetReq.getUserid(),priceSetReq.getSessionId());
		ucResp.addAttribute("prddiscount", discount);
		postProcess(baseRequest, response, ucResp);
	}

}

package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.roamtech.uc.exception.UCBaseException;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.cache.model.OrderEVoucher;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Order;
import com.roamtech.uc.model.UserEVoucher;

public class EVoucherDiscountService extends AbstractService {
	private class EVDiscountRequest extends UCRequest {
		private Long orderId;
		private String evoucherId;

		public EVDiscountRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("orderid");
			orderId = -1L;
			if(StringUtils.isNotBlank(temp)) {
				orderId = Long.parseLong(temp);
			}
			evoucherId = getParameter("evoucherid");
			if (evoucherId == null) {
				evoucherId = "";
			}
		}

		@Override
		public boolean validate() {
			return (super.validate() && (orderId != -1L) && (evoucherId.length() > 0));
		}
	}
	
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		EVDiscountRequest evReq = new EVDiscountRequest(request);
		int status = ErrorCode.SUCCESS;
		OrderEVoucher orderev = null;
		UCResponse ucResp = null;
		if(evReq.validate()) {
			if(ucService.isSessionValid(evReq.getSessionId())) {
				try{
				// 订单使用优惠券，如果成功，返回关联记录
				orderev = ucService.discountEVoucher(evReq.getUserid(), evReq.orderId,evReq.evoucherId);
				if(orderev == null) {
					status = ErrorCode.ERR_EVOUCHER_USED;
				}
				} catch (UCBaseException e) {
					ucResp = e.getError();
				}
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		ucResp = ucResp !=null?ucResp:UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), evReq.getUserid(),evReq.getSessionId());
		if(orderev != null){
			ucResp.addAttribute("order", orderev.getOrder());
//			ucResp.addAttribute("myevoucher", orderev.getMyevoucher());
		}
		postProcess(baseRequest, response, ucResp);
	}
}

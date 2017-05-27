package com.roamtech.uc.handler.om.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Order;
import com.roamtech.uc.model.User;
import com.roamtech.uc.model.UserEVoucher;

public class DeliveryVoucherCompleteService extends AbstractService {
	private class DVCompleteRequest extends UCRequest {
		private Long evoucherSn;

		public DVCompleteRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("evoucher_sn");
			evoucherSn = 0L;
			if(temp != null) {
				evoucherSn = Long.valueOf(temp);
			}
		}

		@Override
		public boolean validate() {
			return (super.validate() && evoucherSn != 0L);
		}
	}
	private Boolean validOrder(Order order) {
		return (order != null) && (order.getOrderStatus()==Order.ORDER_STATUS_INIT||order.getOrderStatus()==Order.ORDER_STATUS_CONFIRMED)
				/*&&(order.getPayStatus()==Order.PAY_STATUS_PAYED)*/;
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		DVCompleteRequest dvverifyReq = new DVCompleteRequest(request);
		int status = ErrorCode.SUCCESS;
		if(dvverifyReq.validate()) {
			Integer usertype = omService.checkSessionValid(dvverifyReq.getSessionId());			
			if(usertype == -1) {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			} else if(usertype == User.ADMIN_USER || usertype == User.CLERK_USER) {
				omService.deliveryDataCard(dvverifyReq.getUserid(), dvverifyReq.evoucherSn);
			} else {
				status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), dvverifyReq.getUserid(),dvverifyReq.getSessionId());
		postProcess(baseRequest, response, ucResp);
	}

}

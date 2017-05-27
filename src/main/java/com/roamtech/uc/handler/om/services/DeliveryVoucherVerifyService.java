package com.roamtech.uc.handler.om.services;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.roamtech.uc.model.*;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;

public class DeliveryVoucherVerifyService extends AbstractService {
	private class DVVerifyRequest extends UCRequest {
		private Long evoucherSn;

		public DVVerifyRequest(HttpServletRequest request) {
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
	private Boolean validUEvoucher(UserEVoucher uev) {
		if(uev.getFailureDatetime()==null) {
			return true;
		}
		Date now = new Date();
		if(now.before(uev.getFailureDatetime())) {
			return true;
		}
		return false;
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		DVVerifyRequest dvverifyReq = new DVVerifyRequest(request);
		int status = ErrorCode.SUCCESS;
		UserEVoucher uev = null;
		User user = null;
		Order order = null;
		Integer quantity=0;
		if(dvverifyReq.validate()) {
			Integer usertype = omService.checkSessionValid(dvverifyReq.getSessionId());			
			if(usertype == -1) {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			} else if(usertype == User.ADMIN_USER || usertype == User.CLERK_USER) {
				uev = omService.verifyEVoucher(dvverifyReq.evoucherSn);
				if(uev == null) {
					status = ErrorCode.ERR_EVOUCHER_NOEXIST;
				} else if(uev.getUsedDatetime()!= null) {
					status = ErrorCode.ERR_EVOUCHER_USED;
				} else if(!validUEvoucher(uev)) {
					status = ErrorCode.ERR_EVOUCHER_INVALID;
				} else {
					if(uev.getEvoucherid() != EVoucher.EVOUCHER_MEMBERID) {
						order = ucService.getOrder(uev.getOrderid());
						if (!validOrder(order)) {
							order = null;
							status = ErrorCode.ERR_EVOUCHER_INVALID;
						} else {
							for (OrderDetail od : order.getOrderDetails()) {
								Product prd = ucService.getProduct(od.getProductid());
								if (prd.getTypeid() == PrdType.PRD_DATACARD || (prd.getBpackage() && prd.getTypeid() == PrdType.PRD_DATATRAFFIC)) {
									quantity += od.getQuantity();
								}
							}
							user = ucService.findOne(uev.getUserid());
						}
					} else {
						quantity = 1;
						user = ucService.findOne(uev.getUserid());
					}
				}
			} else {
				status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), dvverifyReq.getUserid(),dvverifyReq.getSessionId());
		ucResp.addAttribute("order", order);
		ucResp.addAttribute("user", user);
		ucResp.addAttribute("quantity", quantity);
		postProcess(baseRequest, response, ucResp);
	}

}

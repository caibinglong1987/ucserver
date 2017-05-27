package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.roamtech.uc.exception.UCBaseException;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Order;


public class OrderShippingService extends AbstractService {
	private class ODShipRequest extends UCRequest {
		private Long orderId;
		private Long shippingAddr;
		private Integer shippingId;
		private Boolean obtainvoucher;
		private Long outletid;
		private String obtainTime;

		public ODShipRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("shipping_address");
			shippingAddr = -1L;
			if(StringUtils.isNotBlank(temp)) {
				shippingAddr = Long.parseLong(temp);
			}
			temp = getParameter("orderid");
			orderId = -1L;
			if(StringUtils.isNotBlank(temp)) {
				orderId = Long.parseLong(temp);
			}
			temp = getParameter("shipping_id");
			shippingId = 0;
			if(StringUtils.isNotBlank(temp)) {
				shippingId = Integer.parseInt(temp);
			}
			temp = getParameter("obtainvoucher");
			obtainvoucher = false;
			if(StringUtils.isNotBlank(temp)) {
				obtainvoucher = Boolean.valueOf(temp);
			}
			if(obtainvoucher) {
				temp = getParameter("outletid");
				outletid = -1L;
				if(StringUtils.isNotBlank(temp)) {
					outletid = Long.parseLong(temp);
				}
				obtainTime = getParameter("obtaintime");
			}
		}

		@Override
		public boolean validate() {
			return (super.validate() && (orderId != -1L) && ((!obtainvoucher && shippingAddr != -1L && shippingId != 0)||(obtainvoucher && outletid != -1L && StringUtils.isNotBlank(obtainTime))));
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		ODShipRequest odSetReq = new ODShipRequest(request);
		int status = ErrorCode.SUCCESS;
		Order od = null;
		UCResponse ucResp = null;
		if(odSetReq.validate()) {
			if(ucService.isSessionValid(odSetReq.getSessionId())) {
				try {
					if (odSetReq.obtainvoucher) {
						od = ucService.updateOrder(odSetReq.getUserid(), odSetReq.orderId, odSetReq.obtainvoucher, odSetReq.outletid, odSetReq.obtainTime);
					} else {
						od = ucService.updateOrder(odSetReq.getUserid(), odSetReq.orderId, odSetReq.shippingId, odSetReq.shippingAddr);
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
		ucResp = ucResp!=null?ucResp:UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), odSetReq.getUserid(),odSetReq.getSessionId());
		ucResp.addAttribute("order", od);
		postProcess(baseRequest, response, ucResp);
	}
}

package com.roamtech.uc.handler.om.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.roamtech.uc.exception.UCBaseException;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Order;
import com.roamtech.uc.model.User;

public class OrderBindCardService extends AbstractService {
	private class OrderBindCardReq extends UCRequest {
		private Long orderid;
		private String simid;

		public OrderBindCardReq(HttpServletRequest request) {
			super(request);
			String temp = getParameter("orderid");
			orderid = -1L;
			if(temp != null) {
				orderid = Long.parseLong(temp);
			}
			simid = getParameter("simid");
		}

		@Override
		public boolean validate() {
			return (super.validate() && orderid != -1L && StringUtils.isNotBlank(simid));
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		OrderBindCardReq datacardBindReq = new OrderBindCardReq(request);
		int status = ErrorCode.SUCCESS;
		Order order = null;
		UCResponse ucResp = null;
		if(datacardBindReq.validate()) {
			Integer usertype = omService.checkSessionValid(datacardBindReq.getSessionId());
			if(usertype == -1) {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			} else if(usertype == User.ADMIN_USER || usertype == User.OPERATOR_USER) {
				try {
					order = ucService.orderBindDataCard(datacardBindReq.getUserid(), datacardBindReq.simid, datacardBindReq.orderid);
					if (order == null) {
						status = ErrorCode.ERR_DATACARD_NOEXIST;
					}
				} catch (UCBaseException ex) {
					ucResp = ex.getError();
				}
			} else {
				status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
			}	    
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		if(ucResp == null) {
			ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), datacardBindReq.getUserid(),datacardBindReq.getSessionId());
		} else {
			ucResp.setUserId(datacardBindReq.getUserid());
			ucResp.setSessionId(datacardBindReq.getSessionId());
		}
		ucResp.addAttribute("order", order);
		postProcess(baseRequest, response, ucResp);
	}

}

package com.roamtech.uc.handler.services;

import com.roamtech.uc.exception.UCBaseException;
import com.roamtech.uc.handler.UCService;
import com.roamtech.uc.handler.services.wrapper.CartSetRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Order;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OrderSubmitService extends AbstractService {
	private static final Logger LOG = LoggerFactory.getLogger(OrderSubmitService.class);	
	private class OrderSetRequest extends CartSetRequest {
		private Long[] cartIds;
		
		public OrderSetRequest(HttpServletRequest request, UCService ucService) {
			super(request,ucService);
			String temp = getParameter("cartids");
			if(StringUtils.isNotBlank(temp)) {
				if(temp.startsWith("[") && temp.endsWith("]")) {
					temp = temp.substring(1, temp.length()-1);
				}
				String[] scartIds = temp.split(",");
				cartIds = new Long[scartIds.length];
				for(int i=0;i<scartIds.length;i++) {
					cartIds[i] = Long.parseLong(scartIds[i].replace("\"", ""));
				}
			}
		}

		@Override
		public boolean validate() {
			return (super.validate() && (cartIds != null||productId != -1L));
		}		
	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		try {
			OrderSetRequest odSetReq = new OrderSetRequest(request, ucService);
			int status = ErrorCode.SUCCESS;
			Order od = null;
			UCResponse ucResp = null;
			if(odSetReq.validate()) {
				if(ucService.isSessionValid(odSetReq.getSessionId())) {
					try{
						if(odSetReq.cartIds != null) {
							od = ucService.submitOrder(odSetReq.getUserid(), odSetReq.getSessionId(), odSetReq.cartIds, odSetReq.getTenantId());
						} else {
							od = ucService.submitOrder(odSetReq.getUserid(), odSetReq.getSessionId(), odSetReq.getProductId(),odSetReq.getQuantity(), odSetReq.getCartPrdAttrs(), odSetReq.getTenantId());
						}
						if(od == null) {
							status = ErrorCode.ERR_TOUCHVOICE_OVERFLOW;
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
		} catch(Exception ex) {
			LOG.warn("ordersubmit exception",ex);
			throw new ServletException(ex);
		}
	}
}

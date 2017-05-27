package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Cart;

public class CartDeleteService extends AbstractService {
	private class CartDeleteRequest extends UCRequest {
		//private Long cartId;
		private Long[] cartIds;
		public CartDeleteRequest(HttpServletRequest request) {
			super(request);
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
			/*String temp = getParameter("cartid");
			cartId = -1L;
			if(temp != null) {
				cartId = Long.parseLong(temp);
			}	*/
		}

		@Override
		public boolean validate() {
			return (super.validate()&&(cartIds != null));
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		CartDeleteRequest cartSetReq = new CartDeleteRequest(request);
		int status = ErrorCode.SUCCESS;
		if(cartSetReq.validate()) {
			if(ucService.isSessionValid(cartSetReq.getSessionId())) {
			    /*if(cartSetReq.cartId == -1L) {
			    	ucService.resetMyCart(cartSetReq.getUserid(), cartSetReq.getSessionId());
			    } else {*/
			    	ucService.removeFromCarts(cartSetReq.cartIds);
			    //}
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	    
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), cartSetReq.getUserid(),cartSetReq.getSessionId());
		postProcess(baseRequest, response, ucResp);
	}
}

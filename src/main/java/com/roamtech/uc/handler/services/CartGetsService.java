package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Cart;

public class CartGetsService extends AbstractService {

	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		UCRequest cartGetsReq = new UCRequest(request);
		int status = ErrorCode.SUCCESS;
		List<Cart> carts = null;
		if(cartGetsReq.validate()) {
			if(ucService.isSessionValid(cartGetsReq.getSessionId())) {
				carts = ucService.findCart(cartGetsReq.getUserid(), cartGetsReq.getSessionId());
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), cartGetsReq.getUserid(),cartGetsReq.getSessionId());
		ucResp.addAttribute("carts", carts);
		postProcess(baseRequest, response, ucResp);
	}
}

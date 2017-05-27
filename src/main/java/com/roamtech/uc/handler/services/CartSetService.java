package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.cache.model.ODPrdAttr;
import com.roamtech.uc.handler.services.wrapper.CartSetRequest;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Cart;

public class CartSetService extends AbstractService {

	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		CartSetRequest cartSetReq = new CartSetRequest(request, ucService);
		int status = ErrorCode.SUCCESS;
		Cart cart = null;
		if(cartSetReq.validate()&& (cartSetReq.getProductId()!=-1L)) {
			if(ucService.isSessionValid(cartSetReq.getSessionId())) {
			    if(cartSetReq.getCartId() == -1L) {
			    	cart = ucService.addToCart(cartSetReq.getUserid(), cartSetReq.getSessionId(), cartSetReq.getProductId(), cartSetReq.getQuantity(), cartSetReq.getCartPrdAttrs());
			    } else {
			    	cart = ucService.updateCart(cartSetReq.getCartId(), cartSetReq.getProductId() ,cartSetReq.getQuantity(), cartSetReq.getCartPrdAttrs());
			    }
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	    
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), cartSetReq.getUserid(),cartSetReq.getSessionId());
		ucResp.addAttribute("cart", cart);
		postProcess(baseRequest, response, ucResp);
	}
}

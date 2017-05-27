package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.GuestRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Store;

public class StoreGetsService extends AbstractService {

	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		GuestRequest storeGetsReq = new GuestRequest(request);
		int status = ErrorCode.SUCCESS;
		List<Store> stores = null;
		if(storeGetsReq.validate()) {
			stores = ucService.getStores();
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), storeGetsReq.getUserid(),storeGetsReq.getSessionId());
		ucResp.addAttribute("stores", stores);
		postProcess(baseRequest, response, ucResp);
	}
}

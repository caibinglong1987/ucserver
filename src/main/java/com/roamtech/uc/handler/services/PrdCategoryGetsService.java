package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.GuestRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.PrdCategory;


public class PrdCategoryGetsService extends AbstractService {

	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		GuestRequest categoryGetsReq = new GuestRequest(request);
		int status = ErrorCode.SUCCESS;
		List<PrdCategory> categorys = null;
		if(categoryGetsReq.validate()) {
			categorys = ucService.getPrdCategorys();
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), categoryGetsReq.getUserid(),categoryGetsReq.getSessionId());
		ucResp.addAttribute("prdcategorys", categorys);
		postProcess(baseRequest, response, ucResp);
	}
}

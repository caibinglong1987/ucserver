package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Touch;

public class TouchGetsService extends AbstractService {

	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		UCRequest touchGetsReq = new UCRequest(request);
		int status = ErrorCode.SUCCESS;
		List<Touch> touchs = null;
		if(touchGetsReq.validate()) {
			if(ucService.isSessionValid(touchGetsReq.getSessionId())) {
				touchs = ucService.getTouchs(touchGetsReq.getUserid());
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), touchGetsReq.getUserid(),touchGetsReq.getSessionId());
		ucResp.addAttribute("touchs", touchs);
		postProcess(baseRequest, response, ucResp);
	}

}

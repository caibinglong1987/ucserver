package com.roamtech.uc.handler.om.services;

import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.DataCardTraffic;
import com.roamtech.uc.model.Order;
import com.roamtech.uc.model.User;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class DataTrafficGetsService extends AbstractService {
	private class DataTrafficGetsRequest extends UCRequest {
		private Integer group;
		private Integer pageindex;
		private Integer pageSize;
		DataTrafficGetsRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("group");
			group = 0;			
			if(temp != null) {
				group = Integer.parseInt(temp);
			}
			temp = getParameter("pageindex");
			pageindex = 0;		
			if(temp != null) {
				pageindex = Integer.parseInt(temp);
			}
			temp = getParameter("pagesize");
			pageSize = 10;		
			if(temp != null) {
				pageSize = Integer.parseInt(temp);
			}
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		DataTrafficGetsRequest orderGetsReq = new DataTrafficGetsRequest(request);
		int status = ErrorCode.SUCCESS;
		List<DataCardTraffic> dcts = null;
		if(orderGetsReq.validate()) {
			Integer usertype = omService.checkSessionValid(orderGetsReq.getSessionId());
			if(usertype == -1) {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			} else if(usertype == User.ADMIN_USER || usertype == User.OPERATOR_USER) {
				dcts = ucService.getDataTraffics(orderGetsReq.group, orderGetsReq.pageindex, orderGetsReq.pageSize);
			} else {
				status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), orderGetsReq.getUserid(),orderGetsReq.getSessionId());
		ucResp.addAttribute("datatraffics", dcts);
		postProcess(baseRequest, response, ucResp);
	}
}

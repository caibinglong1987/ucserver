package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Phone;
import com.roamtech.uc.model.UserAddress;

public class AddressGetsService extends AbstractService {

	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		UCRequest addrGetsReq = new UCRequest(request);
		int status = ErrorCode.SUCCESS;
		List<UserAddress> addresses = null;
		if(addrGetsReq.validate()) {
			if(ucService.isSessionValid(addrGetsReq.getSessionId())) {
				boolean validAddr=true;
				// 该属性可解释为只返回有效的地址，默认为true，客户端指定false，才返回所有的收货地址
				String svalidAddr = addrGetsReq.getParameter("valid_addr");
				if(StringUtils.isNotBlank(svalidAddr)) {
					validAddr = Boolean.parseBoolean(svalidAddr);
				}
				addresses = ucService.getAddresses(addrGetsReq.getUserid(),validAddr);
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), addrGetsReq.getUserid(),addrGetsReq.getSessionId());
		ucResp.addAttribute("addresses", addresses);
		postProcess(baseRequest, response, ucResp);
	}
}

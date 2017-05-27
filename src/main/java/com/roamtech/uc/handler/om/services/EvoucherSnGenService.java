package com.roamtech.uc.handler.om.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.EVoucher;
import com.roamtech.uc.model.User;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class EvoucherSnGenService extends AbstractService {
	private class EvoucherSnGenRequest extends UCRequest {
		private Long evoucherId;
		private Integer quantity;

		public EvoucherSnGenRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("evoucherid");
			evoucherId = 0L;
			if(StringUtils.isNotBlank(temp)) {
				evoucherId = Long.valueOf(temp);
			}
			quantity = 1;
			temp = getParameter("quantity");
			if(StringUtils.isNotBlank(temp)) {
				quantity = Integer.parseInt(temp);
			}
		}

		@Override
		public boolean validate() {
			return (super.validate() && evoucherId != 0L && quantity != 0);
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		EvoucherSnGenRequest evSnGenReq = new EvoucherSnGenRequest(request);
		int status = ErrorCode.SUCCESS;
		List<String> sns = null;
		if(evSnGenReq.validate()) {
			Integer usertype = omService.checkSessionValid(evSnGenReq.getSessionId());
			if(usertype == -1) {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			} else if(usertype == User.ADMIN_USER) {
				sns = omService.generateEVouchers(evSnGenReq.evoucherId,evSnGenReq.quantity);
			} else {
				status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), evSnGenReq.getUserid(),evSnGenReq.getSessionId());
		ucResp.addAttribute("evoucher_sns", sns);
		postProcess(baseRequest, response, ucResp);
	}

}

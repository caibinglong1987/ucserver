package com.roamtech.uc.handler.services;

import com.roamtech.uc.exception.UCBaseException;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.UserEVoucher;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EVoucherSNExchangeService extends AbstractService {
	private class EVSNExchangeRequest extends UCRequest {
		private String evoucherSn;

		public EVSNExchangeRequest(HttpServletRequest request) {
			super(request);
			evoucherSn = getParameter("evoucher_sn");
			if (evoucherSn == null) {
				evoucherSn = "";
			}
		}

		@Override
		public boolean validate() {
			return (super.validate() && (evoucherSn.length() > 0));
		}
	}
	
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		EVSNExchangeRequest evReq = new EVSNExchangeRequest(request);
		int status = ErrorCode.SUCCESS;
		UserEVoucher uev = null;
		UCResponse ucResp = null;
		if(evReq.validate()) {
			if(ucService.isSessionValid(evReq.getSessionId())) {
				try{
					uev = ucService.exchangeEVoucherSN(evReq.getUserid(), evReq.evoucherSn);
					if(uev == null) {
						status = ErrorCode.ERR_EVOUCHERSN_USED;
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
		ucResp = ucResp !=null?ucResp:UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), evReq.getUserid(),evReq.getSessionId());
		ucResp.addAttribute("evoucher", uev);
		postProcess(baseRequest, response, ucResp);
	}
}

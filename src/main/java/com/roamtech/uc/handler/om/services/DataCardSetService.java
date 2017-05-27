package com.roamtech.uc.handler.om.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.DataCard;
import com.roamtech.uc.model.Touch;
import com.roamtech.uc.model.User;

public class DataCardSetService extends AbstractService {
	private class DataCardSetRequest extends UCRequest {
		private Long datacardid;
		private String iccid;
		private String imsi;
		private String ki;
		private String imsiHK;
		private String kiHK;
        private Integer type;



		public DataCardSetRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("datacardid");
			datacardid = 0L;
			if(StringUtils.isNotBlank(temp)) {
				datacardid = Long.valueOf(temp);
			}
			iccid = getParameter("iccid");
			imsi = getParameter("imsi");
			ki = getParameter("ki");
			imsiHK = getParameter("imsi_hk");
			kiHK = getParameter("ki_hk");
            type = DataCard.EUROPE_DATACARD;
            temp = getParameter("type");
            datacardid = 0L;
            if(StringUtils.isNotBlank(temp)) {
                type = Integer.valueOf(temp);
            }
		}

		@Override
		public boolean validate() {
			return (super.validate() /*&& datacardid != 0L*/ && iccid != null && !iccid.isEmpty());
		}
		public DataCard getDataCard() {
			DataCard card = new DataCard();
			card.setId(datacardid);
			card.setIccid(iccid);
			card.setImsi(imsi);
			card.setKi(ki);
			card.setImsihk(imsiHK);
			card.setKihk(kiHK);
			card.setUserId(0L);
            card.setType(type);
			return card;
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		DataCardSetRequest datacardSetReq = new DataCardSetRequest(request);
		int status = ErrorCode.SUCCESS;
		DataCard dc = null;
		if(datacardSetReq.validate()) {
			Integer usertype = omService.checkSessionValid(datacardSetReq.getSessionId());
			if(usertype == -1) {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			} else if(omService.checkSessionValid(datacardSetReq.getSessionId())==User.ADMIN_USER) {
				dc = omService.setDataCard(datacardSetReq.getDataCard());
			} else {
				status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), datacardSetReq.getUserid(),datacardSetReq.getSessionId());
		ucResp.addAttribute("datacard", dc);
		postProcess(baseRequest, response, ucResp);
	}

}

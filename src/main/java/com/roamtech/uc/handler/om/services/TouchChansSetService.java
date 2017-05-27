package com.roamtech.uc.handler.om.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Touch;
import com.roamtech.uc.model.TouchChans;
import com.roamtech.uc.model.User;

public class TouchChansSetService extends AbstractService {
	private class TouchChansSetRequest extends UCRequest {
		private Long devid;
		private JSONArray chans;


		public TouchChansSetRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("devid");
			devid = 0L;
			if(temp != null) {
				devid = Long.valueOf(temp);
			}
			chans = getParameters("chans");			
		}

		@Override
		public boolean validate() {
			return (super.validate() && devid != 0L && chans != null && !chans.isEmpty());
		}
		public List<TouchChans> getTouchChans() {
			List<TouchChans> lchans = new ArrayList<TouchChans>();
			
			for(Object objchan:chans) {
				TouchChans chan = new TouchChans();
				
				JSONObject jsonchan = (JSONObject)objchan;
				chan.setId(jsonchan.getLong("chanid"));
				chan.setDevId(devid);
				chan.setSubid(jsonchan.getInteger("subid"));
				chan.setPhone(jsonchan.getString("phone"));
				chan.setDomain(jsonchan.getString("domain"));
				Long userId = jsonchan.getLong("userid");
				chan.setUserId(userId==null?0L:userId);
				lchans.add(chan);
			}
			return lchans;
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		TouchChansSetRequest touchSetReq = new TouchChansSetRequest(request);
		int status = ErrorCode.SUCCESS;
		List<TouchChans> chans = null;
		if(touchSetReq.validate()) {
			Integer usertype = omService.checkSessionValid(touchSetReq.getSessionId());
			if(usertype == -1) {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			} else if(usertype==User.ADMIN_USER) {
				chans = omService.setTouchChans(touchSetReq.getTouchChans());
			} else {
				status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), touchSetReq.getUserid(),touchSetReq.getSessionId());
		ucResp.addAttribute("touchchans", chans);
		postProcess(baseRequest, response, ucResp);
	}

}

package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.credtrip.gateway.CredtripService;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.User;
import com.roamtech.uc.model.UserThirdParty;

public class CredtripOpenService extends AbstractService {
	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "http://www.roam-tech.com/uc/services/credtrip/notify";

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String return_url = "http://www.roam-tech.com/uc/services/credtrip/pay_success.html";

	private String credtripBuildReq(User user,String sessionId) {
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("version", "1.0");
        sParaTemp.put("product", "01");
        sParaTemp.put("merchantId", "2016010003");
        sParaTemp.put("mobile", user.getPhone());
		sParaTemp.put("userId", user.getUserId().toString());
		sParaTemp.put("notifyUrl", notify_url);
		sParaTemp.put("returnUrl", return_url);


		
		//建立请求
		String sHtmlText = CredtripService.buildRequest(sParaTemp,"post","确认");
		return sHtmlText;
	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		UCRequest ucReq = new UCRequest(request);
		int status = ErrorCode.SUCCESS;
		UserThirdParty utp = null;
		String html = null;
		if(ucReq.validate()) {
			if(ucService.isSessionValid(ucReq.getSessionId())) {
				utp = ucService.findCredtripAccount(ucReq.getUserid());
				if(utp == null) {
					html = credtripBuildReq(ucService.findOne(ucReq.getUserid()),ucReq.getSessionId());
				} else {
					status = ErrorCode.ERR_CREDTRIPACCOUNT_EXIST;
				}
				/*	response.setStatus(200);
					response.setHeader("Access-Control-Allow-Origin", "*");
					response.setHeader("Content-Type", "text/html;charset=UTF-8");
					
				    response.getWriter().write(credtripBuildReq(ucService.findOne(ucReq.getUserid()),ucReq.getSessionId()));
				    return;*/
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	    
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), ucReq.getUserid(),ucReq.getSessionId());
		if(utp != null) {
			ucResp.addAttribute("credtripaccount", utp);
		}
		if(html != null) {
			ucResp.addAttribute("html", html);
		}
		postProcess(baseRequest, response, ucResp);
	}
}

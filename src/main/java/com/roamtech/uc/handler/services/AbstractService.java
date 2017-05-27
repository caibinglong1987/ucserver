package com.roamtech.uc.handler.services;

import com.roamtech.uc.handler.ServiceHandler;
import com.roamtech.uc.handler.UCService;
import com.roamtech.uc.handler.om.OMService;
import com.roamtech.uc.handler.services.wrapper.AbstractResponse;
import org.eclipse.jetty.server.Request;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

public abstract class AbstractService implements ServiceHandler {
	@Autowired
	protected UCService ucService;
	@Autowired
	protected OMService omService;
	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
//		String sessionId = (String) request.getAttribute("sessionid");
//		if (StringUtils.isNotBlank(sessionId)) {
//			Session session = ucService.findSession(sessionId);
//			if (session != null) {
//				request.setAttribute("userid", session.getUserId());
//				request.setAttribute("tenantid", session.getTenantId());
//				request.setAttribute("clientid", session.getClientId());
//			}
//		}
		doHandle(target, baseRequest, request, response);
	}
	public abstract void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
	
	public void postProcess(Request baseRequest, HttpServletResponse response, AbstractResponse hsidResponse)
			throws IOException, ServletException {
		postProcess(baseRequest, response, hsidResponse, null);
	}
	public void postProcess(Request baseRequest, HttpServletResponse response, AbstractResponse hsidResponse, Map<String,Object> extraLog)
			throws IOException, ServletException {
		String responseContent = hsidResponse.toString();
		response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setStatus(200);
	    response.getWriter().write(responseContent);
		if (baseRequest != null) {
			for(Map.Entry<String, Object> attr:hsidResponse.logInfo().entrySet()) {
				baseRequest.setAttribute(attr.getKey(), attr.getValue());
			}
			if(extraLog != null) {
				for(Map.Entry<String, Object> attr:extraLog.entrySet()) {
					baseRequest.setAttribute(attr.getKey(), attr.getValue());
				}
			}
			int index = responseContent.indexOf("\"password\":");
    		if(index>0) {
	    		int endIndex = responseContent.indexOf(",",index);
	    		if(endIndex < 0)
	    			endIndex = responseContent.length();
	    		responseContent = responseContent.replace(responseContent.subSequence(index, endIndex+1),"");
    		}
		    baseRequest.setAttribute("response", responseContent);
	    }
	}
	protected void sendRedirect(Request baseRequest,HttpServletResponse response,String url) throws IOException {
		response.addHeader("Pragma", "no-cache");
		response.sendRedirect(url);
		baseRequest.setHandled(true);
	}
}

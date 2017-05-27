package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.roamtech.uc.handler.services.wrapper.GuestRequest;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Homepage;

public class HomepageGetsService extends AbstractService {
	Pattern pattern= Pattern.compile("(r|R)oamPhone/(\\d.\\d.\\d{1,2})");
	private class HomepageGetsRequest extends GuestRequest {
		private Integer type;
		private String location;
		HomepageGetsRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("type");
			type = 1;
			if(StringUtils.isNotBlank(temp)) {
				type = Integer.parseInt(temp);
			}
			location = getParameter("location");
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		HomepageGetsRequest ucRequest = new HomepageGetsRequest(request);
		int status = ErrorCode.SUCCESS;
		List<Homepage> homepageList = null;
		if(ucRequest.validate()) {
			String version = null;
			if(ucRequest.type == Homepage.MALL_HOMEPAGE) {
				String agent = request.getHeader(HttpHeaders.USER_AGENT);
				if (agent != null) {
					if(agent.contains("iPhone")) {
						Matcher m = pattern.matcher(agent);
						if(m.find()) {
							version = m.group().substring(10);
						}
					}
				}
			}
			homepageList = ucService.getHomepages(ucRequest.type, ucRequest.location, version);
		} else {
			status = ErrorCode.ERR_SESSIONID_INVALID;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), ucRequest.getUserid(),ucRequest.getSessionId());
		ucResp.addAttribute("homepages", homepageList);
		postProcess(baseRequest, response, ucResp);
	}

}

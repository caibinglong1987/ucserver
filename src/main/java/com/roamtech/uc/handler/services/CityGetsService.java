package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.GuestRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.City;

public class CityGetsService extends AbstractService {
	private class CityGetsRequest extends GuestRequest {
		private Integer type;
		private Long pid;
		CityGetsRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("type");
			type = 0;
			if(StringUtils.isNotBlank(temp)) {
				type = Integer.parseInt(temp);
			}
			temp = getParameter("pid");
			pid = -1L;
			if(StringUtils.isNotBlank(temp)) {
				pid = Long.parseLong(temp);
			}
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		CityGetsRequest cityGetsReq = new CityGetsRequest(request);
		int status = ErrorCode.SUCCESS;
		List<City> cities = null;
		if(cityGetsReq.validate()) {
			if(cityGetsReq.type == City.PROVINCE_TYPE) {
				cities = ucService.getProvinces();
			} else {
				cities = ucService.getCitysByParent(cityGetsReq.pid);
			}
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), cityGetsReq.getUserid(),cityGetsReq.getSessionId());
		ucResp.addAttribute("cities", cities);
		postProcess(baseRequest, response, ucResp);
	}
}

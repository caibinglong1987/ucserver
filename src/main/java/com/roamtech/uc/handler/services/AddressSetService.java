package com.roamtech.uc.handler.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.cache.model.Session;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.UserAddress;


public class AddressSetService extends AbstractService {
	private class AddressSetRequest extends UCRequest {
		private String consignee;
		private String email;
		private String mobile;
		private Integer country;
		private Integer province;
		private Integer city;
		private Integer district;
		private String address;
		private String zipcode;
		private Long addressId;
		private Boolean defaultaddr;

		public AddressSetRequest(HttpServletRequest request) {
			super(request);
			email = getParameter("email");
			mobile = getParameter("mobile");
			String temp = getParameter("country");
			country = 1;
			if(StringUtils.isNotBlank(temp)) {
				country = Integer.parseInt(temp);
			}
			temp = getParameter("province");
			province = 0;
			if(StringUtils.isNotBlank(temp)) {
				province = Integer.parseInt(temp);
			}
			temp = getParameter("city");
			city = 0;
			if(StringUtils.isNotBlank(temp)) {
				city = Integer.parseInt(temp);
			}
			temp = getParameter("district");
			district = 0;
			if(StringUtils.isNotBlank(temp)) {
				district = Integer.parseInt(temp);
			}
			temp = getParameter("address_id");
			addressId = 0L;
			if(StringUtils.isNotBlank(temp)) {
				addressId = Long.parseLong(temp);
			}
			defaultaddr = false;
			temp = getParameter("defaultaddr");
			if(StringUtils.isNotBlank(temp)) {
				defaultaddr = Boolean.parseBoolean(temp);
			}
			consignee = getParameter("consignee");
			address = getParameter("address");
			zipcode = getParameter("zipcode");
		}

		@Override
		public boolean validate() {
			return (addressId != null)||(StringUtils.isNotBlank(consignee) && StringUtils.isNotBlank(address) && StringUtils.isNotBlank(mobile) && country !=0 && province !=0 && city != 0);
		}

		public UserAddress getUserAddress() {
			UserAddress addr = new UserAddress();
			addr.setId(addressId);
			addr.setEmail(email);
			addr.setMobile(mobile);
			addr.setConsignee(consignee);
			addr.setCountry(country);
			addr.setProvince(province);
			addr.setCity(city);
			addr.setDistrict(district);
			addr.setAddress(address);
			addr.setZipcode(zipcode);
			addr.setUserId(userid);
			addr.setDefaultaddr(defaultaddr);
			addr.setStatus(UserAddress.NORMAL_STATUS);
			return addr;
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		AddressSetRequest addrSetReq = new AddressSetRequest(request);
		UserAddress addr = null;
		int status = ErrorCode.SUCCESS;
		if(addrSetReq.validate()) {
			if(ucService.isSessionValid(addrSetReq.getSessionId())) {
				addr = ucService.addOrUpdateUserAddress(addrSetReq.getUserid(), addrSetReq.getUserAddress());
			} else {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), addrSetReq.getUserid(),addrSetReq.getSessionId());
		ucResp.addAttribute("addr", addr);
		postProcess(baseRequest, response, ucResp);
	}

}

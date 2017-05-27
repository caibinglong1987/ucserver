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
import java.util.Set;

public class EvoucherCreateService extends AbstractService {
	private class EvoucherCreateRequest extends UCRequest {
		private EVoucher voucher;
		private List<Long> prdids;

		public EvoucherCreateRequest(HttpServletRequest request) {
			super(request);
			JSONObject evoucher = getParametersJSON("evoucher");
			String productids = getParameter("productids");
			if(StringUtils.isNotBlank(productids)) {
				prdids = JSON.parseObject(productids, new TypeReference<List<Long>>() {
				});
			}
			voucher = new EVoucher();
			voucher.setMoney(evoucher.getDouble("money"));
			voucher.setMinamount(evoucher.getDouble("min_amount"));
			voucher.setMaxamount(evoucher.getDouble("max_amount"));
			voucher.setEffectDatetime(evoucher.getDate("effect_datetime"));
			voucher.setFailureDatetime(evoucher.getDate("failure_datetime"));
			voucher.setBackground(evoucher.getString("background"));
			voucher.setName(evoucher.getString("name"));
			voucher.setDescription(evoucher.getString("description"));
			voucher.setType(evoucher.getInteger("type"));
			voucher.setRepeat(evoucher.getBoolean("repeatable"));
			voucher.setLocation(evoucher.getString("location"));
			voucher.setImage(evoucher.getString("image"));
			voucher.setLogo(evoucher.getString("logo"));
			voucher.setCreateTime(new Date());
		}

		@Override
		public boolean validate() {
			return (super.validate() && StringUtils.isNotBlank(voucher.getName()) && voucher.getType()!=null && voucher.getMoney() != null);
		}

	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		EvoucherCreateRequest evCreateReq = new EvoucherCreateRequest(request);
		int status = ErrorCode.SUCCESS;
		EVoucher eVoucher = null;
		if(evCreateReq.validate()) {
			Integer usertype = omService.checkSessionValid(evCreateReq.getSessionId());
			if(usertype == -1) {
				status = ErrorCode.ERR_SESSIONID_INVALID;
			} else if(usertype == User.ADMIN_USER) {
				eVoucher = omService.createEVoucher(evCreateReq.voucher,evCreateReq.prdids);
			} else {
				status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
			}	
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), evCreateReq.getUserid(),evCreateReq.getSessionId());
		ucResp.addAttribute("evoucher", eVoucher);
		postProcess(baseRequest, response, ucResp);
	}

}

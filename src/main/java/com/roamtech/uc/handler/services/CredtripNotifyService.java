package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.credtrip.gateway.CredtripService;
import com.roamtech.uc.handler.services.wrapper.GuestRequest;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Order;
import com.roamtech.uc.model.OrderDetail;

public class CredtripNotifyService extends AbstractService {
	
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		GuestRequest req = new GuestRequest(request);
		
		//获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = req.getParametersMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			params.put(name, req.getParameter(name));
		}
		
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		//商户订单号

		String accountNo = req.getParameter("accountNo");
		String userId = req.getParameter("userId");
		String mobile = req.getParameter("mobile");

		if(CredtripService.verify(params)){//验证成功
			ucService.CredtripOpenAccount(accountNo, userId, mobile);
				
				//请不要修改或删除
			response.setStatus(200);
			response.setHeader("Content-Type", "text/html;charset=UTF-8");
		    response.getWriter().write("success");
		    return;
			//////////////////////////////////////////////////////////////////////////////////////////
		}else{//验证失败
			response.setStatus(200);
			response.setHeader("Content-Type", "text/html;charset=UTF-8");
		    response.getWriter().write("fail");
		    return;
		}
	}
}

package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.GuestRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Product;

public class ProductGetsService extends AbstractService {
	private class ProductGetsRequest extends GuestRequest {
		private Long storeid;
		private Long categoryid;
		private Long brandid;
		private String name;
		private Long[] prdIds;
		private Boolean summary = false;
		ProductGetsRequest(HttpServletRequest request) {
			super(request);
			String temp = getParameter("storeid");
			storeid = -1L;
			if(StringUtils.isNotBlank(temp)) {
				storeid = Long.parseLong(temp);
			}
			temp = getParameter("categoryid");
			categoryid = -1L;
			if(StringUtils.isNotBlank(temp)) {
				categoryid = Long.parseLong(temp);
			}
			temp = getParameter("brandid");
			brandid = -1L;
			if(StringUtils.isNotBlank(temp)) {
				brandid = Long.parseLong(temp);
			}
			name = getParameter("name");
			prdIds = null;
			temp = getParameter("productids");
			if(StringUtils.isNotBlank(temp)) {
				if(temp.startsWith("[") && temp.endsWith("]")) {
					temp = temp.substring(1, temp.length()-1);
				}
				String[] sprdIds = temp.split(",");
				prdIds = new Long[sprdIds.length];
				for(int i=0;i<sprdIds.length;i++) {
					prdIds[i] = Long.parseLong(sprdIds[i].replace("\"", ""));
				}
			} 
			temp = getParameter("summary");
			if (StringUtils.isNotBlank(temp) && temp.equalsIgnoreCase("true")) {
				summary = true;
			}
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		ProductGetsRequest prdGetsReq = new ProductGetsRequest(request);
		int status = ErrorCode.SUCCESS;
		List<Product> prds = null;
		if(prdGetsReq.validate()) {
			if(prdGetsReq.prdIds != null && prdGetsReq.prdIds.length>0) {
				prds = ucService.getProducts(prdGetsReq.prdIds);
			} else if(prdGetsReq.name == null || prdGetsReq.name.trim().isEmpty()) {
				prds = ucService.getProducts(prdGetsReq.storeid, prdGetsReq.categoryid, prdGetsReq.brandid);
			} else {
				prds = ucService.getProductsByName(prdGetsReq.name);
			}
		} else {
			status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
		}
		UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), prdGetsReq.getUserid(),prdGetsReq.getSessionId());
		// 如果只要摘要信息，把某些字段设为null，这样Json序列化时就会忽略这些字段
		if (prdGetsReq.summary) {
			SimplePropertyPreFilter filter = new SimplePropertyPreFilter(); // 构造方法里，也可以直接传需要序列化的属性名字
			filter.getExcludes().add("prdattrs");
			filter.getExcludes().add("packages");
			ucResp.setFilter(filter);
			/*for (Product product : prds) {
				product.setPrdAttrs(null);
				product.setPackages(null);
			}*/
		}
		ucResp.addAttribute("products", prds);
		postProcess(baseRequest, response, ucResp);
	}
}

package com.credtrip.gateway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.alipay.config.AlipayConfig;

import com.credtrip.config.Merchant;
import com.credtrip.util.SignUtil;




/**
 * Created by eric on 2016-06-23.
 */

public class CredtripService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String CREDTRIP_GATEWAY_NEW = "https://api.credtrip.com/gateway/gw/open";//"https://api-test.credtrip.com/gateway/gw/open";
	
	public static String buildRequestMysign(Map<String, String> sPara) {
		Merchant merchant = new Merchant();
        //merchant.setCode(merchantId);
		merchant.setSignType("MD5");
		//根据请求参数进行加签，签名规则为按字符升序排序，并将字段中的值进行加签约
        Set<String> names = sPara.keySet();//request.getParameterNames();
        List<String> params = new ArrayList<String>();
        params.addAll(names);
        Collections.sort(params);
        StringBuffer sb = new StringBuffer();
        String value;
        StringBuffer printParams = new StringBuffer();

        for (String param : params) {
            //printParams.append(param).append("="+sPara.get(param)).append("&");
            if (param.equals("sign") || param.equals("signType")) {
                continue;
            }
            value = sPara.get(param);
            if (StringUtils.isNotEmpty(value)) {
                sb.append(value);
            }
        }
        //logger.info(String.format("进入验签拦截器,请求参数为[%s]", printParams));
        //String signType = sPara.get("signType");
        
        String sign = SignUtil.sign(sb.toString(), merchant);
        return sign;
    }
    public static Map<String, String> buildRequestPara(Map<String, String> sParaTemp) {
        //生成签名结果
        String mysign = buildRequestMysign(sParaTemp);

        //签名结果与签名方式加入请求提交参数组中
        sParaTemp.put("sign", mysign);
        sParaTemp.put("signType", "MD5");

        return sParaTemp;
    }
	 public static String buildRequest(Map<String, String> sParaTemp, String strMethod, String strButtonName) {
	        //待请求参数数组
	        Map<String, String> sPara = buildRequestPara(sParaTemp);
	        List<String> keys = new ArrayList<String>(sPara.keySet());

	        StringBuffer sbHtml = new StringBuffer();
	        sbHtml.append("<!DOCTYPE html><html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">"
	        		+ "<title>信程分期信用钱包</title></head><body>");
	        sbHtml.append("<form id=\"credtripsubmit\" name=\"credtripsubmit\" action=\"" + CREDTRIP_GATEWAY_NEW
	                      + "\" method=\"" + strMethod
	                      + "\">");

	        for (int i = 0; i < keys.size(); i++) {
	            String name = (String) keys.get(i);
	            String value = (String) sPara.get(name);

	            sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
	            //sbHtml.append(name+"<input type=\"text\" name=\"" + name + "\" value=\"" + value + "\"/><br/>");
	        }

	        //submit按钮控件请不要含有name属性
	        sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");
	        //sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\"></form>");
	        sbHtml.append("<script>document.forms['credtripsubmit'].submit();</script>");
	        sbHtml.append("</body></html>");
	        return sbHtml.toString();
	    }
	public static boolean verify(Map<String, String> params) {
		String sign = buildRequestMysign(params);		
		return sign.equalsIgnoreCase(params.get("sign"));
	}
}

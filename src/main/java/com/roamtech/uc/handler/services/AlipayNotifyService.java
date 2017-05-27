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

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.WebUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Order;
import com.roamtech.uc.model.OrderDetail;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.alipay.api.AlipayConstants.CHARSET_UTF8;

public class AlipayNotifyService extends AbstractService {
	private static final Logger LOG = LoggerFactory.getLogger(AlipayNotifyService.class);
	private Boolean ismobilepay = false;
	protected Map<String,String> getParams(HttpServletRequest request) {
		//获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);//WebUtils.decode(valueStr));
		}
		return params;
	}

	private boolean verify(String target, Map<String,String> params) {
		if(target.equals("/uc/services/alipay/notify")) {
			ismobilepay = false;
			return AlipayNotify.verify(params);
		} else if(target.equals("/uc/services/malipay/notify")) {
			try {
				ismobilepay = true;
				return AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, CHARSET_UTF8);
			} catch (AlipayApiException e) {
				LOG.warn("rsaCheckV1 failed",e);
				return false;
			}
		}
		return false;
	}
	@Override
	public void doHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		Map<String,String> params = getParams(request);
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		//商户订单号

		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

		//支付宝交易号

		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

		//交易状态
		String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
		response.setStatus(200);
		response.setHeader("Content-Type", "text/html;charset=UTF-8");
		if(verify(target, params)){//验证成功
			//////////////////////////////////////////////////////////////////////////////////////////
			//请在这里加上商户的业务逻辑程序代码

			//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
			
			if(trade_status.equals("TRADE_FINISHED")){
				//判断该笔订单是否在商户网站中已经做过处理
					//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
					//如果有做过处理，不执行商户的业务程序
					
				//注意：
				//退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
			} else if (trade_status.equals("TRADE_CLOSED")){
				//判断该笔订单是否在商户网站中已经做过处理
				//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
				//如果有做过处理，不执行商户的业务程序

				//注意：
				//退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
				ucService.refundOrder(Long.parseLong(out_trade_no), trade_no,Double.valueOf(request.getParameter("refund_fee")));
			} else if (trade_status.equals("TRADE_SUCCESS")){
				//判断该笔订单是否在商户网站中已经做过处理
					//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
					//如果有做过处理，不执行商户的业务程序
				boolean failed  = false;
				if(!params.get("seller_id").equals(AlipayConfig.seller_id)) {
					failed = true;
					LOG.warn("seller_id not match "+params.get("seller_id")+" expected "+AlipayConfig.seller_id);
				}
				if(!failed) {
					if (ismobilepay) {
						if (!params.get("app_id").equals(AlipayConfig.ALIPAY_APPID)) {
							failed = true;
							LOG.warn("app_id not match "+params.get("app_id")+" expected "+AlipayConfig.ALIPAY_APPID);
						}
					}
				}
				if(!failed) {
					ucService.paidOrder(Long.parseLong(out_trade_no), trade_no, Double.valueOf(ismobilepay ? request.getParameter("total_amount") : request.getParameter("total_fee")));
				}
				//注意：
				//付款完成后，支付宝系统发送该交易状态通知
			}

			//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
				
				//请不要修改或删除
			response.setStatus(200);
			response.setHeader("Content-Type", "text/html;charset=UTF-8");
		    response.getWriter().write("success");
		    return;
			//////////////////////////////////////////////////////////////////////////////////////////
		}else{//验证失败
		    response.getWriter().write("fail");
		    return;
		}
	}
	/*public static void main(String[] args) {
		String urlparams = "notify_id=61bcbe362da03d2b2c872ae48727223n3m&gmt_payment=2016-08-23+23%3A44%3A07&notify_type=trade_status_sync&sign=vd1SI5Q6mPEF9z4cZY0W2AuzAPVwHEb6y%2Fdsmp%2FLN7rQkwbFv3mjPbfXrDWsUdPs%2BciKVh8brXeOYMPGkZgP8wCCDryDj15o1S6qPsGT%2FADP%2B23mKKpCY7U44DoLAPhvO3UQXgscc5AaPn4V0znlS8bbIwgvneuUvpGsiFch1W8%3D&trade_no=2016082321001004920299350515&buyer_id=2088702974594924&body=217%3A7a79a896-7e28-4170-9cf3-bc9654efd20f&app_id=2016070601585890&gmt_create=2016-08-23+23%3A44%3A05&out_trade_no=1650&seller_id=2088421208349001&notify_time=2016-08-23+23%3A44%3A07&subject=%E5%85%A8%E7%90%83%E4%B8%8A%E7%BD%91%E5%8D%A1%2C&trade_status=TRADE_SUCCESS&total_amount=99.00&sign_type=RSA";
		MultiMap<String> mapvalues = new MultiMap<String>();
		UrlEncoded.decodeTo(urlparams, mapvalues, "UTF-8", 1000);
		Map<String,String> params = new HashMap<String,String>();

		for (Iterator iter = mapvalues.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			*//*String[] values = (String[]) mapvalues.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}*//*
			String valueStr = mapvalues.getString(name);
					//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);//WebUtils.decode(valueStr));
		}
		try {
			*//*String sign = params.get("sign");
			System.out.println(sign);
			sign = sign.replace(' ','+');
			System.out.println(sign);
			params.put("sing",sign);*//*
			boolean result = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, CHARSET_UTF8);
			System.out.println(result);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}

	}*/
}

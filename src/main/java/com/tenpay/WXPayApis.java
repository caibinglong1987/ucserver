package com.tenpay;

import com.alibaba.fastjson.JSON;
import com.roamtech.uc.handler.UCService;
import com.roamtech.uc.model.Order;
import com.roamtech.uc.util.HttpClientManager;
import com.tenpay.util.ConstantUtil;
import com.tenpay.util.PayCommonUtil;
import com.tenpay.util.XMLUtil;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by admin03 on 2016/8/28.
 */
@Component("wxPayApis")
public class WXPayApis implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(WXPayApis.class);
    @Autowired
    private HttpClientManager httpClientManager;
    private String httpClientName="UCSERVER_HTTP_CLIENT";
    private HttpClient _client;
    @Autowired
    UCService ucService;

    public SortedMap<String, Object> unifiedOrder(String userId, String proId, String orderId,
                                                  int price, String wxpayNotifyUrl, boolean isApp, String openid) {
        try {
            logger.info("统一下定单开始");

            // 设置订单参数
            SortedMap<String, Object> parameters = prepareOrder(proId, orderId,
                    price,wxpayNotifyUrl, isApp, openid);
            parameters.put("sign",
                    PayCommonUtil.createSign("UTF-8", parameters));// sign签名 key
            String requestXML = PayCommonUtil.getRequestXml(parameters);// 生成xml格式字符串
            String responseStr = httpClientManager.sendReceive(_client,ConstantUtil.UNIFIED_ORDER_URL,null,requestXML, MimeTypes.TEXT_XML_UTF_8);// 带上post
            logger.info(responseStr);
            // 检验API返回的数据里面的签名是否合法，避免数据在传输的过程中被第三方篡改
            if (!PayCommonUtil.checkIsSignValidFromResponseString(responseStr)) {
                logger.error("微信统一下单失败,签名可能被篡改");
                return null;
            }
            // 解析结果 resultStr
            SortedMap<String, Object> resutlMap = XMLUtil
                    .doXMLParse(responseStr);
            if (resutlMap != null
                    && ConstantUtil.FAIL.equals(resutlMap.get("return_code"))) {
                logger.error("微信统一下单失败,订单编号:" + orderId + ",失败原因:"
                        + resutlMap.get("return_msg"));
                return null;
            }
            // 获取到 prepayid
            // 商户系统先调用该接口在微信支付服务后台生成预支付交易单，返回正确的预支付交易回话标识后再在APP里面调起支付。
            SortedMap<String, Object> map = buildClientJson(resutlMap, isApp);
            map.put("outTradeNo", orderId);
            logger.info("统一下定单结束");
            return map;
        } catch (Exception e) {
            logger.error(
                    "com.tenpay.WXPayApis receipt(String userId,String proId,String ip)：{},{}",
                    userId + "-" + proId , e.getMessage());
            return null;
        }

    }

    private SortedMap<String, Object> prepareOrder(String prodId, String orderId,
                                                   int price,String wxpayNotifyUrl, boolean isApp, String openid) {
        SortedMap<String, Object> oparams = new TreeMap<String, Object>();
        oparams.put("appid", isApp?ConstantUtil.APP_ID:ConstantUtil.MPAPP_ID);// 服务号的应用号
        oparams.put("body", prodId);// 商品描述
        oparams.put("mch_id", isApp?ConstantUtil.PARTNER:ConstantUtil.MPPARTNER);// 商户号 ？
        oparams.put("nonce_str", PayCommonUtil.CreateNoncestr());// 16随机字符串(大小写字母加数字)
        oparams.put("out_trade_no", orderId);// 商户订单号
        oparams.put("total_fee", price+"");// 银行币种 price
        oparams.put("spbill_create_ip", "120.55.193.0");// IP地址
        oparams.put("notify_url", wxpayNotifyUrl); // 微信回调地址
        oparams.put("trade_type", isApp?ConstantUtil.TRADE_TYPE:ConstantUtil.MPTRADE_TYPE);// 支付类型 app
        if(!isApp) {
            oparams.put("openid", openid);
        }

        return oparams;
    }

    /**
     * 生成预付快订单完成，返回给android,ios唤起微信所需要的参数。
     *
     * @param resutlMap
     * @return
     * @throws UnsupportedEncodingException
     */
    private SortedMap<String, Object> buildClientJson(
            Map<String, Object> resutlMap, boolean isApp) throws UnsupportedEncodingException {
        // 获取微信返回的签名

        /**
         * backObject.put("appid", appid);
         *
         * backObject.put("noncestr", payParams.get("noncestr"));
         *
         * backObject.put("package", "Sign=WXPay");
         *
         * backObject.put("partnerid", payParams.get("partnerid"));
         *
         * backObject.put("prepayid", payParams.get("prepayid"));
         *
         * backObject.put("appkey", this.appkey);
         *
         * backObject.put("timestamp",payParams.get("timestamp"));
         *
         * backObject.put("sign",payParams.get("sign"));
         */
        SortedMap<String, Object> params = new TreeMap<String, Object>();

        if(isApp) {
            params.put("appid", isApp?ConstantUtil.APP_ID:ConstantUtil.MPAPP_ID);
            params.put("noncestr", PayCommonUtil.CreateNoncestr());
            params.put("package", "Sign=WXPay");
            params.put("partnerid", ConstantUtil.PARTNER);
            params.put("prepayid", resutlMap.get("prepay_id"));
            params.put("timestamp", String.valueOf((int) (System.currentTimeMillis() / 1000)));
        } else {
            params.put("appId", isApp?ConstantUtil.APP_ID:ConstantUtil.MPAPP_ID);
            params.put("nonceStr", PayCommonUtil.CreateNoncestr());
            params.put("package", "prepay_id="+resutlMap.get("prepay_id"));
            params.put("timeStamp", String.valueOf((int) (System.currentTimeMillis() / 1000)));
            params.put("signType", "MD5");
        }


        // paySign的生成规则和Sign的生成规则同理
        String paySign = PayCommonUtil.createSign("UTF-8", params);
        params.put("sign", paySign);
        return params;
    }

    public String callback(HttpServletRequest request) {
        try {
            String responseStr = parseWeixinCallback(request);
            Map<String, Object> map = XMLUtil.doXMLParse(responseStr);
            // 校验签名 防止数据泄漏导致出现“假通知”，造成资金损失
            if (!PayCommonUtil.checkIsSignValidFromResponseString(responseStr)) {
                logger.error("微信回调失败,签名可能被篡改");
                return PayCommonUtil.setXML("FAIL", "invalid sign");
            }
            if (ConstantUtil.FAIL.equalsIgnoreCase(map.get("result_code")
                    .toString())) {
                logger.error("微信回调失败");
                return PayCommonUtil.setXML("FAIL", "weixin pay fail");
            }
            if (ConstantUtil.SUCCESS.equalsIgnoreCase(map.get("result_code")
                    .toString())) {
                // 对数据库的操作
                String outTradeNo = (String) map.get("out_trade_no");
                String transactionId = (String) map.get("transaction_id");
                String totlaFee = (String) map.get("total_fee");
                Integer totalPrice = Integer.valueOf(totlaFee);

                Order order = ucService.paidOrder(Long.parseLong(outTradeNo),transactionId,totalPrice/100.0);//支付订单成功
                // 告诉微信服务器，我收到信息了，不要在调用回调action了
                if (order != null) {
                    return PayCommonUtil.setXML(ConstantUtil.SUCCESS, "OK");
                } else {
                    return PayCommonUtil
                            .setXML(ConstantUtil.FAIL, "pay fail");
                }
            }
        } catch (Exception e) {
            logger.debug("支付失败" + e.getMessage());
            return PayCommonUtil.setXML(ConstantUtil.FAIL,
                    "weixin pay server exception");
        }
        return PayCommonUtil.setXML(ConstantUtil.FAIL, "weixin pay fail");
    }

    /**
     * 解析微信回调参数
     *
     * @param request
     * @return
     * @throws IOException
     */
    private String parseWeixinCallback(HttpServletRequest request) throws IOException {
        /*InputStream inStream = request.getInputStream();
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        String result = new String(outSteam.toByteArray(), "utf-8");// 获取微信调用我们notify_url的返回信息*/
        ByteArrayBuffer buffer = (ByteArrayBuffer)request.getAttribute("post_body");
        return new String(buffer.array(), "UTF-8");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        _client = httpClientManager.getClient(httpClientName);
        if(!_client.isStarted()){
            _client.start();
        }
    }

/*
    public HttpResult<String> checkOrderStatus(SortedMap<String, Object> params) {
        if (params == null) {
            return HttpResult.error(1, "查询订单参数不能为空");
        }
        try {
            String requestXML = PayCommonUtil.getRequestXml(params);// 生成xml格式字符串
            String responseStr = HttpUtil.httpsRequest(
                    ConstantUtil.CHECK_ORDER_URL, "POST", requestXML);// 带上post
            SortedMap<String, Object> responseMap = XMLUtil
                    .doXMLParse(responseStr);// 解析响应xml格式字符串

            // 校验响应结果return_code
            if (ConstantUtil.FAIL.equalsIgnoreCase(responseMap.get(
                    "return_code").toString())) {
                return HttpResult.error(1, responseMap.get("return_msg")
                        .toString());
            }
            // 校验业务结果result_code
            if (ConstantUtil.FAIL.equalsIgnoreCase(responseMap.get(
                    "result_code").toString())) {
                return HttpResult.error(2, responseMap.get("err_code")
                        .toString() + "=" + responseMap.get("err_code_des"));
            }
            // 校验签名
            if (!PayCommonUtil.checkIsSignValidFromResponseString(responseStr)) {
                logger.error("订单查询失败,签名可能被篡改");
                return HttpResult.error(3, "签名错误");
            }
            // 判断支付状态
            String tradeState = responseMap.get("trade_state").toString();
            if (tradeState != null && tradeState.equals("SUCCESS")) {
                return HttpResult.success(0, "订单支付成功");
            } else if (tradeState == null) {
                return HttpResult.error(4, "获取订单状态失败");
            } else if (tradeState.equals("REFUND")) {
                return HttpResult.error(5, "转入退款");
            } else if (tradeState.equals("NOTPAY")) {
                return HttpResult.error(6, "未支付");
            } else if (tradeState.equals("CLOSED")) {
                return HttpResult.error(7, "已关闭");
            } else if (tradeState.equals("REVOKED")) {
                return HttpResult.error(8, "已撤销（刷卡支付");
            } else if (tradeState.equals("USERPAYING")) {
                return HttpResult.error(9, "用户支付中");
            } else if (tradeState.equals("PAYERROR")) {
                return HttpResult.error(10, "支付失败");
            } else {
                return HttpResult.error(11, "未知的失败状态");
            }
        } catch (Exception e) {
            logger.error("订单查询失败,查询参数 = {}", JSON.toJSONString(params));
            return HttpResult.success(1, "订单查询失败");
        }
    }*/
}

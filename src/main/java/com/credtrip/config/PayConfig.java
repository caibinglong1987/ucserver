package com.credtrip.config;

/**
 * Created by caibinglong
 * on 2016/11/2.
 */
public class PayConfig {
    private final static boolean isDebug = false;
    public static String version = "1.0";
    public static String product = "01";
    public static String signType = "MD5"; //SHA:SHA签名方式 MD5:MD5签名方式
    public static String channel = "H5"; //WEB/Android/IOS/H5 支付渠道
    public static String tradeType = "CONSUME"; //交易类型
    public static String domain = isDebug ? "https://api-test.credtrip.com" : "https://api.credtrip.com"; //正式域名
    public static String merchantId = "2016010003";
    public static String md5Key = isDebug ? "12d5b532a19f7818aa7c9e428152b362" : "f0f9c4493445b8bd4cdb54ccf6d71dcd";
    public static String notify_url = isDebug ? "http://www.roam-tech.com:8000/uc/services/xc_pay/notify" : "http://www.roam-tech.com/uc/services/xc_pay/notify";
    public static String return_url = "http://www.roam-tech.com/uc/services/credtrip/pay_success1.html";
    public static String queryCreditUrl = domain + "/api/user/queryCredit"; //查询额度接口
    public static String openUrl = domain + "/gw/open"; //分期开通
    public static String payUrl = domain + "/gateway/gw/pay";//支付接口
    public static String repaymentUrl = domain + "/uc/ucenter/enter";//还款接口
    public static String refundUrl = domain + "/api/trade/refund";//退款接口
    public static String cancelUrl = domain + "/api/trade/cancel";//取消订单
    public static String queryOrderUrl = domain + "/api/trade/queryOrderStatus";//查询订单支付状态
}

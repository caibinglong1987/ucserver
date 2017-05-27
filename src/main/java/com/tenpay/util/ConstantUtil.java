package com.tenpay.util;

public class ConstantUtil {
	/**
	 * 商家可以考虑读取配置文件
	 */
	
	//初始化
	public static String APP_ID = "wx0bf1f764ee9bf082";//微信开发平台应用id
	//public static String APP_SECRET = "db426a9829e4b49a0dcac7b4162da6b6";//应用对应的凭证
	//应用对应的密钥
	//public static String APP_KEY = "L8LrMqqeGRxST5reouB0K66CaYAWpqhAVsq7ggKkxHCOastWksvuX1uvmvQclxaHoYd3ElNBrNO2DHnnzgfVG9Qs473M3DTOZug5er46FhuGofumV8H2FVR9qkjSlC5K";
	public static String PARTNER = "1381784002";//财付通商户号
	//public static String PARTNER_KEY = "8934e7d15453e97507ef794cf7b0519d";//商户号对应的密钥
	//public static String TOKENURL = "https://api.weixin.qq.com/cgi-bin/token";//获取access_token对应的url
	//public static String GRANT_TYPE = "client_credential";//常量固定值
	//public static String EXPIRE_ERRCODE = "42001";//access_token失效后请求返回的errcode
	//public static String FAIL_ERRCODE = "40001";//重复获取导致上一次获取的access_token失效,返回错误码
	public static String GATEURL = "https://api.weixin.qq.com/pay/genprepay?access_token=";//获取预支付id的接口url
	public static String ACCESS_TOKEN = "access_token";//access_token常量值
	public static String ERRORCODE = "errcode";//用来判断access_token是否失效的值
	public static String SIGN_METHOD = "sha1";//签名算法常量值
	//package常量值
	public static String packageValue = "bank_type=WX&body=%B2%E2%CA%D4&fee_type=1&input_charset=GBK&notify_url=http%3A%2F%2F127.0.0.1%3A8180%2Ftenpay_api_b2c%2FpayNotifyUrl.jsp&out_trade_no=2051571832&partner=1900000109&sign=10DA99BCB3F63EF23E4981B331B0A3EF&spbill_create_ip=127.0.0.1&time_expire=20131222091010&total_fee=1";
	public static String traceid = "testtraceid001";//测试用户id

	public static String SUCCESS = "SUCCESS"; //成功return_code

	public static String FAIL = "FAIL";   //失败return_code

	//public static String PRODUCT_BODY = "时长充值卡"; // 商品描述


	public final static String API_KEY = "RoamingData2016zemin201608weijie";// 支付API密钥
	public final static String SIGN_TYPE = "MD5";// 签名加密方式
	public final static String TRADE_TYPE = "APP";// 支付类型
	public static String MPAPP_ID = "wxa02261a86e83d8d2";
	public static String MPPARTNER = "1380374202";//财付通商户号
	public static String MPAPI_KEY = "f4e1003f6388a65bb1e46a100ad8ca0c";
	public final static String MPTRADE_TYPE = "JSAPI";
	// public final static String CERT_PATH =
	// "/Users/kevin/apiclient_cert.p12";//微信支付证书存放路径地址
	// 微信支付统一接口的回调
	public final static String NOTIFY_URL = "http://www.roam-tech.com/uc/services/wxpay/notify";
	// 微信支付成功支付后跳转的地址 web端使用
	public final static String SUCCESS_URL = "http://ip:port/wxweb/contents/config/pay_success.jsp";
	// oauth2授权时回调action
	public final static String REDIRECT_URI = "http://ip:port/GoMyTrip/a.jsp?port=8016";
	/**
	 * 微信基础接口地址
	 */
	// 获取token接口(GET)
	public final static String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	// oauth2授权接口(GET)
	public final static String OAUTH2_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
	// 刷新access_token接口（GET）
	public final static String REFRESH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
	// 菜单创建接口（POST）
	public final static String MENU_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	// 菜单查询（GET）
	public final static String MENU_GET_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
	// 菜单删除（GET）
	public final static String MENU_DELETE_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
	/**
	 * 微信支付接口地址
	 */
	// 微信支付统一接口(POST)
	public final static String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	// 微信退款接口(POST)
	public final static String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
	// 订单查询接口(POST)
	public final static String CHECK_ORDER_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
	// 关闭订单接口(POST)
	public final static String CLOSE_ORDER_URL = "https://api.mch.weixin.qq.com/pay/closeorder";
	// 退款查询接口(POST)
	public final static String CHECK_REFUND_URL = "https://api.mch.weixin.qq.com/pay/refundquery";
	// 对账单接口(POST)
	public final static String DOWNLOAD_BILL_URL = "https://api.mch.weixin.qq.com/pay/downloadbill";
	// 短链接转换接口(POST)
	public final static String SHORT_URL = "https://api.mch.weixin.qq.com/tools/shorturl";
	// 接口调用上报接口(POST)
	public final static String REPORT_URL = "https://api.mch.weixin.qq.com/payitil/report";
}

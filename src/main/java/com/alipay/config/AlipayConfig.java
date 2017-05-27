package com.alipay.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.4
 *修改日期：2016-03-08
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {
	
//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
	public static String partner = "2088421208349001";
	
	// 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
	public static String seller_id = partner;

	// MD5密钥，安全检验码，由数字和字母组成的32位字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
    public static String key = "crw2a0oktxqy3riip2o7wdz4giqn6jp1";
	

	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "http://www.roam-tech.com/uc/services/alipay/notify";

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String return_url = "http://www.roam-tech.com/roammall/views/market/pay_success.html";

	// 签名方式
	public static String sign_type = "MD5";
	
	// 调试用，创建TXT日志文件夹路径，见AlipayCore.java类中的logResult(String sWord)打印方法。
	public static String log_path = "C:\\";
		
	// 字符编码格式 目前支持utf-8
	public static String input_charset = "utf-8";
		
	// 支付类型 ，无需修改
	public static String payment_type = "1";
		
	// 调用的接口名，无需修改
	public static String service = "alipay.wap.create.direct.pay.by.user";

	public static String ALIPAY_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
	public static String ALIPAY_PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANx23Gc/gKNsoWw0" +
			"hxcd7xFWrXDBGI/rHOPCj87VlX+XT30nZoRct6TBPIUsLP5Do5K24fG6PxNwZcCZ" +
			"ipYNAyl0weLeBEaNZrizm6tfVd1vVttXC1YaJIps57e6Hdk8dRDR7ayiDXC0z+ll" +
			"JkBA1+1dvu2itTIkEvt/GjPZ16W9AgMBAAECgYBSJQucgOHKgrhm2++V4nZobHZ1" +
			"c81WGpGKH717oMNyZgudI2gkW3OltMZ7DGm7V7KEARaQ/gdwk8mmCM/FdxW81FLq" +
			"Z24FdEFtkHCBZUgFE8JRXX7iJpDgMF8rryx/K7XKgZIebrqXPj+Fiq6ORfunhAbI" +
			"9LYAgQvzeNmLbLZbIQJBAPrMcVXOc7rCzFs4xvdPlKB3Npst/jKbTYQzAr+ycWJU" +
			"vsBLAxRG6ajPPd1kRF9WJvo7OOPDasT41zeI6qZ5+EUCQQDhCV2BM+VQJCaH5b2A" +
			"+KQ08CzFgchxDLnvLlZpg0X3fcerqjtBzOGDC/vtXTnizsL2w7gl2jA3xfEx4x5M" +
			"hLsZAkEA+L5ciiZBUjJyTOd7Lz6WwrB6UF7Fh5vQvhV53Hc3TbePmVjMrQfIJ82k" +
			"JjiUN5pS/kxOmFfxRXsoucPqN0MxoQJAGOF+MyDXw8qbb/8Yur/C+A9uA9BbpxQv" +
			"IqDO8Q+QltKTVwmVT6sla+XRTuc11TvjFoGBRL4hAqmiB1ADM5ahUQJAOviZF79m" +
			"SjEcsouBrZRG528cR0egpKJ7nen7WSBUckWh6g/Bt+RW4vWZqHC8epn+IUAXFDQb" +
			"6fAhIpiGjoybMA==";
	public static String ALIPAY_APPID = "2016070601585890";
//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

}


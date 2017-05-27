package com.roamtech.uc.handler.services;

public class ErrorCode {
	public static final int SUCCESS = 0;
	public static final int ERR_EXIST = 1;
	public static final int ERR_NOT_FOUND = 2;
	public static final int ERR_TIMEOUT = 3;
	public static final int ERR_JSON_CONVERT = 4;
	public static final int ERR_JSON_PARSE = 5;
	public static final int ERR_LOCK = 6;
	public static final int ERR_MISMATCH = 7;
	public static final int ERR_EXCEPTIONS = 8;

	public static final int ERR_SERVER_ERROR = 500;
	public static final int ERR_ACCOUNT_EXIST = 1000;
	public static final int ERR_MOBILE_EXIST = 1001;
	public static final int ERR_EMAIL_EXIST = 1002;

	public static final int ERR_MOBILE_MALFORMAT = 1003;
	public static final int ERR_EMAIL_MALFORMAT = 1004;
	
	public static final int ERR_VCODE_MISMATCH = 1005;
	public static final int ERR_VCODE_SEND_FAIL = 1006;
	
	public static final int ERR_PASSWD = 1011;
	public static final int ERR_PASSWD_MISMATCH = 1012;
	public static final int ERR_INVALID_STATUS = 1013;
	public static final int ERR_PASSWD_DEFAULT = 1014;
	public static final int ERR_PASSWD_SAME = 1015;
	
	public static final int ERR_ACCOUNT_NOEXIST = 1021;
	public static final int ERR_ACCOUNT_FROZEN = 1023;
	public static final int ERR_CREDTRIPACCOUNT_NOEXIST = 1024;
	public static final int ERR_CREDTRIPACCOUNT_EXIST = 1025;
	public static final int ERR_CURRENT_ACCOUNT_ASSOCIATED = 1026;
	public static final int ERR_ASSOCIATE_CURRENT_ACCOUNT = 1027;
	public static final int ERR_ACCOUNT_ASSOCIATED_NOT_ALLOWED = 1028;
	public static final int ERR_ADMIN_USERNAME_ERROR = 1029;
	
	public static final int ERR_REQUIRED_PARAMETER_MISSED = 1100;
	public static final int ERR_SESSIONID_INVALID = 1101;
	
	public static final int ERR_TOUCHID_NOEXIST = 1102;
	public static final int ERR_INSUFFICIENT_PRIVILEGE = 1103;
	
	public static final int ERR_EVOUCHER_USED = 1200;
	public static final int ERR_EVOUCHER_NOEXIST = 1201;
	public static final int ERR_EVOUCHER_INVALID = 1202;
	public static final int ERR_EVOUCHERSN_USED = 1203;
	public static final int ERR_EVOUCHERSN_INVALID = 1204;
	
	public static final int ERR_DATACARD_BINDED = 1300;
	public static final int ERR_DATACARD_NOEXIST = 1301;
	public static final int ERR_ORDERDETAIL_BINDED = 1302;
	public static final int ERR_ORDERDETAIL_NOEXIST = 1303;
	public static final int ERR_DATACARD_ORDERDETAIL_BINDED = 1304;
	public static final int ERR_ORDER_NOEXIST = 1305;
	public static final int ERR_ORDER_USER_MISMATCH = 1306;
	public static final int ERR_ORDER_INVALID_STATUS = 1307;
	public static final int ERR_REFUND_NOEXIST = 1308;
	public static final int ERR_REFUND_USER_MISMATCH = 1309;
	public static final int ERR_TOUCHVOICE_OVERFLOW = 1401;
	public static final int ERR_DATATRAFFIC_OVERFLOW = 1402;
	public static final int ERR_WXPAY_UNIFIEDORDER_FAIL = 1500;
	public static final int ERR_XC_PAY_FAIL = 1501;

	public static final int ERR_UNAUTHORIZED_CLIENT = 1600;
	public static final int ERR_INVALID_AUTHORIZATION_HEAD = 1601;
	public static final int ERR_INVALID_SDK_VERSION = 1602;
	public static final int ERR_CONTACT_UPDATE_FAILED = 1603;

	public static final String ERR_SERVER_ERROR_INFO = "服务端异常错误";
	public static final String ERR_ACCOUNT_EXIST_INFO = "用户名已存在";
	public static final String ERR_MOBILE_MALFORMAT_INFO = "非法手机号";
	public static final String ERR_MOBILE_EXIST_INFO = "手机号码已注册";
	public static final String ERR_EMAIL_MALFORMAT_INFO = "非法邮箱地址";
	public static final String ERR_EMAIL_EXIST_INFO = "邮箱已注册";
	public static final String ERR_IDENTITY_EXIST_INFO = "证件号已注册";
	
	public static final String ERR_VCODE_MISMATCH_INFO = "验证码不一致或失效";
	public static final String ERR_PASSWD_INFO = "密码错误";
	public static final String ERR_PASSWD_MISMATCH_INFO = "旧密码不一致";
	public static final String ERR_INVALID_STATUS_INFO = "用户状态不可用";
	public static final String ERR_PASSWD_DEFAULT_INFO = "初始密码需要修改";
	public static final String ERR_PASSWD_SAME_INFO = "新密码不能与旧密码相同";
	
	public static final String ERR_ACCOUNT_NOEXIST_INFO = "用户不存在";
	public static final String ERR_ACCOUNT_FROZEN_INFO = "账户冻结";
	public static final String ERR_CREDTRIPACCOUNT_NOEXIST_INFO = "信程用户账号不存在";
	public static final String ERR_CREDTRIPACCOUNT_EXIST_INFO = "信程账户已开通";
	public static final String ERR_CURRENT_ACCOUNT_ASSOCIATED_INFO = "当前账号已经有关联账号";
	public static final String ERR_ASSOCIATE_CURRENT_ACCOUNT_INFO = "关联账号不能是当前账号";
	public static final String ERR_ACCOUNT_ASSOCIATED_NOT_ALLOWED_INFO = "不能关联该账号";
	public static final String ERR_ADMIN_USERNAME_ERROR_INFO = "用户名错误";

	public static final String ERR_REQUIRED_PARAMETER_MISSED_INFO = "缺少必需参数或参数值无效";
	public static final String ERR_SESSIONID_INVALID_INFO = "会话失效，重新登录";
	public static final String ERR_TOUCHID_NOEXIST_INFO = "设备未注册";
	public static final String ERR_INSUFFICIENT_PRIVILEGE_INFO = "用户权限不够";
	
	public static final String ERR_VCODE_SEND_FAIL_INFO = "验证码发送失败";
	
	public static final String ERR_EVOUCHER_USED_INFO = "电子券已使用";
	public static final String ERR_EVOUCHER_NOEXIST_INFO = "电子券不存在";
	public static final String ERR_EVOUCHER_INVALID_INFO = "电子券失效";
	public static final String ERR_EVOUCHERSN_USED_INFO = "兑换码已使用，请尝试其它兑换码";
	public static final String ERR_EVOUCHERSN_INVALID_INFO = "兑换码无效，请输入正确的兑换码";

	public static final String ERR_DATACARD_BINDED_INFO = "全球芯已绑定";
	public static final String ERR_DATACARD_NOEXIST_INFO = "全球芯不存在";
	public static final String ERR_ORDERDETAIL_BINDED_INFO = "订单详情已绑定";	
	public static final String ERR_ORDERDETAIL_NOEXIST_INFO = "订单详情不存在";	
	public static final String ERR_DATACARD_ORDERDETAIL_BINDED_INFO = "全球芯已绑定该时间段的套餐";
	public static final String ERR_ORDER_NOEXIST_INFO = "订单不存在";
	public static final String ERR_ORDER_USER_MISMATCH_INFO = "订单用户不一致";
	public static final String ERR_ORDER_INVALID_STATUS_INFO = "订单状态无效";
	public static final String ERR_REFUND_NOEXIST_INFO = "退货、退款申请不存在";
	public static final String ERR_REFUND_USER_MISMATCH_INFO = "退货、退款申请用户不一致";
	public static final String ERR_TOUCHVOICE_OVERFLOW_INFO = "络漫专属号分配失败，修改订购开始结束日期或十分钟后再试";
	public static final String ERR_DATATRAFFIC_OVERFLOW_INFO = "全球芯分配失败，修改订购开始结束日期或一小时后再试";
	public static final String ERR_WXPAY_UNIFIEDORDER_FAIL_INFO = "微信支付统一下单失败";
	public static final String ERR_XC_PAY_FAIL_INFO = "信程支付统一下单失败";
	public static final String ERR_UNAUTHORIZED_CLIENT_INFO = "应用没有被授权";
	public static final String ERR_INVALID_AUTHORIZATION_HEAD_INFO = "Authorization请求头无效或格式不正确";
	public static final String ERR_INVALID_SDK_VERSION_INFO = "当前SDK版本不可用";
	public static final String ERR_CONTACT_UPDATE_FAILED_INFO = "联系方式更新失败";

	public static String getErrorInfo(int error) {
		if (error == SUCCESS) {
			return "成功";
		}
		if (error == ERR_ACCOUNT_EXIST) {
			return ERR_ACCOUNT_EXIST_INFO;
		} else if (error == ERR_MOBILE_EXIST) {
			return ERR_MOBILE_EXIST_INFO;
		} else if (error == ERR_EMAIL_EXIST) {
			return ERR_EMAIL_EXIST_INFO;
		} else if (error == ERR_MOBILE_MALFORMAT) {
			return ERR_MOBILE_MALFORMAT_INFO;
		} else if (error == ERR_EMAIL_MALFORMAT) {
			return ERR_EMAIL_MALFORMAT_INFO;
		} else if (error == ERR_VCODE_MISMATCH) {
			return ERR_VCODE_MISMATCH_INFO;
		} else if (error == ERR_PASSWD) {
			return ERR_PASSWD_INFO;
		} else if (error == ERR_PASSWD_MISMATCH) {
			return ERR_PASSWD_MISMATCH_INFO;
		} else if (error == ERR_INVALID_STATUS) {
			return ERR_INVALID_STATUS_INFO;
		} else if (error == ERR_PASSWD_DEFAULT) {
			return ERR_PASSWD_DEFAULT_INFO;
		} else if (error == ERR_PASSWD_SAME) {
			return ERR_PASSWD_SAME_INFO;
		} else if (error == ERR_ACCOUNT_NOEXIST) {
			return ERR_ACCOUNT_NOEXIST_INFO;
		} else if (error == ERR_ACCOUNT_FROZEN) {
			return ERR_ACCOUNT_FROZEN_INFO;
		} else if (error == ERR_CREDTRIPACCOUNT_NOEXIST) {
			return ERR_CREDTRIPACCOUNT_NOEXIST_INFO;
		} else if (error == ERR_CREDTRIPACCOUNT_EXIST) {
			return ERR_CREDTRIPACCOUNT_EXIST_INFO;
		} else if (error == ERR_REQUIRED_PARAMETER_MISSED) {
			return ERR_REQUIRED_PARAMETER_MISSED_INFO;
		} else if (error == ERR_SESSIONID_INVALID) {
			return ERR_SESSIONID_INVALID_INFO;
		} else if (error == ERR_TOUCHID_NOEXIST) {
			return ERR_TOUCHID_NOEXIST_INFO;
		} else if (error == ERR_INSUFFICIENT_PRIVILEGE) {
			return ERR_INSUFFICIENT_PRIVILEGE_INFO;
		} else if (error == ERR_VCODE_SEND_FAIL) {
			return ERR_VCODE_SEND_FAIL_INFO;
		} else if (error == ERR_EVOUCHER_USED) {
			return ERR_EVOUCHER_USED_INFO;
		} else if (error == ERR_EVOUCHER_NOEXIST) {
			return ERR_EVOUCHER_NOEXIST_INFO;
		} else if (error == ERR_EVOUCHER_INVALID) {
			return ERR_EVOUCHER_INVALID_INFO;
		} else if (error == ERR_EVOUCHERSN_USED) {
			return ERR_EVOUCHERSN_USED_INFO;
		} else if (error == ERR_EVOUCHERSN_INVALID) {
			return ERR_EVOUCHERSN_INVALID_INFO;
		} else if (error == ERR_DATACARD_BINDED) {
			return ERR_DATACARD_BINDED_INFO;
		} else if (error == ERR_DATACARD_NOEXIST) {
			return ERR_DATACARD_NOEXIST_INFO;
		} else if (error == ERR_TOUCHVOICE_OVERFLOW) {
			return ERR_TOUCHVOICE_OVERFLOW_INFO;
		} else if (error == ERR_ORDER_NOEXIST) {
			return ERR_ORDER_NOEXIST_INFO;
		} else if (error == ERR_WXPAY_UNIFIEDORDER_FAIL) {
			return ERR_WXPAY_UNIFIEDORDER_FAIL_INFO;
		} else if (error == ERR_XC_PAY_FAIL) {
			return ERR_XC_PAY_FAIL_INFO;
		} else if (error == ERR_UNAUTHORIZED_CLIENT) {
			return ERR_UNAUTHORIZED_CLIENT_INFO;
		} else if (error == ERR_INVALID_AUTHORIZATION_HEAD) {
			return ERR_INVALID_AUTHORIZATION_HEAD_INFO;
		} else if (error == ERR_INVALID_SDK_VERSION) {
			return ERR_INVALID_SDK_VERSION_INFO;
		} else if (error == ERR_CONTACT_UPDATE_FAILED) {
			return ERR_CONTACT_UPDATE_FAILED_INFO;
		} else if (error == ERR_CURRENT_ACCOUNT_ASSOCIATED) {
			return ERR_CURRENT_ACCOUNT_ASSOCIATED_INFO;
		} else if (error == ERR_ASSOCIATE_CURRENT_ACCOUNT) {
			return ERR_ASSOCIATE_CURRENT_ACCOUNT_INFO;
		} else if (error == ERR_ACCOUNT_ASSOCIATED_NOT_ALLOWED) {
			return ERR_ACCOUNT_ASSOCIATED_NOT_ALLOWED_INFO;
		} else if (error == ERR_ADMIN_USERNAME_ERROR) {
			return ERR_ADMIN_USERNAME_ERROR_INFO;
		}
		return "未知错误";
	}
}

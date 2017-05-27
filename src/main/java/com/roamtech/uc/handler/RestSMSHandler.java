package com.roamtech.uc.handler;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloopen.rest.sdk.CCPRestSmsSDK;

public class RestSMSHandler {
	private Logger LOG =LoggerFactory.getLogger(RestSMSHandler.class);
	private CCPRestSmsSDK restAPI;
	private static final String SANDBOX_URL = "sandboxapp.cloopen.com";
	private String smsTemplateId;
	RestSMSHandler(HashMap<String,String> config) {
		restAPI = new CCPRestSmsSDK();
		restAPI.init(getProperty(config,"server",SANDBOX_URL), "8883");
		restAPI.setAccount(getProperty(config, "account_sid", ""), getProperty(config, "auth_token", ""));
		restAPI.setAppId(getProperty(config, "app_id", ""));
		smsTemplateId = getProperty(config, "template_id","1");
	}
	private String getProperty(HashMap<String,String> config, String key, String defaultVal) {
		String val = config.get(key);
		if(val == null || val.isEmpty()) {
			return defaultVal;
		}
		return val;
	}
	public int sendSMS(String to, String checkCode, int minutes) {
		HashMap<String, Object> result = null;
		result = restAPI.sendTemplateSMS(to, smsTemplateId, new String[]{checkCode,""+minutes});
		//System.out.println("SDKTestGetSubAccounts result=" + result);
		if(!"000000".equals(result.get("statusCode"))){
			LOG.error("send sms to "+to+" failed,error_no=" + result.get("statusCode") +" error_info= "+result.get("statusMsg"));
		}
		return Integer.parseInt((String)result.get("statusCode"));
		/*if("000000".equals(result.get("statusCode"))){
			//正常返回输出data包体信息（map）
			HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for(String key:keySet){
				Object object = data.get(key);
				System.out.println(key +" = "+object);
			}
			return "success";
		}else{
			//异常返回输出错误码和错误信息
			System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
			return "error_no=" + result.get("statusCode") +" error_info= "+result.get("statusMsg");
		}*/
	}
}

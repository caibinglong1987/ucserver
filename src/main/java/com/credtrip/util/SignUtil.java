package com.credtrip.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.credtrip.config.Merchant;



/**
 * Created by djq on 2016/5/28.
 * Copyright © mizhuanglicai
 *
 * @author djq
 */
public class SignUtil {

    protected static final Log logger = LogFactory.getLog(SignUtil.class);

    public static boolean validateSign(String srcSign, String targetSign,String signType,Merchant merchant) {
        boolean flag = false;
        String signed = null;
        if(StringUtils.isEmpty(signType) || "MD5".equals(signType)){
            srcSign = srcSign+merchant.getMd5Key();
            signed = Md5Algorithm.getInstance().md5Digest(srcSign.toString().getBytes());
            flag = targetSign.equalsIgnoreCase(signed);
        }else if("RSA".equals(signType)){
            //TODO RSA签名方式实现
        }

        logger.info(String.format("商户传入sign=[%s]，签名原串[%s],加签后[%s]", targetSign, srcSign, signed));
        return flag;
    }

    public static String sign(String srcSign,Merchant merchant){
        String signed = null;
        if(StringUtils.isEmpty(merchant.getSignType()) || "MD5".equals(merchant.getSignType())){
            srcSign = srcSign+merchant.getMd5Key();
				try {
					signed = Md5Algorithm.getInstance().md5Digest(srcSign.toString().getBytes("utf-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }else if("RSA".equals(merchant.getSignType())){
        	//TODO RSA签名方式实现
        }
        logger.info(String.format("签名原串[%s],加签后[%s]", srcSign, signed));
        return signed;
    }
    
    
}

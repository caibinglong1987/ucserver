package com.credtrip;

import com.credtrip.config.PayConfig;
import com.credtrip.config.Merchant;
import com.credtrip.util.SignUtil;
import com.roamtech.uc.model.Order;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by caibinglong
 * on 2016/11/2.
 */
public class PayApi {
    protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String buildRequestMysign(Map<String, String> sPara) {
        //根据请求参数进行加签，签名规则为按字符升序排序，并将字段中的值进行加签约
        Set<String> names = sPara.keySet();//request.getParameterNames();
        List<String> params = new ArrayList<>();
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
        Merchant merchant = new Merchant();
        merchant.setSignType(PayConfig.signType);
        merchant.setMd5Key(PayConfig.md5Key);
        String sign = SignUtil.sign(sb.toString(), merchant);
        return sign;
    }

    /**
     * @param order      订单
     * @param fee        金额 分
     * @return
     */
    public static Map<String, String> XCBuildParams(Order order, int fee, String accountNo) {
        Map<String, String> sParaTemp = new HashMap<>();
        sParaTemp.put("signType", PayConfig.signType);
        sParaTemp.put("version", PayConfig.version);
        sParaTemp.put("product", PayConfig.product);
        sParaTemp.put("merchantId", PayConfig.merchantId);
        sParaTemp.put("channel", PayConfig.channel);
        sParaTemp.put("notifyUrl", PayConfig.notify_url);
        sParaTemp.put("returnUrl", PayConfig.return_url);
        sParaTemp.put("userId", order.getUserid().toString());
        sParaTemp.put("accountNo", accountNo);
        sParaTemp.put("mobile", "");
        sParaTemp.put("tradeType", PayConfig.tradeType);
        sParaTemp.put("outOrderId", order.getId().toString());
        sParaTemp.put("orderTime", sdf.format(order.getCreatetime()));
        sParaTemp.put("goodsName", order.getOrderDetails().get(0).getProductid() + "");
        sParaTemp.put("goodsNo", "");
        sParaTemp.put("orderInfo", "");
        sParaTemp.put("orderMoney", fee + "");
        sParaTemp.put("expireMin", "");
        sParaTemp.put("sign", buildRequestMysign(sParaTemp));
        return sParaTemp;
    }

    public static String getBuildHtml(Map<String, String> sParaTemp, String strMethod, String strButtonName) {
        List<String> keys = new ArrayList<>(sParaTemp.keySet());
        StringBuffer sbHtml = new StringBuffer();
        sbHtml.append("<!DOCTYPE html><html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">"
                + "<title>信程分期信用钱包</title></head><body>");
        sbHtml.append("<form id=\"credtripsubmit\" name=\"credtripsubmit\" action=\"" + PayConfig.payUrl
                + "\" method=\"" + strMethod
                + "\">");
        for (int i = 0; i < keys.size(); i++) {
            String name = keys.get(i);
            String value = sParaTemp.get(name);
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

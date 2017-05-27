package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.WebUtils;
import com.credtrip.PayApi;
import com.credtrip.config.PayConfig;
import com.roamtech.uc.exception.UCBaseException;
import com.roamtech.uc.handler.OrderPriceUtil;
import com.roamtech.uc.model.*;
import com.roamtech.uc.util.JSONUtils;
import com.tenpay.WXPayApis;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipaySubmit;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.roamtech.uc.handler.OrderPriceUtil.calcTotalFee;

public class OrderPayingService extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(OrderPayingService.class);
    private String alipayNotifyUrl = "http://www.roam-tech.com/uc/services/malipay/notify";
    private String wxpayNotifyUrl = "http://www.roam-tech.com/uc/services/wxpay/notify";

    @Autowired
    WXPayApis wxPayApis;

    private class ODPayRequest extends UCRequest {
        private Long orderId;
        private Integer payId;
        private String payVoucher;
        private String tradeType;
        private String openid;

        public ODPayRequest(HttpServletRequest request) {
            super(request);
            String temp = getParameter("orderid");
            orderId = -1L;
            if (StringUtils.isNotBlank(temp)) {
                orderId = Long.parseLong(temp);
            }
            temp = getParameter("payid");
            payId = 0;
            if (StringUtils.isNotBlank(temp)) {
                payId = Integer.valueOf(temp);
            }
            payVoucher = getParameter("payvoucher");
            tradeType = getParameter("trade_type");
            openid = getParameter("openid");
        }

        @Override
        public boolean validate() {
            return (super.validate() && (orderId != -1L) && (payId != 0 /*&& StringUtils.isNotBlank(payVoucher)*/));
        }
    }

    private Map<String, String> alipayBuildParams(Order order, String sessionId) {
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", AlipayConfig.service);
        sParaTemp.put("partner", AlipayConfig.partner);
        sParaTemp.put("seller_id", AlipayConfig.seller_id);
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
        sParaTemp.put("payment_type", AlipayConfig.payment_type);
        sParaTemp.put("notify_url", AlipayConfig.notify_url);
        sParaTemp.put("return_url", AlipayConfig.return_url);
        sParaTemp.put("out_trade_no", order.getId().toString());
        List<OrderDetail> ods = order.getOrderDetails();
        String subject = alipayBuildSubject(order);
        sParaTemp.put("subject", subject);
        DecimalFormat format = new DecimalFormat("0.00");
        sParaTemp.put("total_fee", format.format(calcTotalFee(order)));
        sParaTemp.put("show_url", "");
        sParaTemp.put("body", order.getUserid() + ":" + sessionId);
        return sParaTemp;
    }

    private String alipayBuildSubject(Order order) {
        List<OrderDetail> ods = order.getOrderDetails();
        String subject = "";
        int count = 0;
        for (OrderDetail od : ods) {
            subject += ucService.getProduct(od.getProductid()).getName();
            if (count != ods.size() - 1) {
                subject += ",";
            }
            count++;
            if (count >= 3) {
                break;
            }
        }
        if (ods.size() > 3) {
            subject += ",...";
        }
        return subject;
    }

    private Map<String, String> malipayBuildParams(Order order, String sessionId) {
        Map<String, String> sParaTemp = new HashMap<String, String>();

        sParaTemp.put("app_id", AlipayConfig.ALIPAY_APPID);//TBD
        sParaTemp.put("method", "alipay.trade.app.pay");
        sParaTemp.put("format", "JSON");
        sParaTemp.put("charset", "utf-8");
        sParaTemp.put("sign_type", "RSA");
        sParaTemp.put("timestamp", sdf.format(new Date()));
        sParaTemp.put("version", "1.0");
        sParaTemp.put("notify_url", alipayNotifyUrl);

        Map<String, String> bizTemp = new HashMap<String, String>();
        bizTemp.put("body", order.getUserid() + ":" + sessionId);
        bizTemp.put("out_trade_no", order.getId().toString());
        List<OrderDetail> ods = order.getOrderDetails();
        String subject = alipayBuildSubject(order);
        bizTemp.put("subject", subject);
        bizTemp.put("timeout_express", "15d");
        bizTemp.put("seller_id", AlipayConfig.seller_id);
        DecimalFormat format = new DecimalFormat("0.00");
        bizTemp.put("total_amount", format.format(calcTotalFee(order)));
        bizTemp.put("product_code", "QUICK_MSECURITY_PAY");
        sParaTemp.put("biz_content", JSONUtils.serialize(bizTemp));
        return sParaTemp;
    }

    private String alipayBuildReq(Order order, String sessionId) {

        //其他业务参数根据在线开发文档，添加参数.文档地址:https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.2Z6TSk&treeId=60&articleId=103693&docType=1
        //如sParaTemp.put("参数名","参数值");

        Map<String, String> sParaTemp = alipayBuildParams(order, sessionId);
        //建立请求
        String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
        return sHtmlText;
    }

    private String malipayBuildReq(Order order, String sessionId) throws AlipayApiException, IOException {
        Map<String, String> sParaTemp = malipayBuildParams(order, sessionId);
        /*Signature*/
        String privateKey = "1111111";
        String sign = AlipaySignature.rsaSign(sParaTemp, AlipayConfig.ALIPAY_PRIVATE_KEY, "utf-8");
        sParaTemp.put("sign", sign);
        return WebUtils.buildQuery(sParaTemp, "utf-8");
    }

    @Override
    public void doHandle(String target, Request baseRequest,
                         HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        ODPayRequest odSetReq = new ODPayRequest(request);
        int status = ErrorCode.SUCCESS;
        Order od = null;
        String payparams = null;
        UCResponse ucResp = null;
        if (odSetReq.validate()) {
            if (ucService.isSessionValid(odSetReq.getSessionId())) {
                try {
                    od = ucService.payOrder(odSetReq.getUserid(), odSetReq.orderId, odSetReq.payId, odSetReq.payVoucher);
                    if (od == null) {
                        status = ErrorCode.ERR_ORDER_NOEXIST;
                    } else {
                        if (odSetReq.payId == Payment.WAPALIPAY_ID) {/*网页支付宝*/
                            response.setStatus(200);
                            response.setHeader("Access-Control-Allow-Origin", "*");
                            response.setHeader("Content-Type", "text/html;charset=UTF-8");
                            response.getWriter().write(alipayBuildReq(od, odSetReq.getSessionId()));
                            return;
                        }
                        if (odSetReq.payId == Payment.MOBILEALIPAY_ID) {/*手机支付宝*/
                            try {
                                payparams = malipayBuildReq(od, odSetReq.getSessionId());
                            } catch (AlipayApiException e) {
                                LOG.warn("AlipayApiException", e);
                            } catch (IOException e) {
                                LOG.warn("IOException", e);
                            }
                        }
                        if (odSetReq.payId == Payment.WXPAY_ID) {/*微信支付*/
                            BigDecimal fee = new BigDecimal(OrderPriceUtil.calcTotalFee(od) * 100).setScale(0, BigDecimal.ROUND_HALF_UP);
                            boolean isApp = StringUtils.isBlank(odSetReq.tradeType)||odSetReq.tradeType.equals("APP");
                            SortedMap<String, Object> params = wxPayApis.unifiedOrder(odSetReq.getUserid().toString(), alipayBuildSubject(od), od.getId().toString(), (int) fee.intValue(), wxpayNotifyUrl, isApp, odSetReq.openid);
                            if (params == null) {
                                status = ErrorCode.ERR_WXPAY_UNIFIEDORDER_FAIL;
                            } else {
                                payparams = JSONUtils.serialize(params);
                            }
                        }

                        if (odSetReq.payId == Payment.XC_PAY_ID) {/*信程支付*/
                            BigDecimal fee = new BigDecimal(OrderPriceUtil.calcTotalFee(od) * 100).setScale(0, BigDecimal.ROUND_HALF_UP);
                            UserThirdParty userThirdParty = ucService.findCredtripAccount(od.getUserid());
                            String thirdAccount = "";
                            if (userThirdParty != null) {
                                thirdAccount = userThirdParty.getOpenid();
                            }
                            Map<String, String> params = PayApi.XCBuildParams(od, fee.intValue(), thirdAccount);
                            payparams = PayApi.getBuildHtml(params, "post", "确认");
                        }
                    }
                } catch (UCBaseException e) {
                    ucResp = e.getError();
                }

            } else {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        ucResp = ucResp != null ? ucResp : UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), odSetReq.getUserid(), odSetReq.getSessionId());
        ucResp.addAttribute("payparams", payparams);
        postProcess(baseRequest, response, ucResp);
    }

    public String getAlipayNotifyUrl() {
        return alipayNotifyUrl;
    }

    public void setAlipayNotifyUrl(String alipayNotifyUrl) {
        this.alipayNotifyUrl = alipayNotifyUrl;
    }

    public String getWxpayNotifyUrl() {
        return wxpayNotifyUrl;
    }

    public void setWxpayNotifyUrl(String wxpayNotifyUrl) {
        this.wxpayNotifyUrl = wxpayNotifyUrl;
    }

    public String getXCPayNotifyUrl() {
        return PayConfig.notify_url;
    }

    public void setXCPayNotifyUrl(String xCPayNotifyUrl) {
        PayConfig.notify_url = xCPayNotifyUrl;
    }

    public void setMd5Key(String md5Key){
        PayConfig.md5Key = md5Key;
    }

    public void setDomain(String domain){
        PayConfig.domain = domain;
    }
}

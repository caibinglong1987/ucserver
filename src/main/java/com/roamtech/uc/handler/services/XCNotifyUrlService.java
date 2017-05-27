package com.roamtech.uc.handler.services;

import com.credtrip.PayApi;
import com.roamtech.uc.handler.services.wrapper.GuestRequest;
import com.roamtech.uc.model.Order;
import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by caibinglong
 * on 2016/11/2.
 * 信程支付 回调通知接口
 */
public class XCNotifyUrlService extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(XCNotifyUrlService.class);
    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        GuestRequest req = new GuestRequest(request);
        //获取POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map requestParams = req.getParametersMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            params.put(name, req.getParameter(name));
        }
        LOG.debug("信程支付返回---> "+params.toString());
        String sign = req.getParameter("sign");
        String signType = req.getParameter("signType");
        String notifyId = req.getParameter("notifyId");//信程通知校验ID，商户可以用这个流水号询问该条通知的合法性
        String notifyTime = req.getParameter("notifyTime");
        String merchantId = req.getParameter("merchantId");
        String userId = req.getParameter("userId");
        String mobile = req.getParameter("mobile");
        String accountNo = req.getParameter("accountNo");
        String outOrderId = req.getParameter("outOrderId");//外部订单号
        String payBillId = req.getParameter("payBillId");//支付单号
        String payStatus = req.getParameter("payStatus");//支付状态
        String orderTime = req.getParameter("orderTime");//订单时间
        String settleDate = req.getParameter("settleDate");//清算日期
        String acctDate = req.getParameter("acctDate");//首期记账日期
        String repayDate = req.getParameter("repayDate");//还款日
        String numCredit = req.getParameter("numCredit");//总期数
        Integer orderMoney = Integer.valueOf(req.getParameter("orderMoney"));//交易金额

        if (PayApi.verify(params)) {
            if (payStatus.equals("SUCCESS")) {
                //付款成功
                Order order = ucService.paidOrder(Long.parseLong(outOrderId), payBillId, orderMoney / 100.0);//支付订单成功
                if (order != null) {
                    response.setStatus(200);
                    response.setHeader("Content-Type", "text/html;charset=UTF-8");
                    response.getWriter().write("success");
                }else{
                    response.setStatus(200);
                    response.setHeader("Content-Type", "text/html;charset=UTF-8");
                    response.getWriter().write("fail");
                }
            }
        }
    }
}

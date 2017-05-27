package com.roamtech.uc.handler.services;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Order;
import com.roamtech.uc.model.Refund;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RefundGetsService extends AbstractService {
    private class RefundGetsRequest extends UCRequest {
        private Long orderId;
        private Long orderDetailId;

        RefundGetsRequest(HttpServletRequest request) {
            super(request);
            String temp = getParameter("orderid");
            orderId = -1L;
            if (StringUtils.isNotBlank(temp)) {
                orderId = Long.parseLong(temp);
            }
            temp = getParameter("orderdetailid");
            orderDetailId = -1L;
            if (StringUtils.isNotBlank(temp)) {
                orderDetailId = Long.parseLong(temp);
            }
        }
    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        RefundGetsRequest refundGetsReq = new RefundGetsRequest(request);
        int status = ErrorCode.SUCCESS;
        List<Refund> refunds = null;
        if (refundGetsReq.validate()) {
            if (ucService.isSessionValid(refundGetsReq.getSessionId())) {
                refunds = ucService.getRefunds(refundGetsReq.getUserid(), refundGetsReq.orderId, refundGetsReq.orderDetailId);
            } else {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), refundGetsReq.getUserid(), refundGetsReq.getSessionId());
        ucResp.addAttribute("refunds", refunds);
        postProcess(baseRequest, response, ucResp);
    }
}

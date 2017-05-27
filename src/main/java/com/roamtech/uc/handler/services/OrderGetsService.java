package com.roamtech.uc.handler.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Cart;
import com.roamtech.uc.model.Order;

public class OrderGetsService extends AbstractService {
    private class OrderGetsRequest extends UCRequest {
        private Long orderId;
        private Integer pageindex;
        private Integer pageSize;

        OrderGetsRequest(HttpServletRequest request) {
            super(request);
            String temp = getParameter("orderid");
            orderId = 0l;
            if (StringUtils.isNotBlank(temp)) {
                orderId = Long.parseLong(temp);
            }
            temp = getParameter("pageindex");
            pageindex = 0;
            if (StringUtils.isNotBlank(temp)) {
                pageindex = Integer.parseInt(temp);
            }
            temp = getParameter("pagesize");
            pageSize = 10;
            if (StringUtils.isNotBlank(temp)) {
                pageSize = Integer.parseInt(temp);
            }
        }
    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        OrderGetsRequest orderGetsReq = new OrderGetsRequest(request);
        int status = ErrorCode.SUCCESS;
        List<Order> orders = null;
        Order order;
        if (orderGetsReq.validate()) {
            if (ucService.isSessionValid(orderGetsReq.getSessionId())) {
                if (orderGetsReq.orderId > 0) {
                    order = ucService.getOrder(orderGetsReq.orderId);
                    if (order != null) {
                        orders = new ArrayList<>();
                        orders.add(order);
                    }
                } else {
                    orders = ucService.getOrders(orderGetsReq.getUserid(), orderGetsReq.pageindex, orderGetsReq.pageSize);
                }
            } else {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), orderGetsReq.getUserid(), orderGetsReq.getSessionId());
        ucResp.addAttribute("orders", orders);
        postProcess(baseRequest, response, ucResp);
    }
}

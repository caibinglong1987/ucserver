package com.roamtech.uc.handler.services;

import com.tenpay.WXPayApis;
import org.eclipse.jetty.server.Request;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by admin03 on 2016/8/28.
 */
public class WXpayNotifyService extends AbstractService {
    @Autowired
    WXPayApis wxPayApis;
    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String resp = wxPayApis.callback(request);
        response.setStatus(200);
        response.setHeader("Content-Type", "text/xml;charset=UTF-8");
        response.getWriter().write(resp);
    }
}

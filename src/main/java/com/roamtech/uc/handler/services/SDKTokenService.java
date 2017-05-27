package com.roamtech.uc.handler.services;

import com.roamtech.uc.cache.model.Session;
import com.roamtech.uc.cache.model.Token;
import com.roamtech.uc.client.RoamtechApis;
import com.roamtech.uc.client.RoamtechTokenResponse;
import com.roamtech.uc.handler.services.wrapper.AbstractRequest;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Application;
import com.roamtech.uc.model.User;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by roam-caochen on 2016/12/9.
 */
public class SDKTokenService extends AbstractService {
    private String appId;
    private String appKey;
    @Autowired
    @Qualifier("roamtechApis")
    RoamtechApis roamtechApis;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        UCRequest tokenReq = new UCRequest(request);
        int status = ErrorCode.SUCCESS;
        User user = null;
        Session session = null;
        Token token = null;
        if (tokenReq.validate()) {
            if(ucService.isSessionValid(tokenReq.getSessionId())) {
                user = ucService.findOne(tokenReq.getUserid());
                String phone = user.getPhone();
                RoamtechTokenResponse tokenResp = roamtechApis.getToken(appId, appKey, phone);
                if (tokenResp != null) {
                    if (tokenResp.getErrorNo() == 0) {
                        token = tokenResp.getAttributes().get("token");
                    }
                    status = tokenResp.getErrorNo();
                } else {
                    status = ErrorCode.ERR_SERVER_ERROR;
                }
            } else {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }

        UCResponse tokenResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status));
        tokenResp.addAttribute("token", token);
        postProcess(baseRequest, response, tokenResp);
    }
}

package com.roamtech.uc.handler.services;

import com.roamtech.uc.cache.model.Session;
import com.roamtech.uc.cache.model.Token;
import com.roamtech.uc.handler.services.wrapper.AbstractRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Application;
import com.roamtech.uc.model.Phone;
import com.roamtech.uc.model.User;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by roam-caochen on 2016/12/9.
 */
public class TokenService extends AbstractService {

    private class TokenRequest extends AbstractRequest {
        private String appId;
        private String appKey;
        private String phone;
        private Integer phoneType;

        public TokenRequest(HttpServletRequest request) {
            super(request);
            appId = (String) request.getAttribute("appId");
            appKey = (String) request.getAttribute("appKey");
            phone = getParameter("phone");
            String type = getParameter("phone_type");
            if (type != null) {
                try {
                    phoneType = Integer.parseInt(type);
                } catch (NumberFormatException e) {
                    phoneType = null;
                }
            } else {
                phoneType = Phone.TYPE_PHONE;
            }
        }

        @Override
        public boolean validate() {
            return StringUtils.isNotBlank(appId) && StringUtils.isNotBlank(appKey) && StringUtils.isNotBlank(phone)
                    && phoneType != null;
        }
    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        TokenRequest tokenReq = new TokenRequest(request);
        int status = ErrorCode.SUCCESS;
        User user = null;
        Session session = null;
        Token token = null;
        if (tokenReq.validate()) {
            Application application = ucService.getApplication(tokenReq.appId, tokenReq.appKey);
            if (application != null) {
                Long tenantId = application.getDeveloperId();
                Phone phone = ucService.findPhone(tokenReq.phone, tenantId, tokenReq.phoneType);
                if (phone != null) {
                    user = ucService.findOne(phone.getUserId());
                }
                if (user == null) {
                    user = new User();
                    user.setUserName(tokenReq.phone);
                    user.setPhone(tokenReq.phone);
                    user.setType(User.NORMAL_USER);
                    user.setStatus(User.STATUS_ACTIVE);
                    user.setTenantId(tenantId);
                    user.setPhoneType(tokenReq.phoneType);
                    user = ucService.save(user);
                    ucService.bindPhone(user.getUserId(), tokenReq.phone, tokenReq.phoneType, tenantId,true);
                } else if (user.getType() != User.NORMAL_USER) {
                    unauthorized(response, ErrorCode.ERR_REQUIRED_PARAMETER_MISSED);
                    return;
                }
                session = ucService.login(user, application);
                token = new Token(session.getSessionId());
            } else {
                unauthorized(response, ErrorCode.ERR_UNAUTHORIZED_CLIENT);
                return;
            }
        } else {
            unauthorized(response, ErrorCode.ERR_REQUIRED_PARAMETER_MISSED);
            return;
        }
        UCResponse tokenResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status));
        tokenResp.addAttribute("token", token);
        postProcess(baseRequest, response, tokenResp);
    }

    private void unauthorized(HttpServletResponse resp, int errorCode) throws IOException {
        resp.setStatus(401);
        UCResponse ucResp = UCResponse.buildResponse(errorCode, ErrorCode.getErrorInfo(errorCode));
        resp.getWriter().write(ucResp.toString());
    }
}

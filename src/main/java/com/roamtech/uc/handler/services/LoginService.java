package com.roamtech.uc.handler.services;

import com.roamtech.uc.cache.model.Session;
import com.roamtech.uc.handler.services.wrapper.AbstractRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.jainsip.rxevent.SendMessageEvent;
import com.roamtech.uc.model.User;
import com.roamtech.uc.util.RxBus;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LoginService extends AbstractService {
    private class LoginRequest extends AbstractRequest {
        private String username;
        private String password;
        private String subscribe;

        public LoginRequest(HttpServletRequest request) {
            super(request);
            username = getParameter("username");
            password = getParameter("password");
            subscribe = getParameter("subscribe");
        }

        public boolean validate() {
            return (username != null && !username.isEmpty() && password != null && !password.isEmpty());
        }

    }

    @Override
    public void doHandle(String target, Request baseRequest,
                         HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        LoginRequest loginRequest = new LoginRequest(request);
        int status = ErrorCode.SUCCESS;
        User user = null;
        Session session = null;
        if (loginRequest.validate()) {
            user = ucService.findActiveOne(loginRequest.username);
            if (user == null) {
                status = ErrorCode.ERR_ACCOUNT_NOEXIST;
            } else {
                //user = ucService.findByUsernameRoam(loginRequest.getUsername());//findOne(user.getUserId());

                if (!user.getPassword().equals(loginRequest.password)) {//!hsid.getPassword().equals(MD5Utils.generateValue(loginRequest.getPassword()))){
                    status = ErrorCode.ERR_PASSWD;
                } else {
                    if (user.getPassword().equals("666666") && user.getType() == User.CLERK_USER) {
                        status = ErrorCode.ERR_PASSWD_DEFAULT;
                    } else if (user.getStatus() != User.STATUS_ACTIVE) {
                        status = ErrorCode.ERR_INVALID_STATUS;
                    }
                    session = ucService.login(user);
                    if (loginRequest.subscribe != null && loginRequest.subscribe.equals("true")) {
                        RxBus.getInstance().post(new SendMessageEvent(null, user.getPhone(), null, null, user.getUserId() + "", SendMessageEvent.NEWUSER_WELCOME));
                    }
                }
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), user);
        ucResp.setSessionId(session != null ? session.getSessionId() : null);
        postProcess(baseRequest, response, ucResp);
    }

}

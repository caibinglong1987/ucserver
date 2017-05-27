package com.roamtech.uc.handler.om.services;

import com.roamtech.uc.cache.model.Session;
import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.AbstractRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.User;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by roam-caochen on 2017/3/3.
 */
public class OMLoginService extends AbstractService {

    private class LoginRequest extends AbstractRequest {
        private String username;
        private String password;

        public LoginRequest(HttpServletRequest request) {
            super(request);
            username = getParameter("username");
            password = getParameter("password");
        }

        public boolean validate() {
            return (username != null && !username.isEmpty() && password != null && !password.isEmpty());
        }

    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LoginRequest loginRequest = new LoginRequest(request);
        int status = ErrorCode.SUCCESS;
        User user = null;
        Session session = null;
        if (loginRequest.validate()) {
            List<User> users = ucService.findByUsername(loginRequest.username);
            if (users.isEmpty()) {
                status = ErrorCode.ERR_ACCOUNT_NOEXIST;
            } else if (users.size() == 1) {
                user = users.get(0);
                if (loginRequest.password.equals(user.getPassword())) {
                    Integer type = user.getType();
                    if (type == User.OPERATOR_USER || type == User.ADMIN_USER) {
                        session = ucService.login(user);
                    } else {
                        status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
                    }
                } else {
                    status = ErrorCode.ERR_PASSWD;
                }
            } else {
                status = ErrorCode.ERR_ADMIN_USERNAME_ERROR;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), user);
        ucResp.setSessionId(session != null ? session.getSessionId() : null);
        postProcess(baseRequest, response, ucResp);
    }
}

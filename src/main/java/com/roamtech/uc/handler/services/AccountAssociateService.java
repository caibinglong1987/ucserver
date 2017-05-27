package com.roamtech.uc.handler.services;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.User;
import com.roamtech.uc.model.UserAssociate;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by roam-caochen on 2017/2/23.
 */
public class AccountAssociateService extends AbstractService {

    private class AccountAssociateRequest extends UCRequest {
        private String username;
        private String password;

        public AccountAssociateRequest(HttpServletRequest request) {
            super(request);
            username = getParameter("username");
            password = getParameter("password");
        }

        @Override
        public boolean validate() {
            return super.validate() && username != null && password != null;
        }
    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        AccountAssociateRequest accountAssociateRequest = new AccountAssociateRequest(request);
        int status = ErrorCode.SUCCESS;
        String associateUsername = null;
        if (accountAssociateRequest.validate()) {
            if (ucService.isSessionValid(accountAssociateRequest.getSessionId())) {
                Long userId = accountAssociateRequest.getUserid();
                User user = ucService.findOne(userId);
                if (user != null) {
                    if (ucService.getParentAccount(userId) == null) {
                        if (ucService.getSubAccounts(userId).isEmpty()) {
                            User userBind = ucService.findByUsernameAndTenantId(accountAssociateRequest.username, user.getTenantId());
                            if (userBind != null) {
                                if (accountAssociateRequest.password.equals(userBind.getPassword())) {
                                    Long userIdBind = userBind.getUserId();
                                    if (userId.longValue() != userIdBind.longValue()) {
                                        if (ucService.getParentAccount(userIdBind) == null) {
                                            UserAssociate userAssociate = new UserAssociate();
                                            userAssociate.setUserId(userId);
                                            userAssociate.setParentUserId(userIdBind);
                                            ucService.saveUserAssociate(userAssociate);
                                            associateUsername = userBind.getUserName();
                                        } else {
                                            status = ErrorCode.ERR_ACCOUNT_ASSOCIATED_NOT_ALLOWED;
                                        }
                                    } else {
                                        status = ErrorCode.ERR_ASSOCIATE_CURRENT_ACCOUNT;
                                    }
                                } else {
                                    status = ErrorCode.ERR_PASSWD;
                                }
                            } else {
                                status = ErrorCode.ERR_ACCOUNT_NOEXIST;
                            }
                        } else {
                            status = ErrorCode.ERR_CURRENT_ACCOUNT_ASSOCIATED;
                        }
                    } else {
                        status = ErrorCode.ERR_CURRENT_ACCOUNT_ASSOCIATED;
                    }
                } else {
                    status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
                }
            } else {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), accountAssociateRequest.getUserid(), accountAssociateRequest.getSessionId());
        ucResp.addAttribute("associated_username", associateUsername);
        postProcess(baseRequest, response, ucResp);
    }
}

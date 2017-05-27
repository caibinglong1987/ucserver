package com.roamtech.uc.handler.services;

import com.alibaba.fastjson.JSON;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.User;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by roam-caochen on 2017/2/17.
 */
public class CallOrMessageDeleteService extends AbstractService {

    private class CallMessageDelRequest extends UCRequest {
        private List<String> callIds;

        public CallMessageDelRequest(HttpServletRequest request) {
            super(request);
            String ids = getParameter("callids");
            if (ids != null) {
                callIds = JSON.parseArray(ids, String.class);
            }
        }

        @Override
        public boolean validate() {
            return super.validate() && callIds != null;
        }
    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        CallMessageDelRequest callMessageDelRequest = new CallMessageDelRequest(request);
        int status = ErrorCode.SUCCESS;
        if (callMessageDelRequest.validate()) {
            if (ucService.isSessionValid(callMessageDelRequest.getSessionId())) {
                if (!callMessageDelRequest.callIds.isEmpty()) {
                    Long userId = callMessageDelRequest.getUserid();
                    User user = ucService.findOne(userId);
                    if (user != null) {
                        String loginPhone = user.getPhone();
                        ucService.deleteAccByCallIds(userId, loginPhone, callMessageDelRequest.callIds);
                    }
                }

            } else {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), callMessageDelRequest.getUserid(), callMessageDelRequest.getSessionId());
        postProcess(baseRequest, response, ucResp);
    }
}

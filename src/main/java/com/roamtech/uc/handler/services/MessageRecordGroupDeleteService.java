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

public class MessageRecordGroupDeleteService extends AbstractService {

    private class MessageGroupDelRequest extends UCRequest {
        private List<String> phoneList;

        public MessageGroupDelRequest(HttpServletRequest request) {
            super(request);
            String phones = getParameter("phones");
            if (phones != null) {
                phoneList = JSON.parseArray(phones, String.class);
            }
        }

        @Override
        public boolean validate() {
            return super.validate() && phoneList != null;
        }
    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        MessageGroupDelRequest messageGroupDelRequest = new MessageGroupDelRequest(request);
        int status = ErrorCode.SUCCESS;
        if (messageGroupDelRequest.validate()) {
            if (ucService.isSessionValid(messageGroupDelRequest.getSessionId())) {
                if (!messageGroupDelRequest.phoneList.isEmpty()) {
                    Long userId = messageGroupDelRequest.getUserid();
                    User user = ucService.findOne(userId);
                    if (user != null) {
                        String loginPhone = user.getPhone();
                        ucService.deleteAccByMessageGroup(userId, loginPhone, messageGroupDelRequest.phoneList);
                    }
                }
            } else {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), messageGroupDelRequest.getUserid(), messageGroupDelRequest.getSessionId());
        postProcess(baseRequest, response, ucResp);
    }

}
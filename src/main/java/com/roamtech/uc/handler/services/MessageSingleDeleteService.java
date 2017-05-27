package com.roamtech.uc.handler.services;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Touch;
import com.roamtech.uc.model.User;
import com.roamtech.uc.opensips.model.Acc;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MessageSingleDeleteService extends AbstractService {
    private class MessageSingleDeleteRequest extends UCRequest {
        private String[] CallIds;

        public MessageSingleDeleteRequest(HttpServletRequest request) {
            super(request);
            String temp = getParameter("callids");
            if (StringUtils.isNotBlank(temp)) {
                if (temp.startsWith("[") && temp.endsWith("]")) {
                    temp = temp.substring(1, temp.length() - 1);
                }
                String[] scallIds = temp.split(",");
                CallIds = new String[scallIds.length];
                for (int i = 0; i < scallIds.length; i++) {
                    CallIds[i] = scallIds[i].replace("\"", "").trim();
                }
            }
        }

        @Override
        public boolean validate() {
            return (super.validate() && (CallIds != null));
        }
    }


    @Override
    public void doHandle(String target, Request baseRequest,
                         HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        MessageSingleDeleteRequest messageSingleDeleteRequest = new MessageSingleDeleteRequest(request);
        int status = ErrorCode.SUCCESS;
        if (messageSingleDeleteRequest.validate()) {
            if (ucService.isSessionValid(messageSingleDeleteRequest.getSessionId())) {
                Long userId = messageSingleDeleteRequest.getUserid();
                List<Touch> touchList = ucService.getTouchs(userId);
                User user = ucService.findOne(messageSingleDeleteRequest.getUserid());
                String phone = user.getPhone();
                for (String CallId : messageSingleDeleteRequest.CallIds) {
                    Acc acc = ucService.findByCallId(CallId);
                    if (acc == null) {
                        continue;
                    }
                    if (acc.getCaller().equals(phone)) {
                        acc.setStatus(1);
                        ucService.updateAccByCallId(CallId, 1, acc.getCalleeStatus());
                    } else if (acc.getCallee().equals(phone)) {
                        acc.setCalleeStatus(1);
                        ucService.updateAccByCallId(CallId, acc.getStatus(), 1);
                    }
                    if (touchList != null && touchList.size() > 0) {
                        for (Touch item : touchList) {
                            if (item.getPhone() != null && acc.getCallee().equals(item.getPhone())) {
                                ucService.updateAccByCallId(CallId, acc.getStatus(), 1);
                            }
                            if (item.getPhone() != null && acc.getCaller().equals(item.getPhone())) {
                                ucService.updateAccByCallId(CallId, 1, acc.getCalleeStatus());
                            }
                        }
                    }
                    if (acc.getUserId().longValue() == userId.longValue() && acc.getCallee().equals(phone)) {
                        ucService.updateAccByCallId(CallId, 1, 1);
                    }
                }
            } else {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }

        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), messageSingleDeleteRequest.getUserid(), messageSingleDeleteRequest.getSessionId());

        postProcess(baseRequest, response, ucResp);
    }
}

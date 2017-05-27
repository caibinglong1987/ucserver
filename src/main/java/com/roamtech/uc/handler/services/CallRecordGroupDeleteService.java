package com.roamtech.uc.handler.services;

import com.alibaba.fastjson.JSON;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.User;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by roam-caochen on 2017/2/7.
 */
public class CallRecordGroupDeleteService extends AbstractService {

    private class CallGroupDelRequest extends UCRequest {
        private static final String ALL_CALL = "all";
        private static final String MISSED_CALL = "missed";
        private String callStatus = ALL_CALL;
        private List<String> phoneList;

        public CallGroupDelRequest(HttpServletRequest request) {
            super(request);
            String callStatus = getParameter("call_status");
            if (StringUtils.isNotBlank(callStatus)) {
                this.callStatus = callStatus.toLowerCase();
            }
            String phones = getParameter("phones");
            if (phones != null) {
                phoneList = JSON.parseArray(phones, String.class);
            }
        }

        @Override
        public boolean validate() {
            return super.validate() && phoneList != null && (callStatus.equals(ALL_CALL) || callStatus.equals(MISSED_CALL));
        }
    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        CallGroupDelRequest callGroupDelRequest = new CallGroupDelRequest(request);
        int status = ErrorCode.SUCCESS;
        if (callGroupDelRequest.validate()) {
            if (ucService.isSessionValid(callGroupDelRequest.getSessionId())) {
                if (!callGroupDelRequest.phoneList.isEmpty()) {
                    Long userId = callGroupDelRequest.getUserid();
                    User user = ucService.findOne(userId);
                    if (user != null) {
                        String loginPhone = user.getPhone();
                        if (callGroupDelRequest.callStatus.equals(CallGroupDelRequest.ALL_CALL)) {//全部通话记录
                            ucService.deleteAccByAllCallGroup(userId, loginPhone, callGroupDelRequest.phoneList);
                        } else {//未接通话记录
                            ucService.deleteAccByMissedCallGroup(userId, loginPhone, callGroupDelRequest.phoneList);
                        }
                    }
                }
            } else {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }

        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), callGroupDelRequest.getUserid(), callGroupDelRequest.getSessionId());
        postProcess(baseRequest, response, ucResp);
    }
}

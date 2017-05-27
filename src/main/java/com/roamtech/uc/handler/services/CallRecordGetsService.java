package com.roamtech.uc.handler.services;

import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.roamtech.uc.cache.handler.ReadStatusHandler;
import com.roamtech.uc.cache.model.CallReadInfo;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.User;
import com.roamtech.uc.opensips.model.Acc;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public class CallRecordGetsService extends AbstractService {
    @Autowired
    private ReadStatusHandler readStatusHandler;

    private class CallRequest extends UCRequest {
        private static final String ALL_CALL = "all";
        private static final String MISSED_CALL = "missed";
        private Long id = 0L;
        private String callStatus = ALL_CALL;
        private Integer size = 15;
        private String phone;
        private boolean isFetchNew = false;

        public CallRequest(HttpServletRequest request) {
            super(request);
            String temp = getParameter("call_status");
            if (StringUtils.isNotBlank(temp)) {
                callStatus = temp.toLowerCase();
            }
            temp = getParameter("id");
            if (StringUtils.isNotBlank(temp)) {
                try {
                    id = Long.parseLong(temp);
                } catch (NumberFormatException e) {
                    id = null;
                }
            }
            temp = getParameter("size");
            if (StringUtils.isNotBlank(temp)) {
                try {
                    size = Integer.parseInt(temp);
                    if (size < 0) {
                        size = null;
                    }
                } catch (NumberFormatException e) {
                    size = null;
                }
            }
            phone = getParameter("phone");
            temp = getParameter("is_fetch_new");
            if (StringUtils.isNotBlank(temp)) {
                isFetchNew = Boolean.parseBoolean(temp);
            }
        }

        @Override
        public boolean validate() {
            return (super.validate() && id != null && size != null && phone != null && (callStatus.equals(CallRequest.ALL_CALL) || callStatus.equals(CallRequest.MISSED_CALL)));
        }
    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        CallRequest callRequest = new CallRequest(request);
        int status = ErrorCode.SUCCESS;
        List<Acc> list = null;
        if (callRequest.validate()) {
            if (ucService.isSessionValid(callRequest.getSessionId())) {
                Long userId = callRequest.getUserid();
                User user = ucService.findOne(userId);
                if (user != null) {
                    String loginPhone = user.getPhone();
                    if (callRequest.callStatus.equals(CallRequest.ALL_CALL)) {//所有通话记录
                        if (callRequest.isFetchNew) {
                            list = ucService.getAllCallListGreaterThanId(callRequest.id, userId, loginPhone, callRequest.phone, callRequest.size);
                        } else {
                            list = ucService.getAllCallListLessThanId(callRequest.id, userId, loginPhone, callRequest.phone, callRequest.size);
                        }
                    } else {//未接通话记录
                        if (callRequest.isFetchNew) {
                            list = ucService.getMissedCallListGreaterThanId(callRequest.id, userId, loginPhone, callRequest.phone, callRequest.size);
                        } else {
                            list = ucService.getMissedCallListLessThanId(callRequest.id, userId, loginPhone, callRequest.phone, callRequest.size);
                        }
                    }
                    if (!list.isEmpty()) {
                        Long accId = list.get(0).getId();
                        CallReadInfo callReadInfo = readStatusHandler.getCallReadInfo(userId);
                        if (callReadInfo == null) {
                            callReadInfo = new CallReadInfo();
                            callReadInfo.setUserId(userId);
                            callReadInfo.setReadId(accId);
                            readStatusHandler.saveCallReadInfo(callReadInfo);
                        } else if (callReadInfo.getReadId().compareTo(accId) < 0){
                            callReadInfo.setReadId(accId);
                            readStatusHandler.saveCallReadInfo(callReadInfo);
                        }
                    }
                }

            } else {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }

        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), callRequest.getUserid(), callRequest.getSessionId());
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        Set<String> excludes = filter.getExcludes();
        excludes.add("method");
        excludes.add("fromTag");
        excludes.add("toTag");
        excludes.add("sipReason");
        excludes.add("setupTime");
        excludes.add("created");
        excludes.add("message");
        excludes.add("durationTime");
        excludes.add("userId");
        excludes.add("realdest");
        excludes.add("direction");
        excludes.add("status");
        excludes.add("tosip");
        excludes.add("calleeStatus");
        ucResp.setFilter(filter);
        ucResp.addAttribute("call_records", list);
        postProcess(baseRequest, response, ucResp);
    }
}

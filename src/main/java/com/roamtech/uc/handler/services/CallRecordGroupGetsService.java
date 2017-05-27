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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * Created by roam-caochen on 2017/1/22.
 */
public class CallRecordGroupGetsService extends AbstractService {
    @Autowired
    private ReadStatusHandler readStatusHandler;

    private class CallGroupRequest extends UCRequest {
        private static final String ALL_CALL = "all";
        private static final String MISSED_CALL = "missed";
        private Long id = 0L;
        private String callStatus = ALL_CALL;

        public CallGroupRequest(HttpServletRequest request) {
            super(request);
            String temp = getParameter("id");
            if (StringUtils.isNotBlank(temp)) {
                try {
                    id = Long.parseLong(temp);
                } catch (NumberFormatException e) {
                    id = null;
                }
            }
            String callStatus = getParameter("call_status");
            if (StringUtils.isNotBlank(callStatus)) {
                this.callStatus = callStatus.toLowerCase();
            }
        }

        @Override
        public boolean validate() {
            return super.validate() && id != null && (callStatus.equals(ALL_CALL) || callStatus.equals(MISSED_CALL));
        }
    }
    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        CallGroupRequest callGroupRequest = new CallGroupRequest(request);
        int status = ErrorCode.SUCCESS;
        List<Acc> callList = null;
        int unreadNumber = 0;
        if (callGroupRequest.validate()) {
            if (ucService.isSessionValid(callGroupRequest.getSessionId())) {
                Long userId = callGroupRequest.getUserid();
                User user = ucService.findOne(userId);
                if (user != null) {
                    String loginPhone = user.getPhone();
                    CallReadInfo callReadInfo = readStatusHandler.getCallReadInfo(userId);
                    if (callReadInfo == null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.roll(Calendar.DAY_OF_MONTH, false);
                        unreadNumber = ucService.countUnreadCall(calendar.getTime(), userId, loginPhone);
                    } else {
                        unreadNumber = ucService.countUnreadCall(callReadInfo.getReadId(), userId, loginPhone);
                    }
                    if (callGroupRequest.callStatus.equals(CallGroupRequest.ALL_CALL)) {//全部通话记录
                        List<Acc> list = ucService.getAllCallGroupList(callGroupRequest.id, userId, loginPhone);
                        callList = new ArrayList<>();
                        List<String> phones = new ArrayList<>();
                        for (Acc acc : list) {
                            String phone = null;
                            if (acc.getUserId().equals(userId)) {
                                if (acc.getDirection()) {
                                    phone = acc.getCallee();
                                } else {
                                    phone = acc.getCaller();
                                }
                            } else {
                                phone = acc.getCaller();
                                acc.setDirection(false);
                            }
                            if (phone != null) {
                                if (!phones.contains(phone)) {
                                    phones.add(phone);
                                    callList.add(acc);
                                }
                            }
                        }
                    } else {//未接通话记录
                        callList = ucService.getMissedCallGroupList(callGroupRequest.id, userId, loginPhone);
                    }
                    if (!callList.isEmpty()) {
                        Long accId = callList.get(0).getId();
                        if (callReadInfo == null) {
                            callReadInfo = new CallReadInfo();
                            callReadInfo.setUserId(userId);
                            callReadInfo.setReadId(accId);
                            readStatusHandler.saveCallReadInfo(callReadInfo);
                        } else if (callReadInfo.getReadId().compareTo(accId) < 0) {
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

        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), callGroupRequest.getUserid(), callGroupRequest.getSessionId());
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
        excludes.add("status");
        excludes.add("tosip");
        excludes.add("calleeStatus");
        ucResp.setFilter(filter);
        ucResp.addAttribute("call_records", callList);
        ucResp.addAttribute("unread_number", unreadNumber);
        postProcess(baseRequest, response, ucResp);
    }
}

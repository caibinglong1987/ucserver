package com.roamtech.uc.handler.services;

import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.roamtech.uc.cache.handler.ReadStatusHandler;
import com.roamtech.uc.cache.model.MessageReadInfo;
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


public class MessageRecordGetsService extends AbstractService {
    @Autowired
    private ReadStatusHandler readStatusHandler;

    private class MessageRequest extends UCRequest {
        private Long id = 0L;
        private Integer size = 15;
        private String phone;
        private boolean isFetchNew = false;

        public MessageRequest(HttpServletRequest request) {
            super(request);
            String temp = getParameter("id");
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
            return super.validate() && id != null && size != null && phone != null;
        }

    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        MessageRequest messageRequest = new MessageRequest(request);
        int status = ErrorCode.SUCCESS;
        List<Acc> list = null;
        if (messageRequest.validate()) {
            if (ucService.isSessionValid(messageRequest.getSessionId())) {
                Long userId = messageRequest.getUserid();
                User user = ucService.findOne(userId);
                if (user != null) {
                    String loginPhone = user.getPhone();
                    if (messageRequest.isFetchNew) {
                        list = ucService.getMessageListGreaterThanId(messageRequest.id, userId, loginPhone, messageRequest.phone, messageRequest.size);
                    } else {
                        list = ucService.getMessageListLessThanId(messageRequest.id, userId, loginPhone, messageRequest.phone, messageRequest.size);
                    }
                    if (!list.isEmpty()) {
                        Long accId = list.get(0).getId();
                        MessageReadInfo messageReadInfo = readStatusHandler.getMessageReadInfo(userId);
                        if (messageReadInfo == null) {
                            messageReadInfo = new MessageReadInfo();
                            messageReadInfo.setUserId(userId);
                            messageReadInfo.setReadId(accId);
                            readStatusHandler.saveMessageReadInfo(messageReadInfo);
                        } else if (messageReadInfo.getReadId().compareTo(accId) < 0) {
                            messageReadInfo.setReadId(accId);
                            readStatusHandler.saveMessageReadInfo(messageReadInfo);
                        }
                    }
                }

            } else {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), messageRequest.getUserid(), messageRequest.getSessionId());
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        Set<String> excludes = filter.getExcludes();
        excludes.add("method");
        excludes.add("fromTag");
        excludes.add("toTag");
        excludes.add("sipReason");
        excludes.add("setupTime");
        excludes.add("created");
        excludes.add("durationTime");
        excludes.add("userId");
        excludes.add("realdest");
        excludes.add("direction");
        excludes.add("status");
        excludes.add("tosip");
        excludes.add("calleeStatus");
        ucResp.setFilter(filter);
        ucResp.addAttribute("message_records", list);
        postProcess(baseRequest, response, ucResp);
    }
}

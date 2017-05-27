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
import java.util.*;

/**
 * Created by roam-caochen on 2017/2/16.
 */
public class MessageRecordGroupGetsService extends AbstractService {
    @Autowired
    private ReadStatusHandler readStatusHandler;

    private class MessageGroupRequest extends UCRequest {
        private Long id = 0L;

        public MessageGroupRequest(HttpServletRequest request) {
            super(request);
            String temp = getParameter("id");
            if (StringUtils.isNotBlank(temp)) {
                try {
                    id = Long.parseLong(temp);
                } catch (NumberFormatException e) {
                    id = null;
                }
            }
        }

        @Override
        public boolean validate() {
            return super.validate() && id != null;
        }
    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        MessageGroupRequest messageGroupRequest = new MessageGroupRequest(request);
        int status = ErrorCode.SUCCESS;
        List<Acc> messageList = new ArrayList<>();
        int unreadNumber = 0;
        if (messageGroupRequest.validate()) {
            if (ucService.isSessionValid(messageGroupRequest.getSessionId())) {
                Long userId = messageGroupRequest.getUserid();
                User user = ucService.findOne(userId);
                if (user != null) {
                    String loginPhone = user.getPhone();
                    MessageReadInfo messageReadInfo = readStatusHandler.getMessageReadInfo(userId);
                    Date date = null;
                    if (messageReadInfo == null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.roll(Calendar.DAY_OF_MONTH, false);
                        date = calendar.getTime();
                        unreadNumber = ucService.countUnreadMessage(date, userId, loginPhone);
                    } else {
                        unreadNumber = ucService.countUnreadMessage(messageReadInfo.getReadId(), userId, loginPhone);
                    }
                    List<Acc> list = ucService.getMessageGroupList(messageGroupRequest.id, userId, loginPhone);
                    List<String> phones = new ArrayList<>();
                    int count = 0;
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
                                if (count < unreadNumber) {
                                    int unread = 0;
                                    if (messageReadInfo == null) {
                                        unread = ucService.countUnreadMessage(date, userId, loginPhone, phone);
                                    } else {
                                        unread = ucService.countUnreadMessage(messageReadInfo.getReadId(), userId, loginPhone, phone);
                                    }
                                    count += unread;
                                    acc.setUnreadNumber(unread);
                                }
                                messageList.add(acc);
                            }
                        }
                    }
                }
            } else {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }

        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), messageGroupRequest.getUserid(), messageGroupRequest.getSessionId());
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
        excludes.add("status");
        excludes.add("tosip");
        excludes.add("calleeStatus");
        ucResp.setFilter(filter);
        ucResp.addAttribute("message_records", messageList);
        ucResp.addAttribute("unread_number", unreadNumber);
        postProcess(baseRequest, response, ucResp);
    }
}

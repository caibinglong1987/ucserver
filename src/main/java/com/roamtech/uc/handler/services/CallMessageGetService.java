package com.roamtech.uc.handler.services;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Touch;
import com.roamtech.uc.opensips.model.Acc;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.springframework.data.domain.PageRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caibinglong
 * on 2016/11/30.
 * 获取 通话 消息 记录 分页
 * 通话记录 分  全部通话 和 未接来电
 */
public class CallMessageGetService extends AbstractService {

    private final String AllCallMethod = "ALL_CALL";//全部通话
    private final String AllMessageMethod = "ALL_MESSAGE"; //全部消息
    private final String GROUP_MESSAGE_METHOD = "GROUP_MESSAGE";
    private final String MissCallMethod = "MISS_CALL";//未接来电
    private final String CALL_METHOD = "INVITE";
    private final String MESSAGE_METHOD = "MESSAGE";

    private class CallRequest extends UCRequest {

        private int pageIndex = 0;
        private int pageSize = 15;
        private Long id = 0l;
        private String phoneNumber = "";
        private String toPhoneNumber = "";
        private String methodType = AllCallMethod; // 1 全部通话  2 消息 3是 未接来电
        private boolean isNext = true;//true加载更多最新 往上获取历史

        public CallRequest(HttpServletRequest request) {
            super(request);
            String temp = getParameter("methodType");
            if (StringUtils.isNotBlank(temp)) {
                methodType = temp;
            }
            temp = getParameter("id");
            if (StringUtils.isNotBlank(temp)) {
                id = Long.parseLong(temp);
            }

            temp = getParameter("pageIndex");
            if (StringUtils.isNotBlank(temp)) {
                pageIndex = Integer.parseInt(temp);
            }
            temp = getParameter("pageSize");
            if (StringUtils.isNotBlank(temp)) {
                pageSize = Integer.parseInt(temp);
            }

            temp = getParameter("phoneNumber");
            if (StringUtils.isNotBlank(temp)) {
                phoneNumber = temp.trim();
            }

            temp = getParameter("toPhoneNumber");
            if (StringUtils.isNotBlank(temp)) {
                toPhoneNumber = temp.trim();
            }

            temp = getParameter("isNext");
            if (StringUtils.isNotBlank(temp)) {
                isNext = Boolean.parseBoolean(temp);
            }
        }

        @Override
        public boolean validate() {
            return phoneNumber.length() > 0 && toPhoneNumber.length() > 0;
        }
    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        CallRequest callRequest = new CallRequest(request);
        int status = ErrorCode.SUCCESS;
        List<Acc> list = null;
        String touchPhone = "-1";
        List<Touch> touchList;

        if (ucService.isSessionValid(callRequest.getSessionId())) {
            touchList = ucService.getTouchs(callRequest.getUserid());
            if (touchList != null && touchList.size() > 0) {
                touchPhone = touchList.get(0).getPhone();
            }
            switch (callRequest.methodType) {
                case GROUP_MESSAGE_METHOD:
                    if (callRequest.isNext) {
                        list = ucService.getMoreMessageListGroupByCaller(callRequest.id, callRequest.phoneNumber, touchPhone,
                                callRequest.getUserid(), MESSAGE_METHOD, callRequest.pageSize);
                        List<Acc> groupList = new ArrayList<>();
                        if (list != null && list.size() > 0) {
                            for (Acc item : list) {
                                Acc result = ucService.getOneByFromAndToAndUserId(MESSAGE_METHOD, item.getCallee(), item.getCaller(), callRequest.getUserid());
                                if (result != null) {
                                    groupList.add(result);
                                }
                            }
                            list.clear();
                            list = groupList;
                        }

                    } else {
                        list = ucService.getLessMessageListGroupByCaller(callRequest.id, callRequest.phoneNumber, callRequest.getUserid(),
                                MESSAGE_METHOD, callRequest.pageSize);
                    }
                    break;
                case AllCallMethod:
                    //全部电话 包含 未接
                    if (callRequest.isNext) { //
                        list = ucService.getMoreCallList(callRequest.id, CALL_METHOD, callRequest.phoneNumber,
                                callRequest.getUserid(), touchPhone, new PageRequest(callRequest.pageIndex, callRequest.pageSize));
                    } else {
                        list = ucService.getLessCallList(callRequest.id, CALL_METHOD, callRequest.phoneNumber, callRequest.getUserid(),
                                touchPhone, new PageRequest(callRequest.pageIndex, callRequest.pageSize));
                    }
                    break;
                case AllMessageMethod:
                    //全部消息
                    if (callRequest.validate()) {
                        if (callRequest.isNext) {
                            list = ucService.getMoreMessageList(callRequest.id, MESSAGE_METHOD, callRequest.getUserid(),
                                    callRequest.phoneNumber, callRequest.toPhoneNumber, callRequest.pageSize);
                        } else {
                            list = ucService.getLessMessageList(callRequest.id, MESSAGE_METHOD, callRequest.getUserid(),
                                    callRequest.phoneNumber, callRequest.toPhoneNumber, callRequest.pageSize);
                        }
                    }
                    break;
                case MissCallMethod:
                    //未接来电
                    if (callRequest.isNext) {
                        list = ucService.getMoreMissCallList(callRequest.id, CALL_METHOD, callRequest.phoneNumber, touchPhone
                                , new PageRequest(callRequest.pageIndex, callRequest.pageSize));
                    } else {
                        list = ucService.getLessMissedCallList(callRequest.id, CALL_METHOD, callRequest.phoneNumber, touchPhone
                                , new PageRequest(callRequest.pageIndex, callRequest.pageSize));
                    }
                    break;
            }
        } else {
            status = ErrorCode.ERR_SESSIONID_INVALID;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), callRequest.getUserid(), callRequest.getSessionId());
        ucResp.addAttribute("historyList", list);
        postProcess(baseRequest, response, ucResp);
    }
}

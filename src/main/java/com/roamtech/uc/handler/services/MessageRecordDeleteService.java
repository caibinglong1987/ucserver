package com.roamtech.uc.handler.services;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Touch;
import com.roamtech.uc.model.User;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MessageRecordDeleteService extends AbstractService {

    @Override
    public void doHandle(String target, Request baseRequest,
                         HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        UCRequest messageRecordDeleteRequest = new UCRequest(request);
        int status = ErrorCode.SUCCESS;
        if (messageRecordDeleteRequest.validate()) {
            if (ucService.isSessionValid(messageRecordDeleteRequest.getSessionId())) {
                String caller = messageRecordDeleteRequest.getParameter("caller");
                String callee = messageRecordDeleteRequest.getParameter("callee");
                Long myUserid = messageRecordDeleteRequest.getUserid();
                List<Touch> touchList = ucService.getTouchs(myUserid);
                User user = ucService.findOne(myUserid);
                String myPhone = user.getPhone();
                String opposite; //定义会话对方
                if (touchList != null && touchList.size() > 0) {
                    String touchPhone;
                    //络漫宝数据关联处理
                    for (Touch touch : touchList) {
                        if (touch.getPhone() != null) {
                            touchPhone = touch.getPhone();
                            if (touchPhone.equals(caller)) {
                                ucService.updateAccByCallerAndCallee(touchPhone, callee, myUserid);
                            }
                            if (touchPhone.equals(callee)) {
                                ucService.updateAccByCallerAndCallee(touchPhone, caller, myUserid);
                            }
                            if (myPhone.equals(caller)) {
                                ucService.updateAccByCallerAndCallee(touchPhone, callee,myUserid);
                                ucService.updateAccByCallerAndCalleeAndUserid(callee, myUserid);
                            }
                            if (myPhone.equals(callee)) {
                                ucService.updateAccByCallerAndCallee(touchPhone, caller, myUserid);
                                ucService.updateAccByCallerAndCalleeAndUserid(caller, myUserid);
                            }

                        }
                    }
                }
                if (caller.equals(myPhone)) {
                    ucService.updateAccByCallerAndCallee(myPhone, callee, myUserid);
                }
                if (callee.equals(myPhone)) {
                    ucService.updateAccByCallerAndCallee(myPhone, caller, myUserid);
                }

            } else {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), messageRecordDeleteRequest.getUserid(), messageRecordDeleteRequest.getSessionId());
        postProcess(baseRequest, response, ucResp);
    }
}
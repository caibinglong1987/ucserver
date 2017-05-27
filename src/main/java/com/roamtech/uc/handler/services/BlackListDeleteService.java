package com.roamtech.uc.handler.services;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.BlackList;
import com.roamtech.uc.repository.BlackListRepository;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Baron Jobs on 2017/3/1.
 */
public class BlackListDeleteService extends AbstractService{
    @Autowired
    BlackListRepository blackListRepo;

    private class BlackListGetRequest extends UCRequest {
        private String phone;
        public BlackListGetRequest(HttpServletRequest request) {
            super(request);
            phone = getParameter("phone");
        }

        @Override
        public boolean validate() {
            return (super.validate() && StringUtils.isNotBlank(phone));
        }
    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        BlackListGetRequest blackListGetRequest = new BlackListGetRequest(request);
        int status = ErrorCode.SUCCESS;
        BlackList result = null;
        if (blackListGetRequest.validate()) {
            if (ucService.isSessionValid(blackListGetRequest.getSessionId())) {
                result = blackListRepo.findByPhoneAndUserId(blackListGetRequest.phone, blackListGetRequest.getUserid());
                blackListRepo.delete(result);
            } else {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), blackListGetRequest.getUserid(), blackListGetRequest.getSessionId());
        postProcess(baseRequest, response, ucResp);
    }
}

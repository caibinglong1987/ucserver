package com.roamtech.uc.handler.services;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Bell;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;


public class BellGetService extends AbstractService {

    @Override
    public void doHandle(String target, Request baseRequest,
                         HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        UCRequest bellGetRequest = new UCRequest(request);
        int status = ErrorCode.SUCCESS;
        Bell bell = null;
        if (bellGetRequest.validate()) {
            if (ucService.isSessionValid(bellGetRequest.getSessionId())) {
                Date now = new Date(System.currentTimeMillis());
                Long tenantid = ucService.findOne(bellGetRequest.getUserid()).getTenantId();
                bell = ucService.findByTimeAndTenantid(now, tenantid);
            } else {
                status = ErrorCode.ERR_SESSIONID_INVALID;
              }
            } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status),bellGetRequest.getUserid(), bellGetRequest.getSessionId());
        ucResp.addAttribute("bell",bell);
        postProcess(baseRequest, response,ucResp);
    }
}

package com.roamtech.uc.handler.om.services;

import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Refund;
import com.roamtech.uc.model.User;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class RefundGetsService extends AbstractService {
    private class RefundGetsRequest extends UCRequest {
        private Integer group;
        private Integer status;
        private Integer pageindex;
        private Integer pageSize;

        RefundGetsRequest(HttpServletRequest request) {
            super(request);
            String temp = getParameter("group");
            group = 0;
            if (StringUtils.isNotBlank(temp)) {
                group = Integer.parseInt(temp);
            }
            temp = getParameter("pageindex");
            pageindex = 0;
            if (StringUtils.isNotBlank(temp)) {
                pageindex = Integer.parseInt(temp);
            }
            temp = getParameter("pagesize");
            pageSize = 10;
            if (StringUtils.isNotBlank(temp)) {
                pageSize = Integer.parseInt(temp);
            }
            temp = getParameter("status");
            status = 0;
            if (StringUtils.isNotBlank(temp)) {
                status = Integer.parseInt(temp);
            }
        }
    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        RefundGetsRequest refundGetsReq = new RefundGetsRequest(request);
        int status = ErrorCode.SUCCESS;
        List<Refund> refunds = null;
        if (refundGetsReq.validate()) {
            Integer usertype = omService.checkSessionValid(refundGetsReq.getSessionId());
            if(usertype == -1) {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            } else if(usertype == User.ADMIN_USER || usertype == User.OPERATOR_USER) {
                refunds = ucService.getRefunds(refundGetsReq.group,refundGetsReq.status, refundGetsReq.pageindex,refundGetsReq.pageSize);
            } else {
                status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), refundGetsReq.getUserid(), refundGetsReq.getSessionId());
        ucResp.addAttribute("refunds", refunds);
        postProcess(baseRequest, response, ucResp);
    }
}

package com.roamtech.uc.handler.services;

import com.alibaba.fastjson.annotation.JSONField;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Company;
import com.roamtech.uc.model.Server;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by roam-caochen on 2017/2/27.
 */
public class SipDomainGetsService extends AbstractService {

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        UCRequest req = new UCRequest(request);
        int status = ErrorCode.SUCCESS;
        SipDomains sipDomains = new SipDomains();
        if (req.validate()) {
            if (ucService.isSessionValid(req.getSessionId())) {
                Company company = ucService.findCompany(req.getTenantId());
                if (company != null) {
                    List<Server> servers = ucService.getSipServer(company.getServerGroup());
                    if (!servers.isEmpty()) {
                        sipDomains.setDomain(servers.get(0).getHostip());
                    }
                    List<Server> pushServers = ucService.getPushServer(company.getServerGroup());
                    if (!servers.isEmpty()) {
                        sipDomains.setPushDomain(pushServers.get(0).getHostip());
                    }
                }
            } else {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), req.getUserid(), req.getSessionId());
        ucResp.addAttribute("domains", sipDomains);
        postProcess(baseRequest, response, ucResp);
    }

    private static class SipDomains {
        @JSONField(name = "domain")
        private String domain;
        @JSONField(name = "push_domain")
        private String pushDomain;

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getPushDomain() {
            return pushDomain;
        }

        public void setPushDomain(String pushDomain) {
            this.pushDomain = pushDomain;
        }
    }
}

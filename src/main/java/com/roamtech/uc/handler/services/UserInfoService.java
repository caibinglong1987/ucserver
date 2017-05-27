package com.roamtech.uc.handler.services;

import com.roamtech.uc.cache.model.VoiceNumber;
import com.roamtech.uc.cache.model.VoiceTalk;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.*;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public class UserInfoService extends AbstractService {

    private class UserInfoRequest extends UCRequest {
        private Integer type;
        private String versionName;

        public UserInfoRequest(HttpServletRequest request) {
            super(request);
            String type = getParameter("type");
            if (type != null) {
                this.type = Integer.parseInt(type);
            }
            versionName = getParameter("version_name");
        }

        @Override
        public boolean validate() {
            return super.validate() && type != null && versionName != null;
        }

    }

    @Override
    public void doHandle(String target, Request baseRequest,
                         HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        UserInfoRequest hsidRequest = new UserInfoRequest(request);
        int status = ErrorCode.SUCCESS;
        User user = null;
        List<Touch> touchs = null;
        String sipDomain = null;
        String pushDomain = null;
        List<VoiceNumber> voiceNumbers = null;
        VoiceTalk voiceTalk = null;
        List<Phone> phones = null;
        if (hsidRequest.validate()) {
            if (ucService.isRoamSdkValid(hsidRequest.type, hsidRequest.versionName)) {
                if (ucService.isSessionValid(hsidRequest.getSessionId())) {
                    Long userId = hsidRequest.getUserid();
                    user = ucService.findOne(userId);
                    if (user == null) {
                        status = ErrorCode.ERR_ACCOUNT_NOEXIST;
                    } else {
                        touchs = ucService.getTouchs(userId);
                        Company company = ucService.findCompany(user.getTenantId());
                        if (company != null) {
                            List<Server> servers = ucService.getSipServer(company.getServerGroup());
                            if (!servers.isEmpty()) {
                                sipDomain = servers.get(0).getHostip();
                            }
                            List<Server> pushServers = ucService.getPushServer(company.getServerGroup());
                            if (!pushServers.isEmpty()) {
                                pushDomain = pushServers.get(0).getHostip();
                            }
                        }
                        voiceNumbers = ucService.getAvailableVoiceNumber(userId);
                        voiceTalk = ucService.getCurrentAvailableVoiceTalk(userId);
                        phones = ucService.getPhones(userId);
                    }
                } else {
                    status = ErrorCode.ERR_SESSIONID_INVALID;
                }
            } else {
                status = ErrorCode.ERR_INVALID_SDK_VERSION;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse hsidResponse;
        if (user != null) {
            hsidResponse = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), user);
            hsidResponse.addAttribute("touchs", touchs);
            hsidResponse.addAttribute("sip_domain", sipDomain);
            hsidResponse.addAttribute("push_domain", pushDomain);
            hsidResponse.addAttribute("voicenumbers", voiceNumbers);
            hsidResponse.addAttribute("voiceavailable", voiceTalk);
            hsidResponse.addAttribute("phones", phones);
        } else {
            hsidResponse = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), hsidRequest.getUserid(), hsidRequest.getSessionId());
        }
        postProcess(baseRequest, response, hsidResponse);
    }

}

package com.roamtech.uc.handler.services;

import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Phone;
import com.roamtech.uc.model.User;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by roam-caochen on 2017/1/16.
 */
public class ContactUpdateService extends AbstractService {

    private class ContactUpdateRequest extends UCRequest {
        private String origContact;
        private Integer origContactType;
        private String contact;
        private Integer contactType;

        public ContactUpdateRequest(HttpServletRequest request) {
            super(request);
            origContact = getParameter("orig_contact");
            if (origContact != null) {
                String origType = getParameter("orig_contact_type");
                if (origType != null) {
                    try {
                        origContactType = Integer.parseInt(origType);
                    } catch (NumberFormatException e) {
                        origContactType = null;
                    }
                }
            }
            contact = getParameter("contact");
            if (contact != null) {
                String type = getParameter("contact_type");
                if (type != null) {
                    try {
                        contactType = Integer.parseInt(type);
                    } catch (NumberFormatException e) {
                        contactType = null;
                    }
                }
            }
        }

        @Override
        public boolean validate() {
            return (super.validate() && (origContactType != null || contactType != null));
        }

    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ContactUpdateRequest contactUpdateReq = new ContactUpdateRequest(request);
        int status = ErrorCode.SUCCESS;
        Phone result = null;
        if(contactUpdateReq.validate()) {
            if(ucService.isSessionValid(contactUpdateReq.getSessionId())) {
                if (contactUpdateReq.origContactType != null && contactUpdateReq.contactType != null) {//更新联系方式
                    Phone origPhone = ucService.findPhone(contactUpdateReq.origContact, contactUpdateReq.getTenantId(), contactUpdateReq.origContactType);
                    if (origPhone != null && origPhone.getUserId().equals(contactUpdateReq.getUserid())) {
                        Phone phone = ucService.findPhone(contactUpdateReq.contact, contactUpdateReq.getTenantId(), contactUpdateReq.contactType);
                        if (phone != null) {
                            if (!phone.getUserId().equals(contactUpdateReq.getUserid())) {
                                status = ErrorCode.ERR_CONTACT_UPDATE_FAILED;
                            } else {
                                if (!origPhone.equals(phone)) {
                                    ucService.unbindPhone(origPhone);
                                    User user = ucService.findOne(contactUpdateReq.getUserid());
                                    if (user.getPhone().equals(origPhone.getPhone()) && user.getPhoneType().equals(origPhone.getPhoneType())) {
                                        user.setPhone(contactUpdateReq.contact);
                                        user.setPhoneType(contactUpdateReq.contactType);
                                        ucService.save(user);
                                    }
                                }
                                result = phone;
                            }
                        } else {
                            User user = ucService.findOne(contactUpdateReq.getUserid());
                            if (user.getPhone().equals(origPhone.getPhone()) && user.getPhoneType().equals(origPhone.getPhoneType())) {
                                user.setPhone(contactUpdateReq.contact);
                                user.setPhoneType(contactUpdateReq.contactType);
                                ucService.save(user);
                            }
                            origPhone.setPhone(contactUpdateReq.contact);
                            origPhone.setPhoneType(contactUpdateReq.contactType);
                            result = ucService.savePhone(origPhone);
                        }
                    } else {
                        status = ErrorCode.ERR_CONTACT_UPDATE_FAILED;
                    }
                } else if (contactUpdateReq.contactType == null) {//删除联系方式
                    status = ErrorCode.ERR_CONTACT_UPDATE_FAILED;
                    User user = ucService.findOne(contactUpdateReq.getUserid());
                    if (!user.getPhone().equals(contactUpdateReq.origContact) || !user.getPhoneType().equals(contactUpdateReq.origContactType)) {
                        Phone origPhone = ucService.findPhone(contactUpdateReq.origContact, contactUpdateReq.getTenantId(), contactUpdateReq.origContactType);
                        if (origPhone != null && origPhone.getUserId().equals(contactUpdateReq.getUserid())) {
                            ucService.unbindPhone(origPhone);
                            result = origPhone;
                            status = ErrorCode.SUCCESS;
                        }
                    }
                } else {//新增联系方式
                    Phone phone = ucService.findPhone(contactUpdateReq.contact, contactUpdateReq.getTenantId(), contactUpdateReq.contactType);
                    result = phone;
                    if (phone == null) {
                        result = ucService.bindPhone(contactUpdateReq.getUserid(), contactUpdateReq.contact, contactUpdateReq.contactType, contactUpdateReq.getTenantId(), true);
                    } else if (!phone.getUserId().equals(contactUpdateReq.getUserid())) {
                        status = ErrorCode.ERR_CONTACT_UPDATE_FAILED;
                    }
                }
            } else {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucRes = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status));
        ucRes.addAttribute("contact", result);
        postProcess(baseRequest, response, ucRes);
    }
}

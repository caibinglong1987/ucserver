package com.roamtech.uc.handler.om.services;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.roamtech.uc.opensips.model.om.OMAcc;
import com.roamtech.uc.repository.UserRepository;
import com.roamtech.uc.opensips.repository.OMCallRecordRepository;
import org.eclipse.jetty.server.Request;

import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

public class CallRecordGetsService extends AbstractService {
    @Autowired
    OMCallRecordRepository OMCallRecordRepo;
    @Autowired
    UserRepository UserRepo;

    private class CallRecordGetsRequest extends UCRequest {
        private Long[] userids;
        private String phone;
        private String caller;
        private String callee;
        private Date createTimeFrom;
        private Date createTimeTo;
        private Integer minDuration;
        private Integer maxDuration;
        private Integer pageIndex = 1;
        private Integer pageSize = 10;
        private String orderBy;
        private String descOrAsc = "asc";

        CallRecordGetsRequest(HttpServletRequest request) {
            super(request);
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
            phone = getParameter("phone");
            caller = getParameter("caller");
            callee = getParameter("callee");
            String temp = getParameter("duration_min");
            if(temp != null) {
                minDuration = Integer.parseInt(temp);
            }
            temp = getParameter("duration_max");
            if(temp != null) {
                maxDuration = Integer.parseInt(temp);
            }
            temp = getParameter("createtime_from");
            if(temp != null) {
                try {
                    createTimeFrom = sdf.parse(temp);
                } catch (ParseException e) {
                    createTimeFrom = null;
                    e.printStackTrace();
                }
            }
            temp = getParameter("createtime_to");
            if(temp != null) {
                try {
                    createTimeTo = sdf.parse(temp);
                } catch (ParseException e) {
                    createTimeTo = null;
                    e.printStackTrace();
                }
            }
            temp = getParameter("pageindex");
            if(temp != null) {
                pageIndex = Integer.parseInt(temp);
            }
            temp = getParameter("pagesize");
            if(temp != null) {
                pageSize = Integer.parseInt(temp);
            }
            temp = getParameter("orderby");
            if(temp != null) {
                orderBy = temp;
            }
            temp = getParameter("desc_or_asc");
            if(temp != null) {
                descOrAsc = temp;
            }
        }
    }
    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        CallRecordGetsRequest callRecordGetsRequest = new CallRecordGetsRequest(request);
        int status = ErrorCode.SUCCESS;
        Page<OMAcc> callRecords = null;
        if(callRecordGetsRequest.validate()) {
            Integer usertype = omService.checkSessionValid(callRecordGetsRequest.getSessionId());
            if(usertype == -1) {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            } else if(usertype == User.ADMIN_USER || usertype == User.OPERATOR_USER) {
                if (callRecordGetsRequest.phone != null) {
                    if (UserRepo.findByPhoneLikeAndTenantId(callRecordGetsRequest.phone+"%", callRecordGetsRequest.getTenantId()).size() != 0) {
                        List<User> userList = UserRepo.findByPhoneLikeAndTenantId(callRecordGetsRequest.phone+"%", callRecordGetsRequest.getTenantId());
                        if (userList.size() != 0) {
                            int i = 0;
                            callRecordGetsRequest.userids = new Long[userList.size()];
                            for (User user : userList) {
                                if (user.getUserId() != null) {
                                    Long useridForQuery = user.getUserId();
                                    callRecordGetsRequest.userids[i] = useridForQuery;
                                    i ++;
                                }
                            }
                        }
                    } else {
                        callRecordGetsRequest.userids = new Long[1];
                        callRecordGetsRequest.userids[0] = -9L;
                    }
                }
                callRecords = findSearch(callRecordGetsRequest);

                for (OMAcc callRecord:callRecords) {
                    if (callRecord.getUserId() != null) {
                        User user = UserRepo.findOne(callRecord.getUserId());
                        if (user != null) {
                            if (user.getPhone() != null) {
                                callRecord.setUserinfo(user.getPhone());
                            }
                        } else {
                            callRecord.setUserinfo("user not found");
                        }
                    } else {
                        callRecord.setUserinfo("userid not exist");
                    }
                }
            } else {
                status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), callRecordGetsRequest.getUserid(),callRecordGetsRequest.getSessionId());
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        Set<String> excludes = filter.getExcludes();
        excludes.add("method");
        excludes.add("from_tag");
        excludes.add("to_tag");
        excludes.add("callid");
        excludes.add("message");
        excludes.add("userid");
        excludes.add("realdest");
        excludes.add("direction");
        excludes.add("status");
        excludes.add("callee_status");
        excludes.add("setuptime");
        ucResp.setFilter(filter);
        ucResp.addAttribute("callRecords", callRecords);
        if (callRecords != null) {
            ucResp.addAttribute("totalcount",callRecords.getTotalElements());
        }
        postProcess(baseRequest, response, ucResp);
    }

    public Page<OMAcc> findSearch(final CallRecordGetsRequest callRecordGetsRequest) {
        Assert.notNull(callRecordGetsRequest);
        Page<OMAcc> result = null;
        Sort sort = null;
        Specification<OMAcc> spec = new Specification<OMAcc>() {
            @Override
            public Predicate toPredicate(Root<OMAcc> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<>();
                if (callRecordGetsRequest.userids != null) {
                    CriteriaBuilder.In<Long> in = cb.in(root.<Long>get("userId"));   //筛选条件 phone
                    for (Long userid : callRecordGetsRequest.userids) {
                        in.value(userid);
                    }
                    list.add(in);
                }
                if (callRecordGetsRequest.caller != null) {
                    list.add(cb.equal(root.get("caller").as(String.class),callRecordGetsRequest.caller));
                }
                if (callRecordGetsRequest.callee != null) {
                    list.add(cb.equal(root.get("callee").as(String.class),callRecordGetsRequest.callee));
                }
                if (callRecordGetsRequest.minDuration != null) {
                    list.add(cb.gt(root.get("duration").as(Integer.class),callRecordGetsRequest.minDuration));
                }
                if (callRecordGetsRequest.maxDuration != null) {
                    list.add(cb.lt(root.get("duration").as(Integer.class),callRecordGetsRequest.maxDuration));
                }
                if (callRecordGetsRequest.createTimeFrom != null) {
                    list.add(cb.greaterThan(root.<Date>get("created"), callRecordGetsRequest.createTimeFrom));  //筛选条件 createtime_from
                }
                if (callRecordGetsRequest.createTimeTo != null) {
                    list.add(cb.lessThan(root.<Date>get("created"), callRecordGetsRequest.createTimeTo));  //筛选条件 createtime_to
                }
                list.add(cb.equal(root.get("method"),"INVITE"));

                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        };


        if (callRecordGetsRequest.orderBy != null) {
            if (callRecordGetsRequest.descOrAsc.equals("desc")) {
                sort = new Sort(Sort.Direction.DESC, callRecordGetsRequest.orderBy);
            } else {
                sort = new Sort(Sort.Direction.ASC, callRecordGetsRequest.orderBy);
            }
            result = OMCallRecordRepo.findAll(spec,new PageRequest(callRecordGetsRequest.pageIndex-1 , callRecordGetsRequest.pageSize, sort));
        } else {
            result = OMCallRecordRepo.findAll(spec,new PageRequest(callRecordGetsRequest.pageIndex-1 , callRecordGetsRequest.pageSize));
        }

        return result;
    }
}

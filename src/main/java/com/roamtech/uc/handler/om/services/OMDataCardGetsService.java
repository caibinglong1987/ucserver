package com.roamtech.uc.handler.om.services;

import com.alibaba.fastjson.JSON;
import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.DataCardTraffic;
import com.roamtech.uc.model.User;
import com.roamtech.uc.repository.UserRepository;
import com.roamtech.uc.repository.om.OMDataCardTrafficRepository;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Baron Jobs on 2017/2/28.
 */
public class OMDataCardGetsService extends AbstractService {
    @Autowired
    OMDataCardTrafficRepository omDataCardTrafficRepo;
    @Autowired
    UserRepository userRepo;

    private class OMDataCardGetsRequest extends UCRequest {
        private String phone;
        private Long[] userids = null;
        private Date effectDatetimeFrom;
        private Date effectDatetimeTo;
        private Date failureDatetimeFrom;
        private Date failureDatetimeTo;
        private Integer pageIndex = 1;
        private Integer pageSize = 10;


        public OMDataCardGetsRequest(HttpServletRequest request) {
            super(request);
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
            phone = getParameter("phone");
            String temp = getParameter("effect_datetime_from");
            if(temp != null) {
                try {
                    effectDatetimeFrom = sdf.parse(temp);
                } catch (ParseException e) {
                    effectDatetimeFrom = null;
                    e.printStackTrace();
                }
            }
            temp = getParameter("effect_datetime_to");
            if(temp != null) {
                try {
                    effectDatetimeTo = sdf.parse(temp);
                } catch (ParseException e) {
                    effectDatetimeTo = null;
                    e.printStackTrace();
                }
            }
            temp = getParameter("failure_datetime_from");
            if(temp != null) {
                try {
                    failureDatetimeFrom = sdf.parse(temp);
                } catch (ParseException e) {
                    failureDatetimeFrom = null;
                    e.printStackTrace();
                }
            }
            temp = getParameter("failure_datetime_to");
            if(temp != null) {
                try {
                    failureDatetimeTo = sdf.parse(temp);
                } catch (ParseException e) {
                    failureDatetimeTo = null;
                    e.printStackTrace();
                }
            }
            temp = getParameter("pageindex");
            if (StringUtils.isNotBlank(temp)) {
                pageIndex = Integer.parseInt(temp);
            }
            temp = getParameter("pagesize");
            if (StringUtils.isNotBlank(temp)) {
                pageSize = Integer.parseInt(temp);
            }
        }
    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        OMDataCardGetsRequest omDataCardGetsRequest = new OMDataCardGetsRequest(request);
        int status = ErrorCode.SUCCESS;
        Map<String, Object> resultMap = null;
        Page<DataCardTraffic> result = null;
        if (omDataCardGetsRequest.validate()) {
            Integer usertype = omService.checkSessionValid(omDataCardGetsRequest.getSessionId());
            if (usertype == -1) {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            } else if (usertype == User.ADMIN_USER || usertype == User.OPERATOR_USER) {
                if (omDataCardGetsRequest.phone != null) {
                    List<User> users = userRepo.findByPhone(omDataCardGetsRequest.phone);
                    if (users.size() != 0) {
                        omDataCardGetsRequest.userids = new Long[users.size()];
                        int i = 0;
                        for (User user : users) {
                            omDataCardGetsRequest.userids[i++] = user.getUserId();
                        }
                    } else {
                        omDataCardGetsRequest.userids = new Long[1];
                        omDataCardGetsRequest.userids[0] = -9L;
                    }
                }
                result = findSearch(omDataCardGetsRequest);
                List<DataCardTraffic> dataCardList = result.getContent();
                List<Map<String, Object>> dataCardMapList = new ArrayList<>();
                int i = 0;
                for (DataCardTraffic datacard:dataCardList) {
                    String dataCardjson = JSON.toJSONString(datacard);
                    Map<String, Object> datacardMap = JSON.parseObject(dataCardjson);
                    Long userid = datacard.getUserId();
                    if (userid != null) {
                        User user = null;
                        if (userid == 0) {
                            datacardMap.put("userphone","络漫所有");
                        } else {
                            user = userRepo.findOne(userid);
                            if (user != null) {
                                if (user.getPhone() != null) {
                                    datacardMap.put("userphone",user.getPhone());
                                } else {
                                    datacardMap.put("userphone","userphone not exist");
                                }
                            } else {
                                datacardMap.put("userphone","user not exist");
                            }
                        }
                    } else {
                        datacardMap.put("userphone","userid not exist");
                    }
                    datacardMap.remove("status");
                    switch (datacard.getStatus()) {
                        case 0:
                            datacardMap.put("status","未确认");
                            break;
                        case 1:
                            datacardMap.put("status","已确认");
                            break;
                        case 2:
                            datacardMap.put("status","已取消");
                            break;
                    }
                    dataCardMapList.add(i++ , datacardMap);
                }
                resultMap = transBean2Map(result);
                resultMap.remove("content");
                resultMap.put("content", dataCardMapList);
            } else {
                status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), omDataCardGetsRequest.getUserid(), omDataCardGetsRequest.getSessionId());
        ucResp.addAttribute("datacards",resultMap);
        postProcess(baseRequest, response, ucResp);
    }

    public Page<DataCardTraffic> findSearch(final OMDataCardGetsRequest omDataCardGetsRequest) {
        Assert.notNull(omDataCardGetsRequest);
        Page<DataCardTraffic> result;
        Sort sort = null;

        Specification<DataCardTraffic> spec = new Specification<DataCardTraffic>() {
            @Override
            public Predicate toPredicate(Root<DataCardTraffic> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<>();
                if (omDataCardGetsRequest.userids != null) {
                    CriteriaBuilder.In<Long> in = cb.in(root.<Long>get("userId"));   //筛选条件 phone
                    for (Long userid : omDataCardGetsRequest.userids) {
                        in.value(userid);
                    }
                    list.add(in);
                }
                if (omDataCardGetsRequest.effectDatetimeFrom != null) {
                    list.add(cb.greaterThanOrEqualTo(root.<Date>get("effectDatetime"), omDataCardGetsRequest.effectDatetimeFrom));  //筛选条件 effect_datetime_from
                }
                if (omDataCardGetsRequest.effectDatetimeTo != null) {
                    list.add(cb.lessThanOrEqualTo(root.<Date>get("effectDatetime"), omDataCardGetsRequest.effectDatetimeTo));  //筛选条件 effect_datetime_to
                }
                if (omDataCardGetsRequest.failureDatetimeFrom != null) {
                    list.add(cb.greaterThanOrEqualTo(root.<Date>get("failureDatetime"), omDataCardGetsRequest.failureDatetimeFrom));  //筛选条件 failure_datetime_from
                }
                if (omDataCardGetsRequest.failureDatetimeTo != null) {
                    list.add(cb.lessThanOrEqualTo(root.<Date>get("failureDatetime"), omDataCardGetsRequest.failureDatetimeTo));  //筛选条件 failure_datetime_to
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        };
        result = omDataCardTrafficRepo.findAll(spec, new PageRequest(omDataCardGetsRequest.pageIndex - 1, omDataCardGetsRequest.pageSize));
        return result;
    }

    public static Map<String, Object> transBean2Map(Object obj) {
        if(obj == null){
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);

                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }
        return map;
    }

}

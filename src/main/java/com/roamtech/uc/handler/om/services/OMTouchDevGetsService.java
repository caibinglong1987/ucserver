package com.roamtech.uc.handler.om.services;

import com.alibaba.fastjson.JSON;
import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.Touch;
import com.roamtech.uc.model.User;
import com.roamtech.uc.repository.UserRepository;
import com.roamtech.uc.repository.om.OMTouchRepository;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Baron Jobs on 2017/2/27.
 */
public class OMTouchDevGetsService extends AbstractService{
    @Autowired
    OMTouchRepository OMTouchRepo;
    @Autowired
    UserRepository UserRepo;

    private class OMTouchDevGetsRequest extends UCRequest {
        private String userPhone;
        private String devPhone;
        private Integer pageIndex = 1;
        private Integer pageSize = 10;

        public OMTouchDevGetsRequest(HttpServletRequest request) {
            super(request);
            userPhone = getParameter("userphone");
            devPhone = getParameter("devphone");
            String temp = getParameter("pageindex");
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
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        OMTouchDevGetsRequest omTouchDevGetsRequest = new OMTouchDevGetsRequest(request);
        int status = ErrorCode.SUCCESS;
        Map<String, Object> resultMap = null;
        if (omTouchDevGetsRequest.validate()) {
            Integer usertype = omService.checkSessionValid(omTouchDevGetsRequest.getSessionId());
            if (usertype == -1) {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            } else if (usertype == User.ADMIN_USER || usertype == User.OPERATOR_USER) {
                Page<Touch> result = findSearch(omTouchDevGetsRequest);
                List<Touch> touchList = result.getContent();
                List<Map<String, Object>> touchMapList = new ArrayList<>();
                int i = 0;
                for (Touch touch : touchList) {
                    String touchJson = JSON.toJSONString(touch);
                    Map<String, Object> touchMap = JSON.parseObject(touchJson);
                    Long userid = touch.getUserId();
                    if (userid != null) {
                        User user = null;
                        if (userid == 0) {
                            touchMap.put("userphone","络漫池");
                        } else {
                            user = UserRepo.findOne(userid);
                            if (user != null) {
                                if (user.getPhone() != null) {
                                    touchMap.put("userphone",user.getPhone());
                                } else {
                                    touchMap.put("userphone","userphone not exist");
                                }
                            } else {
                                touchMap.put("userphone", "user not exist");
                            }
                        }
                    } else {
                            touchMap.put("userphone", "userid not exist");
                    }
                    touchMap.remove("verified");
                    if (touch.getVerified() != null) {
                        if (touch.getVerified()) {
                            touchMap.put("verified","已认证");
                        } else {
                            touchMap.put("verified","未认证");
                        }
                    } else {
                        touchMap.put("verified","未知");
                    }
                    touchMap.remove("devtype");
                    switch (touch.getDevtype()) {
                        case 1:
                            touchMap.put("devtype","卡池专用号码");
                            break;
                        case 2:
                            touchMap.put("devtype","卡池自由号码");
                            break;
                        case 3:
                            touchMap.put("devtype","络漫宝设备");
                            break;
                        default:
                            touchMap.put("devtype","未知设备");
                            break;
                    }
                    touchMapList.add( i++ , touchMap);
                    }
                resultMap = transBean2Map(result);
                resultMap.remove("content");
                resultMap.put("content",touchMapList);
            } else {
                status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), omTouchDevGetsRequest.getUserid(), omTouchDevGetsRequest.getSessionId());
        ucResp.addAttribute("touches", resultMap);
        postProcess(baseRequest, response, ucResp);
    }

    public Page<Touch> findSearch(final OMTouchDevGetsRequest omTouchDevGetsRequest) {
        Assert.notNull(omTouchDevGetsRequest);
        Page<Touch> result;
        Sort sort = null;

        Specification<Touch> spec = new Specification<Touch>() {
            @Override
            public javax.persistence.criteria.Predicate toPredicate(Root<Touch> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<javax.persistence.criteria.Predicate> list = new ArrayList<>();
                if (omTouchDevGetsRequest.userPhone != null) {
                    User user = UserRepo.findByPhoneAndTenantId(omTouchDevGetsRequest.userPhone, omTouchDevGetsRequest.getTenantId());
                    if (user != null) {
                        list.add(cb.equal(root.get("userId"), user.getUserId()));
                    } else {
                        list.add(cb.equal(root.get("userId"), -9L));
                    }
                }
                if (omTouchDevGetsRequest.devPhone != null) {
                    list.add(cb.equal(root.get("phone"), omTouchDevGetsRequest.devPhone));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        };
        result = OMTouchRepo.findAll(spec, new PageRequest(omTouchDevGetsRequest.pageIndex -1, omTouchDevGetsRequest.pageSize));
        return result;
    }

    // Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map
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

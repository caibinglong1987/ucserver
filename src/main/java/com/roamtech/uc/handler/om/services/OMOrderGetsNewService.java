package com.roamtech.uc.handler.om.services;

import com.alibaba.fastjson.annotation.JSONField;
import com.roamtech.uc.handler.services.AbstractService;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.handler.services.wrapper.UCRequest;
import com.roamtech.uc.handler.services.wrapper.UCResponse;
import com.roamtech.uc.model.*;
import com.roamtech.uc.model.om.OMOrder;
import com.roamtech.uc.model.om.OrderDetailJoin;
import com.roamtech.uc.repository.*;
import com.roamtech.uc.repository.om.OMOrderDetailRepository;
import org.eclipse.jetty.server.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import javax.persistence.criteria.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class OMOrderGetsNewService extends AbstractService {
    @Autowired
    OMOrderDetailRepository OMOrderDetailRepo;
    @Autowired
    OrderDetailRepository OrderDetailRepo;
    @Autowired
    UserRepository UserRepo;
    @Autowired
    PrdTypeRepository PrdTypeRepo;
    @Autowired
    CityRepository CityRepo;
    @Autowired
    UserAddressRepository UserAddressRepo;
    @Autowired
    PaymentRepository paymentRepo;

    private class OMOrderGetsRequest extends UCRequest {
        private String orderid;
        private String phone;
        private Long[] userids;
        private Date createTimeFrom;
        private Date createTimeTo;
        private Integer shippingStatus;//商品配送情况：0，未发货；1，已发货；2，已收货；3，备货中
        private Integer payStatus;//支付状态：0，未付款；1，付款中；2，已付款 3,已退款
        private Integer orderStatus;//订单状态：0，未确认；1，已确认；2，已取消；3，退货中；4，退款中；5，关闭
        private Integer payId;
        private Long productType;
        private String areaname;
        private Date effectDatetimeFrom;
        private Date effectDatetimeTo;
        private Date failureDatetimeFrom;
        private Date failureDatetimeTo;
        private Integer callDuration;
        private String exclusiveNumber;
        private Long userAddressId;
        private Integer pageIndex = 1;
        private Integer pageSize = 10;
        private String orderBy;
        private String descOrAsc = "asc";
        //        private OrderDetail orderDetail;
        OMOrderGetsRequest(HttpServletRequest request) {
            super(request);
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
            orderid = getParameter("orderid");
            String temp = getParameter("shipping_status");
            if(temp != null) {
                shippingStatus = Integer.parseInt(temp);
            }
            temp = getParameter("pay_status");
            if (temp != null) {
                payStatus = Integer.parseInt(temp);
            }
            temp = getParameter("order_status");
            if (temp != null) {
                orderStatus = Integer.parseInt(temp);
            }
            temp = getParameter("payid");
            if (temp != null) {
                payId = Integer.parseInt(temp);
            }

            temp = getParameter("product_type");
            if(temp != null) {
                productType = Long.parseLong(temp);
            }

            temp = getParameter("areaname");
            if (temp != null) {
                areaname = temp;
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
            temp = getParameter("effect_datetime_from");
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
            temp = getParameter("call_duration");
            if(temp != null) {
                callDuration = Integer.parseInt(temp);
            }
            exclusiveNumber = getParameter("exclusive_number");

            temp = getParameter("phone");
            if(temp != null) {
                phone = temp;
            }
            temp = getParameter("user_address_id");
            if (temp != null) {
                userAddressId = Long.parseLong(temp);
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

    private class OMOrderDetailDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        @JSONField(name = "id")
        private Long Id;
        @JSONField(name = "orderid")
        private String orderid;
        @JSONField(name = "userid")
        private Long userid;
        @JSONField(name = "userinfo")
        private String userInfo;
        @JSONField(name = "createtime",format="yyyy-MM-dd HH:mm:ss")
        private Date createtime;
        @JSONField(name = "order_status")
        private Integer orderStatus;//订单状态。0，未确认；1，已确认；2，已取消；3，退货中；4，退款中；5，关闭
        @JSONField(name = "pay_status")
        private Integer payStatus;//支付状态；0，未付款；1，付款中；2，已付款 3,已退款
        @JSONField(name = "shipping_status")
        private Integer shippingStatus;//商品配送情况，0，未发货；1，已发货；2，已收货；3，备货中
        @JSONField(name = "shipping_time")
        private Date shippingTime;
        @JSONField(name = "ship_address")
        private Map<String, Object> shipAddress;
        @JSONField(name = "pay_time")
        private Date payTime;
        @JSONField(name = "payment")
        private String payment;
        @JSONField(name = "pay_voucher")
        private String payVoucher;
        @JSONField(name = "usit_price")
        private Double usitPrice;
        @JSONField(name = "source")
        private String source;

        @JSONField(name = "prd_name")
        private String prdName;
        //        @JSONField(name = "prd_attr")
//        private List<ODPrdAttr> prdAttr;
        @JSONField(name = "prd_type")
        private String prdType;
        @JSONField(name = "exclusive_number")
        private String phone;
        @JSONField(name = "call_duration")
        private Integer callDuration;
        @JSONField(name = "areaname")
        private String areaname;
        @JSONField(name = "effect_datetime",format="yyyy-MM-dd HH:mm:ss")
        private Date effectDatetime;
        @JSONField(name = "failure_datetime",format="yyyy-MM-dd HH:mm:ss")
        private Date failureDatetime;

        public Long getId() {
            return Id;
        }

        public void setId(Long id) {
            Id = id;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }

        public Long getUserid() {
            return userid;
        }

        public void setUserid(Long userid) {
            this.userid = userid;
        }

        public String getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(String userInfo) {
            this.userInfo = userInfo;
        }

        public Date getCreatetime() {
            return createtime;
        }

        public void setCreatetime(Date createtime) {
            this.createtime = createtime;
        }

        public Integer getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(Integer orderStatus) {
            this.orderStatus = orderStatus;
        }

        public Integer getPayStatus() {
            return payStatus;
        }

        public void setPayStatus(Integer payStatus) {
            this.payStatus = payStatus;
        }

        public Integer getShippingStatus() {
            return shippingStatus;
        }

        public void setShippingStatus(Integer shippingStatus) {
            this.shippingStatus = shippingStatus;
        }

        public Date getShippingTime() {
            return shippingTime;
        }

        public void setShippingTime(Date shippingTime) {
            this.shippingTime = shippingTime;
        }

        public Map<String, Object> getShipAddress() {
            return shipAddress;
        }

        public void setShipAddress(Map<String, Object> shipAddress) {
            this.shipAddress = shipAddress;
        }

        public Date getPayTime() {
            return payTime;
        }

        public void setPayTime(Date payTime) {
            this.payTime = payTime;
        }

        public String getPayment() {
            return payment;
        }

        public void setPayment(String payment) {
            this.payment = payment;
        }

        public String getPayVoucher() {
            return payVoucher;
        }

        public void setPayVoucher(String payVoucher) {
            this.payVoucher = payVoucher;
        }

        public Double getUsitPrice() {
            return usitPrice;
        }

        public void setUsitPrice(Double usitPrice) {
            this.usitPrice = usitPrice;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getPrdName() {
            return prdName;
        }

        public void setPrdName(String prdName) {
            this.prdName = prdName;
        }

        public String getPrdType() {
            return prdType;
        }

        public void setPrdType(String prdType) {
            this.prdType = prdType;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Integer getCallDuration() {
            return callDuration;
        }

        public void setCallDuration(Integer callDuration) {
            this.callDuration = callDuration;
        }

        public String getAreaname() {
            return areaname;
        }

        public void setAreaname(String areaname) {
            this.areaname = areaname;
        }

        public Date getEffectDatetime() {
            return effectDatetime;
        }

        public void setEffectDatetime(Date effectDatetime) {
            this.effectDatetime = effectDatetime;
        }

        public Date getFailureDatetime() {
            return failureDatetime;
        }

        public void setFailureDatetime(Date failureDatetime) {
            this.failureDatetime = failureDatetime;
        }
    }

    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        OMOrderGetsRequest orderGetsReq = new OMOrderGetsRequest(request);
        int status = ErrorCode.SUCCESS;
        Page<OrderDetailJoin> orderdetails = null;
        List<OMOrderDetailDTO> orderDetailDTOs = new ArrayList<>();
        if(orderGetsReq.validate()) {
            Integer usertype = omService.checkSessionValid(orderGetsReq.getSessionId());
            if(usertype == -1) {
                status = ErrorCode.ERR_SESSIONID_INVALID;
            } else if(usertype == User.ADMIN_USER || usertype == User.OPERATOR_USER) {
                if (orderGetsReq.phone != null) {
                    if (UserRepo.findByPhoneLikeAndTenantId(orderGetsReq.phone+"%", orderGetsReq.getTenantId()).size() != 0) {
                        List<User> userList = UserRepo.findByPhoneLikeAndTenantId(orderGetsReq.phone+"%", orderGetsReq.getTenantId());
                        if (userList.size() != 0) {
                            int i = 0;
                            orderGetsReq.userids = new Long[userList.size()];
                            for (User user : userList) {
                                if (user.getUserId() != null) {
                                    Long useridForQuery = user.getUserId();
                                    orderGetsReq.userids[i] = useridForQuery;
                                    i ++;
                                }
                            }
                        }
                    } else {
                        orderGetsReq.userids = new Long[1];
                        orderGetsReq.userids[0] = 0L;
                    }
                }
                orderdetails = findSearch(orderGetsReq);


                for (OrderDetailJoin orderDetail : orderdetails) {
                    OMOrderDetailDTO orderDetailDTO = new OMOrderDetailDTO();
                    orderDetailDTO.setId(orderDetail.getId());
//                    orderDetailDTO.orderInfo = new OMOrderGetsDTO();
                    OMOrder order = orderDetail.getPrdOrder();//OrderRepo.findOne(orderDetail.getOrderid());
                    if (order != null) {
                        orderDetailDTO.setOrderid(orderDetail.getPrdOrder().getId().toString());
                        if (order.getUserid() != null) {
                            orderDetailDTO.setUserid(order.getUserid());
                            User user = UserRepo.findOne(order.getUserid());
                            if (user.getPhone() != null) {
                                orderDetailDTO.setUserInfo(user.getPhone());
                            }
                        }
                        if (order.getCreatetime() != null) {
                            orderDetailDTO.setCreatetime(order.getCreatetime());
                        }
                        if (order.getOrderStatus() != null) {
                            orderDetailDTO.setOrderStatus(order.getOrderStatus());
                        }
                        if (order.getPayStatus() != null) {
                            orderDetailDTO.setPayStatus(order.getPayStatus());
                        }
                        if (order.getShippingStatus() != null) {
                            orderDetailDTO.setShippingStatus(order.getShippingStatus());
                        }
                        if (order.getShippingtime() != null) {
                            orderDetailDTO.setShippingTime(order.getShippingtime());
                        }
                        if (order.getShipAddress() != null) {
                            UserAddress userAddress = UserAddressRepo.findOne(order.getShipAddress());
                            Map<String, Object> map = new HashMap<>();
                            if (userAddress != null) {
                                map = transBean2Map(userAddress);
                                map.remove("country");
                                map.remove("province");
                                map.remove("city");
                                map.remove("status");
                                orderDetailDTO.setShipAddress(map);
                                if (userAddress.getCountry() != null) {
                                    City city = CityRepo.findOne(userAddress.getCountry().longValue());
                                    if (city != null) {
                                        map.put("country", city.getName());
                                    }
                                }
                                if (userAddress.getProvince() != null) {
                                    City city = CityRepo.findOne(userAddress.getProvince().longValue());
                                    if (city != null) {
                                        map.put("province", city.getName());
                                    }
                                }
                                if (userAddress.getDistrict() != null) {
                                    City city = CityRepo.findOne(userAddress.getDistrict().longValue());
                                    if (city != null) {
                                        map.put("district", city.getName());
                                    }
                                }
                            }
                            if (map != null) {
                                orderDetailDTO.setShipAddress(map);
                            }
                        }
                        if (order.getPaytime() != null) {
                            orderDetailDTO.setPayTime(order.getPaytime());
                        }
                        if (order.getPayId() != null) {
                            Payment payment = paymentRepo.findOne(order.getPayId().longValue());
                            if (payment != null) {
                                orderDetailDTO.setPayment(payment.getName());
                            }
                        }
                        if (order.getPayvoucher() != null) {
                            orderDetailDTO.setPayVoucher(order.getPayvoucher());
                        }
                        if (order.getPrice() != null) {
                            orderDetailDTO.setUsitPrice(order.getPrice());
                        }
                    } else {
                        if (OrderDetailRepo.findOne(orderDetail.getId()) != null) {
                            orderDetailDTO.setOrderid(OrderDetailRepo.findOne(orderDetail.getId()).getOrderid().toString());
                        }
                    }
                    if (orderDetail.getProductid() != null) {
                        Product product = ucService.getProduct(orderDetail.getProductid()); //ProductRepo.findOne(orderDetail.getProductid());
                        if (product.getName() != null) {
                            orderDetailDTO.setPrdName(product.getName());
                        }
                        if (product.getTypeid() != null) {
                            PrdType prdType = PrdTypeRepo.findOne(product.getTypeid());
                            if (prdType.getName() != null) {
                                orderDetailDTO.setPrdType(prdType.getName());
                            }
                        }
                    }
                    if (orderDetail.getPhone() != null) {
                        orderDetailDTO.setPhone(orderDetail.getPhone());
                    }

                    if (orderDetail.getCallDuration() != null) {
                        orderDetailDTO.setCallDuration(orderDetail.getCallDuration());
                    }
                    if (orderDetail.getAreaname() != null) {
                        orderDetailDTO.setAreaname(orderDetail.getAreaname());
                    }
                    if (orderDetail.getEffectDatetime() != null) {
                        orderDetailDTO.setEffectDatetime(orderDetail.getEffectDatetime());
                    }
                    if (orderDetail.getFailureDatetime() != null) {
                        orderDetailDTO.setFailureDatetime(orderDetail.getFailureDatetime());
                    }
                    if (orderDetail.getSource() != null) {
                        orderDetailDTO.setSource(orderDetail.getSource());
                    }
//                    if (orderDetail.getAttrs() != null) {
//                        orderDetailDTO.setPrdAttr(orderDetail.getOdprdattrs());
//                    }
                    orderDetailDTOs.add(orderDetailDTO);
                }
            } else {
                status = ErrorCode.ERR_INSUFFICIENT_PRIVILEGE;
            }
        } else {
            status = ErrorCode.ERR_REQUIRED_PARAMETER_MISSED;
        }
        UCResponse ucResp = UCResponse.buildResponse(status, ErrorCode.getErrorInfo(status), orderGetsReq.getUserid(),orderGetsReq.getSessionId());
        ucResp.addAttribute("orders", orderDetailDTOs);
        if (orderdetails != null) {
            ucResp.addAttribute("totalcount",orderdetails.getTotalElements());
        }
        postProcess(baseRequest, response, ucResp);
    }


    public Page<OrderDetailJoin> findSearch(final OMOrderGetsRequest orderGetsReq) {
        Assert.notNull(orderGetsReq);
        Page<OrderDetailJoin> result;
        Sort sort = null;

        Specification<OrderDetailJoin> spec = new Specification<OrderDetailJoin>() {
            @Override
            public Predicate toPredicate(Root<OrderDetailJoin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<>();
                Join<OrderDetail, OMOrder> odJoin = root.join("prdOrder", JoinType.LEFT);

                if (orderGetsReq.orderid != null) {
                    list.add(cb.like(odJoin.<String>get("id"), orderGetsReq.orderid +"%")); //筛选条件orderid
                }
                if (orderGetsReq.shippingStatus != null) {
                    list.add(cb.equal(odJoin.get("shippingStatus"), orderGetsReq.shippingStatus));  //筛选条件 shipping_status
                }
                if (orderGetsReq.payStatus != null) {
                    list.add(cb.equal(odJoin.get("payStatus"), orderGetsReq.payStatus));  //筛选条件 pay_status
                }
                if (orderGetsReq.orderStatus != null) {
                    list.add(cb.equal(odJoin.get("orderStatus"), orderGetsReq.orderStatus));  //筛选条件 order_status
                }
                if (orderGetsReq.payId != null) {
                    list.add(cb.equal(odJoin.get("payId"), orderGetsReq.payId));  //筛选条件 payid
                }
                if (orderGetsReq.userAddressId != null) {
                    list.add(cb.equal(odJoin.get("shipAddress"), orderGetsReq.userAddressId));  //筛选条件 ship_address
                }
                if(orderGetsReq.productType != null) {
                    list.add(cb.equal(root.get("productid"), orderGetsReq.productType));  //筛选条件 product_type
                }
                if (orderGetsReq.callDuration != null) {
                    list.add(cb.equal(root.get("callDuration"), orderGetsReq.callDuration));  //筛选条件 call_duration
                }

                if (orderGetsReq.exclusiveNumber != null) {
                    list.add(cb.equal(root.get("phone"), orderGetsReq.exclusiveNumber));  //筛选条件 exclusive_number
                }

//                if (orderGetsReq.useridForQuery != null) {
//                    list.add(cb.equal(odJoin.get("userid"), orderGetsReq.useridForQuery));  //筛选条件 phone
//                }
//                if (orderGetsReq.userids != null) {
//                    list.add(cb.in(odJoin.get("userid")).in(orderGetsReq.userids));  //筛选条件 phone
//                }
                if (orderGetsReq.userids != null) {
                    CriteriaBuilder.In<Long> in = cb.in(odJoin.<Long>get("userid"));   //筛选条件 phone
                    for (Long userid : orderGetsReq.userids) {
                        in.value(userid);
                    }
                    list.add(in);
                }

                if (orderGetsReq.areaname != null) {
                    list.add(cb.equal(root.get("areaname"), orderGetsReq.areaname));  //筛选条件 areaname
                }
                if (orderGetsReq.effectDatetimeFrom != null) {
                    list.add(cb.greaterThan(root.<Date>get("effectDatetime"), orderGetsReq.effectDatetimeFrom));  //筛选条件 effect_datetime_from
                }
                if (orderGetsReq.effectDatetimeTo != null) {
                    list.add(cb.lessThan(root.<Date>get("effectDatetime"), orderGetsReq.effectDatetimeTo));  //筛选条件 effect_datetime_to
                }
                if (orderGetsReq.failureDatetimeFrom != null) {
                    list.add(cb.greaterThan(root.<Date>get("failureDatetime"), orderGetsReq.failureDatetimeFrom));  //筛选条件 failure_datetime_from
                }
                if (orderGetsReq.failureDatetimeTo != null) {
                    list.add(cb.lessThan(root.<Date>get("failureDatetime"), orderGetsReq.failureDatetimeTo));  //筛选条件 failure_datetime_to
                }
                if (orderGetsReq.createTimeFrom != null) {
                    list.add(cb.greaterThan(odJoin.<Date>get("createtime"), orderGetsReq.createTimeFrom));  //筛选条件 createtime_from
                }
                if (orderGetsReq.createTimeTo != null) {
                    list.add(cb.lessThan(odJoin.<Date>get("createtime"), orderGetsReq.createTimeTo));  //筛选条件 createtime_to
                }
                list.add(cb.equal(odJoin.get("tenantId"), orderGetsReq.getTenantId()));  //租户限制
//                list.add(cb.greaterThan(root.<Long>get("Id"), 1L));
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        };

        if (orderGetsReq.orderBy != null) {
            switch (orderGetsReq.orderBy) {
                case "orderid":
                    orderGetsReq.orderBy = "prdOrder.id";
                    break;
                case "usit_price":
                    orderGetsReq.orderBy = "prdOrder.price";
                    break;
                case "effect_datetime":
                    orderGetsReq.orderBy = "effectDatetime";
                    break;
                case "failure_datetime":
                    orderGetsReq.orderBy = "failureDatetime";
                    break;
                case "call_duration":
                    orderGetsReq.orderBy = "callDuration";
                    break;
                case "shippingtime":
                    orderGetsReq.orderBy = "prdOrder.shippingtime";
                    break;
                case "createtime":
                    orderGetsReq.orderBy = "prdOrder.createtime";
                    break;
            }
            if (orderGetsReq.descOrAsc.equals("desc")) {
                sort = new Sort(Sort.Direction.DESC, orderGetsReq.orderBy);
            } else {
                sort = new Sort(Sort.Direction.ASC, orderGetsReq.orderBy);
            }
        }


            if (orderGetsReq.orderBy != null) {
                result = OMOrderDetailRepo.findAll(spec,new PageRequest(orderGetsReq.pageIndex-1 , orderGetsReq.pageSize, sort));
            } else {
                result = OMOrderDetailRepo.findAll(spec,new PageRequest(orderGetsReq.pageIndex-1 , orderGetsReq.pageSize));
            }

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

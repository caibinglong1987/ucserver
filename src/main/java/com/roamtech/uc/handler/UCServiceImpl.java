package com.roamtech.uc.handler;

import com.roamtech.uc.cache.handler.SessionHandler;
import com.roamtech.uc.cache.handler.TouchVoiceHandler;
import com.roamtech.uc.cache.model.*;
import com.roamtech.uc.client.BssApis;
import com.roamtech.uc.client.CredtripApis;
import com.roamtech.uc.client.CredtripCredit;
import com.roamtech.uc.exception.UCBaseException;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.model.*;
import com.roamtech.uc.opensips.model.Acc;
import com.roamtech.uc.opensips.repository.AccRepository;
import com.roamtech.uc.opensips.repository.MissedCallsRepository;
import com.roamtech.uc.repository.*;
import com.roamtech.uc.util.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component("ucService")
public class UCServiceImpl implements UCService, InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(UCServiceImpl.class);
    @Autowired
    UserRepository userRepo;
    @Autowired
    VerifyCodeRepository vcRepo;
    @Autowired
    PhoneRepository phoneRepo;
    @Autowired
    TouchRepository touchRepo;
    @Autowired
    TouchChansRepository touchchansRepo;
    @Autowired
    SessionHandler sessionHandler;
    @Autowired
    AppReleasedRepository appRepo;

    @Autowired
    @Qualifier("ucRestSms")
    private RestSMSHandler restSms;

    @Autowired
    AreaCodeRepository areaCodeRepo;
    @Autowired
    AreaGroupRepository areaGroupRepo;
    @Autowired
    CityRepository cityRepo;
    @Autowired
    ShippingRepository shippingRepo;
    @Autowired
    ShippingAreaRepository shippingAreaRepo;
    @Autowired
    PaymentRepository paymentRepo;
    @Autowired
    StoreRepository storeRepo;
    @Autowired
    PrdCategoryRepository prdCategoryRepo;
    @Autowired
    PrdBrandRepository prdBrandRepo;
    @Autowired
    ProductRepository productRepo;
    @Autowired
    PrdUnitPriceRepository prdUnitPriceRepo;
    @Autowired
    PrdDiscountRepository prdDiscountRepo;
    @Autowired
    CartRepository cartRepo;
    @Autowired
    OrderRepository orderRepo;
    @Autowired
    OrderDetailRepository orderDetailRepo;
    @Autowired
    RefundRepository refundRepo;
    @Autowired
    UserAddressRepository userAddrRepo;
    @Autowired
    UserProfileRepository userProfileRepo;
    @Autowired
    UserEVoucherRepository userEVoucherRepo;
    @Autowired
    EVoucherRepository evoucherRepo;
    @Autowired
    OutletRepository outletRepo;
    @Autowired
    DataCardRepository datacardRepo;
    @Autowired
    DataCardAreaRepository dcaRepo;
    @Autowired
    DataCardTrafficRepository dctRepo;
    @Autowired
    TouchVoiceRepository tvRepo;
    @Autowired
    TouchVoiceHandler tvHandler;
    @Autowired
    CDRRepository cdrRepo;
    @Autowired
    RoleRepository roleRepo;
    @Autowired
    CompanyRepository compRepo;
    @Autowired
    UserThirdPartyRepository utpRepo;
    @Autowired
    HomepageRepository homepageRepo;
    @Autowired
    @Qualifier("credtripApis")
    private CredtripApis credtripApis;
    @Autowired
    BssApis bssApis;
    @Autowired
    OrderHandler orderHandler;
    @Autowired
    RefundReasonRepository refundReasonRepo;
    @Autowired
    MissedCallsRepository missedCallsRepo;

    @Autowired
    AccRepository accRepo;

    @Autowired
    ApplicationRepository applicationRepo;

    @Autowired
    ServerRepository serverRepo;

    @Autowired
    BellRepository bellRepo;

    @Autowired
    UserAssociateRepository userAssociateRepo;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd HH");
    private Integer nodeid;
    private Integer nodenum;
    private Integer snlen;
    private SNGenerator snGen;
    private Boolean released = false;
    private List<EVoucher> mEvoucherCache;
    private Map<Long, EVoucher> mEvoucherMapCache = new ConcurrentHashMap<Long, EVoucher>();
    private long lastEvoucherCacheTime;
    private ReentrantLock _lockEvouchersLock = new ReentrantLock();

    public Boolean getReleased() {
        return released;
    }

    public void setReleased(Boolean released) {
        this.released = released;
    }

    public Integer getNodeid() {
        return nodeid;
    }

    public void setNodeid(Integer nodeid) {
        this.nodeid = nodeid;
    }

    public Integer getNodenum() {
        return nodenum;
    }

    public void setNodenum(Integer nodenum) {
        this.nodenum = nodenum;
    }

    public Integer getSnlen() {
        return snlen;
    }

    public void setSnlen(Integer snlen) {
        this.snlen = snlen;
    }

    @Override
    public User findByUsernameRoam(String username) {
        return UserHandlerUtil.findByUsernameRoam(username);
    }

    @Override
    public List<User> findByUsername(String username) {
        return UserHandlerUtil.findByUsername(username);
    }

    @Override
    public User findByUsernameAndTenantId(String username, Long tenantId) {
        return UserHandlerUtil.findByUsernameAndTenantId(username, tenantId);
    }

    @Override
    public User findByEmail(String email) {
        return UserHandlerUtil.findByEmail(email);
    }

    @Override
    public User findByPhone(String phone) {
        return UserHandlerUtil.findByPhone(phone);
    }

    @Override
    public User findOne(Long userId) {
        return UserHandlerUtil.findOne(userId);
    }

    @Override
    public User findActiveOne(String key) {
        return UserHandlerUtil.findActiveOne(key);
    }

    @Override
    public User save(User user) {
        return UserHandlerUtil.save(user);
    }

    @Override
    public int changePassword(Long userId, String opassword, String npassword) {
        return UserHandlerUtil.changePassword(userId, opassword, npassword);
    }

    @Override
    public int resetPassword(Long userId, String password) {
        return UserHandlerUtil.resetPassword(userId, password);
    }

    @Override
    public int changeMobile(Long userId, String mobile) {
        return UserHandlerUtil.changeMobile(userId, mobile);
    }

    @Override
    public int changeEmail(Long userId, String email) {
        return UserHandlerUtil.changeEmail(userId, email);
    }

    @Override
    public boolean isUsernameExist(String username) {
        return findByUsernameRoam(username) != null;
    }

    @Override
    public boolean isMobileExist(String mobile) {
        return findByPhone(mobile) != null;
    }

    @Override
    public boolean isEmailExist(String email) {
        return findByEmail(email) != null;
    }

    @Override
    public Session login(User user, Application application) {
        if (user.getUserName().equals("zhengsj")) {
            bssApis.testHKApis();
        }
        return UserHandlerUtil.login(user, application);
    }

    @Override
    public Session login(User user) {
        return login(user, null);
    }

    @Override
    public void logout(Long userId, String sessionId) {
        UserHandlerUtil.logout(userId, sessionId);
    }

    @Override
    public User freeze(Long userId) {
        return UserHandlerUtil.freeze(userId);
    }

    @Override
    public User unfreeze(Long userId) {
        return UserHandlerUtil.unfreeze(userId);
    }

    private Random rm = new Random();

    private String getFixLenthString(int strLength) {

        double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);

        String fixLenthString = String.valueOf(pross);

        return fixLenthString.substring(1, strLength + 1);
    }

    @Override
    public VerifyCode getCode(Long userId, String phone, String email) {
        VerifyCode vCode = new VerifyCode();
        vCode.setCode(getFixLenthString(6));
        vCode.setPhone(phone);
        vCode.setEmail(email);
        vCode.setUserId(userId);
        int result = restSms.sendSMS(phone, vCode.getCode(), 5);
        vCode.setStatus(result);
        return vcRepo.save(vCode);
    }

    @Override
    public boolean verifyCode(Long checkId, String checkCode) {
        VerifyCode vCode = vcRepo.findOne(checkId);
        if (vCode == null) {
            return false;
        }
        if (!vCode.getCode().equals(checkCode)) {
            return false;
        }
        Long now = new Date().getTime();
        if (now > vCode.getExpired()) {
            return false;
        }
        return true;
    }

    @Override
    public Phone bindPhone(Long userId, String phone, Long tenantId, boolean verified) {
        return bindPhone(userId, phone, null, tenantId, verified);
    }

    @Override
    public Phone bindPhone(Long userId, String phone, Integer phoneType, Long tenantId, boolean verified) {
        Phone phoneObj = new Phone();
        phoneObj.setPhone(phone);
        phoneObj.setUserId(userId);
        phoneObj.setTenantId(tenantId);
        phoneObj.setVerified(verified);
        if (phoneType != null) {
            phoneObj.setPhoneType(phoneType);
        } else {
            phoneObj.setPhoneType(Phone.TYPE_PHONE);
        }
        return phoneRepo.save(phoneObj);
    }

    @Override
    public Phone savePhone(Phone phone) {
        return phoneRepo.save(phone);
    }

    @Override
    public void unbindPhone(Phone phone) {
        phoneRepo.delete(phone);
    }

    @Override
    public boolean isSessionValid(String sessionId) {
        if (StringUtils.isBlank(sessionId)) {
            return false;
        }
        Session session = sessionHandler.findOne(sessionId);
        return session != null;
    }


    @Override
    public Session findSession(String sessionId) {
        return sessionHandler.findOne(sessionId);
    }

    @Override
    public Touch bindTouch(Long userId, String devId, String phone, String wifiSsid, String wifiPassword) {
        Touch touch = touchRepo.findByDevid(devId);
        if (touch == null) {
            touch = new Touch();
        }
        touch.setDevid(devId);
        touch.setPhone(phone);
        touch.setUserId(userId);
        touch.setDevtype(Touch.DEVTYPE_ROAMBOX);
        if (StringUtils.isNotBlank(wifiSsid)) {
            touch.setWifiSsid(wifiSsid);
            touch.setWifiPassword(wifiPassword);
        }
        return touchRepo.save(touch);
    }

    @Override
    public void unbindTouch(Long userId, String devId) {
        Touch touch = touchRepo.findByDevid(devId);
        touch.setPhone(null);
        touch.setUserId(userId);
        touchRepo.save(touch);
    }

    @Override
    public List<Phone> getPhones(Long userId) {
        return phoneRepo.findByUserId(userId);
    }

    @Override
    public List<Touch> getTouchs(Long userId) {
        return touchRepo.findByUserId(userId);
    }

    @Override
    public Phone findPhone(String phone) {
        return phoneRepo.findByPhoneAndTenantIdAndPhoneType(phone, User.ROAM_TENANT_ID, Phone.TYPE_PHONE);
    }

    @Override
    public Phone findPhone(String phone, Long tenantId, Integer phoneType) {
        return phoneRepo.findByPhoneAndTenantIdAndPhoneType(phone, tenantId, phoneType);
    }

    @Override
    public Touch findTouch(String devId) {
        return touchRepo.findByDevid(devId);
    }

    @Override
    public AppReleased checkTouchUpgrade(Integer type, Integer version) {
        List<AppReleased> apps = appRepo.findByTypeAndVersionGreaterThanAndStatusOrderByVersionDesc(type, version, AppReleased.RELEASED_STATUS);
        if (apps.isEmpty()) {
            return null;
        } else {
            for (AppReleased app : apps) {
                if (app.getSupportedMinVersion() != null && app.getSupportedMinVersion().compareTo(version) <= 0) {
                    return app;
                } else if (app.getSupportedMinVersion() == null) {
                    return app;
                }
            }
            return null;
        }
    }

    @Override
    public Integer convertVersion(String version) {
        String[] versions = StringUtils.split(version, ".");
        int va = 0, vb = 0, vc = 0;
        if (versions.length > 0) {
            if (versions[0].startsWith("T") || versions[0].startsWith("V")) {
                va = Integer.parseInt(versions[0].substring(1));
            } else {
                va = Integer.parseInt(versions[0]);
            }
            if (versions.length > 1) {
                vb = Integer.parseInt(versions[1]);
                if (version.length() > 2) {
                    vc = Integer.parseInt(versions[2]);
                }
            }
        }
        return va * 1000 + vb * 100 + vc;
    }

    @Override
    public AppReleased checkRoamchatUpgrade(Integer type, Integer version) {
        List<AppReleased> apps = appRepo.findByTypeAndVersionGreaterThanAndStatusOrderByVersionDesc(type, version, AppReleased.RELEASED_STATUS);
        if (apps.isEmpty()) {
            return null;
        } else {
            return apps.get(0);
        }
    }

    @Override
    public List<AreaGroup> getAreaGroups() {
        return CollectionUtils.toList(areaGroupRepo.findAll());
    }

    @Override
    public List<AreaCode> getAreaCodes(Long groupId) {
        return areaCodeRepo.findByGroupid(groupId);
    }

    @Override
    public List<City> getProvinces() {
        return CollectionUtils.toList(cityRepo.findByType(0));
    }

    @Override
    public List<City> getCitysByParent(Long pid) {
        if (pid < 0) {
            return CollectionUtils.toList(cityRepo.findAll());
        }
        return cityRepo.findByPid(pid);
    }

    @Override
    public List<Shipping> getShippings() {
        return CollectionUtils.toList(shippingRepo.findAll());
    }

    @Override
    public List<ShippingArea> getShippingAreas(Integer shippingId) {
        if (shippingId < 0) {
            return CollectionUtils.toList(shippingAreaRepo.findAll());
        }
        return shippingAreaRepo.findByShippingId(shippingId);
    }

    @Override
    public List<Payment> getPayments(Integer terminalType) {
        return CollectionUtils.toList(paymentRepo.findByEnabledAndTerminalTypeOrderBySortAsc(true, terminalType));
    }

    @Override
    public List<Store> getStores() {
        return CollectionUtils.toList(storeRepo.findAll());
    }

    @Override
    public Store findMyStore(Long userId) {
        return storeRepo.findByUserid(userId);
    }

    @Override
    public List<Store> findStoreByName(String name) {
        return storeRepo.findByName(name);
    }

    @Override
    public List<PrdCategory> getPrdCategorys() {
        return CollectionUtils.toList(prdCategoryRepo.findAll());
    }

    @Override
    public List<PrdBrand> getPrdBrands() {
        return CollectionUtils.toList(prdBrandRepo.findAll());
    }

    @Override
    public List<Product> getProducts(Long storeId, Long categoryId, Long brandId) {
        return orderHandler.getProducts(storeId, categoryId, brandId);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return orderHandler.getProductsByName(name);//mergeProductAttr(productRepo.findByName(name));
    }

    @Override
    public List<Product> getProducts(Long[] prdIds) {
        return orderHandler.getProducts(prdIds);//mergeProductAttr(CollectionUtils.toList(productRepo.findAll(Arrays.asList(prdIds))));
    }

    @Override
    public Product getProduct(Long productId) {
        return orderHandler.getProduct(productId);//mergeProductAttr(productRepo.findOne(productId));
    }

    @Override
    public List<PrdUnitPrice> getPrdUnitPrices(Long productId) {
        Date currDate = new Date();
        String currDatetime = DateFormatUtils.format(currDate,
                "yyyy-MM-dd HH:mm:ss");
        return prdUnitPriceRepo.findByfailureDatetime(productId, currDatetime);
    }

    @Override
    public List<PrdDiscount> getPrdDiscounts(Long productId) {
        Date currDate = new Date();
        String currDatetime = DateFormatUtils.format(currDate,
                "yyyy-MM-dd HH:mm:ss");
        return prdDiscountRepo.findByfailureDatetime(productId, currDatetime);
    }

    @Override
    public List<Cart> findCart(Long userId, String sessionId) {
        return cartRepo.findByUserid(userId);//cartRepo.findBySessionid(sessionId);
    }

    @Override
    public Cart addToCart(Long userId, String sessionId, Long productId, Integer quantity, List<ODPrdAttr> cartPrdAttrs) {
        Cart cart = new Cart();
        cart.setUserid(userId);
        cart.setSessionid(sessionId);
        cart.setProductid(productId);
        return cartRepo.save(orderHandler.updateCart(cart, quantity, cartPrdAttrs));
    }

    @Override
    public Cart updateCart(Long cartId, Long productId, Integer quantity, List<ODPrdAttr> cartPrdAttrs) {
        Cart cart = cartRepo.findOne(cartId);
        cart.setProductid(productId);
        return cartRepo.save(orderHandler.updateCart(cart, quantity, cartPrdAttrs));
    }


    @Override
    public void removeFromCart(Long cartId) {
        cartRepo.delete(cartId);
    }

    @Override
    public void resetMyCart(Long userId, String sessionId) {
        //cartRepo.deleteBySessionid(sessionId);
        cartRepo.deleteByUserid(userId);
    }

    @Override
    public Order getOrder(Long orderId) {
        Order order = orderRepo.findOne(orderId);
        order.setOrderDetails(getOrderDetails(order.getId()));
        return order;
    }

    @Override
    public List<Order> getOrders(Long userId, Integer pageindex, Integer limit) {
        List<Order> orders = orderRepo.findByUseridAndOrderStatusNotAndOrderStatusNotOrderByCreatetimeDesc(userId, Order.ORDER_STATUS_INIT, Order.ORDER_STATUS_CLOSED, new PageRequest(pageindex, limit));//.findByUseridOrderByCreatetimeDesc(userId, orderId, limit);
        for (Order order : orders) {
            if ((order.getPayStatus() == Order.PAY_STATUS_INIT || order.getPayStatus() == Order.PAY_STATUS_PAYING) && order.getOrderStatus() != Order.ORDER_STATUS_CANCELLED) {
                Date now = new Date();
                Date orderTime = order.getPaytime() != null ? order.getPaytime() : order.getCreatetime();
                if (now.getTime() - orderTime.getTime() > 15 * 86400000) {
                    orderHandler.cancelOrder(userId, order, false);
                    continue;
                }
            }
            if ((order.getPayStatus() == Order.PAY_STATUS_PAYED) && order.getOrderStatus() != Order.ORDER_STATUS_RETURNING && order.getShippingStatus() == Order.SHIPPING_STATUS_SHIPPED) {
                Date now = new Date();
                Date orderTime = order.getShippingtime();
                if (now.getTime() - orderTime.getTime() > 15 * 86400000) {
                    orderHandler.receivedOrder(order);
                }
            }
            order.setOrderDetails(getOrderDetails(order.getId()));
        }
        return orders;
    }

    /**
     * 购物车选择后提交订单，可能有多个商品。因为购物车去掉了，暂时不会用到该方法
     */
    @Override
    public Order submitOrder(Long userId, String sessionId, Long[] cartIds, Long tenantId) throws UCBaseException {
        List<Cart> carts;
        if (cartIds == null || cartIds.length == 0) {
            carts = findCart(userId, sessionId);
        } else {
            carts = CollectionUtils.toList(cartRepo.findAll(Arrays.asList(cartIds)));
        }
        if (carts == null) {
            return null;
        }
        Order order = submitOrder(userId, carts, tenantId);
        //cartRepo.delete(carts);
        return order;
    }

    /**
     * 单个商品提交订单
     */
    @Override
    public Order submitOrder(Long userId, String sessionId, Long productId, Integer quantity, List<ODPrdAttr> cartPrdAttrs, Long tenantId) throws UCBaseException {
        Cart cart = new Cart();
        cart.setUserid(userId);
        cart.setSessionid(sessionId);
        cart.setProductid(productId);
        cart = orderHandler.updateCart(cart, quantity, cartPrdAttrs);
        List<Cart> carts = new ArrayList<Cart>();
        carts.add(cart);
        return submitOrder(userId, carts, tenantId);
    }

    private Order submitOrder(Long userId, List<Cart> carts, Long tenantId) throws UCBaseException {
        return orderHandler.submitOrder(userId, carts, tenantId);
    }

    @Override
    public Order payOrder(Long userId, Long orderId, Integer payId, String payvoucher) throws UCBaseException {
        orderHandler.confirmOrder(userId, orderId);
        return orderHandler.payOrder(userId, orderId, payId, payvoucher);
    }

    @Override
    public Order paidOrder(Long orderId, String payvoucher, Double amount) {
        return orderHandler.paidOrder(orderId, payvoucher, amount);
    }

    private Double calcTotalRefundFee(Order order) {
        Double fee = order.getPayableAmount();
        /*if(order.getShippingFee()!=null)
            fee+=order.getShippingFee();
		if(order.getTaxFee()!=null)
			fee+=order.getTaxFee();*/
        if (order.getVoucherAmount() != null)
            fee -= order.getVoucherAmount();
        return fee;
    }

    @Override
    public Order cancelOrder(Long userId, Long orderId, Boolean delete) throws UCBaseException {
        return orderHandler.cancelOrder(userId, orderId, delete);
    }

    @Override
    public Order confirmOrder(Long userId, Long orderId) throws UCBaseException {
        return orderHandler.confirmOrder(userId, orderId);
    }

    @Override
    public Order receivedOrder(Long userId, Long orderId) throws UCBaseException {
        return orderHandler.receivedOrder(userId, orderId);
    }

    @Override
    public Order updateOrder(Long userId, Long orderId, Integer shippingId, Long shippingAddr) throws UCBaseException {
        return orderHandler.updateOrder(userId, orderId, shippingId, shippingAddr);
    }

    @Override
    public Order updateOrder(Long userId, Long orderId, Boolean obtainvoucher, Long outletid, String obtainTime) throws UCBaseException {
        Order order = orderRepo.findOne(orderId);
        orderHandler.checkOrderUserMatch(order, userId);
        /*order.setObtainvoucher(obtainvoucher);
        order.setOutletid(outletid);
		try {
			order.setObtaintime(sdf.parse(obtainTime));
		} catch (ParseException e) {
			
		}
		*//*生成提货券*//*
        generateDeliveryEVoucher(order);
		order.setShippingFee(0.00);
		order.setShippingId(null);
		order.setShipAddress(null);
		return orderRepo.save(order);*/
        return order;
    }

    private Order updateTaxFee(Order order) {
        return order;
    }

    @Override
    public Order updateOrder(Long userId, Long orderId, Boolean receipt, String invPayee, String invContent) throws UCBaseException {
        Order order = orderRepo.findOne(orderId);
        orderHandler.checkOrderUserMatch(order, userId);
        order.setReceipt(receipt);
        order.setInvPayee(invPayee);
        order.setInvContent(invContent);
        /*获取发票税费*/
        updateTaxFee(order);
        return orderRepo.save(order);
    }

    @Override
    public OrderDetail cancelOrderDetail(Long userId, Long orderId, Long orderDetailId) throws UCBaseException {
        Order order = orderRepo.findOne(orderId);
        orderHandler.checkOrderUserMatch(order, userId);
        OrderDetail od = orderDetailRepo.findOne(orderDetailId);
        if (order.getPayStatus() == Order.PAY_STATUS_INIT) {
            od.setStatus(Order.ORDER_STATUS_CANCELLED);
        }
        if (order.getShippingStatus() == Order.SHIPPING_STATUS_SHIPPED) {
            /*已发货，则是退货操作*/
            od.setStatus(Order.ORDER_STATUS_RETURNING);
        }
        return orderDetailRepo.save(od);
    }

    @Override
    public List<OrderDetail> getOrderDetails(Long orderId) {
        return orderDetailRepo.findByOrderid(orderId);
    }

    @Override
    public Order addOrderDetail(Long userId, Long orderId, Long productId, Integer quantity, List<ODPrdAttr> odPrdAttrs) throws UCBaseException {
        OrderDetail od = new OrderDetail();
        od.setOrderid(orderId);
        od.setQuantityPerUnit(1);

        return orderHandler.updateOrderDetail(userId, od, productId, quantity, odPrdAttrs);
    }

    @Override
    public Order updateOrderDetail(Long userId, Long orderDetailId, Long productId, Integer quantity, List<ODPrdAttr> odPrdAttrs) throws UCBaseException {
        OrderDetail od = orderDetailRepo.findOne(orderDetailId);
        return orderHandler.updateOrderDetail(userId, od, productId, quantity, odPrdAttrs);
    }

    @Override
    public UserAddress addOrUpdateUserAddress(Long userId, UserAddress addr) {
        addr.setUserId(userId);
        if (addr.getId() != 0L) {
            UserAddress oaddr = userAddrRepo.findOne(addr.getId());
            if (addr.getCountry() == null) {
                addr.setCountry(oaddr.getCountry());
            }
            if (addr.getProvince() == 0) {
                addr.setProvince(oaddr.getProvince());
            }
            if (addr.getCity() == 0) {
                addr.setCity(oaddr.getCity());
            }
            if (addr.getDistrict() == 0) {
                addr.setDistrict(oaddr.getDistrict());
            }
            if (StringUtils.isBlank(addr.getConsignee())) {
                addr.setConsignee(oaddr.getConsignee());
            }
            if (StringUtils.isBlank(addr.getAddress())) {
                addr.setAddress(oaddr.getAddress());
            }
            if (StringUtils.isBlank(addr.getMobile())) {
                addr.setMobile(oaddr.getMobile());
            }
            if (StringUtils.isBlank(addr.getEmail())) {
                addr.setEmail(oaddr.getEmail());
            }
        }
        UserAddress naddr = userAddrRepo.save(addr);
        if (addr.getDefaultaddr()) {
            UserProfile profile = userProfileRepo.findByUserId(userId);
            if (profile == null) {
                profile = new UserProfile();
                profile.setUserId(userId);
            }
            profile.setAddressId(naddr.getId());
            userProfileRepo.save(profile);
            naddr.setDefaultaddr(addr.getDefaultaddr());
        }
        return updateCities(naddr);
    }

    @Override
    public List<UserAddress> getAddresses(Long userId, boolean validAddr) {
        // validAddr可解释为只返回有效的地址，true的话需要查询我的未删除的地址，false返回全部地址
        List<UserAddress> addrs = validAddr ? userAddrRepo.findByUserIdAndStatusNot(userId, UserAddress.DELETED_STATUS) : userAddrRepo.findByUserId(userId);
        UserProfile profile = userProfileRepo.findByUserId(userId);
        for (UserAddress addr : addrs) {
            updateCities(addr);
            if (profile != null && profile.getAddressId().equals(addr.getId())) {
                addr.setDefaultaddr(true);
            }
        }
        return addrs;
    }

    private UserAddress updateCities(UserAddress addr) {
        List<Long> ids = new ArrayList<Long>();
        ids.add(addr.getProvince().longValue());
        ids.add(addr.getCity().longValue());
        ids.add(addr.getDistrict().longValue());
        addr.setCities(CollectionUtils.toList(cityRepo.findAll(ids)));
        return addr;
    }

    private Date getToday() {
        Date now = new Date();
        try {
            return sdfdate.parse(sdfdate.format(now));
        } catch (ParseException e) {
            return now;
        }
    }

    @Override
    public AvailableServiceRDO getCurrentAvailableService(Long userId) {
        Date today = new Date();//getToday();
        List<OrderDetail> utvs = orderDetailRepo.findByUserId(0L);
        if (getReleased()) {
            utvs.addAll(orderDetailRepo.findByUserIdAndStatusAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndPhoneIsNull(userId, Order.ORDER_STATUS_CONFIRMED, today, today));
        } else {
            utvs.addAll(orderDetailRepo.findByUserIdAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndPhoneIsNull(userId, today, today));
        }
        List<ServicePackage> sps = new ArrayList<ServicePackage>();
        Date now = new Date();
        VoiceTalk vt = new VoiceTalk();
        vt.setTotaltime(0L);
        /*处理流量套餐和语音通话套餐*/
        processTrafficVoices(utvs, sps, now, vt);
        /*计算已用掉的通话时长*/
        Long usedTimeSeconds = cdrRepo.sumCallDurationByUserId(userId);
        vt.setUsedtime(usedTimeSeconds == null ? 0 : usedTimeSeconds / 60);
        vt.setRemaindertime(vt.getTotaltime() - vt.getUsedtime());
        /*处理专属号套餐*/
        List<TouchVoice> tvs = tvRepo.findByUserIdAndEffectDatetimeLessThanAndFailureDatetimeGreaterThan(userId, today, today);
        processTouchVoices(sps, now, tvs);

        AvailableServiceRDO rdo = new AvailableServiceRDO();
        rdo.setServicePackages(sps);
        rdo.setVoiceTalk(vt);
        return rdo;
    }

    private void processTrafficVoices(List<OrderDetail> utvs, List<ServicePackage> sps, Date now, VoiceTalk vt) {
        for (OrderDetail utv : utvs) {
            if (utv.getCallDuration() == null || utv.getCallDuration() == 0) {
                ServicePackage sp = processTrafficVoice(now, utv);
                sps.add(sp);
            } else {
                vt.setTotaltime(vt.getTotaltime() + utv.getCallDuration());
            }
        }
    }

    private ServicePackage processTrafficVoice(Date now, OrderDetail utv) {
        ServicePackage sp = new ServicePackage();
        Product prd = productRepo.findOne(utv.getProductid());
        sp.setName(prd.getName());
        sp.setProductId(prd.getId());
        sp.setLogo(prd.getImage());
        sp.setStartTime(utv.getEffectDatetime());
        sp.setEndTime(utv.getFailureDatetime());
        sp.setOrderId(utv.getOrderid());
        sp.setOrderdetailId(utv.getId());
        if (sp.getStartTime().before(now)) {
            sp.setRemainder((sp.getEndTime().getTime() - now.getTime()) / 86400000.0);
        } else {
            sp.setRemainder(utv.getQuantityPerUnit().doubleValue());
        }
        sp.setType(prd.getTypeid().intValue());
        sp.setAreaname(utv.getAreaname());
        sp.setQuantity(utv.getQuantity());
        sp.setSimids(utv.getSimidlist());
        if (!sp.getSimids().isEmpty()) {
            sp.setSimid(sp.getSimids().iterator().next());
        }
        return sp;
    }

    private void processTouchVoices(List<ServicePackage> sps, Date now, List<TouchVoice> tvs) {
        for (TouchVoice tv : tvs) {
            ServicePackage sp = new ServicePackage();
            if (tv.getOrderdetailid() != null) {
                OrderDetail od = orderDetailRepo.findOne(tv.getOrderdetailid());
                if (getReleased()) {
                    if (od.getStatus() != Order.ORDER_STATUS_CONFIRMED) {
                        continue;
                    }
                }
                Product prd = productRepo.findOne(od.getProductid());
                sp.setName(prd.getName());
                sp.setProductId(prd.getId());
                sp.setLogo(prd.getImage());
                sp.setType(prd.getTypeid().intValue());
            }
            sp.setStartTime(tv.getEffectDatetime());
            sp.setEndTime(tv.getFailureDatetime());
            sp.setOrderId(tv.getOrderid());
            sp.setOrderdetailId(tv.getOrderdetailid());
            if (now.after(sp.getStartTime())) {
                sp.setRemainder((sp.getEndTime().getTime() - now.getTime() + 1) / 86400000.0);
            } else {
                sp.setRemainder((sp.getEndTime().getTime() - sp.getStartTime().getTime() + 1) / 86400000.0);
            }
            sp.setPhone(tv.getPhone());
            sps.add(sp);
        }
    }

    @Override
    public AvailableServiceRDO getAvailableServices(Long userId) {
        Date today = new Date();
        List<OrderDetail> utvs = orderDetailRepo.findByUserId(0L);
        if (getReleased()) {
            utvs.addAll(orderDetailRepo.findByUserIdAndStatusAndFailureDatetimeGreaterThanAndPhoneIsNull(userId, Order.ORDER_STATUS_CONFIRMED, today));
        } else {
            utvs.addAll(orderDetailRepo.findByUserIdAndFailureDatetimeGreaterThanAndPhoneIsNull(userId, today));
        }

        List<ServicePackage> sps = new ArrayList<ServicePackage>();
        Date now = new Date();
        VoiceTalk vt = new VoiceTalk();
        vt.setTotaltime(0L);
        processTrafficVoices(utvs, sps, now, vt);
        /*计算已用掉的通话时长*/
        Long usedTimeSeconds = cdrRepo.sumCallDurationByUserId(userId);
        vt.setUsedtime(usedTimeSeconds == null ? 0 : usedTimeSeconds / 60);
        vt.setRemaindertime(vt.getTotaltime() - vt.getUsedtime());
        /*处理专属号套餐*/
        List<TouchVoice> tvs = tvRepo.findByUserIdAndFailureDatetimeGreaterThan(userId, today);
        processTouchVoices(sps, now, tvs);

        AvailableServiceRDO rdo = new AvailableServiceRDO();
        rdo.setServicePackages(sps);
        rdo.setVoiceTalk(vt);
        return rdo;
    }


    @Override
    public AvailableServiceRDO getCurrentAvailableVoice(Long userId) {
        VoiceTalk vt = getCurrentAvailableVoiceTalk(userId);
        /*处理专属号套餐*/
        Date now = new Date();
        List<TouchVoice> tvs = tvRepo.findByUserIdAndEffectDatetimeLessThanAndFailureDatetimeGreaterThan(userId, now, now);
        VoiceNumber vn = new VoiceNumber();
        for (TouchVoice tv : tvs) {
            if (tv.getOrderdetailid() != null) {
                OrderDetail od = orderDetailRepo.findOne(tv.getOrderdetailid());
                if (getReleased()) {
                    if (od.getStatus() != Order.ORDER_STATUS_CONFIRMED) {
                        continue;
                    }
                }
            }
            vn.setStartTime(tv.getEffectDatetime());
            vn.setEndTime(tv.getFailureDatetime());
            vn.setRemainder((vn.getEndTime().getTime() - now.getTime()) / 86400000.0);
            vn.setPhone(tv.getPhone());
        }

        AvailableServiceRDO rdo = new AvailableServiceRDO();
        rdo.setVoiceNumber(vn);
        rdo.setVoiceTalk(vt);
        return rdo;
    }

    @Override
    public VoiceTalk getCurrentAvailableVoiceTalk(Long userId) {
        Date today = new Date();//getToday();
        List<OrderDetail> utvs = orderDetailRepo.findByUserId(0L);
        if (getReleased()) {
            utvs.addAll(orderDetailRepo.findByUserIdAndStatusAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndCallDurationNotNull(userId, Order.ORDER_STATUS_CONFIRMED, today, today));
        } else {
            utvs.addAll(orderDetailRepo.findByUserIdAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndCallDurationNotNull(userId, today, today));
        }
        VoiceTalk vt = new VoiceTalk();
        vt.setTotaltime(0L);
        /*处理语音通话套餐*/
        for (OrderDetail utv : utvs) {
            vt.setTotaltime(vt.getTotaltime() + utv.getCallDuration());
        }
        /*计算已用掉的通话时长*/
        Long usedTimeSeconds = cdrRepo.sumCallDurationByUserId(userId);
        vt.setUsedtime(usedTimeSeconds == null ? 0 : usedTimeSeconds / 60);
        vt.setRemaindertime(vt.getTotaltime() - vt.getUsedtime());
        return vt;
    }

    @Override
    public List<VoiceNumber> getAvailableVoiceNumber(Long userId) {
        Date now = new Date();
        List<TouchVoice> tvs = tvRepo.findByUserIdAndFailureDatetimeGreaterThan(userId, now);
        List<VoiceNumber> vns = new ArrayList<>();
        for (TouchVoice tv : tvs) {
            if (tv.getOrderdetailid() != null) {
                OrderDetail od = orderDetailRepo.findOne(tv.getOrderdetailid());
                if (getReleased()) {
                    if (od.getStatus() != Order.ORDER_STATUS_CONFIRMED) {
                        continue;
                    }
                }
            }
            VoiceNumber vn = new VoiceNumber();
            vn.setStartTime(tv.getEffectDatetime());
            vn.setEndTime(tv.getFailureDatetime());
            if (now.after(vn.getStartTime())) {
                vn.setRemainder((vn.getEndTime().getTime() - now.getTime()) / 86400000.0);
            } else {
                vn.setRemainder((vn.getEndTime().getTime() - vn.getStartTime().getTime()) / 86400000.0);
            }
            vn.setPhone(tv.getPhone());
            vns.add(vn);
        }
        return vns;
    }

    private void loadEvouchers(Boolean force) {
        List<EVoucher> evs;
        if (force || mEvoucherCache == null || System.currentTimeMillis() - lastEvoucherCacheTime > 3600 * 1000) {
            mEvoucherCache = null;
            _lockEvouchersLock.lock();
            if (mEvoucherCache == null || System.currentTimeMillis() - lastEvoucherCacheTime > 3600 * 1000) {
                lastEvoucherCacheTime = System.currentTimeMillis();
                evs = CollectionUtils.toList(evoucherRepo.findAll());
                for (EVoucher ev : evs) {
                    mEvoucherMapCache.put(ev.getId(), ev);
                }
                mEvoucherCache = evs;
            }
            _lockEvouchersLock.unlock();
        }
    }

    public EVoucher getEVoucher(Long evoucherId) {
        loadEvouchers(false);
        EVoucher evoucher = mEvoucherMapCache.get(evoucherId);
        if (evoucher == null) {
            loadEvouchers(true);
            evoucher = mEvoucherMapCache.get(evoucherId);
        }
        return evoucher;
    }

    @Override
    public List<UserEVoucher> getEVouchers(Long userId, String orderid) {
        if (StringUtils.isNotBlank(orderid)) {
            return userEVoucherRepo.findByOrderid(Long.parseLong(orderid));
        }
        List<UserEVoucher> uevouchers = userEVoucherRepo.findByUserid(0L);
        uevouchers.addAll(userEVoucherRepo.findByUserid(userId));
        List<UserEVoucher> uevs = new ArrayList<UserEVoucher>();
        Date now = new Date();
        for (UserEVoucher uev : uevouchers) {
            EVoucher ev = getEVoucher(uev.getEvoucherid());
            if (uev.getUsedDatetime() != null /*&& isTalkTimeOrVoiceNumberEvoucher(ev)*/) {
                continue;
            }

            if ((uev.getFailureDatetime() != null && now.after(uev.getFailureDatetime()))) {
                continue;
            }
            uev.setLocation(ev.getLocation());
            uev.setLogo(ev.getLogo());
            if (uev.getImage() == null) {
                uev.setImage(ev.getImage());
            }
            uevs.add(uev);
        }
        return uevs;
    }

    @Override
    public List<UserEVoucher> getAvailableEVouchers(Long userId, Long orderId) {
        List<UserEVoucher> uevouchers = userEVoucherRepo.findByUseridAndUsedDatetimeIsNullAndOrderidIsNull(userId);
        Order order = orderRepo.findOne(orderId);
        order.setOrderDetails(getOrderDetails(orderId));
        List<UserEVoucher> oevouchers = new ArrayList<UserEVoucher>();
        Date now = new Date();
        for (UserEVoucher uevoucher : uevouchers) {
            if ((uevoucher.getFailureDatetime() == null || now.before(uevoucher.getFailureDatetime())) &&
                    (uevoucher.getEffectDatetime() == null || now.after(uevoucher.getEffectDatetime()))) {
                // 时间有效
                EVoucher evoucher = evoucherRepo.findOne(uevoucher.getEvoucherid());
                List<PrdEVoucher> prdEVouchers = evoucher.getPrdEVouchers();
                if (prdEVouchers == null || prdEVouchers.isEmpty()) {
                    // 任意品类都可用
                    oevouchers.add(uevoucher);
                } else {
                    for (PrdEVoucher prdevoucher : prdEVouchers) {
                        boolean gotit = false;
                        for (OrderDetail od : order.getOrderDetails()) {
                            // 商品可能是组合商品，所以要拆开再进行判断
                            List<Product> products = orderHandler.getPackageProducts(getProduct(od.getProductid()));
                            boolean canUse = false;
                            for (Product product : products) {
                                if (prdevoucher.getProductid().equals(product.getId())) {
                                    canUse = true;
                                    break;
                                }
                            }
                            if (canUse) {
                                // 符合品类要求
                                if (evoucher.getType() == EVoucher.EVOUCHER_MXJX) {
                                    if (od.getQuantityPerUnit() >= evoucher.getMinamount()) {
                                        // 满几天减几天券，满足数量要求
                                        oevouchers.add(uevoucher);
                                        gotit = true;
                                        break;
                                    }
                                } else {
                                    // TODO 后面新增类型判断
                                    oevouchers.add(uevoucher);
                                    gotit = true;
                                    break;
                                }
                            }
                        }
                        if (gotit) {
                            break;
                        }
                    }
                }
            }
        }
        return oevouchers;
    }

    @Override
    public List<Outlet> getOutlets(Integer province, Integer city) {
        List<Outlet> outlets;
        if (province == 0 && city == 0) {
            outlets = CollectionUtils.toList(outletRepo.findAll());
        } else if (city == 0) {
            outlets = outletRepo.findByProvince(province);
        } else if (province == 0) {
            outlets = outletRepo.findByCity(city);
        } else {
            outlets = outletRepo.findByProvinceAndCity(province, city);
        }
        for (Outlet outlet : outlets) {
            orderHandler.updateCities(outlet);
        }
        return outlets;
    }

    private void deleteAddress(UserAddress addr) {
        if (addr != null) {
            addr.setStatus(UserAddress.DELETED_STATUS);
            userAddrRepo.save(addr);
        }
    }

    @Override
    public void removeAddress(Long userId, Long addrId) {
        if (addrId == -1L) {
            List<UserAddress> addrs = userAddrRepo.findByUserIdAndStatusNot(userId, UserAddress.DELETED_STATUS);
            for (UserAddress addr : addrs) {
                deleteAddress(addr);
            }
        } else {
            deleteAddress(userAddrRepo.findOne(addrId));
        }
    }

    @Override
    public OrderEVoucher discountEVoucher(Long userId, Long orderId, String uEvId) throws UCBaseException {
        Order order = orderHandler.updateOrderEVoucherUsage(userId, orderId, uEvId);
        OrderEVoucher oe = new OrderEVoucher();
        oe.setOrder(order);
        return oe;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        snGen = new SNGenerator(nodeid, nodenum, snlen, 0);
        UserHandlerUtil.setRepos(sessionHandler, userRepo);
        OrderPriceUtil.setRepos(prdUnitPriceRepo, productRepo, prdDiscountRepo);
        orderHandler.setSnGen(snGen);
    }

    @Override
    public DataCard bindDataCard(Long userid, String datacardid) {
        DataCard dc = datacardRepo.findByIccid(datacardid);
        if (dc == null) {
            if (datacardid.trim().length() < 20 && StringUtils.isNumeric(datacardid)) {
                dc = datacardRepo.findOne(Long.parseLong(datacardid));
            }
        }
        if (dc == null || (dc.getUserId() != 0L && !dc.getUserId().equals(userid))) {
            return dc;
        }
        dc.setUserId(userid);
        datacardRepo.save(dc);
        dc.setImsi(null);
        dc.setKi(null);
        dc.setCreatetime(null);
        return dc;
    }

    @Override
    public List<DataCard> getDataCards(Long userid) {
        List<DataCard> dcs = datacardRepo.findByUserId(userid);
        for (DataCard dc : dcs) {
            dc.setImsi(null);
            dc.setKi(null);
            dc.setCreatetime(null);
        }
        return dcs;
    }

    @Override
    public void unbindDataCard(Long userid, String datacardid) {
        DataCard dc = datacardRepo.findByIccid(datacardid);
        if (dc == null) {
            if (datacardid.trim().length() < 20 && StringUtils.isNumeric(datacardid)) {
                dc = datacardRepo.findOne(Long.parseLong(datacardid));
            }
        }
        if (dc == null || !dc.getUserId().equals(userid)) {
            LOG.info("unbind datacard failure " + (dc == null ? datacardid : dc.getUserId() + ":" + userid));
            return;
        }
        dc.setUserId(0L);
        datacardRepo.save(dc);
    }

    @Override
    public Touch getTouch(String devId, String host) {
        Touch touch = touchRepo.findByDevid(devId);
        if (touch == null) {
            return null;
        }
        if (!host.equalsIgnoreCase(touch.getHost())) {
            touch.setHost(host);
            touch = touchRepo.save(touch);
        }
        touch.setTouchChans(touchchansRepo.findByDevId(touch.getId()));
        return touch;
    }

    @Override
    public List<VoiceNumber> getVoicePackagesByTouch(String devId, String host) {
        Touch touch = touchRepo.findByDevid(devId);
        if (touch == null) {
            return null;
        }
        touch.setTouchChans(touchchansRepo.findByDevId(touch.getId()));
        List<VoiceNumber> voicePkgs = new ArrayList<VoiceNumber>();
        Date now = new Date();
        List<TouchVoice> tvs = tvRepo.findByTouchdevidAndEffectDatetimeLessThanAndFailureDatetimeGreaterThan(touch.getId(), now, now);
        if (tvs == null || tvs.isEmpty()) {
            return voicePkgs;
        }
        for (TouchVoice tv : tvs) {
            VoiceNumber vn = new VoiceNumber();
            User user = userRepo.findOne(tv.getUserId());
            vn.setPhone(user.getPhone());
            vn.setStartTime(tv.getEffectDatetime());
            vn.setEndTime(tv.getFailureDatetime());
            vn.setUserid(tv.getUserId());
            if (tv.getTouchchansid() != null && tv.getTouchchansid() > 0) {
                List<TouchChans> tchns = touch.getTouchChans();
                for (TouchChans tchn : tchns) {
                    if (tchn.getId().equals(tv.getTouchchansid())) {
                        vn.setSubid(tchn.getSubid());
                        break;
                    }
                }
            }
            voicePkgs.add(vn);
        }
        return voicePkgs;
    }

    @Override
    public List<Role> getRoles() {
        return CollectionUtils.toList(roleRepo.findAll());
    }

    @Override
    public List<Company> getCompanys() {
        return CollectionUtils.toList(compRepo.findAll());
    }

    @Override
    public Company findCompany(Long id) {
        return compRepo.findOne(id);
    }

    @Override
    public ServicePackage trafficBindDataCard(Long userid, String simid, Long orderdetailid, Long trafficid) throws UCBaseException {
        DataCard dc = datacardRepo.findByIccid(simid);
        if (dc == null) {
            throw new UCBaseException(ErrorCode.ERR_DATACARD_NOEXIST, ErrorCode.ERR_DATACARD_NOEXIST_INFO);
        }
        OrderDetail utv = orderDetailRepo.findOne(orderdetailid);
        if (utv == null) {
            throw new UCBaseException(ErrorCode.ERR_ORDERDETAIL_NOEXIST, ErrorCode.ERR_ORDERDETAIL_NOEXIST_INFO);
        }
        Set<String> simids = utv.getSimidlist();
        if (simids.size() == utv.getQuantity()) {
            throw new UCBaseException(ErrorCode.ERR_ORDERDETAIL_BINDED, ErrorCode.ERR_ORDERDETAIL_BINDED_INFO);
        }
        if (simids.contains(simid)) {
            throw new UCBaseException(ErrorCode.ERR_ORDERDETAIL_BINDED, ErrorCode.ERR_ORDERDETAIL_BINDED_INFO);
        }
        orderHandler.checkPaiedDataCardTraffic(dc, utv);
        simids.add(simid);
        utv.setSimidlist(simids);
        utv = orderDetailRepo.save(utv);
        /*处理datacard_traffic表信息更新*/
        if (trafficid != -1) {
            DataCardTraffic dct = dctRepo.findOne(trafficid);
            updateDataCardTraffic(dct, dc);
        } else {
            List<DataCardTraffic> dcts = dctRepo.findByOrderdetailid(orderdetailid);
            for (DataCardTraffic dct : dcts) {
                if (StringUtils.isBlank(dct.getIccid())) {
                    updateDataCardTraffic(dct, dc);
                    break;
                }
            }
        }
        /*订单详情simid属性值更新*/
        return processTrafficVoice(new Date(), utv);
    }

    private void updateDataCardTraffic(DataCardTraffic dct, DataCard dc) {
        if (dct != null) {
            dct.setDatacardid(dc.getId());
            dct.setIccid(dc.getIccid());
            dct.setImsi(dc.getImsi());
            if (dc.getType() == DataCard.EUROPEHK_DATACARD) {
                DataCardArea dca = dcaRepo.findByAreaname(dct.getAreaname());
                if (dca != null) {
                    dct.setImsi(dca.getDatacardType() == DataCard.HK_DATACARD ? dc.getImsihk() : dc.getImsi());
                }
            }
            dctRepo.save(dct);
        }
    }

    @Override
    public Order shippedOrder(Long orderId, String invoiceNo) {
        Order order = orderRepo.findOne(orderId);
        order.setInvoiceNo(invoiceNo);
        order.setShippingtime(new Date());
        order.setShippingStatus(Order.SHIPPING_STATUS_SHIPPED);
        return orderRepo.save(order);
    }

    @Override
    public List<Order> getOrders(Integer group, Integer pageindex, Integer pageSize) {
        List<Order> orders = null;
        if (group == 1) {
            orders = orderRepo.findByPayStatusAndShippingStatusAndShippingIdNotNull(Order.PAY_STATUS_PAYED, Order.SHIPPING_STATUS_INIT, new PageRequest(pageindex, pageSize));
        } else if (group == 2) {
            orders = orderRepo.findByPayStatusAndShippingStatusAndObtainvoucher(Order.PAY_STATUS_PAYED, Order.SHIPPING_STATUS_INIT, true, new PageRequest(pageindex, pageSize));
        } else {
            return null;
        }
        for (Order order : orders) {
            order.setOrderDetails(getOrderDetails(order.getId()));
        }
        return orders;
    }

    @Override
    public Order orderBindDataCard(Long userid, String simid, Long orderid) throws UCBaseException {
        DataCard dc = datacardRepo.findByIccid(simid);
        if (dc == null) {
            return null;
        }
        if (dc.getUserId() != 0L) {
            return null;
        }
        Order order = orderRepo.findOne(orderid);
        List<OrderDetail> ods = getOrderDetails(orderid);
        order.setOrderDetails(ods);
        for (OrderDetail od : ods) {
            Set<String> simids = od.getSimidlist();
            if (simids.size() == od.getQuantity()) {
                continue;
            }
            trafficBindDataCard(od.getUserId(), simid, od.getId(), -1L);
            /*simids.add(simid);
            od.setSimidlist(simids);
            orderDetailRepo.save(od);*/
        }
        return order;
    }

    private OrderDetail buildDefaultOrderDetail(Long userId, Long productid) {
        OrderDetail od = new OrderDetail();
        od.setOrderid(0L);
        od.setProductid(productid);
        od.setUserId(userId);
        od.setPrice(0.0);
        od.setQuantity(1);
        od.setQuantityPerUnit(1);
        od.setDiscount(0.0);
        od.setStatus(Order.ORDER_STATUS_CONFIRMED);
        od.setEffectDatetime(new Date());
        return od;
    }

    private OrderDetail rechargeTalkTime(Long userId, Long productid, Integer callDuration, String source) {
        OrderDetail od = buildDefaultOrderDetail(userId, productid);
        od.setCallDuration(callDuration);
        Integer validity = 365;
        String endtime = sdf.format(od.getEffectDatetime().getTime() + validity * 86400000L);
        try {
            od.setFailureDatetime(sdf.parse(endtime));
        } catch (ParseException e) {

        }
        od.setSource(source);
        return orderDetailRepo.save(od);
    }

    private void credtripOpenAccountGift(String accountNo, Long userId) {
        OrderDetail od = orderDetailRepo.findByUserIdAndStatusAndCallDurationAndOrderid(userId, Order.ORDER_STATUS_CONFIRMED, 500, 0L);
        if (od == null) {
            rechargeTalkTime(userId, 3L, 500, "信程账户开通" + accountNo);
        }
    }

    @Override
    public void CredtripOpenAccount(String accountNo, String userId, String mobile) {
        Long luserid = Long.parseLong(userId);
        Company comp = compRepo.findByCode("credtrip");
        UserThirdParty utp = utpRepo.findByUserIdAndCompanyid(luserid, comp.getId());
        credtripOpenAccountGift(accountNo, luserid);
        if (utp == null) {
            utp = new UserThirdParty();
        } else if (utp.getOpenid().equals(accountNo)) {
            return;
        }
        utp.setUserId(luserid);
        utp.setCompanyid(comp.getId());
        utp.setOpenid(accountNo);
        utpRepo.save(utp);
        if (StringUtils.isNotBlank(mobile)) {
            List<Phone> phones = getPhones(luserid);
            for (Phone phone : phones) {
                if (phone.getPhone().equals(mobile)) {
                    return;
                }
            }
            Phone phone = new Phone();
            phone.setPhone(mobile);
            phone.setUserId(luserid);
            phoneRepo.save(phone);
        }

    }

    @Override
    public void removeFromCarts(Long[] cartIds) {
        cartRepo.delete(cartRepo.findAll(Arrays.asList(cartIds)));
    }

    @Override
    public CredtripCredit queryCredtripCredit(Long userid) {
        Company comp = compRepo.findByCode("credtrip");
        UserThirdParty utp = utpRepo.findByUserIdAndCompanyid(userid, comp.getId());
        if (utp == null) {
            return null;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("version", "1.0");
        params.put("product", "01");
        params.put("accountNo", utp.getOpenid());
        params.put("merchantId", "2016010003");
        return credtripApis.queryCredit(params);
    }

    @Override
    public UserThirdParty findCredtripAccount(Long userid) {
        Company comp = compRepo.findByCode("credtrip");
        return utpRepo.findByUserIdAndCompanyid(userid, comp.getId());
    }

    @Override
    public void refundOrder(Long orderId, String payvoucher, Double refundFee) {
        orderHandler.refundOrder(orderId, payvoucher, refundFee);
    }

    @Override
    public List<Homepage> getHomepages(Integer type, String location, String version) {
        if (StringUtils.isNotBlank(location)) {
            return homepageRepo.findByTypeAndLocationAndDisabledNotOrderBySortAsc(type, location, true);
        } else {
            List<Homepage> homepages = homepageRepo.findByTypeAndDisabledNotOrderBySortAsc(type, true);
            if (version != null) {
                Integer iversion = convertVersion(version);
                AppReleased appReleased = appRepo.findByTypeAndVersion(AppReleased.AppType.RoamAppIos.mValue, iversion);
                if (appReleased != null && appReleased.getStatus() == AppReleased.AUDITING_STATUS) {
                    /*skip virtual products and travalroutes*/
                    List<Homepage> pages = new ArrayList<Homepage>();
                    for (Homepage page : homepages) {
                        if (page.getLocation().equals("products")) {
                            if (page.getUrl().equals("/products/1/1") || page.getUrl().equals("")) {
                                pages.add(page);
                            }
                        } else if (page.getLocation().equals("banners")) {
                            pages.add(page);
                        }
                    }
                    return pages;
                }
            }
            return homepages;
        }
    }

    private Boolean isTalkTimeOrVoiceNumberEvoucher(EVoucher voucher) {
        if (voucher.getLocation() != null && voucher.getLocation().equals(EVoucher.EVOUCHER_LOCATION)) {
            List<PrdEVoucher> prdevs = voucher.getPrdEVouchers();
            if (prdevs != null && !prdevs.isEmpty()) {
                for (PrdEVoucher pev : prdevs) {
                    Product prd = orderHandler.getProduct(pev.getProductid());
                    if (prd.getTypeid() == PrdType.PRD_VOICE || prd.getTypeid() == PrdType.PRD_VOICENUMBER) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public UserEVoucher exchangeEVoucherSN(Long userid, String evoucherSn) throws UCBaseException {
        UserEVoucher uev = userEVoucherRepo.findBySn(Long.parseLong(evoucherSn));
        if (uev == null) {
            throw new UCBaseException(ErrorCode.ERR_EVOUCHERSN_INVALID, ErrorCode.ERR_EVOUCHERSN_INVALID_INFO);
        }
        if (uev.getUserid() != -1L) {
            throw new UCBaseException(ErrorCode.ERR_EVOUCHERSN_USED, ErrorCode.ERR_EVOUCHERSN_USED_INFO);
        }
        uev.setUserid(userid);
        userEVoucherRepo.save(uev);
        EVoucher eVoucher = getEVoucher(uev.getEvoucherid());
        uev.setLocation(eVoucher.getLocation());
        uev.setLogo(eVoucher.getLogo());
        List<PrdEVoucher> prdevs = eVoucher.getPrdEVouchers();
        uev.setShowdetail(true);
        if (uev.getLocation() != null && uev.getLocation().equals(EVoucher.EVOUCHER_LOCATION)) {
            if (prdevs != null && !prdevs.isEmpty()) {
                for (PrdEVoucher pev : prdevs) {
                    Product prd = orderHandler.getProduct(pev.getProductid());
                    if (prd.getTypeid() == PrdType.PRD_VOICE || prd.getTypeid() == PrdType.PRD_VOICENUMBER) {
                        uev.setShowdetail(false);
                        if (prd.getTypeid() == PrdType.PRD_VOICE) {
                            OrderDetail od = rechargeTalkTime(userid, prd.getId(), uev.getMoney().intValue(), uev.getDescription());
                            uev.setUsedDatetime(new Date());
                            uev.setOrderid(od.getId());
                        } else if (prd.getTypeid() == PrdType.PRD_VOICENUMBER) {
                            OrderDetail od = buildDefaultOrderDetail(userid, prd.getId());
                            String endtime = sdf.format(od.getEffectDatetime().getTime() + uev.getMoney() * 86400000L);
                            try {
                                od.setFailureDatetime(sdf.parse(endtime));
                            } catch (ParseException e) {

                            }
                            TouchVoice tv = tvHandler.orderFixedPhone(userid, null, od.getEffectDatetime(), od.getFailureDatetime());
                            if (tv == null) {
                                //throw new UCBaseException(ErrorCode.ERR_TOUCHVOICE_OVERFLOW,ErrorCode.ERR_TOUCHVOICE_OVERFLOW_INFO);
                                LOG.warn("orderFixedPhone failed for " + userid + ",startTime=" + od.getEffectDatetime() + ",endTime=" + od.getFailureDatetime());
                                return uev;
                            }
                            uev.setUsedDatetime(new Date());
                            od.setPhone(tv.getPhone());
                            od.setSource(uev.getDescription());
                            od = orderDetailRepo.save(od);
                            tv.setOrderid(od.getOrderid());
                            tv.setOrderdetailid(od.getId());
                            tvRepo.save(tv);
                            uev.setOrderid(od.getId());
                        }
                        userEVoucherRepo.save(uev);
                    }
                }
            }
        }
        return uev;
    }

    @Override
    public List<RefundReason> getRefundReasons() {
        return CollectionUtils.toList(refundReasonRepo.findAll());
    }

    @Override
    public Refund applyRefundOrder(Long userid, Long orderId, Long orderDetailId, Integer reasonId, String reason) throws UCBaseException {
        Order order = null;
        OrderDetail od = null;
        if (orderId != -1L) {
            order = getOrder(orderId);
        }
        if (orderDetailId != -1L) {
            od = orderDetailRepo.findOne(orderDetailId);
            if (od != null && order == null) {
                order = orderRepo.findOne(od.getOrderid());
            }
        }
        orderHandler.checkOrderUserMatch(order, userid);
        if (order.getPayStatus() != Order.PAY_STATUS_PAYED || (od != null && od.getStatus() != Order.ORDER_STATUS_CONFIRMED)) {
            throw new UCBaseException(ErrorCode.ERR_ORDER_INVALID_STATUS, ErrorCode.ERR_ORDER_INVALID_STATUS_INFO);
        }
        Refund refund = new Refund();
        refund.setUserid(userid);
        refund.setOrderid(order.getId());
        refund.setOrderDetailId(orderDetailId);
        refund.setCreatetime(new Date());
        refund.setStatus(Refund.REFUND_STATUS_INIT);
        refund.setPayVoucher(order.getPayvoucher());
        refund.setRefundType(order.getPayId());
        refund.setReasonId(reasonId);
        refund.setReason(reason);
        if (od != null) {
            refund.setReal(orderHandler.isRealProduct(od.getProductid()));
            refund.setMoney(od.getPrice() * od.getQuantity() - od.getDiscount() - (od.getVoucherAmount() == null ? 0.0 : od.getVoucherAmount()));
        } else {
            Boolean isReal = false;
            for (OrderDetail iod : order.getOrderDetails()) {
                if (orderHandler.isRealProduct(iod.getProductid())) {
                    isReal = true;
                }
            }
            refund.setReal(isReal);
            refund.setMoney(order.getPaidmoney());
        }
        return refundRepo.save(refund);
    }

    @Override
    public List<Refund> getRefunds(Long userid, Long orderId, Long orderDetailId) {
        if (orderId != -1L) {
            return refundRepo.findByUseridAndOrderid(userid, orderId);
        } else if (orderDetailId != -1L) {
            return refundRepo.findByUseridAndOrderDetailId(userid, orderDetailId);
        } else {
            return refundRepo.findByUserid(userid);
        }
    }

    @Override
    public Refund shippingRefund(Long userid, Long refundId, String shippingName, String invoiceNo) throws UCBaseException {
        Refund refund = refundRepo.findOne(refundId);
        if (refund == null) {
            throw new UCBaseException(ErrorCode.ERR_REFUND_NOEXIST, ErrorCode.ERR_REFUND_NOEXIST_INFO);
        }
        if (!refund.getUserid().equals(userid)) {
            throw new UCBaseException(ErrorCode.ERR_REFUND_USER_MISMATCH, ErrorCode.ERR_REFUND_USER_MISMATCH_INFO);
        }
        refund.setShippingName(shippingName);
        refund.setInvoiceNo(invoiceNo);
        refund.setShippingStatus(Refund.REFUND_SHIPPING_SHIPPED);
        refund.setShippingTime(new Date());
        return refundRepo.save(refund);
    }

    @Override
    public List<Refund> getRefunds(Integer group, Integer status, Integer pageindex, Integer pageSize) {
        if (group == 1) {
            return refundRepo.findByRealAndStatus(true, status, new PageRequest(pageindex, pageSize));
        } else if (group == 2) {
            return refundRepo.findByRealAndStatus(false, status, new PageRequest(pageindex, pageSize));
        } else {
            return refundRepo.findByStatus(status, new PageRequest(pageindex, pageSize));
        }
    }

    @Override
    public Refund confirmRefund(Long refundId, Boolean confirmed) throws UCBaseException {
        Refund refund = refundRepo.findOne(refundId);
        if (refund == null) {
            throw new UCBaseException(ErrorCode.ERR_REFUND_NOEXIST, ErrorCode.ERR_REFUND_NOEXIST_INFO);
        }

        refund.setStatus(Refund.REFUND_STATUS_CONFIRMED);
        refund.setConfirmTime(new Date());
        return refundRepo.save(refund);

    }

    @Override
    public List<Refund> receivedRefunds(List<Long> refundIds, String invoiceNo) {
        List<Refund> refunds = new ArrayList<>();
        for (Long refundId : refundIds) {
            Refund refund = refundRepo.findOne(refundId);
            if (refund != null) {
                refund.setInvoiceNo(invoiceNo);
                refund.setShippingStatus(Refund.REFUND_SHIPPING_RECEIVED);
                refund.setReceivedTime(new Date());
                refunds.add(refundRepo.save(refund));
            }
        }
        return refunds;
    }

    @Override
    public List<Acc> getMoreMissCallList(Long accId, String method, String phoneNumber, String touchPhone, Pageable pageable) {
        return accRepo.getMoreMissedCallList(accId, method, phoneNumber, touchPhone, pageable);
    }

    @Override
    public List<Acc> getMoreCallList(Long accId, String method, String phoneNumber, long userId, String touchPhone, Pageable pageable) {
        return accRepo.getMoreCallList(accId, method, phoneNumber, userId, touchPhone, pageable);
    }

    @Override
    public List<Acc> getLessCallList(Long accId, String method, String phoneNumber, long userId, String touchPhone, Pageable pageable) {
        return accRepo.getLessCallList(accId, method, phoneNumber, userId, touchPhone, pageable);
    }

    @Override
    public List<Acc> getLessMissedCallList(Long accId, String method, String phoneNumber, String touchPhone, Pageable pageable) {
        return accRepo.getLessMissedCallList(accId, method, phoneNumber, touchPhone, pageable);
    }

    @Override
    public List<Acc> getMoreMessageListGroupByCaller(Long accId, String loginPhone, String touchPhone, long userId, String method, Integer limit) {
        return accRepo.getMoreMessageListGroupByCaller(accId, loginPhone, touchPhone, userId, method, limit);
    }

    @Override
    public List<Acc> getLessMessageListGroupByCaller(Long accId, String loginPhone, long userId, String method, Integer limit) {
        return accRepo.getLessMessageListGroupByCaller(accId, loginPhone, userId, method, limit);
    }

    @Override
    public Acc getOneByFromAndToAndUserId(String method, String fromPhoneNumber, String toPhoneNumber, Long userId) {
        return accRepo.getOneByFromAndToAndUserId(method, fromPhoneNumber, toPhoneNumber, userId);
    }

    @Override
    public List<Acc> getMoreMessageList(Long accId, String method, long userId, String phoneNumber, String toPhoneNumber, Integer limit) {
        return accRepo.getMoreMessageList(accId, method, userId, phoneNumber, toPhoneNumber, limit);
    }

    @Override
    public List<Acc> getLessMessageList(Long accId, String method, long userId, String phoneNumber, String toPhoneNumber, Integer limit) {
        return accRepo.getLessMessageList(accId, method, userId, phoneNumber, toPhoneNumber, limit);
    }

    @Override
    public List<Acc> getAllCallGroupList(Long accId, Long userId, String loginPhone) {
        return accRepo.getAllCallGroupList(accId, userId, loginPhone);
    }

    @Override
    public List<Acc> getMissedCallGroupList(Long accId, Long userId, String loginPhone) {
        return accRepo.getMissedCallGroupList(accId, userId, loginPhone);
    }

    @Override
    public List<Acc> getAllCallListGreaterThanId(Long accId, Long userId, String loginPhone, String phone, Integer size) {
        return accRepo.getAllCallListGreaterThanId(accId, userId, loginPhone, phone, size);
    }

    @Override
    public List<Acc> getAllCallListLessThanId(Long accId, Long userId, String loginPhone, String phone, Integer size) {
        if (accId == 0L) {
            return accRepo.getAllCallList(userId, loginPhone, phone, size);
        } else {
            return accRepo.getAllCallListLessThanId(accId, userId, loginPhone, phone, size);
        }
    }

    @Override
    public List<Acc> getMissedCallListGreaterThanId(Long accId, Long userId, String loginPhone, String phone, Integer size) {
        return accRepo.getMissedCallListGreaterThanId(accId, userId, loginPhone, phone, size);
    }

    @Override
    public List<Acc> getMissedCallListLessThanId(Long accId, Long userId, String loginPhone, String phone, Integer size) {
        if (accId == 0L) {
            return accRepo.getMissedCallList(userId, loginPhone, phone, size);
        } else {
            return accRepo.getMissedCallListLessThanId(accId, userId, loginPhone, phone, size);
        }
    }

    @Override
    public List<Acc> getMessageListGreaterThanId(Long accId, Long userId, String loginPhone, String phone, Integer size) {
        return accRepo.getMessageListGreaterThanId(accId, userId, loginPhone, phone, size);
    }

    @Override
    public List<Acc> getMessageListLessThanId(Long accId, Long userId, String loginPhone, String phone, Integer size) {
        if (accId == 0L) {
            return accRepo.getMessageList(userId, loginPhone, phone, size);
        } else {
            return accRepo.getMessageListLessThanId(accId, userId, loginPhone, phone, size);
        }
    }

    public List<Acc> getMessageGroupList(Long accId, Long userId, String loginPhone) {
        return accRepo.getMessageGroupList(accId, userId, loginPhone);
    }

    @Override
    public int countUnreadCall(Long accId, Long userId, String loginPhone) {
        return accRepo.countUnreadCall(accId, userId, loginPhone);
    }

    @Override
    public int countUnreadCall(Date date, Long userId, String loginPhone) {
        return accRepo.countUnreadCall(date, userId, loginPhone);
    }

    @Override
    public int countUnreadMessage(Long accId, Long userId, String loginPhone) {
        return accRepo.countUnreadMessage(accId, userId, loginPhone);
    }

    @Override
    public int countUnreadMessage(Date date, Long userId, String loginPhone) {
        return accRepo.countUnreadMessage(date, userId, loginPhone);
    }

    @Override
    public int countUnreadMessage(Long accId, Long userId, String loginPhone, String phone) {
        return accRepo.countUnreadMessage(accId, userId, loginPhone, phone);
    }

    @Override
    public int countUnreadMessage(Date date, Long userId, String loginPhone, String phone) {
        return accRepo.countUnreadMessage(date, userId, loginPhone, phone);
    }

    @Override
    public int deleteAccByAllCallGroup(Long userId, String loginPhone, List<String> phones) {
        int i = accRepo.updateStatusByAllCallGroup(userId, phones);
        i += accRepo.updateCalleeStatusByAllCallGroup(userId, loginPhone, phones);
        return i;
    }

    @Override
    public int deleteAccByMissedCallGroup(Long userId, String loginPhone, List<String> phones) {
        return accRepo.updateCalleeStatusByMissedCallGroup(userId, loginPhone, phones);
    }

    @Override
    public int deleteAccByMessageGroup(Long userId, String loginPhone, List<String> phones) {
        int i = accRepo.updateStatusByMessageGroup(userId, phones);
        i += accRepo.updateCalleeStatusByMessageGroup(userId, loginPhone, phones);
        return i;
    }

    @Override
    public int deleteAccByCallIds(Long userId, String loginPhone, List<String> callIds) {
        int i = accRepo.updateStatusByCallIds(userId, callIds);
        i += accRepo.updateCalleeStatusByCallIds(userId, loginPhone, callIds);
        return i;
    }

    @Override
    public Application getApplication(String appId, String appKey) {
        return applicationRepo.getApplication(appId, appKey);
    }

    @Override
    public List<Server> getSipServer(Integer group) {
        return serverRepo.findByServiceCodeAndGroup("sip", group);
    }

    @Override
    public List<Server> getPushServer(Integer group) {
        return serverRepo.findByServiceCodeAndGroup("push", group);
    }

    @Override
    public List<DataCardTraffic> getDataTraffics(Integer group, Integer pageindex, Integer pageSize) {
        List<DataCardTraffic> dcts = null;
        if (group == 1) {
            dcts = dctRepo.findByFailureDatetimeGreaterThanAndDatacardidIsNull(new Date(), new PageRequest(pageindex, pageSize));
        } else if (group == 2) {
            dcts = dctRepo.findByFailureDatetimeGreaterThanAndDatacardidNotNull(new Date(), new PageRequest(pageindex, pageSize));
        } else if (group == 0) {
            dcts = dctRepo.findByFailureDatetimeGreaterThan(new Date(), new PageRequest(pageindex, pageSize));
        }
        if (dcts != null && !dcts.isEmpty()) {
            for (DataCardTraffic dct : dcts) {
                DataCardArea dca = dcaRepo.findByAreaname(dct.getAreaname());
                if (dca != null) {
                    dct.setType(dca.getDatacardType());
                } else {
                    dct.setType(DataCard.EUROPE_DATACARD);
                }
            }
        }
        return dcts;
    }

    @Override
    public Acc findByCallId(String callid) {
        return accRepo.findByCallId(callid);
    }

    @Override
    public int updateAccByCallId(String callId, int status, int calleeStatus) {
        return accRepo.updateAccByCallId(callId, status, calleeStatus);
    }

    @Override
    public int updateAccByCallerAndCalleeAndUserid(String hisPhone, Long myUserid) {
        return accRepo.updateAccByCallerAndCalleeAndUserid(hisPhone, myUserid);
    }

    @Override
    public Bell findByTimeAndTenantid(Date now, Long tenantid) {
        return bellRepo.findByTimeAndTenantid(now, tenantid);
    }

    @Override
    public int updateAccByCallerAndCallee(String myPhone, String hisPhone, Long userId) {
        return accRepo.updateAccByCallerAndCallee(myPhone, hisPhone, userId);
    }

    @Override
    public boolean isRoamSdkValid(Integer type, String versionName) {
        AppReleased appReleased = appRepo.findByTypeAndVersionName(type, versionName);
        if (appReleased != null) {
            if (appReleased.getStatus() == AppReleased.RELEASED_STATUS) {
                return true;
            }
        }
        return false;
    }

    @Override
    public UserAssociate getParentAccount(Long userId) {
        return userAssociateRepo.findByUserId(userId);
    }

    @Override
    public List<UserAssociate> getSubAccounts(Long parentUserId) {
        return userAssociateRepo.findByParentUserId(parentUserId);
    }

    @Override
    public UserAssociate saveUserAssociate(UserAssociate userAssociate) {
        return userAssociateRepo.save(userAssociate);
    }

    @Override
    public List<Long> getAssociateUserId(Long userId) {
        List<Long> userIds = new ArrayList<>();
        if (userId != null) {
            UserAssociate userAssociate = getParentAccount(userId);
            List<UserAssociate> userAssociateList = null;
            if (userAssociate != null) {
                userIds.add(userAssociate.getParentUserId());
                userAssociateList = getSubAccounts(userAssociate.getParentUserId());
            } else {
                userIds.add(userId);
                userAssociateList = getSubAccounts(userId);
            }
            for (UserAssociate u : userAssociateList) {
                userIds.add(u.getUserId());
            }
        }
        return userIds;
    }
}

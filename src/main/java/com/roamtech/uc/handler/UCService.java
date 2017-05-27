package com.roamtech.uc.handler;

import com.roamtech.uc.cache.model.*;
import com.roamtech.uc.client.CredtripCredit;
import com.roamtech.uc.exception.UCBaseException;
import com.roamtech.uc.model.*;
import com.roamtech.uc.opensips.model.Acc;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface UCService {
    User findByUsernameRoam(String username);

    List<User> findByUsername(String username);

    User findByUsernameAndTenantId(String username, Long tenantId);

    User findByEmail(String email);

    User findByPhone(String phone);

    User findOne(Long userId);

    User findActiveOne(String key);

    User save(User user);

    int changePassword(Long userId, String opassword, String npassword);

    int resetPassword(Long userId, String password);

    int changeMobile(Long userId, String mobile);

    int changeEmail(Long userId, String email);

    boolean isUsernameExist(String username);

    boolean isMobileExist(String mobile);

    boolean isEmailExist(String email);

    Session login(User user, Application application);

    Session login(User user);

    void logout(Long userId, String sessionId);

    boolean isSessionValid(String sessionId);

    Session findSession(String sessionId);

    public User freeze(Long userId);

    public User unfreeze(Long userId);

    VerifyCode getCode(Long userId, String phone, String email);

    boolean verifyCode(Long checkId, String checkCode);

    Phone findPhone(String phone);

    Phone findPhone(String phone, Long tenantId, Integer phoneType);

    Phone bindPhone(Long userId, String phone, Long tenantId, boolean verified);

    Phone bindPhone(Long userId, String phone, Integer phoneType, Long tenantId, boolean verified);

    Phone savePhone(Phone phone);

    void unbindPhone(Phone phone);

    Touch bindTouch(Long userId, String devId, String phone, String wifiSsid, String wifiPassword);

    void unbindTouch(Long userId, String devId);

    List<Phone> getPhones(Long userId);

    List<Touch> getTouchs(Long userId);

    Touch findTouch(String devId);

    AppReleased checkTouchUpgrade(Integer type, Integer version);

    Integer convertVersion(String version);

    AppReleased checkRoamchatUpgrade(Integer type, Integer version);

    List<AreaGroup> getAreaGroups();

    List<AreaCode> getAreaCodes(Long groupId);

    List<City> getProvinces();

    List<City> getCitysByParent(Long pid);

    List<Shipping> getShippings();

    List<ShippingArea> getShippingAreas(Integer shippingId);

    List<Payment> getPayments(Integer terminalType);

    List<Store> getStores();

    Store findMyStore(Long userId);

    List<Store> findStoreByName(String name);

    List<PrdCategory> getPrdCategorys();

    List<PrdBrand> getPrdBrands();

    List<Product> getProducts(Long storeId, Long categoryId, Long brandId);

    List<Product> getProductsByName(String name);

    List<Product> getProducts(Long[] pdIds);

    Product getProduct(Long productId);

    List<PrdUnitPrice> getPrdUnitPrices(Long productId);

    List<PrdDiscount> getPrdDiscounts(Long productId);

    List<Cart> findCart(Long userId, String sessionId);

    Cart addToCart(Long userId, String sessionId, Long productId, Integer quantity, List<ODPrdAttr> cartPrdAttrs);

    Cart updateCart(Long cartId, Long productId, Integer quantity, List<ODPrdAttr> cartPrdAttrs);

    void removeFromCart(Long cartId);

    void resetMyCart(Long userId, String sessionId);

    AvailableServiceRDO getCurrentAvailableService(Long userId);

    AvailableServiceRDO getCurrentAvailableVoice(Long userId);

    VoiceTalk getCurrentAvailableVoiceTalk(Long userId);

    List<VoiceNumber> getAvailableVoiceNumber(Long userId);

    Order getOrder(Long orderId);

    List<Order> getOrders(Long userId, Integer pageindex, Integer limit);

    Order submitOrder(Long userId, String sessionId, Long[] cartIds, Long tenantId) throws UCBaseException;

    Order submitOrder(Long userId, String sessionId, Long productId, Integer quantity, List<ODPrdAttr> cartPrdAttrs, Long tenantId) throws UCBaseException;

    Order payOrder(Long userId, Long orderId, Integer payId, String payvoucher) throws UCBaseException;

    Order updateOrder(Long userId, Long orderId, Integer shippingId, Long shippingAddr) throws UCBaseException;

    Order updateOrder(Long userId, Long orderId, Boolean obtainvoucher, Long outletid, String obtainTime) throws UCBaseException;

    Order updateOrder(Long userId, Long orderId, Boolean receipt, String invPayee, String invContent) throws UCBaseException;

    Order paidOrder(Long orderId, String payvoucher, Double amount);

    Order confirmOrder(Long userId, Long orderId) throws UCBaseException;

    Order cancelOrder(Long userId, Long orderId, Boolean delete) throws UCBaseException;

    Order shippedOrder(Long orderId, String invoiceNo);

    Order receivedOrder(Long userId, Long orderId) throws UCBaseException;

    List<OrderDetail> getOrderDetails(Long orderId);

    Order addOrderDetail(Long userId, Long orderId, Long productId, Integer quantity, List<ODPrdAttr> odPrdAttrs) throws UCBaseException;

    Order updateOrderDetail(Long userId, Long orderDetailId, Long productId, Integer quantity, List<ODPrdAttr> odPrdAttrs) throws UCBaseException;

    OrderDetail cancelOrderDetail(Long userId, Long orderId, Long orderDetailId) throws UCBaseException;

    UserAddress addOrUpdateUserAddress(Long userId, UserAddress addr);

    void removeAddress(Long userId, Long addrId);

    List<UserAddress> getAddresses(Long userId, boolean validAddr);

    List<Outlet> getOutlets(Integer province, Integer city);

    List<UserEVoucher> getEVouchers(Long userId, String orderid);

    List<UserEVoucher> getAvailableEVouchers(Long userId, Long orderId);

    /**
     * 使用优惠券更新订单详情
     *
     * @param orderId 订单号，需要更新的订单编号
     * @param uEvId   用户电子券号，支持多个优惠券ID，格式如"[1,2,3]"，如果只使用一张，直接传ID，格式如"1"
     * @return
     */
    OrderEVoucher discountEVoucher(Long userId, Long orderId, String uEvId) throws UCBaseException;

    DataCard bindDataCard(Long userid, String datacardid);

    List<DataCard> getDataCards(Long userid);

    void unbindDataCard(Long userid, String datacardid);

    Touch getTouch(String devId, String host);

    List<VoiceNumber> getVoicePackagesByTouch(String devId, String host);

    List<Role> getRoles();

    List<Company> getCompanys();

    Company findCompany(Long id);

    AvailableServiceRDO getAvailableServices(Long userid);

    ServicePackage trafficBindDataCard(Long userid, String simid, Long orderdetailid, Long trafficid) throws UCBaseException;

    List<Order> getOrders(Integer group, Integer pageindex, Integer pageSize);

    Order orderBindDataCard(Long userid, String simid, Long orderid) throws UCBaseException;

    void CredtripOpenAccount(String accountNo, String userId, String mobile);

    void removeFromCarts(Long[] cartIds);

    CredtripCredit queryCredtripCredit(Long userid);

    UserThirdParty findCredtripAccount(Long userid);

    void refundOrder(Long orderId, String payvoucher, Double refundFee);

    List<Homepage> getHomepages(Integer type, String location, String version);

    UserEVoucher exchangeEVoucherSN(Long userid, String evoucherId) throws UCBaseException;

    List<RefundReason> getRefundReasons();

    Refund applyRefundOrder(Long userid, Long orderId, Long orderDetailId, Integer reasonId, String reason) throws UCBaseException;

    List<Refund> getRefunds(Long userid, Long orderId, Long orderDetailId);

    Refund shippingRefund(Long userid, Long refundId, String shippingName, String invoiceNo) throws UCBaseException;

    List<Refund> getRefunds(Integer group, Integer status, Integer pageindex, Integer pageSize);

    Refund confirmRefund(Long refundId, Boolean confirmed) throws UCBaseException;

    List<Refund> receivedRefunds(List<Long> refundIds, String invoiceNo);

    List<Acc> getMoreCallList(Long accId, String method, String phoneNumber, long userId, String touchPhone, Pageable pageable);

    List<Acc> getLessCallList(Long accId, String method, String phoneNumber, long userId, String touchPhone, Pageable pageable);

    List<Acc> getMoreMissCallList(Long accId, String method, String phoneNumber, String touchPhone, Pageable pageable);

    List<Acc> getLessMissedCallList(Long accId, String method, String phoneNumber, String touchPhone, Pageable pageable);

    List<Acc> getMoreMessageListGroupByCaller(Long accId, String loginPhone, String touchPhone, long userId, String method, Integer size);

    List<Acc> getLessMessageListGroupByCaller(Long accId, String loginPhone, long userId, String method, Integer size);

    List<Acc> getMoreMessageList(Long accId, String method, long userId, String phoneNumber, String toPhoneNumber, Integer limit);

    List<Acc> getLessMessageList(Long accId, String method, long userId, String phoneNumber, String toPhoneNumber, Integer limit);

    List<Acc> getAllCallGroupList(Long accId, Long userId, String loginPhone);

    List<Acc> getMissedCallGroupList(Long accId, Long userId, String loginPhone);

    List<Acc> getAllCallListGreaterThanId(Long accId, Long userId, String loginPhone, String phone, Integer size);

    List<Acc> getAllCallListLessThanId(Long accId, Long userId, String loginPhone, String phone, Integer size);

    List<Acc> getMissedCallListGreaterThanId(Long accId, Long userId, String loginPhone, String phone, Integer size);

    List<Acc> getMissedCallListLessThanId(Long accId, Long userId, String loginPhone, String phone, Integer size);

    List<Acc> getMessageListGreaterThanId(Long accId, Long userId, String loginPhone, String phone, Integer size);

    List<Acc> getMessageListLessThanId(Long accId, Long userId, String loginPhone, String phone, Integer size);

    List<Acc> getMessageGroupList(Long accId, Long userId, String loginPhone);

    int countUnreadCall(Long accId, Long userId, String loginPhone);

    int countUnreadCall(Date date, Long userId, String loginPhone);

    int countUnreadMessage(Long accId, Long userId, String loginPhone);

    int countUnreadMessage(Date date, Long userId, String loginPhone);

    int countUnreadMessage(Long accId, Long userId, String loginPhone, String phone);

    int countUnreadMessage(Date date, Long userId, String loginPhone, String phone);

    int deleteAccByAllCallGroup(Long userId, String loginPhone, List<String> phones);

    int deleteAccByMissedCallGroup(Long userId, String loginPhone, List<String> phones);

    int deleteAccByMessageGroup(Long userId, String loginPhone, List<String> phones);

    int deleteAccByCallIds(Long userId, String loginPhone, List<String> callIds);

    Acc getOneByFromAndToAndUserId(String method, String fromPhoneNumber, String toPhoneNumber, Long userId);

    Application getApplication(String appId, String appKey);

    Acc findByCallId(String callid);

    int updateAccByCallId(String callId, int status, int calleeStatus);

    List<Server> getSipServer(Integer group);

    List<Server> getPushServer(Integer group);

    List<DataCardTraffic> getDataTraffics(Integer group, Integer pageindex, Integer pageSize);

    int updateAccByCallerAndCalleeAndUserid(String myPhone, Long myUserid);

    Bell findByTimeAndTenantid(Date now, Long tenantid);

    int updateAccByCallerAndCallee(String myPhone, String hisPhone, Long userId);

    boolean isRoamSdkValid(Integer type, String versionName);

    UserAssociate getParentAccount(Long userId);

    List<UserAssociate> getSubAccounts(Long parentUserId);

    UserAssociate saveUserAssociate(UserAssociate userAssociate);

    List<Long> getAssociateUserId(Long userId);

}

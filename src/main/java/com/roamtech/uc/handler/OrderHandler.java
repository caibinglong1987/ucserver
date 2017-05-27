package com.roamtech.uc.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.roamtech.uc.cache.RDCache;
import com.roamtech.uc.cache.handler.DataCardTrafficHandler;
import com.roamtech.uc.cache.handler.TouchVoiceHandler;
import com.roamtech.uc.cache.model.ODAttr;
import com.roamtech.uc.cache.model.ODPrdAttr;
import com.roamtech.uc.client.rxevent.PurchaseDataTrafficEvent;
import com.roamtech.uc.client.rxevent.RemoveDataTrafficEvent;
import com.roamtech.uc.exception.UCBaseException;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.model.*;
import com.roamtech.uc.repository.*;
import com.roamtech.uc.util.CollectionUtils;
import com.roamtech.uc.util.JSONUtils;
import com.roamtech.uc.util.RxBus;
import com.roamtech.uc.zxing.EVoucherGenerator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by admin03 on 2016/9/22.
 */
@Component
public class OrderHandler  implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(OrderHandler.class);
    private RDCache cache;
    @Autowired
    @Qualifier("ucCacheManager")
    private CacheManager cacheManager;
    @Autowired
    OrderRepository orderRepo;
    @Autowired
    OrderDetailRepository orderDetailRepo;
    @Autowired
    ProductRepository productRepo;
    @Autowired
    PrdPackageRepository prdPkgRepo;
    @Autowired
    PrdAttrRepository prdAttrRepo;
    @Autowired
    TouchVoiceHandler tvHandler;
    @Autowired
    DataCardTrafficRepository dctRepo;
    @Autowired
    DataCardRepository datacardRepo;
    @Autowired
    DataCardAreaRepository dcaRepo;
    @Autowired
    TouchVoiceRepository tvRepo;
    @Autowired
    EVoucherRepository evoucherRepo;
    @Autowired
    UserEVoucherRepository userEVoucherRepo;
    @Autowired
    OutletRepository outletRepo;
    private SNGenerator snGen;
    @Autowired
    CityRepository cityRepo;
    @Autowired
    UserAddressRepository userAddrRepo;
    @Autowired
    ShippingAreaRepository shippingAreaRepo;
    @Autowired
    DataCardTrafficHandler dctHandler;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<Product> mProductCache;
    private Map<Long,Product> mProductMapCache = new ConcurrentHashMap<Long,Product>();
    private long lastProductCacheTime;
    private ReentrantLock _lockProductsLock = new ReentrantLock();

    public void setSnGen(SNGenerator snGen) {
        this.snGen = snGen;
    }
    private List<Product> findProductsByStoreid(Long storeId) {
        List<Product> prds = new ArrayList<Product>();
        for(Product prd:mProductCache) {
            if(prd.getStoreid().equals(storeId)) {
                prds.add(prd);
            }
        }
        return prds;
    }
    private List<Product> findProductsByCategoryid(Long categoryId) {
        List<Product> prds = new ArrayList<Product>();
        for(Product prd:mProductCache) {
            if(prd.getCategoryid().equals(categoryId)) {
                prds.add(prd);
            }
        }
        return prds;
    }
    private List<Product> findProductsByBrandid(Long brandId) {
        List<Product> prds = new ArrayList<Product>();
        for(Product prd:mProductCache) {
            if(prd.getBrandid().equals(brandId)) {
                prds.add(prd);
            }
        }
        return prds;
    }
    private List<Product> findProductsByStoreidAndBrandid(Long storeId,Long brandId) {
        List<Product> prds = new ArrayList<Product>();
        for(Product prd:mProductCache) {
            if(prd.getStoreid().equals(storeId) && prd.getBrandid().equals(brandId)) {
                prds.add(prd);
            }
        }
        return prds;
    }
    private List<Product> findProductsByStoreidAndCategoryid(Long storeId,Long categoryId) {
        List<Product> prds = new ArrayList<Product>();
        for(Product prd:mProductCache) {
            if(prd.getStoreid().equals(storeId) && prd.getCategoryid().equals(categoryId)) {
                prds.add(prd);
            }
        }
        return prds;
    }
    private List<Product> findProductsByCategoryidAndBrandid(Long categoryId,Long brandId) {
        List<Product> prds = new ArrayList<Product>();
        for(Product prd:mProductCache) {
            if(prd.getBrandid().equals(brandId) && prd.getCategoryid().equals(categoryId)) {
                prds.add(prd);
            }
        }
        return prds;
    }
    private List<Product> findProductsByStoreidAndBrandidAndCategoryid(Long storeId,Long brandId,Long categoryId) {
        List<Product> prds = new ArrayList<Product>();
        for(Product prd:mProductCache) {
            if(prd.getStoreid().equals(storeId) && prd.getBrandid().equals(brandId) && prd.getCategoryid().equals(categoryId)) {
                prds.add(prd);
            }
        }
        return prds;
    }
    private List<Product> findProductsByIds(List<Long> ids) {
        List<Product> prds = new ArrayList<Product>();
        for(Long id:ids) {
            prds.add(mProductMapCache.get(id));
        }
        return prds;
    }
    private void loadProducts() {
        List<Product> prds;
        if(mProductCache == null || System.currentTimeMillis() - lastProductCacheTime>3600*1000) {
            _lockProductsLock.lock();
            if(mProductCache == null || System.currentTimeMillis() - lastProductCacheTime>3600*1000) {
                lastProductCacheTime = System.currentTimeMillis();
                prds = CollectionUtils.toList(productRepo.findAll());
                for(Product prd:prds) {
                    if(prd.getBpackage()) {
                        prd.setPackages(prdPkgRepo.findByPackageid(prd.getId()));
                        LOG.info("product packages "+JSONUtils.serialize(prd.getPackages()));
                    }
                    prd.setPrdAttrs(prdAttrRepo.findByProductid(prd.getId()));
                    mProductMapCache.put(prd.getId(),prd);
                }
                mProductCache = CollectionUtils.toList(mProductMapCache.values());
            }
            _lockProductsLock.unlock();
        }
    }
    public List<Product> getProducts(Long storeId, Long categoryId, Long brandId) {
        List<Product> prds;
        loadProducts();
        if(storeId == -1L && categoryId == -1L && brandId == -1L) {
            prds = mProductCache;//CollectionUtils.toList(productRepo.findAll());
        } else if(categoryId == -1L && brandId == -1L) {
            prds = findProductsByStoreid(storeId);//productRepo.findByStoreid(storeId);
        } else if(storeId == -1L && brandId == -1L) {
            prds = findProductsByCategoryid(categoryId);
        } else if(storeId == -1L && categoryId == -1L) {
            prds = findProductsByBrandid(brandId);
        } else if(categoryId == -1L) {
            prds = findProductsByStoreidAndBrandid(storeId, brandId);
        } else if(brandId == -1L) {
            prds = findProductsByStoreidAndCategoryid(storeId, categoryId);
        } else if(storeId == -1L) {
            prds = findProductsByCategoryidAndBrandid(categoryId, brandId);
        } else {
            prds = findProductsByStoreidAndBrandidAndCategoryid(storeId, brandId, categoryId);
        }
        return mergeProductAttr(prds);
    }
    public List<Product> getProducts(Long[] prdIds) {
        List<Product> prds;
        loadProducts();
        prds = findProductsByIds(Arrays.asList(prdIds));
        return mergeProductAttr(prds);
    }
    public List<Product> getProductsByName(String name) {
        List<Product> prds = new ArrayList<Product>();
        for(Product prd:mProductCache) {
            if(prd.getName().equals(name)) {
                prds.add(prd);
            }
        }
        return mergeProductAttr(prds);
    }
    public Product getProduct(Long productId) {
        loadProducts();
        Product prd = mProductMapCache.get(productId);
        return mergeProductAttr(prd);
    }
    public List<Product> mergeProductAttr(List<Product> prds) {
        for(Product prd:prds) {
            mergeProductAttr(prd);
        }
        return prds;
    }
    public Product mergeProductAttr(Product prd) {
        if(prd == null) {
            return prd;
        }
        if(prd.getBpackage()) {
            List<PrdPackage> prdpacks = prd.getPackages();
            List<Long> ids = new ArrayList<Long>();
            if(prdpacks == null) {
                LOG.info("product packages "+JSONUtils.serialize(prd));
                return prd;
            }
            for(PrdPackage pack:prdpacks) {
                ids.add(pack.getProductid());
            }
            List<Product> prds = findProductsByIds(ids);
            List<PrdAttr> prdattrs = new ArrayList<PrdAttr>();
            for(Product subprd:prds){
                List<PrdAttr> attrs = subprd.getPrdAttrs();
                if(attrs != null) {
                    for(PrdAttr attr:attrs) {
                        boolean exist = false;
                        for(PrdAttr oattr:prdattrs) {
                            if(attr.getAttr().getVarname().equals(oattr.getAttr().getVarname())&& (attr.getValue().equals(oattr.getValue()))) {
                                exist = true;
                            }
                        }
                        if(!exist){
                            attr.setProductid(prd.getId());
                            attr.getAttr().setTypeid(prd.getTypeid());
                            prdattrs.add(attr);
                        }
                    }
                }
            }
            prd.setPrdAttrs(prdattrs);
        }
        return prd;
    }
    public List<Product> getPackageProducts(Product prd) {
        List<Product> prds = new ArrayList<Product>();
        if(prd == null) {
            return prds;
        }
        if(prd.getBpackage()) {
            List<PrdPackage> prdpacks = prd.getPackages();
            List<Long> ids = new ArrayList<Long>();
            for(PrdPackage pack:prdpacks) {
                ids.add(pack.getProductid());
            }
            return findProductsByIds(ids);
        }
        prds.add(prd);
        return prds;
    }
    public boolean isVoiceNumber(Product prd) {
        if(PrdType.isVoiceNumber(prd.getTypeid())) {
            return true;
        }
        if(prd.getBpackage()) {
            List<Product> prds = getPackageProducts(prd);
            for(Product lprd:prds) {
                if(PrdType.isVoiceNumber(lprd.getTypeid())) {
                    return true;
                }
            }
        }
        return false;
    }
    public Boolean checkOrderUserMatch(Order order, Long userId) throws UCBaseException {
        if(order == null) {
            throw new UCBaseException(ErrorCode.ERR_ORDER_NOEXIST,ErrorCode.ERR_ORDER_NOEXIST_INFO);
        }
        if(order.getUserid().equals(userId)) {
            return true;
        }
        throw new UCBaseException(ErrorCode.ERR_ORDER_USER_MISMATCH,ErrorCode.ERR_ORDER_USER_MISMATCH_INFO);
    }
    public void checkTrafficBindDataCard(Long userid, Long orderId, String simid, Date startTime, Date endTime, String areaName) throws UCBaseException {
    	checkPaiedDataCardTraffic(getImsiByAreaName(simid,areaName), startTime, endTime);
    	
    	List<OrderDetail> utvs = orderId!=null
    			?orderDetailRepo.findByUserIdAndOrderidNotAndStatusAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndPhoneIsNull(userid, orderId, Order.ORDER_STATUS_INIT, endTime, startTime)
    			:orderDetailRepo.findByUserIdAndStatusAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndPhoneIsNull(userid, Order.ORDER_STATUS_INIT, endTime, startTime);
        if(!utvs.isEmpty()) {
            for(OrderDetail od:utvs) {
            	// 把未付款的绑定该卡号时间冲突的流量套餐都先取消掉
                if ((od.getId() != null) && (od.getId() != 0L) && (od.getSimidlist().contains(simid))) {
                    Order order = orderRepo.findOne(od.getOrderid());
                    if (order != null) {
                        cancelOrder(userid, order, false);
                    }
                }
            }
        }
    }
    public void checkPaiedDataCardTraffic(String simid, Date startTime, Date endTime) throws UCBaseException {
        List<DataCardTraffic> dcts = dctRepo.findByImsiAndEffectDatetimeLessThanAndFailureDatetimeGreaterThan(simid, endTime, startTime);
        if(!dcts.isEmpty()) {
            throw new UCBaseException(ErrorCode.ERR_DATACARD_ORDERDETAIL_BINDED,ErrorCode.ERR_DATACARD_ORDERDETAIL_BINDED_INFO);
        }
    }
    private UserEVoucher buildEVoucher(Order order, EVoucher ev) {
        String SN = snGen.generateSN(order.getId().intValue());
        UserEVoucher uev = new UserEVoucher();
        uev.setName(ev.getName());
        uev.setEffectDatetime(new Date());
        if(ev.getFailureDatetime()!=null) {
            String endtime = sdf.format(uev.getEffectDatetime().getTime() + 30 * 86400000L);
            try {
                uev.setFailureDatetime(sdf.parse(endtime));
            } catch (ParseException e) {

            }
        }
        uev.setBackground(ev.getBackground());
        uev.setUserid(order.getUserid());
        uev.setRepeatable(false);
        uev.setSn(Long.valueOf(SN));
        uev.setOrderid(order.getId());
        uev.setEvoucherid(ev.getId());
        String imagefile = "images/"+DateFormatUtils.format(uev.getEffectDatetime(),"yyyyMM")+"/evouchers_img/"+SN+".PNG";
        uev.setImage(imagefile);
        return uev;
    }
    public void generateMemberEVoucher(Order order) {
        EVoucher ev = getEVoucherByType(EVoucher.EVOUCHER_MEMBER);
        List<UserEVoucher> uevs = userEVoucherRepo.findByUseridAndEvoucherid(order.getUserid(),ev.getId());
        if(uevs.isEmpty()) {
            UserEVoucher uev = buildEVoucher(order, ev);
            EVoucherGenerator.generatorMemberEV(uev, uev.getSn() + "", uev.getBackground(), uev.getImage());//EVoucherGenerator.generatorEV(uev, SN, uev.getBackground(), quantity, imagefile);
            userEVoucherRepo.save(uev);
        }
    }
    public EVoucher getEVoucherByType(int type) {
        List<EVoucher> evouchers = evoucherRepo.findByType(type);
        EVoucher ev = evouchers.get(evouchers.size()-1);
        return ev;
    }
    public void saveDataCardTraffic(Order order){
        {
            List<DataCardTraffic> mydcts = dctRepo.findByOrderid(order.getId());
            if (!mydcts.isEmpty()) {
                for (DataCardTraffic dct : mydcts) {
                    dct.setStatus(Order.ORDER_STATUS_CONFIRMED);
                }
                dctRepo.save(mydcts);
                return;
            }
        }
        List<DataCardTraffic> dcts = new ArrayList<DataCardTraffic>();
        List<OrderDetail> ods = order.getOrderDetails();
        for(OrderDetail od:ods) {
            Product prd = getProduct(od.getProductid());
            if(PrdType.isDataType(prd.getTypeid())) {
                String simids = od.getSimids();
                List<String> simidlist;
                if(simids != null && StringUtils.isNotBlank(simids)) {
                    simidlist = JSON.parseObject(simids,new TypeReference<List<String>>(){});
                } else {
                    simidlist = new ArrayList<String>();
                }
                for(int i=0;i<od.getQuantity();i++) {
                    DataCardTraffic dct = new DataCardTraffic();
                    dct.setStatus(Order.ORDER_STATUS_CONFIRMED);

                    dct.setEffectDatetime(od.getEffectDatetime());
                    dct.setFailureDatetime(od.getFailureDatetime());
                    dct.setAreaname(od.getAreaname());
                    if (i < simidlist.size()) {
                        DataCard dc = datacardRepo.findByIccid(simidlist.get(i));
                        if (dc != null) {
                            dct.setDatacardid(dc.getId());
                            dct.setIccid(dc.getIccid());
                            dct.setImsi(dc.getImsi());
                            if (dc.getType() == DataCard.EUROPEHK_DATACARD) {
                                DataCardArea dca = dcaRepo.findByAreaname(od.getAreaname());
                                if (dca != null) {
                                    dct.setImsi(dca.getDatacardType() == DataCard.HK_DATACARD ? dc.getImsihk() : dc.getImsi());
                                }
                            }
                        }
                    }
                    dct.setOrderdetailid(od.getId());
                    dct.setOrderid(od.getOrderid());
                    dct.setUserId(order.getUserid());
                    dcts.add(dct);
				/*if(dct.getDatacardid()!=null && dct.getEffectDatetime()!=null && dct.getFailureDatetime()!=null) {
					dcts.add(dct);
				} else {
					LOG.info("datacardid "+dct.getDatacardid()+",startTime "+dct.getEffectDatetime()+",endTime "+dct.getFailureDatetime());
				}*/
                }
            }
        }
        if(!dcts.isEmpty()) {
			/*check whether the user first purchase data traffic service package*/
            List<DataCardTraffic> mydcts = dctRepo.findByUserId(order.getUserid());
            if(mydcts.isEmpty()) {
                generateMemberEVoucher(order);
            }
            mydcts = dctRepo.findByOrderid(order.getId());
            if(mydcts.isEmpty()) {
                dctRepo.save(dcts);
            }
        }
        RxBus.getInstance().post(new PurchaseDataTrafficEvent(dcts));
    }
    public void deleteDataCardTraffic(Order order){
        List<DataCardTraffic> dcts = dctRepo.findByOrderid(order.getId());

        if(!dcts.isEmpty()) {
            dctRepo.delete(dcts);
            dctHandler.delete(dcts);
            RxBus.getInstance().post(new RemoveDataTrafficEvent(dcts));
        }
    }
    private EVoucher removeDeliveryEVoucher(Order order) {
        EVoucher ev = getEVoucherByType(EVoucher.EVOUCHER_DELIVERY);
        return removeDeliveryEVoucher(order, ev);
    }
    private EVoucher removeDeliveryEVoucher(Order order, EVoucher ev) {
        List<UserEVoucher> uevs = userEVoucherRepo.findByOrderidAndEvoucherid(order.getId(), ev.getId());
        if(uevs != null && !uevs.isEmpty()) {
            userEVoucherRepo.delete(uevs);
            for(UserEVoucher uev:uevs) {
                EVoucherGenerator.deleteEVImage(uev.getImage());
            }
        }
        return ev;
    }
    public Outlet updateCities(Outlet addr) {
        addr.setCities(getCities(addr.getProvince().longValue(),addr.getCity().longValue(),addr.getDistrict().longValue()));
        return addr;
    }
    public List<City> getCities(Long province, Long city, Long district) {
        List<Long> ids = new ArrayList<Long>();
        ids.add(province);
        ids.add(city);
        ids.add(district);
        return CollectionUtils.toList(cityRepo.findAll(ids));
    }
    private String getCityAddress(List<City> cities) {
        String detailaddr;
        String province = cities.get(0).getName();
        if(province.equals("北京")||province.equals("上海")||province.equals("天津")||province.equals("重庆")) {
            detailaddr=province+"市";
        } else {
            detailaddr=province+"省";
        }
        detailaddr+=cities.get(1).getName()+"市"+cities.get(2).getName();
        return detailaddr;
    }
    private void generateDeliveryEVoucher(Order order) {
        EVoucher ev = removeDeliveryEVoucher(order,getEVoucherByType(EVoucher.EVOUCHER_DELIVERY));
        UserEVoucher uev = buildEVoucher(order,ev);

        Outlet outlet = outletRepo.findOne(order.getOutletid());
        updateCities(outlet);
        //String description = "请在截止日前到"+outlet.getName()+"网点\r\n"+"现场领取全球上网卡\r\n详细地址："+getCityAddress(outlet.getCities())+"\r\n"+outlet.getAddress();
        String description = getCityAddress(outlet.getCities())+outlet.getAddress();
        uev.setDescription(description);
        List<OrderDetail> ods = orderDetailRepo.findByOrderid(order.getId());
        Integer quantity = 0;
        for(OrderDetail od:ods) {
            Product prd = getProduct(od.getProductid());
            if(prd.getTypeid()==PrdType.PRD_DATACARD || (prd.getBpackage() && prd.getTypeid()==PrdType.PRD_DATATRAFFIC)) {
                quantity += od.getQuantity();
            }
        }
        EVoucherGenerator.generatorEV(uev, uev.getSn()+"", uev.getBackground(), quantity, uev.getImage());
        userEVoucherRepo.save(uev);
    }
    public void generatorRechargeEVouchers(Order order) {
        List<OrderDetail> ods = order.getOrderDetails();
        if(ods == null || ods.isEmpty()) {
            return;
        }
        for(OrderDetail od:ods){
            generatorRechargeEVoucher(order,od);
        }
    }

    /**
     * 生成该订单的优惠券，可以在下次订单中使用？
     * @param order
     * @param od
     * @return
     */
    public List<UserEVoucher> generatorRechargeEVoucher(Order order, OrderDetail od) {
        ODPrdAttr genEVoucher = OrderPriceUtil.findODPrdAttrByVarname(od.getOdprdattrs(), ODPrdAttr.GENEVOUCHER_TAG);
        if(genEVoucher == null || !Boolean.valueOf(genEVoucher.getValue())){
            return null;
        }
        List<EVoucher> evouchers = evoucherRepo.findByType(EVoucher.EVOUCHER_RECHARGE);
        EVoucher ev = evouchers.get(evouchers.size()-1);
        List<UserEVoucher> ouevs = userEVoucherRepo.findByOrderidAndEvoucherid(od.getId(), ev.getId());
        if(ouevs != null && !ouevs.isEmpty()) {
            userEVoucherRepo.delete(ouevs);
        }
        List<UserEVoucher> uevs = new ArrayList<UserEVoucher>();
        ODPrdAttr startTime = OrderPriceUtil.findODPrdAttrByVarname(od.getOdprdattrs(), ODPrdAttr.STARTTIME_TAG);
        Date now = new Date();
        Date startDate;
        if(startTime != null) {
            try {
                startDate = sdf.parse(startTime.getValue());
            } catch (ParseException e) {
                startDate = now;
            }
        } else {
            String endtime = sdf.format(now.getTime()+30*86400000L);
            try {
                startDate = sdf.parse(endtime);
            } catch (ParseException e) {
                startDate = now;
            }
        }
        for(int i=0;i<od.getQuantity();i++) {
            UserEVoucher uev = new UserEVoucher();
            String SN = snGen.generateSN(od.getId().intValue(),i,od.getQuantity().toString().length());
            uev.setName(ev.getName());
            //uev.setEffectDatetime(now);
			/*String endtime = sdf.format(uev.getEffectDatetime().getTime()+30*86400000L);
			try {
				uev.setFailureDatetime(sdf.parse(endtime));
			} catch (ParseException e) {

			}*/
            uev.setFailureDatetime(startDate);
            uev.setBackground(ev.getBackground());
            uev.setUserid(order.getUserid());
            uev.setRepeatable(false);
            uev.setSn(Long.valueOf(SN));
            uev.setOrderid(od.getId());
            uev.setEvoucherid(ev.getId());
            uev.setMoney(od.getPrice());

            String description = "请在截止日前分享给好友";
            uev.setDescription(description);
            String imagefile = "images/"+ DateFormatUtils.format(now,"yyyyMM")+"/evouchers_img/"+SN+".PNG";
            imagefile = EVoucherGenerator.generatorEV(uev, SN, uev.getBackground(), imagefile);
            uev.setImage(imagefile);
            uevs.add(userEVoucherRepo.save(uev));
        }
        return uevs;
    }
    public int compareDouble(Double a, Double b) {
        BigDecimal bda = new BigDecimal(a);
        BigDecimal bdb = new BigDecimal(b);
        return bda.compareTo(bdb);
    }
    public Order updateOrderDetail(Long userId, OrderDetail od, Long productId, Integer quantity, List<ODPrdAttr> odPrdAttrs) throws UCBaseException {
        if(od == null) {
            throw new UCBaseException(ErrorCode.ERR_ORDERDETAIL_NOEXIST,ErrorCode.ERR_ORDERDETAIL_NOEXIST_INFO);
        }
        Order inOrder = orderRepo.findOne(od.getOrderid());
        checkOrderUserMatch(inOrder, userId);

        ODAttr odAttr = getODAttr(odPrdAttrs);
        Product prd = getProduct(productId);
        TouchVoice tv = null;
        if(isVoiceNumber(prd)) {
            tv = tvHandler.orderFixedPhone(userId, od.getId(), odAttr.getStartTime(), odAttr.getEndTime());
            if(tv == null) {
                //inOrder.setOrderStatus(Order.ORDER_STATUS_CANCELLED);
                //orderRepo.save(inOrder);
                return inOrder;
            }
        } else if(PrdType.isDataType(prd.getTypeid())) {
            if(StringUtils.isNotBlank(odAttr.getSimid())) {
                checkTrafficBindDataCard(userId,od.getOrderid(), odAttr.getSimid(),odAttr.getStartTime(),odAttr.getEndTime(),odAttr.getAreaname());
            }
        }
        od.setQuantityPerUnit(odAttr.getQuantityPerUnit());
        od.setProductid(productId);
        od.setQuantity(quantity);
        updateODAttrs(od, odAttr, prd);
        List<PrdUnitPrice> prices = OrderPriceUtil.getPrdPrices(odPrdAttrs, prd,odAttr);
        od.setPrice(OrderPriceUtil.getPrdPrice(prices));
        //PrdDiscount discount = OrderPriceUtil.getPrdDiscount(prices,prd, quantity, odAttr);//getDiscountByTimeAndQuantity(cart.getProductid(),startDate,cart.getQuantity()*cart.getQuantityPerUnit());
        od.setDiscount(OrderPriceUtil.getPrdDiscount(prices,prd, quantity, odAttr));

        od.setOdprdattrs(odPrdAttrs);
        od = orderDetailRepo.save(od);
        // ------- 更新orderDetail完成 --------
        if(tv != null) {
            tv.setOrderdetailid(od.getId());
            tvRepo.save(tv);
        }

        //generatorRechargeEVoucher(inOrder, od);
        // -------- 重新计算价格 --------
        List<OrderDetail> ods = orderDetailRepo.findByOrderid(od.getOrderid());
        Double totalPrice = 0.0,totalDiscount = 0.0;
        for(OrderDetail iod:ods) {
            totalPrice += iod.getPrice()*iod.getQuantity();
            totalDiscount += iod.getDiscount();
        }
        if(compareDouble(totalPrice,inOrder.getPrice())!=0) {
	        inOrder.setPrice(totalPrice);
	        inOrder.setDiscount(totalDiscount);
	        inOrder.setPayableAmount(totalPrice-totalDiscount);
	        // 保持到数据库中
	        inOrder = orderRepo.save(inOrder);
		}
        inOrder.setOrderDetails(ods);
        
        // 更新订单详情后清空电子券的使用情况
        inOrder = updateOrderEVoucherUsage(userId, inOrder, "0");
        // TODO 计算使用优惠券之后的订单价格        
//        String cachedUserEVIds = cachedUEvIdForOrderId(userId, inOrder.getId());
//        if (StringUtils.isNotEmpty(cachedUserEVIds)) {
//        	inOrder = updateOrderEVoucherUsage(userId, inOrder.getId(), cachedUEvIdForOrderId(userId, inOrder.getId()));        	
//        }
        return inOrder;
    }
    public Order payOrder(Long userId, Long orderId, Integer payId, String payvoucher) throws UCBaseException {
        Order order = orderRepo.findOne(orderId);
        checkOrderUserMatch(order, userId);
        
        if(order != null) {
            Long lastOrder = cache.get(userId,Long.class);
            if(lastOrder != null && lastOrder.equals(orderId)) {
                cache.evict(userId);
            }
            if((order.getOrderStatus() == Order.ORDER_STATUS_INIT||order.getOrderStatus() == Order.ORDER_STATUS_CONFIRMED)&&(order.getPayStatus()!=Order.PAY_STATUS_PAYED)) {
                List<OrderDetail> ods = orderDetailRepo.findByOrderid(order.getId());
                for(OrderDetail od:ods) {
                    Product prd = getProduct(od.getProductid());
                    if (PrdType.isDataType(prd.getTypeid())) {
                        if (StringUtils.isNotBlank(od.getSimids())) {
                            for(String simid:od.getSimidlist()) {
                                checkPaiedDataCardTraffic(getImsiByAreaName(simid,od.getAreaname()), od.getEffectDatetime(), od.getFailureDatetime());
                            }
                        }
                    }
                }
                order.setPayId(payId);
                order.setPayvoucher(payvoucher);
                order.setPayStatus(Order.PAY_STATUS_PAYING);
                order.setPaytime(new Date());
                order.setOrderStatus(Order.ORDER_STATUS_CONFIRMED);
                order = orderRepo.save(order);
                order.setOrderDetails(ods);
            } else {
                throw new UCBaseException(ErrorCode.ERR_ORDER_INVALID_STATUS,ErrorCode.ERR_ORDER_INVALID_STATUS_INFO);
            }
        }
        return order;
    }
    public ODAttr getODAttr(List<ODPrdAttr> cartPrdAttrs) {
        ODAttr odAttr = new ODAttr();

        odAttr.setStartTime(new Date());
        odAttr.setQuantityPerUnit(1);
        odAttr.setCallDuration(0L);
        odAttr.setGenEvoucher(false);
        if(cartPrdAttrs == null) {
            return odAttr;
        }
        for(ODPrdAttr attr:cartPrdAttrs) {
            if(attr.getVarname().equalsIgnoreCase(ODPrdAttr.STARTTIME_TAG)) {
                try {
                    odAttr.setStartTime(sdf.parse(attr.getValue()));
                } catch (ParseException e) {

                }
            } else if(attr.getVarname().equalsIgnoreCase(ODPrdAttr.ENDTIME_TAG)) {
                try {
                    odAttr.setEndTime(sdf.parse(attr.getValue()));
                } catch (ParseException e) {

                }
            } else if(attr.getVarname().equalsIgnoreCase(ODPrdAttr.AREANAME_TAG)) {
                odAttr.setAreaname(attr.getValue());
            } else if(attr.getVarname().equalsIgnoreCase(ODPrdAttr.SIMID_TAG)) {
                odAttr.setSimid(attr.getValue());
            } else if(attr.getVarname().equalsIgnoreCase(ODPrdAttr.PHONE_TAG)) {
                odAttr.setPhone(attr.getValue());
            } else if(attr.getVarname().equalsIgnoreCase(ODPrdAttr.CALLDURATION_TAG)) {
                odAttr.setCallDuration(Long.parseLong(attr.getValue()));
            } else if(attr.getVarname().equalsIgnoreCase(ODPrdAttr.GENEVOUCHER_TAG)) {
                odAttr.setGenEvoucher(Boolean.parseBoolean(attr.getValue()));
            }
        }
        if(odAttr.getEndTime() != null) {
            odAttr.setQuantityPerUnit((int) ((odAttr.getEndTime().getTime()+1000-odAttr.getStartTime().getTime())/86400000));
        }
        return odAttr;
    }
    private OrderDetail updateODAttrs(OrderDetail od, ODAttr odAttr, Product prd) {
        if(odAttr.getCallDuration()!=null && odAttr.getCallDuration()!=0) {
            od.setCallDuration(odAttr.getCallDuration().intValue());
            Integer validity = prd.getValidity();
            if(validity == -1){
                validity = 365;
            }
            od.setEffectDatetime(new Date());
            String endtime = sdf.format(od.getEffectDatetime().getTime()+validity*86400000L);
            try {
                od.setFailureDatetime(sdf.parse(endtime));
            } catch (ParseException e) {

            }
        } else {
            od.setEffectDatetime(odAttr.getStartTime());
            od.setFailureDatetime(odAttr.getEndTime());
        }
        Set<String> simidlist = new HashSet<String>();
        if(StringUtils.isNotBlank(odAttr.getSimid())) {
            simidlist.add(odAttr.getSimid());
        }
        od.setSimidlist(simidlist);
        od.setAreaname(odAttr.getAreaname());
        od.setPhone(odAttr.getPhone());
        return od;
    }
    public Order submitOrder(Long userId, List<Cart> carts, Long tenantId) throws UCBaseException {
        LOG.info("submitOrder entering..."+userId);
        // 清除缓存的订单信息
        Long orderId = cache.get(userId,Long.class);
        if(orderId != null) {
            Order order = orderRepo.findOne(orderId);
            if(order !=null && order.getOrderStatus() == Order.ORDER_STATUS_INIT) {
                cancelOrder(userId, order, true);
            }
            cache.evict(userId);
        }
        List<TouchVoice> tvs = new ArrayList<TouchVoice>();
        //List<DataCardTraffic> dcts = new ArrayList<>();
        List<Product> prds = new ArrayList<Product>();
        for(Cart cart:carts) {

            ODAttr odAttr = getODAttr(cart.getOdprdattrs());
            Product prd = getProduct(cart.getProductid());
            prds.add(prd);
            if(isVoiceNumber(prd)) {
                if(odAttr.getStartTime()==null || odAttr.getEndTime()==null) {
                    LOG.info("缺少使用日期："+ JSONUtils.serialize(cart.getOdprdattrs())+"商品属性："+JSONUtils.serialize(mergeProductAttr(prd).getPrdAttrs()));
                    return null;
                }
                TouchVoice tv = tvHandler.orderFixedPhone(userId, null, odAttr.getStartTime(), odAttr.getEndTime());
                if(tv == null) {
                    throw new UCBaseException(ErrorCode.ERR_TOUCHVOICE_OVERFLOW,ErrorCode.ERR_TOUCHVOICE_OVERFLOW_INFO);
                }
                tvs.add(tv);
                ODPrdAttr odprdattr = new ODPrdAttr();
                odprdattr.setName("专用号码");
                odprdattr.setVarname(ODPrdAttr.PHONE_TAG);
                odprdattr.setValue(tv.getPhone());
                odprdattr.setPrice("");
                cart.getOdprdattrs().add(odprdattr);
            } else if(PrdType.isDataType(prd.getTypeid())) {
                if(StringUtils.isNotBlank(odAttr.getSimid())) {
                    checkTrafficBindDataCard(userId, null, odAttr.getSimid(),odAttr.getStartTime(),odAttr.getEndTime(),odAttr.getAreaname());
                }/* else if(!odAttr.getGenEvoucher() && !prd.getBpackage()) {
                    List<DataCardTraffic> ldcts = dctHandler.orderDataCard(userId, odAttr.getStartTime(), odAttr.getEndTime(), odAttr.getAreaname(), cart.getQuantity());
                    if(ldcts == null) {
                        throw new UCBaseException(ErrorCode.ERR_DATATRAFFIC_OVERFLOW,ErrorCode.ERR_DATATRAFFIC_OVERFLOW_INFO);
                    }
                    cart.setDcts(ldcts);
                    dcts.addAll(ldcts);
                }*/
            }
        }
        Order inOrder = new Order();
        inOrder.setCreatetime(new Date());
        inOrder.setUserid(userId);
        inOrder.setTenantId(tenantId);
        inOrder.setOrderStatus(Order.ORDER_STATUS_INIT);
        inOrder.setShippingStatus(Order.SHIPPING_STATUS_INIT);
        inOrder.setPayStatus(Order.PAY_STATUS_INIT);
        Double amount = 0.0,payable = 0.0;
        for(Cart cart:carts) {
            amount+=cart.getPrice()*cart.getQuantity();//*cart.getQuantityPerUnit();
            payable+=cart.getPrice()*cart.getQuantity()-cart.getDiscount();///**cart.getQuantityPerUnit()*//100;
        }
        inOrder.setPrice(amount);
        inOrder.setDiscount(amount-payable);
        inOrder.setPayableAmount(payable);
        inOrder.setVoucherAmount(0.0);
        Order order = orderRepo.save(inOrder);
        List<OrderDetail> ods = new ArrayList<OrderDetail>();
        for(Cart cart:carts) {
            OrderDetail od = new OrderDetail();
            od.setOrderid(order.getId());
            od.setProductid(cart.getProductid());
            od.setOdprdattrs(cart.getOdprdattrs());
//			od.setEffectDatetime(cart.getEffectDatetime());
//			od.setFailureDatetime(cart.getFailureDatetime());

            od.setQuantity(cart.getQuantity());
            od.setPrice(cart.getPrice()/**cart.getQuantityPerUnit()*/);
            od.setQuantityPerUnit(cart.getQuantityPerUnit());
            od.setDiscount(cart.getDiscount());
            od.setStatus(Order.ORDER_STATUS_INIT);
            for(Product prd:prds) {
                if(od.getProductid().equals(prd.getId())) {
                    if(PrdType.isDataOrVoice(prd.getTypeid())||PrdType.isVoiceNumber(prd.getTypeid())) {
                        ODAttr odAttr = getODAttr(od.getOdprdattrs());
                        od.setUserId(userId);
                        od.setOrderid(od.getOrderid());
                        od.setProductid(prd.getId());
                        updateODAttrs(od, odAttr, prd);
                        od.setSource("来自订单");
                    }
                }
            }
            if(cart.getDcts()!=null) {
                Set<String> simidlist = new HashSet<String>();
                for(DataCardTraffic dct:cart.getDcts()) {
                    if(StringUtils.isNotBlank(dct.getIccid())) {
                        simidlist.add(dct.getIccid());
                    }
                }
                od.setSimidlist(simidlist);
            }
            ods.add(od);
        }
        orderDetailRepo.save(ods);
        order.setOrderDetails(orderDetailRepo.findByOrderid(order.getId()));
        for(OrderDetail od:order.getOrderDetails()) {
            for(Product prd:prds) {
                if(od.getProductid().equals(prd.getId())) {
                    if(!tvs.isEmpty() && isVoiceNumber(prd)) {
                        for(TouchVoice tv:tvs) {
                            ODAttr odAttr = getODAttr(od.getOdprdattrs());
                            if(odAttr.getStartTime().equals(tv.getEffectDatetime()) && odAttr.getEndTime().equals(tv.getFailureDatetime())) {
                                tv.setOrderdetailid(od.getId());
                                tv.setOrderid(od.getOrderid());
                            }
                        }
                    }
                    /*if(!dcts.isEmpty() && PrdType.isDataType(prd.getTypeid())) {
                        List<DataCard> dcs = new ArrayList<DataCard>();
                        for(DataCardTraffic dct:dcts) {
                            ODAttr odAttr = getODAttr(od.getOdprdattrs());
                            if(odAttr.getStartTime().equals(dct.getEffectDatetime()) && odAttr.getEndTime().equals(dct.getFailureDatetime())) {
                                dct.setOrderdetailid(od.getId());
                                dct.setOrderid(od.getOrderid());
                                dcs.add(dct.getDatacard());
                            }
                        }
                        od.setDcs(dcs);
                    }*/
                }
            }
            tvRepo.save(tvs);
            //dctRepo.save(dcts);
        }
        cache.put(userId,order.getId());
        //generatorRechargeEVouchers(order);
        return order;
    }
    private Order updateShipFee(Order order) {
        List<ShippingArea> shippingAreas = shippingAreaRepo.findByShippingId(order.getShippingId());
        UserAddress addr = userAddrRepo.findOne(order.getShipAddress());
        List<City> cities = getCities(addr.getProvince().longValue(),addr.getCity().longValue(),addr.getDistrict().longValue());
        ShippingArea otherarea = null;
        for(ShippingArea area:shippingAreas) {
            String[] areas = area.getName().split("、");
            for(String scity:areas) {
                for(City city:cities) {
                    if(city.getName().equals(scity)) {
                        order.setShippingFee(area.getFee());
                        return order;
                    }
                }
                if(scity.equals("其它")) {
                    otherarea = area;
                }
            }
        }
        if(otherarea != null) {
            order.setShippingFee(otherarea.getFee());
        }
        return order;
    }
    public Order updateOrderEVoucherUsage(Long userId, Long orderId, String uEvId) throws UCBaseException {
        Order order = orderRepo.findOne(orderId);
        checkOrderUserMatch(order, userId);
        order.setOrderDetails(orderDetailRepo.findByOrderid(order.getId()));
        return updateOrderEVoucherUsage(userId, order, uEvId);
    }
    private Order updateOrderEVoucherUsage(Long userId, Order order, String uEvId) {
    	if(uEvId.startsWith("[") && uEvId.endsWith("]")) {
			uEvId = uEvId.substring(1, uEvId.length()-1);
		}
		// 重新建立订单和优惠券绑定的缓存
		cacheUEvIdForOrderId(userId, order.getId(), uEvId);
		//Order order = orderRepo.findOne(orderId);
		if ("0".equals(uEvId)) {
			// 取消使用优惠券
			return order;
		}
		String[] uEvIds = uEvId.split(",");
		List<OrderDetail> orderDetailList = order.getOrderDetails();
		List<OrderDetail> discountedOrderDetailList = new ArrayList<>();
		/**
		 * 优惠后价格计算步骤
		 * 1.遍历所有orderDetail，遍历所有限品类优惠券，判断是否能用，如果能用计算某个orderDetail优惠后价格，
		 * 2.遍历所有不限品类优惠券，用总价减去优惠券价格，得到最终价格
		 */
		
		for (int i = 0; i < uEvIds.length; i++) {
			Long uEvItemId = Long.parseLong(uEvIds[i]);
			UserEVoucher uevoucher = userEVoucherRepo.findOne(uEvItemId);
			
			EVoucher eVoucher = evoucherRepo.findOne(uevoucher.getEvoucherid());
			List <PrdEVoucher> prdEvList = eVoucher.getPrdEVouchers();
			if (prdEvList != null && !prdEvList.isEmpty()) {
				// 处理限品类优惠券
				for (OrderDetail od : orderDetailList) {
					if (discountedOrderDetailList.contains(od)) {
						continue;
					}
					List<Product> products = getPackageProducts(getProduct(od.getProductid()));
					Long prdId = null;
					for (Product product : products) {
						for (PrdEVoucher prdEVoucher : prdEvList) {
							if (product.getId().equals(prdEVoucher.getProductid())) {
								prdId = product.getId();
								break;
							}
						}
						if (prdId != null) {
							break;
						}
					}
					if (prdId != null) {
						if (eVoucher.getType() == EVoucher.EVOUCHER_MXJX) {
							if (od.getQuantityPerUnit() >= eVoucher.getMinamount()) {
								// 获取商品单价
								Product product = getProduct(prdId);
								ODAttr odAttr = getODAttr(od.getOdprdattrs());
								odAttr.setQuantityPerUnit(1);
								Double prdUnitPrice = OrderPriceUtil.getPrdPrice(OrderPriceUtil.getPrdPrices(od.getOdprdattrs(), product, odAttr));
								// 使用该优惠券
								od.setVoucherAmount(prdUnitPrice * eVoucher.getMoney());
								// 该订单详情已经用过了，下张优惠券就不能再用了
								discountedOrderDetailList.add(od);
								break;
							}
						} else if (eVoucher.getType() == 100) {
							// TODO 添加未来需要支持的优惠券类型
						}						
					}
				}
			} else {
				// 不限品类的直接使用money字段
				order.setVoucherAmount(uevoucher.getMoney()+order.getVoucherAmount());
			}
		}
		Double totalVoucherAmount = order.getVoucherAmount();
		for (OrderDetail od : discountedOrderDetailList) {
			totalVoucherAmount += od.getVoucherAmount();
		}
		// price为原总价(由每个orderDetail的price相加而得到)，
		// discount为总的梯度折扣价(由每个orderDetail的discount相加而得到)，
		// totalVoucherAmount为总的电子券优惠价格
		order.setOrderDetails(orderDetailList);
        order.setPayableAmount(order.getPrice() - order.getDiscount() - totalVoucherAmount);
		
		// TODO 临时存一下order详情
		cache.put("order:"+order.getId(), order);
		if(order.getPayableAmount()<0.0) {
		    order.setPayableAmount(0.0);
        }
		return order;
	}

    public Order updateOrder(Long userId, Long orderId, Integer shippingId, Long shippingAddr) throws UCBaseException {
        Order order = orderRepo.findOne(orderId);
        checkOrderUserMatch(order, userId);
        order.setShippingId(shippingId);
        order.setShipAddress(shippingAddr);
		/*获取邮费*/
        updateShipFee(order);
        order.setObtainvoucher(false);
		/*删除提货券*/
        removeDeliveryEVoucher(order);
        return orderRepo.save(order);
    }
    public Boolean isRealProduct(Long productId) {
        Product prd = getProduct(productId);
        if(prd.getReal()) {
            return true;
        }
        if(prd.getBpackage()) {
            List<Product> prds = getPackageProducts(prd);
            for(Product iprd:prds) {
                if(iprd.getReal()) {
                    return true;
                }
            }
        }
        return false;
    }
    public Order paidOrder(Long orderId, String payvoucher, Double amount) {
        LOG.info("orderId:"+orderId+",payvoucher:"+payvoucher+",amount:"+amount);
        Order order = orderRepo.findOne(orderId);
        if(order == null) {
            LOG.warn("paridOrder cann't find order by "+orderId+",payvoucher="+payvoucher+",amount="+amount);
            return null;
        }
        Double totalFee = OrderPriceUtil.calcTotalFee(order);
        if(compareDouble(amount,totalFee)!=0) {
            int odFee = (int)(totalFee*100);
            int paidmoney = (int) (amount*100);
            if(odFee != paidmoney) {
                LOG.warn("paridOrder payvoucher=" + payvoucher + ",paidmoney=" + amount + ",order total fee=" + totalFee);
                return null;
            }
        }
        return paidOrder(order, payvoucher, amount);
    }

    private Order paidOrder(Order order, String payvoucher, Double amount) {
        order.setPayvoucher(payvoucher);
        order.setPayStatus(Order.PAY_STATUS_PAYED);
        order.setOrderStatus(Order.ORDER_STATUS_CONFIRMED);
        order.setPaytime(new Date());
        order.setPaidmoney(amount);
        order.setOrderDetails(orderDetailRepo.findByOrderid(order.getId()));
        Boolean isReal = false;
        for(OrderDetail od:order.getOrderDetails()) {
            od.setStatus(Order.ORDER_STATUS_CONFIRMED);
            if(isRealProduct(od.getProductid())) {
                isReal = true;
            }
        }
        if(!isReal) {/*虚拟商品配送状态直接在付款后更新为已收货状态*/
            order.setShippingStatus(Order.SHIPPING_STATUS_RECEIVED);
        }
        orderDetailRepo.save(order.getOrderDetails());
        saveDataCardTraffic(order);
        tvHandler.delete(order);
        //dctHandler.delete(order);
        return orderRepo.save(order);
    }
    public Order cancelOrder(Long userId, Long orderId, Boolean delete) throws UCBaseException {
        Order order = orderRepo.findOne(orderId);
        checkOrderUserMatch(order, userId);
        return cancelOrder(userId, order, delete);
    }
    public Order cancelOrder(Long userId, Order order, Boolean delete) {
        if(order == null) {
            return null;
        }
        if((order.getPayStatus() == Order.PAY_STATUS_INIT)||(order.getPayStatus() == Order.PAY_STATUS_PAYING)) {
            order.setOrderStatus(Order.ORDER_STATUS_CANCELLED);
            order.setOrderDetails(orderDetailRepo.findByOrderid(order.getId()));
            tvHandler.delete(order);
            for(OrderDetail od:order.getOrderDetails()) {
                od.setStatus(Order.ORDER_STATUS_CANCELLED);
            }
            deleteDataCardTraffic(order);
            /*释放该订单占用的优惠券*/
            List<UserEVoucher> uevs = userEVoucherRepo.findByOrderid(order.getId());
            if(uevs != null && !uevs.isEmpty()) {
                for (UserEVoucher uev : uevs) {
                    uev.setOrderid(null);
                    uev.setUsedDatetime(null);
                }
                userEVoucherRepo.save(uevs);
            }
            //utvRepo.delete(utvRepo.findByOrderid(orderId));/*org.springframework.dao.InvalidDataAccessApiUsageException: Removing a detached instance com.roamtech.uc.model.UserTrafficVoice#9;*/
            if(delete) {
                orderDetailRepo.delete(order.getOrderDetails());
                cacheUEvIdForOrderId(userId, order.getId(), "0");
                orderRepo.delete(order);
                return order;
            }
            orderDetailRepo.save(order.getOrderDetails());
            return orderRepo.save(order);
        } else {/*refund*/
            if(compareDouble(order.getPaidmoney(),0.0)==0||(order.getPayvoucher().equals("OFFLINECHANNELPAY"))) {
                /*实际未付钱允许直接取消*/
                refundOrder(order.getId(),order.getPayvoucher(),order.getPaidmoney());
            }
			/*Refund refund = new Refund();
			refund.setOrderid(orderId);
			refund.setMoney(calcTotalRefundFee(order));
			refund.setUserid(order.getUserid());
			refund.setRefundType(order.getPayId());
			refund.setRefundAccount(order.getPayvoucher());
			refund.setCreatetime(new Date());
			refund.setStatus(Refund.REFUND_STATUS_INIT);
			refundRepo.save(refund);
			order.setOrderDetails(getOrderDetails(order.getId()));
			deleteDataCardTraffic(order);
			order.setOrderStatus(Order.ORDER_STATUS_REFUNDING);
			tvHandler.delete(order);
			for(OrderDetail od:order.getOrderDetails()) {
				od.setStatus(Order.ORDER_STATUS_REFUNDING);
			}
			orderDetailRepo.save(order.getOrderDetails());*/
            //utvRepo.deleteByOrderid(orderId);
            return order;
        }
        //return orderRepo.save(order);
    }
    public Order confirmOrder(Long userId, Long orderId) throws UCBaseException {
        Order order = orderRepo.findOne(orderId);
        checkOrderUserMatch(order, userId);
        order.setOrderStatus(Order.ORDER_STATUS_CONFIRMED);
        String uEvIds = cachedUEvIdForOrderId(userId, orderId);
        if (StringUtils.isNotEmpty(uEvIds)) {
        	String[] uEvIdArr = uEvIds.split(",");
			for (int i = 0; i < uEvIdArr.length; i++) {
				UserEVoucher uEv = userEVoucherRepo.findOne(Long.parseLong(uEvIdArr[i]));
				uEv.setOrderid(orderId);
                uEv.setUsedDatetime(new Date());
				userEVoucherRepo.save(uEv);
			}
			
			// 从缓存中读取order和orderdetail的voucheramount，保存到数据库中
			Order cachedOrder = cache.get("order:"+orderId, Order.class);
			List<OrderDetail> details = cachedOrder.getOrderDetails();
			for (OrderDetail orderDetail : details) {
				if (orderDetail.getVoucherAmount() != null) {
					orderDetailRepo.save(orderDetail);
				}
			}
			// 刷新payableAmount，下面进行save
			order.setPayableAmount(cachedOrder.getPayableAmount());
            if(compareDouble(OrderPriceUtil.calcTotalFee(order),0.0)<=0) {
                /*无需付钱，订单状态变更为已支付*/
                paidOrder(order,"EVOUCHERPAY"+uEvIds,0.0);
                order.setPayableAmount(0.0);
            }

			// 缓存已经不需要了，清除缓存
			cacheUEvIdForOrderId(userId, orderId, "0");
		}
        cache.evict(userId);
        return orderRepo.save(order);
    }
    public Order receivedOrder(Long userId, Long orderId) throws UCBaseException {
        Order order = orderRepo.findOne(orderId);
        checkOrderUserMatch(order, userId);
        return receivedOrder(order);
    }
    public Order receivedOrder(Order order) {
        order.setShippingStatus(Order.SHIPPING_STATUS_RECEIVED);
        return orderRepo.save(order);
    }
    public void refundOrder(Long orderId, String payvoucher, Double refundFee) {
        LOG.info("refund "+orderId+" "+payvoucher+" "+refundFee);
        Order order = orderRepo.findOne(orderId);
        order.setOrderDetails(orderDetailRepo.findByOrderid(order.getId()));
        if(compareDouble(refundFee,OrderPriceUtil.calcTotalFee(order))>=0) {
            order.setPayStatus(Order.PAY_STATUS_REFUNDED);
            for(OrderDetail od:order.getOrderDetails()) {
                od.setStatus(OrderDetail.ORDERDETAIL_REFUNDED);
            }
            order.setOrderStatus(Order.ORDER_STATUS_CLOSED);
            orderRepo.save(order);
            orderDetailRepo.save(order.getOrderDetails());
            deleteDataCardTraffic(order);
            tvHandler.delete(order);
            List<UserEVoucher> vouchers = userEVoucherRepo.findByOrderid(orderId);
            if(vouchers != null && !vouchers.isEmpty()) {
                for(UserEVoucher voucher:vouchers) {
                    voucher.setOrderid(null);
                    voucher.setUsedDatetime(null);
                }
                userEVoucherRepo.save(vouchers);
            }
        }
    }
    public Cart updateCart(Cart cart, Integer quantity, List<ODPrdAttr> cartPrdAttrs) {
        ODAttr odAttr = getODAttr(cartPrdAttrs);
        cart.setQuantityPerUnit(odAttr.getQuantityPerUnit());

        cart.setQuantity(quantity);
        Product prd = getProduct(cart.getProductid());
        List<PrdUnitPrice> prices = OrderPriceUtil.getPrdPrices(cartPrdAttrs, prd,odAttr);
        cart.setPrice(OrderPriceUtil.getPrdPrice(prices));
        //PrdDiscount discount = OrderPriceUtil.getPrdDiscount(prices,prd, quantity, odAttr);//getDiscountByTimeAndQuantity(cart.getProductid(),startDate,cart.getQuantity()*cart.getQuantityPerUnit());
        cart.setDiscount(OrderPriceUtil.getPrdDiscount(prices,prd, quantity, odAttr));
        cart.setOdprdattrs(cartPrdAttrs);
        return cart;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        cache  = (RDCache)cacheManager.getCache("orderCache");
        cache.setL1CacheRefreshTime(0);
    }
    
    /**
     * 缓存订单使用的优惠券ID
     * @param userId 	用户编号
     * @param orderId	订单号
     * @param UEvId		用户电子券编号，传0就取消缓存
     */
    public void cacheUEvIdForOrderId(Long userId, Long orderId, String UEvId) {
    	String orderIdStr = userId+"_"+orderId;
    	if ("0".equals(UEvId)) {
			cache.evict(orderIdStr);
			cache.evict("order:"+orderId);
		} else {
			cache.put(orderIdStr, UEvId);
		}
	}
    
    public String cachedUEvIdForOrderId(Long userId, Long orderId) {
    	String orderIdStr = userId+"_"+orderId;
    	return cache.get(orderIdStr, String.class);
    }

    public void checkPaiedDataCardTraffic(DataCard dc, OrderDetail utv)  throws UCBaseException {
            checkPaiedDataCardTraffic(getImsiByAreaName(dc,utv.getAreaname()), utv.getEffectDatetime(), utv.getFailureDatetime());
    }

    private String getImsiByAreaName(String simid, String areaName) throws UCBaseException {
        DataCard dc = datacardRepo.findByIccid(simid);
        if (dc == null) {
            throw new UCBaseException(ErrorCode.ERR_DATACARD_NOEXIST, ErrorCode.ERR_DATACARD_NOEXIST_INFO);
        }
        return getImsiByAreaName(dc, areaName);
    }
    private String getImsiByAreaName(DataCard dc, String areaName) {
        String imsi;
        if(dc.getType() == DataCard.EUROPEHK_DATACARD) {
            DataCardArea dca = dcaRepo.findByAreaname(areaName);
            if (dca != null) {
                return dca.getDatacardType() == DataCard.HK_DATACARD?dc.getImsihk():dc.getImsi();
            } else {
                return dc.getImsi();
            }
        } else {
            return dc.getImsi();
        }
    }
}

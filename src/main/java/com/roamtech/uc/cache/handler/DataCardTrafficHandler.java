package com.roamtech.uc.cache.handler;

import com.roamtech.uc.cache.RDCache;
import com.roamtech.uc.model.*;
import com.roamtech.uc.repository.*;
import com.roamtech.uc.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import java.util.*;

@Component
public class DataCardTrafficHandler implements InitializingBean {
	private static final Logger LOG = LoggerFactory.getLogger(DataCardTrafficHandler.class);
	private RDCache cache;
	@Autowired
	@Qualifier("ucCacheManager")
	private CacheManager cacheManager;
	@Autowired
	DataCardTrafficRepository dctRepo;
	@Autowired
	DataCardRepository dcRepo;
	@Autowired
	OrderRepository orderRepo;
	@Autowired
	OrderDetailRepository orderDetailRepo;
	
	private JedisCluster client;
	private static final int DEFAULT_TIME_OUT = 3600;
	private Map<String,DataCard> dataCards;
	@Override
	public void afterPropertiesSet() throws Exception {
		cache  = (RDCache)cacheManager.getCache("datatraffic");
		client = (JedisCluster)cache.getNativeCache();
		//loadDataCards();
	}
	public void loadDataCards() {
		Map<String,DataCard> cards = new HashMap<String,DataCard>();
		List<DataCard> datacards = dcRepo.findByDistributor(DataCard.ROAMDATA_COMPID);
		for(DataCard dc:datacards) {
			cards.put(dc.getIccid(),dc);
		}
		dataCards = cards;
	}
	public void save(DataCardTraffic dct) {
		client.zadd(cache.getCacheKey("dctzset"), dct.getId(), dct.getId().toString());
		cache.put(dct.getId().toString(), dct, DEFAULT_TIME_OUT);
	}
	public void save(List<DataCardTraffic> dcts) {
		for(DataCardTraffic dct:dcts) {
			save(dct);
		}
	}
	public void delete(DataCardTraffic dct) {
		client.zrem(cache.getCacheKey("dctzset"), dct.getId().toString());
		cache.evict(dct.getId().toString());
	}
	public void delete(List<DataCardTraffic> dcts) {
		if(dcts != null && !dcts.isEmpty()) {
			for(DataCardTraffic dct:dcts) {
				delete(dct);
			}
		}
	}
	public void delete(Long id) {
		client.zrem(cache.getCacheKey("dctzset"), id.toString());
		cache.evict(id.toString());
	}	
	public DataCardTraffic findOne(Long id) {
		return cache.get(id.toString(), DataCardTraffic.class);
	}
	
	public List<DataCardTraffic> findAll() {
		String dctzsetkey = cache.getCacheKey("dctzset");
		Set<String> dctzset = client.zrange(dctzsetkey, 0, -1);
		List<DataCardTraffic> result = new ArrayList<DataCardTraffic>();
		if(dctzset != null) {
			for (String id : dctzset) {
				DataCardTraffic dct = cache.get(id,
						DataCardTraffic.class);
				if(null != dct) {
					result.add(dct);
				} else {
					DataCardTraffic ldct = dctRepo.findOne(Long.parseLong(id));
					if(ldct != null && ldct.getOrderdetailid() != null) {
						OrderDetail od = orderDetailRepo.findOne(ldct.getOrderdetailid());
						Order order = orderRepo.findOne(od.getOrderid());
						if((order != null)&&((order.getPayStatus() == Order.PAY_STATUS_INIT)||(order.getPayStatus() == Order.PAY_STATUS_PAYING))) {
							order.setOrderStatus(Order.ORDER_STATUS_CANCELLED);
							orderRepo.save(order);
							dctRepo.delete(ldct);
						}				
					}
					client.zrem(dctzsetkey, id);
				}
			}
		}
		return result;
	}
	private List<DataCardTraffic> allocDataCard(Long userId, Date startTime, Date endTime, String areaName, Integer quantity) {
		/*检查该用户是否已订购有交集的时间段的专属号*//*
		List<DataCardTraffic> tvs = otv!=null?dctRepo.findByIccidAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndOrderdetailidNot(otv.getIccid(), endTime, startTime, orderDetailId)
				:dctRepo.findByUserIdAndEffectDatetimeLessThanAndFailureDatetimeGreaterThan(userId, endTime, startTime);
		if(tvs != null && !tvs.isEmpty()) {
			return null;
		}
		if(otv != null) {
			otv.setEffectDatetime(startTime);
			otv.setFailureDatetime(endTime);
			return otv;
		}*/
		/*查询用户购买过的语音套餐*//*
		tvs = tvRepo.findByUserIdOrderByFailureDatetimeDesc(userId,new PageRequest(0, 3));
		if(tvs != null && !tvs.isEmpty()) {
			for(TouchVoice ltv:tvs) {
				List<TouchVoice> ltvs = tvRepo.findByPhoneAndEffectDatetimeLessThanAndFailureDatetimeGreaterThan(ltv.getPhone(), endTime, startTime);
				if(ltvs != null && !ltvs.isEmpty()) {
					continue;
				}
				tv.setPhone(ltv.getPhone());
				tv.setTouchdevid(ltv.getTouchdevid());
				tv.setTouchchansid(ltv.getTouchchansid());
				tv.setSgroup(ltv.getSgroup());
				return tv;
			}
		}*/

		List<DataCardTraffic> busydcts = dctRepo.findByEffectDatetimeLessThanAndFailureDatetimeGreaterThan(endTime, startTime);
		Set<String> freeDataCards = new HashSet<String>();
		freeDataCards.addAll(dataCards.keySet());
		for(DataCardTraffic bdct:busydcts) {
			freeDataCards.remove(bdct.getIccid());
		}
		if(freeDataCards.isEmpty()) {
			return null;
		}
		if(freeDataCards.size()<quantity) {
			return null;
		}
		List<DataCardTraffic> dcts = new ArrayList<>();
		Iterator<String> iter = freeDataCards.iterator();
		for(int i=0;i<quantity;i++) {
			DataCardTraffic dct = new DataCardTraffic();
			dct.setUserId(userId);
			dct.setEffectDatetime(startTime);
			dct.setFailureDatetime(endTime);
			dct.setIccid(iter.next());
			DataCard dc = dataCards.get(dct.getIccid());
			dct.setDatacard(dc);
			dct.setDatacardid(dc.getId());
			dct.setIccid(dc.getIccid());
			dct.setAreaname(areaName);
			dct.setStatus(0);
			dcts.add(dct);
		}
		return dcts;
	}
	public List<DataCardTraffic> orderDataCard(Long userId, Date startTime, Date endTime, String areaName, Integer quantity) {
		LOG.info("orderDataCard entering...");
		List<DataCardTraffic> dcts = allocDataCard(userId,startTime,endTime, areaName, quantity);
		if(dcts != null) {
			LOG.info("allocDataCard called..."+dcts.get(0).getIccid());
			boolean locked = cache.getRDLock().lock("lock");
			if(locked) {
				LOG.info("check the datacard "+dcts.get(0).getIccid()+" is free...");
				/*List<DataCardTraffic> ltvs = orderDetailId!=null
						?dctRepo.findByIccidAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndOrderdetailidNot(dct.getIccid(), endTime, startTime, orderDetailId)
						:dctRepo.findByIccidAndEffectDatetimeLessThanAndFailureDatetimeGreaterThan(dct.getIccid(), endTime, startTime);
				if(ltvs != null && !ltvs.isEmpty()) {
					cache.getRDLock().unlock("lock", locked);
					LOG.info("the datacard "+dcts.get(0).getIccid()+" not free, repeat orderDataCard");
					return orderDataCard(userId, orderDetailId, startTime, endTime, cart.getQuantity());
				}*/
				LOG.info("dctRepo.save calling...");
				dcts = CollectionUtils.toList(dctRepo.save(dcts));
				LOG.info("cache.save calling...");
				save(dcts);
			}
			LOG.info("RDLock unlock calling...");
			cache.getRDLock().unlock("lock", locked);
		}
		LOG.info("orderDataCard exit.");
		return dcts;
	}
	public void delete(Order order) {
		if(order.getPayStatus() == Order.PAY_STATUS_PAYED) {
			List<DataCardTraffic> tvs = dctRepo.findByOrderid(order.getId());
			delete(tvs);
		}
		if(order.getOrderStatus() == Order.ORDER_STATUS_CANCELLED || order.getOrderStatus() == Order.ORDER_STATUS_REFUNDING) {
			List<DataCardTraffic> tvs = dctRepo.findByOrderid(order.getId());
			delete(tvs);
			dctRepo.delete(tvs);
		}
	}
}

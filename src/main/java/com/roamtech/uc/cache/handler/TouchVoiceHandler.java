package com.roamtech.uc.cache.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.roamtech.uc.cache.RDCache;
import com.roamtech.uc.cache.model.FixedPhone;
import com.roamtech.uc.model.Order;
import com.roamtech.uc.model.OrderDetail;
import com.roamtech.uc.model.Touch;
import com.roamtech.uc.model.TouchChans;
import com.roamtech.uc.model.TouchVoice;
import com.roamtech.uc.repository.OrderDetailRepository;
import com.roamtech.uc.repository.OrderRepository;
import com.roamtech.uc.repository.TouchChansRepository;
import com.roamtech.uc.repository.TouchRepository;
import com.roamtech.uc.repository.TouchVoiceRepository;

import redis.clients.jedis.JedisCluster;

@Component
public class TouchVoiceHandler implements InitializingBean {
	private static final Logger LOG = LoggerFactory.getLogger(TouchVoiceHandler.class);
	private RDCache cache;
	@Autowired
	@Qualifier("ucCacheManager")
	private CacheManager cacheManager;
	@Autowired
	TouchVoiceRepository tvRepo;
	@Autowired
	TouchRepository touchRepo;
	@Autowired
	TouchChansRepository tchnsRepo;
	@Autowired
	OrderRepository orderRepo;
	@Autowired
	OrderDetailRepository orderDetailRepo;
	@Autowired
	DataCardTrafficHandler dctHandler;
	private JedisCluster client;
	private static final int DEFAULT_TIME_OUT = 600;
	private Map<String,FixedPhone> fixedPhones;
	@Override
	public void afterPropertiesSet() throws Exception {
		cache  = (RDCache)cacheManager.getCache("touchvoice");
		client = (JedisCluster)cache.getNativeCache();
		loadFixedPhone();
	}
	public void loadFixedPhone() {
		Map<String,FixedPhone> phones = new HashMap<String,FixedPhone>();
		List<Touch> touchs = touchRepo.findByDevtype(Touch.DEVTYPE_FIXEDNUMBER);
		for(Touch touch:touchs) {
			if(StringUtils.isNotBlank(touch.getPhone())) {
				FixedPhone phone = new FixedPhone();
				phone.setPhone(touch.getPhone());
				phone.setTouchdevid(touch.getId());
				phone.setSgroup(touch.getSgroup());
				phones.put(touch.getPhone(),phone);
			} else {
				List<TouchChans> tchns = tchnsRepo.findByDevId(touch.getId());
				for(TouchChans chn:tchns) {
					if(StringUtils.isNotBlank(chn.getPhone())) {
						FixedPhone phone = new FixedPhone();
						phone.setPhone(touch.getPhone());
						phone.setTouchdevid(touch.getId());
						phone.setSgroup(touch.getSgroup());
						phone.setTouchchansid(chn.getId());
						phones.put(chn.getPhone(),phone);
					}
				}
			}
		}
		fixedPhones = phones;
		dctHandler.loadDataCards();
	}
	public void save(TouchVoice tv) {
		client.zadd(cache.getCacheKey("tvzset"), tv.getId(), tv.getId().toString());
		cache.put(tv.getId().toString(), tv, DEFAULT_TIME_OUT);
	}
	
	public void delete(TouchVoice tv) {
		client.zrem(cache.getCacheKey("tvzset"), tv.getId().toString());
		cache.evict(tv.getId().toString());
	}
	public void delete(List<TouchVoice> tvs) {
		if(tvs != null && !tvs.isEmpty()) {
			for(TouchVoice tv:tvs) {
				delete(tv);
			}
		}
	}
	public void delete(Long id) {
		client.zrem(cache.getCacheKey("tvzset"), id.toString());
		cache.evict(id.toString());
	}	
	public TouchVoice findOne(Long id) {
		return cache.get(id.toString(), TouchVoice.class);
	}
	
	public List<TouchVoice> findAll() {
		String tvzsetkey = cache.getCacheKey("tvzset");
		Set<String> tvzset = client.zrange(tvzsetkey, 0, -1);
		List<TouchVoice> result = new ArrayList<TouchVoice>();
		if(tvzset != null) {
			for (String id : tvzset) {
				TouchVoice tv = cache.get(id,
						TouchVoice.class);
				if(null != tv) {
					result.add(tv);
				} else {
					TouchVoice ltv = tvRepo.findOne(Long.parseLong(id));
					if(ltv != null && ltv.getOrderdetailid() != null) {
						OrderDetail od = orderDetailRepo.findOne(ltv.getOrderdetailid());
						Order order = orderRepo.findOne(od.getOrderid());
						if((order != null)&&((order.getPayStatus() == Order.PAY_STATUS_INIT)||(order.getPayStatus() == Order.PAY_STATUS_PAYING))) {
							order.setOrderStatus(Order.ORDER_STATUS_CANCELLED);
							orderRepo.save(order);
							tvRepo.delete(ltv);
						}				
					}
					client.zrem(tvzsetkey, id);
				}
			}
		}
		return result;
	}
	private TouchVoice allocTouchChannel(Long userId, Long orderDetailId, Date startTime, Date endTime) {
		TouchVoice otv = null;
		TouchVoice tv = new TouchVoice();
		tv.setUserId(userId);
		tv.setEffectDatetime(startTime);
		tv.setFailureDatetime(endTime);
		if(orderDetailId != null) {
			otv = tvRepo.findByOrderdetailid(orderDetailId);
		}
		/*检查该用户是否已订购有交集的时间段的专属号*/
		List<TouchVoice> tvs = otv!=null?tvRepo.findByUserIdAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndOrderdetailidNot(userId, endTime, startTime, orderDetailId)
				:tvRepo.findByUserIdAndEffectDatetimeLessThanAndFailureDatetimeGreaterThan(userId, endTime, startTime);
		if(tvs != null && !tvs.isEmpty()) {
			return null;
		}
		if(otv != null) {
			otv.setEffectDatetime(startTime);
			otv.setFailureDatetime(endTime);
			return otv;
		}
		/*查询用户购买过的语音套餐*/
		tvs = tvRepo.findByUserIdOrderByFailureDatetimeDesc(userId,new PageRequest(0, 3));
		if(tvs != null && !tvs.isEmpty()) {
			for(TouchVoice ltv:tvs) {
				List<TouchVoice> ltvs = tvRepo.findByPhoneAndEffectDatetimeLessThanAndFailureDatetimeGreaterThan(ltv.getPhone(), endTime, startTime);
				if(ltvs != null && !ltvs.isEmpty()) {
					continue;
				}
				if(!fixedPhones.keySet().contains(ltv.getPhone())) {
					continue;/*号码类型变化*/
				}
				tv.setPhone(ltv.getPhone());
				tv.setTouchdevid(ltv.getTouchdevid());
				tv.setTouchchansid(ltv.getTouchchansid());
				tv.setSgroup(ltv.getSgroup());
				return tv;
			}
		}

		List<TouchVoice> busytvs = tvRepo.findByEffectDatetimeLessThanAndFailureDatetimeGreaterThan(endTime, startTime);
		Set<String> freePhones = new HashSet<String>();
		freePhones.addAll(fixedPhones.keySet());
		for(TouchVoice btv:busytvs) {
			freePhones.remove(btv.getPhone());
		}
		if(freePhones.isEmpty()) {
			return null;
		}
		/*找最空闲的号码，TBD*/
		tv.setPhone(freePhones.iterator().next());
		FixedPhone phone = fixedPhones.get(tv.getPhone());
		tv.setTouchdevid(phone.getTouchdevid());
		tv.setTouchchansid(phone.getTouchchansid());
		tv.setSgroup(phone.getSgroup());
		return tv;
	}
	public TouchVoice orderFixedPhone(Long userId, Long orderDetailId, Date startTime, Date endTime) {
		LOG.info("orderFixedPhone entering...");
		TouchVoice tv = allocTouchChannel(userId,orderDetailId,startTime,endTime);
		if(tv != null) {
			LOG.info("allocTouchChannel called..."+tv.getPhone());
			boolean locked = cache.getRDLock().lock("lock"+tv.getPhone());
			if(locked) {
				LOG.info("check the phone "+tv.getPhone()+" is free...");
				List<TouchVoice> ltvs = orderDetailId!=null
						?tvRepo.findByPhoneAndEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndOrderdetailidNot(tv.getPhone(), endTime, startTime, orderDetailId)
						:tvRepo.findByPhoneAndEffectDatetimeLessThanAndFailureDatetimeGreaterThan(tv.getPhone(), endTime, startTime);
				if(ltvs != null && !ltvs.isEmpty()) {
					cache.getRDLock().unlock("lock"+tv.getPhone(), locked);
					LOG.info("the phone "+tv.getPhone()+" not free, repeat orderFixedPhone");
					return orderFixedPhone(userId, orderDetailId, startTime, endTime);
				}
				LOG.info("tvRepo.save calling...");
				tv = tvRepo.save(tv);
				LOG.info("cache.save calling...");
				save(tv);
			}
			LOG.info("RDLock unlock calling...");
			cache.getRDLock().unlock("lock"+tv.getPhone(), locked);		
		}
		LOG.info("orderFixedPhone exit.");
		return tv;
	}
	public void delete(Order order) {
		if(order.getPayStatus() == Order.PAY_STATUS_PAYED) {
			List<TouchVoice> tvs = tvRepo.findByOrderid(order.getId());
			delete(tvs);
			
		}
		if(order.getOrderStatus() == Order.ORDER_STATUS_CANCELLED || order.getOrderStatus() == Order.ORDER_STATUS_REFUNDING) {
			List<TouchVoice> tvs = tvRepo.findByOrderid(order.getId());
			delete(tvs);
			tvRepo.delete(tvs);
		}
	}
}

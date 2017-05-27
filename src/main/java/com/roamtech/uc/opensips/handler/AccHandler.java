package com.roamtech.uc.opensips.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.roamtech.uc.model.Touch;
import com.roamtech.uc.repository.TouchRepository;
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
import com.roamtech.uc.cache.RDCounter;
import com.roamtech.uc.cache.RDLock;
import com.roamtech.uc.model.CallDetailRecord;
import com.roamtech.uc.model.Phone;
import com.roamtech.uc.opensips.model.Acc;
import com.roamtech.uc.opensips.repository.AccRepository;
import com.roamtech.uc.repository.CDRRepository;
import com.roamtech.uc.repository.PhoneRepository;
import com.roamtech.uc.util.JSONUtils;

import redis.clients.jedis.JedisCluster;

@Component
public class AccHandler  implements InitializingBean {
	private static final Logger LOG = LoggerFactory.getLogger(AccHandler.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final int pagesize = 100;
	private RDCache cache;
	private RDCounter counter;
	private RDLock dlock;
	private JedisCluster client;
	@Autowired
	@Qualifier("ucCacheManager")
	private CacheManager cacheManager;
	
	@Autowired
	AccRepository accRepo;
	@Autowired
	PhoneRepository phoneRepo;
	@Autowired
	CDRRepository cdrRepo;
	@Autowired
	TouchRepository touchRepo;
	ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
	//private Map<String,Phone> phones = new HashMap<String,Phone>();
	@Override
	public void afterPropertiesSet() throws Exception {
		cache  = (RDCache)cacheManager.getCache("acc");
		client = (JedisCluster)cache.getNativeCache();
		counter = cache.getHSCounter();
		dlock = cache.getRDLock();
		service.scheduleWithFixedDelay(
                new Runnable(){
					@Override
					public void run() {
						while(true) {
							if(processAccs()<pagesize){
								break;
							}
						}
					}
                }, 1,
                10, TimeUnit.SECONDS);
	}
	public int processAccs() {
		boolean locked = false;
		int page=0;
		long id=0;
		int size = 0;
		try {
			locked = dlock.lock("acclock",10);
			if(locked) {
				page = (int)counter.incr("accpage", 1);
				id = counter.incr("accid", 0);
			}
			dlock.unlock("acclock",locked);
			/*if(phones.isEmpty()) {
				Iterable<Phone> allphones = phoneRepo.findAll();
				for(Phone phone:allphones) {
					phones.put(phone.getPhone(), phone);
				}
			}*/
			if(id == 0) {
				//Set<String> keys = client.hkeys(cache.getCacheKey("cdrhash"));
				//client.hdel(cache.getCacheKey("cdrhash"), keys.toArray(new String[keys.size()]));
				Set<String> userids = client.zrange(cache.getCacheKey("userset"),0,-1);
				for(String userid:userids) {
					client.zremrangeByRank(cache.getCacheKey("usercdrs")+userid, 0, -1);
					client.del(cache.getCacheKey("usercdrstats")+userid);
				}
			}
			if(locked) {
				//LOG.info("accRepo.findByIdGreaterThan("+id+","+(page-1)+") calling");
				List<Acc> accs = accRepo.findByIdGreaterThan(id,new PageRequest(page-1,pagesize));
				LOG.info("accRepo.findByIdGreaterThan("+id+","+(page-1)+") accs.size="+accs.size());
				long lastId = 0;
				if(accs != null && !accs.isEmpty()){
					size = accs.size();
					lastId = accs.get(accs.size()-1).getId();

						Map<String,CallDetailRecord> cdrs = new HashMap<String,CallDetailRecord>();
					for(Acc acc:accs) {
						String calluuid = acc.getCallId();//+";"+acc.getFromTag()+";"+acc.getToTag();
						//LOG.debug("client.hget("+cache.getCacheKey("cdrhash")+","+calluuid+")");
						CallDetailRecord cdr = cdrs.get(calluuid);
						if(cdr == null && acc.getMethod().equals("BYE")) {
							cdr = JSONUtils.deserialize(client.hget(cache.getCacheKey("cdrhash"),calluuid),CallDetailRecord.class);
						}
						if(cdr == null) {
							cdr = new CallDetailRecord();
						}
						if(acc.getMethod().equals("INVITE")) {
							//cdr.setId(acc.getId());
							cdr.setCaller(acc.getCaller());
							cdr.setCallee(acc.getCallee());
							cdr.setCreateTime(acc.getCreated());
							cdr.setSetuptime(acc.getSetupTime());
							cdr.setStartTime(acc.getTime());
							cdr.setDuration(acc.getDuration());
							cdr.setUserId(acc.getUserId());
							cdr.setCallid(calluuid);
							cdr.setDirection(acc.getDirection());
							cdr.setMyroambox(false);
							String sipuri = acc.getRealdest();
							if(StringUtils.isNotBlank(sipuri)) {
								cdr.setRelayphone(sipuri.substring(sipuri.indexOf(":")+1, sipuri.indexOf("@")));
							}
							if(acc.getDuration()>0) {
								String endtime = sdf.format(acc.getTime().getTime() + acc.getDuration());
								try {
									cdr.setEndTime(sdf.parse(endtime));
								} catch (ParseException e) {

								}
							}
							/*if(cdr.getUserId()==null || cdr.getUserId() == 0) *//*{
								Phone phone = phones.get(cdr.getCaller());//phoneRepo.findOne(cdr.getCaller());
								if(phone == null) {
									phone = phones.get(cdr.getCallee());//phoneRepo.findOne(cdr.getCallee());
								} else {
									cdr.setDirection(true);
								}
								if(phone != null) {
									cdr.setUserId(phone.getUserId());
								}
							}*/
							cdr.setSipCode(acc.getSipCode());
							cdr.setSipReason(acc.getSipReason());
							if(cdr.getUserId()!=null && cdr.getUserId() != 0 && StringUtils.isNotBlank(cdr.getRelayphone())) {
								List<Touch> touchs = touchRepo.findByUserId(cdr.getUserId());
								String tphone = cdr.getRelayphone();
								if(tphone.startsWith("T")) {
									tphone = tphone.substring(1);
									if (!touchs.isEmpty()) {
										for (Touch touch : touchs) {
											if(touch.getPhone()!=null && touch.getPhone().equals(tphone)) {
												cdr.setMyroambox(true);
												break;
											}
										}
									}
								} else if(!touchs.isEmpty()) {
									cdr.setMyroambox(true);
								}
							}
							if(cdr.getDirection()&&cdr.getCreateTime()!=null) {
								client.zadd(cache.getCacheKey("userset"),acc.getId(),cdr.getUserId().toString());
								client.zadd(cache.getCacheKey("usercdrs")+cdr.getUserId(),acc.getId(),JSONUtils.serialize(cdr));
								counter.incr("usercdrstats"+cdr.getUserId(), cdr.getDuration());
							}
							/*Phone phone = phones.get(cdr.getCaller());//phoneRepo.findOne(cdr.getCaller());
							if(phone == null) {
								phone = phones.get(cdr.getCallee());//phoneRepo.findOne(cdr.getCallee());
							} else {
								cdr.setDirection(true);
							}
							if(phone != null) {
								cdr.setUserId(phone.getUserId());
							}*/
						} else if(acc.getMethod().equals("BYE")) {
							//cdr.setId(acc.getId());
							cdr.setEndTime(acc.getTime());
							cdr.setSipCode(acc.getSipCode());
							cdr.setSipReason(acc.getSipReason());
							if(cdr.getStartTime()!=null){
								cdr.setDuration((int)(cdr.getEndTime().getTime()-cdr.getStartTime().getTime())/1000);
								if(cdr.getDirection()) {
									client.zadd(cache.getCacheKey("userset"),acc.getId(),cdr.getUserId().toString());
									client.zadd(cache.getCacheKey("usercdrs")+cdr.getUserId(),acc.getId(),JSONUtils.serialize(cdr));
									counter.incr("usercdrstats"+cdr.getUserId(), cdr.getDuration());
								}
							}
						}
						if(cdr.getCaller()!= null) {
							//LOG.debug("client.hset("+cache.getCacheKey("cdrhash")+","+calluuid+")"+JSONUtils.serialize(cdr));
							client.hset(cache.getCacheKey("cdrhash"),calluuid, JSONUtils.serialize(cdr));
							cdrs.put(calluuid,cdr);
						}
					}
					//Map<String,String> scdrs = new HashMap<String,String>();
					Set<String> keys = new HashSet<String>();
					List<CallDetailRecord> pcdrs = new ArrayList<CallDetailRecord>();
					for(CallDetailRecord cdr:cdrs.values()) {
						if(cdr.getEndTime()!=null) {
							keys.add(cdr.getCallid());
							pcdrs.add(cdr);
						}
						//scdrs.put(cdr.getCallid(), JSONUtils.serialize(cdr));
					}
					//client.hmset(cache.getCacheKey("cdrhash"), scdrs);
					if(!keys.isEmpty()) {
						cdrRepo.save(pcdrs);
						client.hdel(cache.getCacheKey("cdrhash"), keys.toArray(new String[keys.size()]));
					}
				}
				locked = dlock.lock("acclock",10);
				if(locked) {
					long pageindex = counter.decr("accpage", page);
					if(lastId != 0) {
						if(pageindex == 0){
							counter.incr("accid", lastId-id);
						} else {
							id = counter.incr("accid", 0);
							if(id<lastId) {
								counter.incr("accid", lastId-id);
							}
							counter.decr("accpage", pageindex);
						}
					}
				}
				dlock.unlock("acclock",locked);
			} else {
				LOG.warn("processAccs lock failed");
			}
		} catch (Exception ex) {
			LOG.warn("processAccs exception",ex);
			dlock.unlock("acclock",locked);
		}
		return size;
	}
}

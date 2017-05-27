package com.roamtech.uc.client;

import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.roamtech.uc.client.retrofit.FastJsonConverterFactory;
import com.roamtech.uc.client.rxevent.PurchaseDataTrafficEvent;
import com.roamtech.uc.client.rxevent.RegisterDataCardEvent;
import com.roamtech.uc.client.rxevent.RemoveDataTrafficEvent;
import com.roamtech.uc.exception.UCBaseException;
import com.roamtech.uc.model.AreaCode;
import com.roamtech.uc.model.Order;
import com.roamtech.uc.repository.AreaCodeRepository;
import com.roamtech.uc.repository.DataCardTrafficRepository;
import com.roamtech.uc.util.*;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.Buffer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.UrlEncoded;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import com.alibaba.fastjson.parser.deserializer.ExtraTypeProvider;
import com.roamtech.uc.cache.RDCache;
import com.roamtech.uc.cache.RDCounter;
import com.roamtech.uc.cache.RDLock;
import com.roamtech.uc.model.DataCard;
import com.roamtech.uc.model.DataCardTraffic;
import com.roamtech.uc.repository.DataCardRepository;

import redis.clients.jedis.JedisCluster;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.schedulers.ScheduledAction;
import rx.schedulers.Schedulers;

@Component("bssApis")
public class BssApisImpl implements BssApis, InitializingBean {
	private static final Logger LOG = LoggerFactory
			.getLogger(BssApisImpl.class);
	@Autowired
	private HttpClientManager httpClientManager;
	private String httpClientName="UCSERVER_HTTP_CLIENT";
    private HttpClient _client;	
    private String bssApiUri="https://test-be.foggmobile.eu/backe/st/json/api/";
    private String password;
	private final static String DEF_CLIENT_NAME = "$__DEFAULT_HTTP_CLIENT";
    private String userName = "dashigroup";
    private String authToken="4ead2738-b319-4223-b1d4-5764e4c698c4";
	private RDCache cache;
	@Autowired
	@Qualifier("ucCacheManager")
	private CacheManager cacheManager;
	private JedisCluster jedisclient;
	private RDCounter counter;
	private RDLock dlock;
	private int pagesize = 100;
	@Autowired
	DataCardRepository datacardRepo;
	@Autowired
	DataCardTrafficRepository dctRepo;
	@Autowired
	AreaCodeRepository countryRepo;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdfmm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	SimpleDateFormat hksdf = new SimpleDateFormat("yyyyMMddHHmmss");
	Retrofit retrofit;
	BssApiService apiService;
	private String hkApiUri="http://203.160.90.165:8080/httpApi/v1/dashi/";
	HKUnicomApiService hkApiService;
	private Boolean running = true;
	private static final String BSSAPIS_AUTHTOKEN_KEY = "uc.bssapis.authToken";
    public String sendReceive(String serviceUri,
			Map<String, String> params,String body, String contentType) {
    	return httpClientManager.sendReceive(_client,serviceUri,params,body,contentType);
    	//return sendReceive(_client,serviceUri,params,body,contentType);
    }
    private void loadBssUsersAndSurfPasses() {
		boolean locked;
		long offset=0;
        int limit = 20;

    	while(true) {
			locked = dlock.lock("userlock",10);
			if(locked) {
				offset = counter.incr("useroffset", 0);
			}
			dlock.unlock("userlock",locked);
			if(locked) {
				List<BssUserSummary> users = GetUsersAPI(null, null, (int)offset,
						limit);
				if (users == null||users.isEmpty()) {
					break;
				}
				LOG.info("GetUsersAPI("+offset+","+limit+")="+users.size());
				Map<String, String> userhash = new HashMap<String, String>();
				for (BssUserSummary user : users) {
					userhash.put(user.getImsi(), JSONUtils.serialize(user));
					List<DataCard> dcs = datacardRepo.findByImsi(user.getImsi());
					if(dcs == null || dcs.isEmpty()) continue;
					for(DataCard dc:dcs) {
						if (StringUtils.isBlank(dc.getUserIdentifier())) {
							dc.setUserIdentifier(user.getUserIdentifier());
							datacardRepo.save(dc);
						}
					}
				}
				jedisclient.hmset(cache.getCacheKey("usersummhash"), userhash);
				locked = dlock.lock("userlock",10);
				if(locked) {
					offset = counter.incr("useroffset", users.size());
				}
				dlock.unlock("userlock",locked);
				if (users.size() < limit) {
					break;
				}
			}
    	}
		List<DataCard> dcs = datacardRepo.findByIdGreaterThanAndUserIdentifierNotNull(1,new PageRequest(0,1));
    	BssUserSummary user = JSON.parseObject(jedisclient.hget(cache.getCacheKey("usersummhash"),dcs.get(0).getImsi()),BssUserSummary.class);
    	List<ExtraPass> extraPasses = GetExtraPassesAPI(user.getUserIdentifier()); 	
    	if (extraPasses != null && !extraPasses.isEmpty()) {
    		Map<String,String> sphash = new HashMap<String,String>();
	    	for(ExtraPass sp:extraPasses) {
	    		//sphash.put(sp.getId(), JSONUtils.serialize(sp));
				Set<String> isocodes = new HashSet<String>();
				for(Country country:sp.getSurfingCountries()) {
					isocodes.add(country.getIsocode().substring(0,2));
				}
				if(isocodes.size() == 1) {
					sphash.put(isocodes.iterator().next(), JSONUtils.serialize(sp));
				} else {
					if(isocodes.size() == 2 && isocodes.contains("GB")) {
						sphash.put(isocodes.iterator().next(), JSONUtils.serialize(sp));
						continue;
					}
					/*region */
					AreaCode code = countryRepo.findByNationalcode(isocodes.iterator().next());
					if(code != null && code.getGroupid()!= 0) {
						code = countryRepo.findOne(code.getGroupid());
					}
					if(code != null) {
						sphash.put(code.getNationalcode(), JSONUtils.serialize(sp));
					}
				}
                /*Country country = sp.getSurfingCountries().get(0);
                if(StringUtils.isNotBlank(country.getContinentIsoCode())) {
                    sphash.put(country.getContinentIsoCode(), JSONUtils.serialize(sp));
                } else {
                    sphash.put(country.getIsocode(), JSONUtils.serialize(sp));
                }*/
	    	}
	    	jedisclient.hmset(cache.getCacheKey("surfpasshash"), sphash); 
    	}
    }
    @Override
	public void loadData() {
    	//GetAuthTokenAPI(userName,password);
		if(!running) return;
		LOG.info("loadData() scheduling");
    	registerDataCards(); 
    	loadBssUsersAndSurfPasses();
    }

    @Override
	public void registerDataCards() {
		/*Observable.timer(1,TimeUnit.SECONDS).subscribe(new Action1<Long>(){
			@Override
			public void call(Long aLong) {
				if(registerDataCardsPage()==pagesize){
					registerDataCards();
				}
			}
		});*/
		while(true) {
		    if(registerDataCardsPage()<pagesize){
		        break;
            }
        }
    }
    private int registerDataCardsPage() {
    	boolean locked;
		int page=0;
		long id=0;
		int size = 0;
		locked = dlock.lock("dclock",10);
		if(locked) {
			page = (int)counter.incr("dcpage", 1);
			id = counter.incr("dcid", 0);
		}
		dlock.unlock("dclock",locked);
		if(locked) {
			List<DataCard> dcs = datacardRepo.findByIdGreaterThan(id,new PageRequest(page-1,pagesize));
			long lastId = 0;
			if(dcs != null && !dcs.isEmpty()) {
				size = dcs.size();
				lastId = dcs.get(size-1).getId();
				String jobtoken = registerDataCards(dcs);
				if(jobtoken == null) {
					return size;
				}
			}
			locked = dlock.lock("dclock",10);
			if(locked) {
				long pageindex = counter.decr("dcpage", page);
				if(lastId != 0) {
					if(pageindex == 0){
						counter.incr("dcid", lastId-id);
					} else {
						id = counter.incr("dcid", 0);
						if(id<lastId) {
							counter.incr("dcid", lastId-id);
						}
						counter.decr("dcpage", pageindex);
					}			
				}
			}
			dlock.unlock("dclock",locked);	
		}	
		return size;
    }
	@Override
	public String registerDataCards(List<DataCard> datacards) {
		List<DataCard> noregdcs = new ArrayList<DataCard>();
		Set<String> imsis = jedisclient.hkeys(cache.getCacheKey("usersummhash"));
		for(DataCard dc:datacards) {
			if(!dc.isHKDataCard() && !imsis.contains(dc.getImsi())) {
				noregdcs.add(dc);
			}
			//cache.put(dc.getIccid(), dc);
		}
		if(noregdcs.isEmpty()) {
			return "empty";
		}
		List<BssUser> users = new ArrayList<BssUser>();
		for(DataCard dc:noregdcs) {
			BssUser user = new BssUser();
			user.setFirstName(dc.getId().toString());
			user.setLastName(dc.getIccid());
			user.setPassword(MD5Utils.generateValue(user.getFirstName()));
			user.setEmail("dc"+user.getFirstName()+"@roam-tech.com");
			user.setImsi(dc.getImsi());
			users.add(user);
		}
		return RegisterUsersBatchAsyncAPI(users);
	}
	@Override
	public void purchaseSurfPass(List<DataCardTraffic> dcts) {
		final List<String> userids = new ArrayList<String>();
		final List<DataCardTraffic> nopurchasedcts = new ArrayList<DataCardTraffic>();
		Date now = new Date();
		for(DataCardTraffic dct:dcts) {
			if(StringUtils.isBlank(dct.getIccid())) {
				continue;
			}
			DataCard dc = datacardRepo.findByIccid(dct.getIccid()); //cache.get(dct.getIccid(),DataCard.class);
			if(dc.isHKDataCard()|| (dct.getEffectDatetime().before(now) && dct.getFailureDatetime().after(now))) {
				String purchaseId = cache.get(dct.getId(), String.class);
				if (StringUtils.isBlank(purchaseId)) {
					nopurchasedcts.add(dct);
				} else {
					dct.setPurchaseId(purchaseId);
					dctRepo.save(dct);
				}
			}
		}
		LOG.info(JSONUtils.serialize(dcts));
		if(nopurchasedcts.isEmpty()) {
			return;
		}
        LOG.info(JSONUtils.serialize(nopurchasedcts));
		List<PurchaseExtraPassRequest> surfPassReqs = new ArrayList<PurchaseExtraPassRequest>();
		final Map<String,BssUserSummary> userSumms = new HashMap<String,BssUserSummary>();
		for(DataCardTraffic dct:nopurchasedcts) {			
			PurchaseExtraPassRequest pspr = new PurchaseExtraPassRequest();
			AreaCode country = countryRepo.findByAreaname(dct.getAreaname());
            if(country == null) {
                LOG.warn("cann't find country by name "+dct.getAreaname());
                continue;
            }
			DataCard dc = datacardRepo.findByIccid(dct.getIccid()); //cache.get(dct.getIccid(),DataCard.class);
			if(dc.isHKDataCard()||(dc.getType()==DataCard.EUROPEHK_DATACARD&&dc.getImsihk().equals(dct.getImsi()))) {
				addPackHK(dc, dct);
				continue;
			}
			ExtraPass pass = JSON.parseObject(jedisclient.hget(cache.getCacheKey("surfpasshash"),country.getNationalcode()),ExtraPass.class);
            if(pass == null) {
                LOG.warn("cann't find extrapass by country:"+JSONUtils.serialize(country));
                continue;
            }
			pspr.setExtraPassId(Integer.valueOf(pass.getId()));

			BssUserSummary user = JSON.parseObject(jedisclient.hget(cache.getCacheKey("usersummhash"),dc.getImsi()),BssUserSummary.class);
            if(user == null) {
                LOG.warn("cann't find user by imsi:"+dc.getImsi());
                continue;
            }
			userids.add(user.getUserIdentifier());
			userSumms.put(dc.getIccid(), user);
			pspr.setUserIdentifier(user.getUserIdentifier());
			pspr.setExternalTransactionId(dct.getId().toString());//dct.getOrderdetailid().toString());
			pspr.setExpirationDate(sdf.format(dct.getFailureDatetime().getTime()+86400000L));/*考虑时长问题延迟一天关闭*/
			surfPassReqs.add(pspr);			
		}
		if(surfPassReqs.isEmpty()) {
		    return;
        }
		String batchJobToken;
		int i=3;
		do {
			batchJobToken = PurchaseExtraPassesBatchAsyncAPI(surfPassReqs);
			i--;
		}while(StringUtils.isBlank(batchJobToken)&&i>0);
		if(StringUtils.isBlank(batchJobToken)) {
			LOG.error("PurchaseExtraPassesBatchAsyncAPI"+JSONUtils.serialize(surfPassReqs));
			return;
		}
		Map<String,String> params = buildAuthParams();
		params.put("batchJobToken",batchJobToken);
		final String url = "batch/async/GetAsyncBatchJobStatusAPI?"+buildUrlParams(params);
		asyncWaitBatchJobComplete(url, new Action1<BatchJobStatus>() {
			@Override
			public void call(BatchJobStatus batchJobStatus) {
				int i=0;
				while(i<3) {
					List<UserPurchaseData> upds = GetUserPurchasesBatchAPI(userids);
					if(upds == null) {
						i++;
						try {
							Thread.sleep(300000);
						} catch (InterruptedException e) {

						}
						continue;
					}
					Date now = new Date();
					for (UserPurchaseData upd : upds) {
						List<UserPurchase> ups = upd.getEntries();
						for (UserPurchase up : ups) {
							try {
								for (DataCardTraffic dct : nopurchasedcts) {
									BssUserSummary user = userSumms.get(dct.getIccid());
									if (user == null) {
										LOG.info("user is null " + dct.getIccid());
										continue;
									}
									if (user.getUserIdentifier().equals(upd.getUserIdentifier())) {
								/*if(up.getExpirationDate().equals(sdfmm.format(dct.getFailureDatetime())))*/
										if (up.getTransactionId().equals(dct.getId().toString())) {
											dct.setPurchaseId(up.getId().toString());
											dctRepo.save(dct);
											int expire = (int) ((dct.getFailureDatetime().getTime() + 86400000L - now.getTime()) / 1000L);
											cache.put(dct.getId(), up.getId(), expire);
										} else {
								    /**/
										}
									}
								}
							} catch (Exception ex) {
								LOG.warn(ex.getMessage(), ex);
							}
						}
					}
					break;
				}
			}
		});

	}
	private void asyncWaitBatchJobComplete(final String url, final Action1<BatchJobStatus> complete) {
		apiService.GetAsyncBatchJobStatusAPI(url).delay(5,TimeUnit.SECONDS).subscribe(new Action1<BatchJobStatus>() {
			@Override
			public void call(BatchJobStatus status) {
				if(status != null && status.getProcessedEntryCount() == status.getSubmittedEntryCount()) {
					complete.call(status);
				} else {
					asyncWaitBatchJobComplete(url, complete);
				}
			}
		} );
	}
	private String buildUrlParams(Map<String,String> params) {
		return HttpClientManager.buildUrlParams(params, null);
	}
	@Override
	public void removePurchaseSurfPass(List<DataCardTraffic> dcts) {
		//List<String> userids = new ArrayList<String>();
		Map<String,BssUserSummary> userSumms = new HashMap<String,BssUserSummary>();

		for(DataCardTraffic dct:dcts) {
			DataCard dc = datacardRepo.findByIccid(dct.getIccid()); //cache.get(dct.getIccid(),DataCard.class);
			if(dc.isHKDataCard()||(dc.getType()==DataCard.EUROPEHK_DATACARD&&dc.getImsihk().equals(dct.getImsi()))) {
				delPackHK(dc,dct);
				continue;
			}
			BssUserSummary user = JSON.parseObject(jedisclient.hget(cache.getCacheKey("usersummhash"),dc.getImsi()),BssUserSummary.class);
			//userids.add(user.getUserIdentifier());
			userSumms.put(dc.getIccid(), user);
		}
		if(userSumms.isEmpty()) {
			return;
		}
		List<RemoveUserPurchaseRequest> ruprs = new ArrayList<RemoveUserPurchaseRequest>();
		for(Map.Entry<String,BssUserSummary> user:userSumms.entrySet()) {
			RemoveUserPurchaseRequest rupr = new RemoveUserPurchaseRequest();
			rupr.setUserIdentifier(user.getValue().getUserIdentifier());
			List<Long> ids = new ArrayList<Long>();
			for(DataCardTraffic dct:dcts) {
				if(dct.getIccid().equals(user.getKey())) {
					Long id = cache.get(dct.getId(),Long.class);
					if(id != null) {
						ids.add(id);
					}
				}
			}
			if(!ids.isEmpty()) {
				rupr.setIds(ids);
				ruprs.add(rupr);
			}
		}

		if(!ruprs.isEmpty()) {
			String batchJobToken = RemoveUserPurchasesBatchAsyncAPI(ruprs);
		}
	}
	@Override
	public String GetAuthTokenAPI(String userName, String password) {
		Map<String, String> inparams = new HashMap<String,String>();
		inparams.put("userName", userName);
		inparams.put("password", password);
		JSONObject json = JSON.parseObject(sendReceive(bssApiUri+"GetAuthTokenAPI",inparams,null,null));
		if(json == null) {
			return null;
		}
		this.userName = userName;
		authToken = json.getString("authToken");
		//cache.put("authToken", authToken,95*360);
		jedisclient.setex(BSSAPIS_AUTHTOKEN_KEY,95*360,authToken);
		return authToken;
	}

	private Map<String,String> buildAuthParams() {
		Map<String, String> inparams = new HashMap<String,String>();
		inparams.put("userName", userName);
		//if(StringUtils.isBlank(authToken)) {
		//	GetAuthTokenAPI(userName,password);
		//}
		//inparams.put("authToken", authToken);
		authToken = jedisclient.get(BSSAPIS_AUTHTOKEN_KEY); //cache.get("authToken",String.class);
		if(StringUtils.isBlank(authToken)) {
			GetAuthTokenAPI(userName,password);
		}
		inparams.put("authToken", authToken);
		jedisclient.expire(BSSAPIS_AUTHTOKEN_KEY, 95*360);
		//inparams.put("authToken", cache.get("authToken",String.class));
		return inparams;
	}
	
	@Override
	public String PingAPI(String echo) {
		Map<String, String> inparams = buildAuthParams();
		inparams.put("echo", echo);
		return sendReceive(bssApiUri+"PingAPI",inparams,null,null);
	}
	private void processBssResponse(BssResponse bssResp) {
		if(bssResp != null && bssResp.getErrorCode() != null && bssResp.getErrorCode()==102) {
			GetAuthTokenAPI(userName, password);
		}
	}
	@Override
	public String RegisterUsersBatchAsyncAPI(List<BssUser> users) {
		Map<String, String> inparams = buildAuthParams();
		String response = sendReceive(bssApiUri+"batch/async/RegisterUsersBatchAsyncAPI",inparams,JSONUtils.serialize(users),MimeTypes.TEXT_JSON_UTF_8);
		BssResponse bssResp = JSON.parseObject(response,BssResponse.class);
		processBssResponse(bssResp);
		LOG.info(response);
		return bssResp.getBatchJobToken();
	}

	@Override
	public String UpdateUsersBatchAsyncAPI(List<BssUser> users) {
		Map<String, String> inparams = buildAuthParams();
		String response = sendReceive(bssApiUri+"batch/async/UpdateUsersBatchAsyncAPI",inparams,JSONUtils.serialize(users),MimeTypes.TEXT_JSON_UTF_8);
		JSONObject json = JSON.parseObject(response);
		return json.getString("batchJobToken");
	}

	@Override
	public List<BssUserSummary> GetUsersAPI(String imsi, String msisdn, Integer offset,
			Integer limit) {
		Map<String, String> inparams = buildAuthParams();
		if(offset != null) {
			inparams.put("offset", offset+"");	
		}
		if(limit != null) {
			inparams.put("limit", limit+"");
		}
		if(StringUtils.isNotBlank(imsi)) {
			inparams.put("imsi", imsi);
		}
		if(StringUtils.isNotBlank(msisdn)) {
			inparams.put("msisdn", msisdn);
		}
		String response = sendReceive(bssApiUri+"GetUsersAPI",inparams,null,null);
		//System.out.println(response);
		BssResponse bssResp = JSON.parseObject(response,BssResponse.class,new BssParseProcess("users",new TypeReference<List<BssUserSummary>>(){}),new Feature[0]);
		if(bssResp != null) {
			processBssResponse(bssResp);
			LOG.info("GetUsersAPI()"+bssResp.getErrorName()+bssResp.getErrorCode()+bssResp.getSize());
			return (List<BssUserSummary>)bssResp.getResult();
		}
		return null;
	}

	@Override
	public List<BssUser> GetUsersBatchAPI(List<String> identifiers) {
		Map<String, String> inparams = buildAuthParams();
		String response = sendReceive(bssApiUri+"batch/GetUsersBatchAPI",inparams,JSONUtils.serialize(identifiers),MimeTypes.TEXT_JSON_UTF_8);
		//System.out.println(response);
		BssResponse bssResp = JSON.parseObject(response,BssResponse.class,new BssParseProcess("users",new TypeReference<List<BssUser>>(){}),new Feature[0]);
		if(bssResp != null) {
			processBssResponse(bssResp);
			return (List<BssUser>)bssResp.getResult();
		}
		return null;
		//GetUsersBatchResponse usersResp = JSONUtils.deserialize(response,GetUsersBatchResponse.class);
		//return usersResp.getUsers();
	}

	@Override
	public String SuspendUsersBatchAsyncAPI(List<String> identifiers) {
		Map<String, String> inparams = buildAuthParams();
		String response = sendReceive(bssApiUri+"batch/async/SuspendUsersBatchAsyncAPI",inparams,JSONUtils.serialize(identifiers),MimeTypes.TEXT_JSON_UTF_8);
		JSONObject json = JSON.parseObject(response);
		return json.getString("batchJobToken");
	}

	@Override
	public String ResumeUsersBatchAsyncAPI(List<String> identifiers) {
		Map<String, String> inparams = buildAuthParams();
		String response = sendReceive(bssApiUri+"batch/async/ResumeUsersBatchAsyncAPI",inparams,JSONUtils.serialize(identifiers),MimeTypes.TEXT_JSON_UTF_8);
		JSONObject json = JSON.parseObject(response);
		return json.getString("batchJobToken");
	}

	@Override
	public List<UserPurchaseData> GetUserPurchasesBatchAPI(List<String> identifiers) {
		Map<String, String> inparams = buildAuthParams();
		String response = sendReceive(bssApiUri+"batch/GetUserPurchasesBatchAPI",inparams,JSONUtils.serialize(identifiers),MimeTypes.TEXT_JSON_UTF_8);
		LOG.info(response);
		BssResponse bssResp = JSON.parseObject(response,BssResponse.class,new BssParseProcess("userPurchases",new TypeReference<List<UserPurchaseData>>(){}),new Feature[0]);
		if(bssResp != null) {
			return (List<UserPurchaseData>)bssResp.getResult();
		} else {
			LOG.warn("GetUserPurchasesBatchAPI parse error");
		}
		return null;
	}

	@Override
	public List<UserVolumeDataResponse> GetUserVolumeDataBatchAPI(List<UserVolumeDataRequest> identifiers) {
		Map<String, String> inparams = buildAuthParams();
		String response = sendReceive(bssApiUri+"batch/GetUserVolumeDataBatchAPI",inparams,JSONUtils.serialize(identifiers),MimeTypes.TEXT_JSON_UTF_8);
		LOG.debug(response);
		BssResponse bssResp = JSON.parseObject(response,BssResponse.class,new BssParseProcess("userVolumeData",new TypeReference<List<UserVolumeDataResponse>>(){}),new Feature[0]);
		if(bssResp != null) {
			return (List<UserVolumeDataResponse>)bssResp.getResult();
		}
		return null;
	}

	@Override
	public List<UserSessionLogData> GetUserSessionLogsBatchAPI(List<String> identifiers) {
		Map<String, String> inparams = buildAuthParams();
		String response = sendReceive(bssApiUri+"batch/GetUserSessionLogsBatchAPI",inparams,JSONUtils.serialize(identifiers),MimeTypes.TEXT_JSON_UTF_8);
		LOG.debug(response);
		BssResponse bssResp = JSON.parseObject(response,BssResponse.class,new BssParseProcess("userSessionLogs",new TypeReference<List<UserSessionLogData>>(){}),new Feature[0]);
		if(bssResp != null) {
			return (List<UserSessionLogData>)bssResp.getResult();
		}
		return null;
	}

	@Override
	public List<TransactionData> GetTransactionsBatchAPI(Boolean detailedTransactionLog, List<String> identifiers) {
		Map<String, String> inparams = buildAuthParams();
		if(detailedTransactionLog != null) {
			inparams.put("detailedTransactionLog", detailedTransactionLog.toString());
		}
		String response = sendReceive(bssApiUri+"batch/GetTransactionsBatchAPI",inparams,JSONUtils.serialize(identifiers),MimeTypes.TEXT_JSON_UTF_8);
		LOG.debug(response);
		BssResponse bssResp = JSON.parseObject(response,BssResponse.class,new BssParseProcess("transactions",new TypeReference<List<TransactionData>>(){}),new Feature[0]);
		if(bssResp != null) {
			return (List<TransactionData>)bssResp.getResult();
		}
		return null;
	}

	@Override
	public String RemoveUserPurchasesBatchAsyncAPI(List<RemoveUserPurchaseRequest> identifiers) {
		Map<String, String> inparams = buildAuthParams();		
		String response = sendReceive(bssApiUri+"batch/async/RemoveUserPurchasesBatchAsyncAPI",inparams,JSONUtils.serialize(identifiers),MimeTypes.TEXT_JSON_UTF_8);
		LOG.debug(response);
		JSONObject json = JSON.parseObject(response);
		return json.getString("batchJobToken");
	}

	@Override
	public String UpdateUserPurchasesBatchAsyncAPI(List<UpdateUserPurchaseRequest> identifiers) {
		Map<String, String> inparams = buildAuthParams();		
		String response = sendReceive(bssApiUri+"batch/async/UpdateUserPurchasesBatchAsyncAPI",inparams,JSONUtils.serialize(identifiers),MimeTypes.TEXT_JSON_UTF_8);
		LOG.debug(response);
		JSONObject json = JSON.parseObject(response);
		return json.getString("batchJobToken");
	}

	@Override
	public List<ExtraPass> GetExtraPassesAPI(String userIdentifier) {
		Map<String, String> inparams = buildAuthParams();
		inparams.put("userIdentifier", userIdentifier);
		String response = sendReceive(bssApiUri+"GetExtraPassesAPI",inparams,null,null);
		LOG.debug(response);
		BssResponse bssResp = JSON.parseObject(response,BssResponse.class,new BssParseProcess("extraPasses",new TypeReference<List<ExtraPass>>(){}),new Feature[0]);
		if(bssResp != null) {
			return (List<ExtraPass>)bssResp.getResult();
		}
		return null;
	}

	@Override
	public String PurchaseExtraPassesBatchAsyncAPI(List<PurchaseExtraPassRequest> extraPassReqs) {
		Map<String, String> inparams = buildAuthParams();
		String response = sendReceive(bssApiUri+"batch/async/PurchaseExtraPassesBatchAsyncAPI",inparams,JSONUtils.serialize(extraPassReqs),MimeTypes.TEXT_JSON_UTF_8);
		BssResponse bssResp = JSON.parseObject(response,BssResponse.class);
		if(bssResp != null) {
			processBssResponse(bssResp);
			LOG.info(response);
			return bssResp.getBatchJobToken();
		} 
		return null;
	}

	@Override
	public BatchJobStatus GetAsyncBatchJobStatusAPI(String batchJobToken,
			Integer offset, Integer limit, List<String> identifiers) {
		Map<String, String> inparams = buildAuthParams();
		inparams.put("batchJobToken",batchJobToken);
		if(offset != null && limit != null) {
			inparams.put("offset", offset+"");
			inparams.put("limit", limit+"");
		}
		String response = sendReceive(bssApiUri+"batch/async/GetAsyncBatchJobStatusAPI",inparams,identifiers!=null?JSONUtils.serialize(identifiers):null,identifiers!=null?MimeTypes.TEXT_JSON_UTF_8:null);
		return JSONUtils.deserialize(response, BatchJobStatus.class);
	}
	ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
	@Override
	public void afterPropertiesSet() throws Exception {
		if(!running) return;
		cache  = (RDCache)cacheManager.getCache("bssapis");
		jedisclient = (JedisCluster)cache.getNativeCache();
		_client = httpClientManager.getClient(httpClientName);
		if(!_client.isStarted()){
			_client.start();
		}
		counter = cache.getHSCounter();
		dlock = cache.getRDLock();
		//registerDataCards();
    	//loadBssUsersAndSurfPasses();
		Observable<PurchaseDataTrafficEvent> pdteSubject = RxBus.getInstance().register(PurchaseDataTrafficEvent.class);
		pdteSubject.observeOn(Schedulers.io()).subscribe(new Action1<PurchaseDataTrafficEvent>() {
			@Override
			public void call(PurchaseDataTrafficEvent pdte) {
				purchaseSurfPass(pdte.getDataCardTraffics());
			}
		});
		Observable<RemoveDataTrafficEvent> rdteSubject = RxBus.getInstance().register(RemoveDataTrafficEvent.class);
		rdteSubject.observeOn(Schedulers.io()).subscribe(new Action1<RemoveDataTrafficEvent>() {
			@Override
			public void call(RemoveDataTrafficEvent rdte) {
				removePurchaseSurfPass(rdte.getDataCardTraffics());
			}
		});
		/*Observable<RegisterDataCardEvent> rdceSubject = RxBus.getInstance().register(RegisterDataCardEvent.class);
		rdceSubject.subscribeOn(Schedulers.io()).subscribe(new Action1<RegisterDataCardEvent>() {
			@Override
			public void call(RegisterDataCardEvent rdce) {
				registerDataCards(rdce.getDataCards());
			}
		});*/
		HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
				.connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS)
				.writeTimeout(30, TimeUnit.SECONDS)
				.build();
		retrofit = new Retrofit.Builder()
				.baseUrl(bssApiUri)
				.client(okHttpClient)
				.addConverterFactory(FastJsonConverterFactory.create())
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.build();
		apiService = retrofit.create(BssApiService.class);
		hkApiService = new Retrofit.Builder()
				.baseUrl(hkApiUri)
				.client(okHttpClient)
				.addConverterFactory(FastJsonConverterFactory.create())
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.build().create(HKUnicomApiService.class);
		//purchaseEffectTraffics();
		service.scheduleWithFixedDelay(
				new Runnable(){
					@Override
					public void run() {
						try {
							purchaseEffectTraffics();
						} catch (Exception ex) {
							LOG.warn("purchaseEffectTraffics exception",ex);
						}
					}
				}, 100,
				3600, TimeUnit.SECONDS);
	}
	public void waitBatchJobComplete(String batchJobToken) {
		if(StringUtils.isNotBlank(batchJobToken)) {
			while(true) {
				BatchJobStatus jobStatus = GetAsyncBatchJobStatusAPI(batchJobToken,null,null,null);
				System.out.println(JSONUtils.serialize(jobStatus));
				if(jobStatus == null) {
					continue;
				}
				if(jobStatus.getProcessedEntryCount().equals(jobStatus.getSubmittedEntryCount())) {
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private void addPackHK(DataCard dc,final DataCardTraffic dct) {
		HKUnicomAddPackRequest params = new HKUnicomAddPackRequest();
		params.setAuthKey("SFIqXAX6");
		params.setPackCode("540002");
		params.setImsi(dc.getType()==DataCard.EUROPEHK_DATACARD?dc.getImsihk():dc.getImsi());
		Date now = new Date();
		if(dct.getEffectDatetime().before(now)) {
			params.setEffTime(hksdf.format(now.getTime()+600000));
		} else {
			params.setEffTime(hksdf.format(dct.getEffectDatetime()));
		}
		params.setExpTime(hksdf.format(dct.getFailureDatetime().getTime()+86400000L));
		LOG.info("addPackHK Request:"+JSONUtils.serialize(params));
		hkApiService.addpack(params).subscribe(new Action1<HKUnicomResponse>() {
			@Override
			public void call(HKUnicomResponse hkUnicomResponse) {
				LOG.info(JSONUtils.serialize(hkUnicomResponse));
				if(hkUnicomResponse != null && hkUnicomResponse.getAcceptSn()!=null && hkUnicomResponse.isSuccess()) {
					Date now = new Date();
					dct.setPurchaseId(hkUnicomResponse.getAcceptSn());
					dctRepo.save(dct);
					int expire = (int) (dct.getFailureDatetime().getTime() + 86400000L - now.getTime()) / 1000;
					cache.put(dct.getId(), hkUnicomResponse.getAcceptSn(), expire);
				}
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				LOG.info(throwable.getMessage());
			}
		});
	}
	private Boolean delPackHK(DataCard dc,DataCardTraffic dct) {
		HKUnicomDelPackRequest delparam = new HKUnicomDelPackRequest();
		delparam.setAuthKey("SFIqXAX6");
		delparam.setPackCode("540002");
		delparam.setImsi(dc.getType()==DataCard.EUROPEHK_DATACARD?dc.getImsihk():dc.getImsi());
		delparam.setPackOrderSn(cache.get(dct.getId(),String.class));
		LOG.info("delPackHK Request:"+JSONUtils.serialize(delparam));
		HKUnicomResponse resp = hkApiService.delpack(delparam).toBlocking().first();
		if(resp == null) {
			return false;
		}
		if(resp.isSuccess()) {
			LOG.info(JSONUtils.serialize(resp));
		} else {
			LOG.error(JSONUtils.serialize(resp));
		}
		return resp.isSuccess();
	}
	public void testHKApis() {
		/*HKUnicomAddPackRequest params = new HKUnicomAddPackRequest();
		params.setAuthKey("SFIqXAX6");
		params.setPackCode("540002");
		params.setImsi("454070012500007");
		params.setEffTime(hksdf.format(new Date()));
		params.setExpTime(hksdf.format(new Date().getTime()+86400000*5L));
		hkApiService.addpack(params).subscribe(new Action1<HKUnicomResponse>() {
			@Override
			public void call(HKUnicomResponse hkUnicomResponse) {
				LOG.info(JSONUtils.serialize(hkUnicomResponse));
				if(hkUnicomResponse != null && hkUnicomResponse.getAcceptSn()!=null) {
					HKUnicomDelPackRequest delparam = new HKUnicomDelPackRequest();
					delparam.setAuthKey("SFIqXAX6");
					delparam.setPackCode("540002");
					delparam.setImsi("454070012500007");
					delparam.setPackOrderSn(hkUnicomResponse.getAcceptSn());
					HKUnicomResponse resp = hkApiService.delpack(delparam).toBlocking().first();
					LOG.info(JSONUtils.serialize(resp));
				}
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				LOG.info(throwable.getMessage());
			}
		});*/
	}
	/*    public BssApisImpl() {
    	Map<String, String> config = new HashMap<String,String>();
    	config.put("name", httpClientName);
    	config.put("maxThreads", "250");
    	config.put("maxConnections", "500");
    	config.put("timeout", "60000");
    	try {
			_client = createClient(config);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(!_client.isStarted()){
			try {
				_client.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
				.connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS)
				.writeTimeout(30, TimeUnit.SECONDS)
				.build();
		retrofit = new Retrofit.Builder()
				.baseUrl(bssApiUri)
				.client(okHttpClient)
				.addConverterFactory(FastJsonConverterFactory.create())
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.build();
		apiService = retrofit.create(BssApiService.class);
		hkApiService = new Retrofit.Builder()
				.baseUrl(hkApiUri)
				.client(okHttpClient)
				.addConverterFactory(FastJsonConverterFactory.create())
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.build().create(HKUnicomApiService.class);
    }
	private HttpClient createClient(Map<String, String> config)
			throws Exception {
		if (null == config || !config.containsKey("name")) {
			config = new HashMap<String, String>();
			config.put("name", DEF_CLIENT_NAME);
		}
		String name = StringUtils.isEmpty(config.get("name")) ? DEF_CLIENT_NAME
				: config.get("name");
		
		HttpClient client = new HttpClient();
		client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
		
		Integer maxThreads = StringUtils.isEmpty(config.get("maxThreads")) ? 100
				: Integer.valueOf(config.get("maxThreads"));
		if (0 < maxThreads) {
			client.setThreadPool(new QueuedThreadPool(maxThreads));
		} else {
			client.setThreadPool(new QueuedThreadPool());
		}
		
		((QueuedThreadPool) client.getThreadPool()).setName(name);		

		Integer maxConnections = StringUtils.isEmpty(config
				.get("maxConnections")) ? 100 : Integer.valueOf(config
				.get("maxConnections"));
		if (0 < maxConnections) {
			client.setMaxConnectionsPerAddress(maxConnections);
		}

		Long timeout = StringUtils.isEmpty(config.get("timeout")) ? 10000L : Long
				.valueOf(config.get("timeout"));
		if (0L < timeout) {
			client.setTimeout(timeout);
		}

		Long idleTimeout = StringUtils.isEmpty(config.get("idleTimeout")) ? 0L
				: Long.valueOf(config.get("idleTimeout"));
		if (0L < idleTimeout) {
			client.setIdleTimeout(idleTimeout);
		}

		Integer requestHeaderSize = StringUtils.isEmpty(config
				.get("requestHeaderSize")) ? 0 : Integer.valueOf(config
				.get("requestHeaderSize"));
		if (0 < requestHeaderSize) {
			client.setRequestHeaderSize(requestHeaderSize);
		}

		Integer responseHeaderSize = StringUtils.isEmpty(config
				.get("responseHeaderSize")) ? 0 : Integer.valueOf(config
				.get("responseHeaderSize"));
		if (0 < responseHeaderSize) {
			client.setResponseHeaderSize(responseHeaderSize);
		}

		Integer requestBufferSize = StringUtils.isEmpty(config
				.get("requestBufferSize")) ? 0 : Integer.valueOf(config
				.get("requestBufferSize"));
		if (0 < requestBufferSize) {
			client.setRequestBufferSize(requestBufferSize);
		}

		Integer responseBufferSize = StringUtils.isEmpty(config
				.get("responseBufferSize")) ? 0 : Integer.valueOf(config
				.get("responseBufferSize"));
		if (0 < responseBufferSize) {
			client.setResponseBufferSize(responseBufferSize);
		}
		LOG.debug("HttpClient [" + name + "] has been created.");
		return client;
	}

	public String sendReceive(HttpClient client, String serviceUri,
			Map<String, String> params,String body, String contentType) {
		ContentExchange exchange = new ContentExchange();
		URI uri = URI.create(serviceUri);
		String query = uri.getQuery();
		if (null != params) {
			for (String key : params.keySet()) {
				String value = params.get(key);
				if (StringUtils.isEmpty(query)) {
					query = key.concat("=").concat(value);
				} else {
					query = query.concat("&").concat(key).concat("=")
							.concat(value);
				}
			}
		}
		try {
			URI realUri = new URI(uri.getScheme(), uri.getUserInfo(),
					uri.getHost(), uri.getPort(), uri.getPath(), query,
					uri.getFragment());
			exchange.setURI(realUri);
			System.out.println(new Date()+realUri.toASCIIString());
			exchange.setTimeout(30000);
			if(StringUtils.isNotBlank(body)) {
				exchange.setRequestHeader("Content-Type",contentType);
				exchange.setMethod(HttpMethods.POST);
				byte[] sbody = body.getBytes();
				ByteArrayBuffer buffer = new ByteArrayBuffer(sbody.length);
				buffer.put(sbody);
				exchange.setRequestContent(buffer);
				System.out.println("POST body:"+body);
			}
			client.send(exchange);

			// Waits until the exchange is terminated
			int exchangeState = exchange.waitForDone();
			if (exchangeState == HttpExchange.STATUS_COMPLETED) {
				if (exchange.getResponseStatus() != 200) {
					System.out.println("Http response status="+exchange.getResponseStatus());
					return null;
				}
				String response = exchange.getResponseContent();
				return response;
			} else if (exchangeState == HttpExchange.STATUS_EXCEPTED) {
				System.out.println("HttpExchange status="+exchangeState);
				return null;
			} else if (exchangeState == HttpExchange.STATUS_EXPIRED) {
				System.out.println(new Date()+"HttpExchange status="+exchangeState);
				return null;
			}
			System.out.println("HttpExchange status="+exchangeState);
			return null;
		} catch (Exception e) {
			System.out.println("exception"+e.getMessage());
			return null;
		}
	}	

	public static void main(String[] args) {
		final BssApisImpl bssApis = new BssApisImpl();
        *//*HKUnicomDelPackRequest delparam = new HKUnicomDelPackRequest();
        delparam.setAuthKey("SFIqXAX6");
        delparam.setPackCode("540002");
        delparam.setImsi("454070012500007");
        delparam.setPackOrderSn("11000004597636");
        HKUnicomResponse resp = bssApis.hkApiService.delpack(delparam).toBlocking().first();
        System.out.println(JSONUtils.serialize(resp));*//*
		final HKUnicomAddPackRequest params = new HKUnicomAddPackRequest();
		params.setAuthKey("SFIqXAX6");
		params.setPackCode("540002");
		params.setImsi("454070012500007");
		params.setEffTime(bssApis.hksdf.format(new Date().getTime()+86400000*1L));
		params.setExpTime(bssApis.hksdf.format(new Date().getTime()+86400000*2L));
		bssApis.hkApiService.addpack(params).subscribe(new Action1<HKUnicomResponse>() {
			@Override
			public void call(HKUnicomResponse hkUnicomResponse) {
				System.out.println(JSONUtils.serialize(hkUnicomResponse));
				if(hkUnicomResponse != null && hkUnicomResponse.getAcceptSn()!=null) {
					HKUnicomDelPackRequest delparam = new HKUnicomDelPackRequest();
					delparam.setAuthKey("SFIqXAX6");
					delparam.setPackCode("540002");
					delparam.setImsi("454070012500007");
                    delparam.setExpTime(params.getExpTime());
					delparam.setPackOrderSn(hkUnicomResponse.getAcceptSn());
					HKUnicomResponse resp = bssApis.hkApiService.delpack(delparam).toBlocking().first();
					System.out.println(JSONUtils.serialize(resp));
				}
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				System.out.println(throwable.getMessage());
			}
		});
		//String response = "{\"restrictedOperation\":\"GET_SURF_PASSES_API\",\"surfPasses\":[{\"accountAddition\":false,\"countries\":[{\"continentIsoCode\":\"EU\",\"enabled\":true,\"id\":39,\"isocode\":\"AT\",\"mccmnc\":\"23205\",\"name\":\"AUSTRIA\",\"pmn\":\"AUTHU\",\"providerTitle\":\"Orange\"},{\"continentIsoCode\":\"EU\",\"enabled\":true,\"id\":16,\"isocode\":\"AT\",\"mccmnc\":\"23210\",\"name\":\"AUSTRIA\",\"pmn\":\"AUTHU\",\"providerTitle\":\"3\"},{\"continentIsoCode\":\"EU\",\"enabled\":true,\"id\":25,\"isocode\":\"CH\",\"mccmnc\":\"22803\",\"name\":\"SWITZERLAND\",\"pmn\":\"CHEOR\",\"providerTitle\":\"Orange\"},{\"continentIsoCode\":\"EU\",\"enabled\":false,\"id\":19,\"isocode\":\"DE\",\"mccmnc\":\"26203\",\"name\":\"GERMANY\",\"pmn\":\"DEUE1\",\"providerTitle\":\"E-Plus \"},{\"continentIsoCode\":\"EU\",\"enabled\":true,\"id\":5,\"isocode\":\"DK\",\"mccmnc\":\"23806\",\"name\":\"DENMARK\",\"pmn\":\"DNKHU\",\"providerTitle\":\"Hi3G\"},{\"continentIsoCode\":\"EU\",\"enabled\":true,\"id\":26,\"isocode\":\"ES\",\"mccmnc\":\"21403\",\"name\":\"SPAIN\",\"pmn\":\"ESPRT\",\"providerTitle\":\"Orange\"},{\"continentIsoCode\":\"EU\",\"enabled\":true,\"id\":30,\"isocode\":\"FR\",\"mccmnc\":\"20820\",\"name\":\"FRANCE\",\"pmn\":\"FRAF3\",\"providerTitle\":\"Bouygues Telecom\"},{\"continentIsoCode\":\"EU\",\"enabled\":true,\"id\":31,\"isocode\":\"FR\",\"mccmnc\":\"20821\",\"name\":\"FRANCE\",\"pmn\":\"FRAF3\",\"providerTitle\":\"Bouygues Telecom\"},{\"continentIsoCode\":\"EU\",\"enabled\":true,\"id\":13,\"isocode\":\"GB\",\"mccmnc\":\"23420\",\"name\":\"UNITED KINGDOM\",\"pmn\":\"GBRHU\",\"providerTitle\":\"3\"},{\"continentIsoCode\":\"EU\",\"enabled\":true,\"id\":23,\"isocode\":\"IE\",\"mccmnc\":\"27205\",\"name\":\"IRELAND\",\"pmn\":\"IRLH3\",\"providerTitle\":\"H3G\"},{\"continentIsoCode\":\"EU\",\"enabled\":false,\"id\":10,\"isocode\":\"IT\",\"mccmnc\":\"22299\",\"name\":\"ITALY\",\"pmn\":\"ITAH3\",\"providerTitle\":\"Hi3G\"},{\"continentIsoCode\":\"EU\",\"enabled\":true,\"id\":78,\"isocode\":\"LT\",\"mccmnc\":\"24603\",\"name\":\"LITHUANIA\",\"pmn\":\"LTU03\",\"providerTitle\":\"Tele2\"},{\"continentIsoCode\":\"EU\",\"enabled\":true,\"id\":18,\"isocode\":\"NL\",\"mccmnc\":\"20408\",\"name\":\"NETHERLANDS\",\"pmn\":\"NLDPT\",\"providerTitle\":\"KPN\"},{\"continentIsoCode\":\"EU\",\"enabled\":false,\"id\":17,\"isocode\":\"PT\",\"mccmnc\":\"26803\",\"name\":\"PORTUGAL\",\"pmn\":\"PRTOP\",\"providerTitle\":\"Optimus\"},{\"continentIsoCode\":\"EU\",\"enabled\":true,\"id\":4,\"isocode\":\"SE\",\"mccmnc\":\"24002\",\"name\":\"SWEDEN\",\"pmn\":\"SWEHU\",\"providerTitle\":\"Sweden provider\"},{\"continentIsoCode\":\"\",\"enabled\":true,\"id\":958,\"isocode\":\"UND\",\"mccmnc\":\"11111\",\"name\":\"UNDEFINED NETWORK\",\"pmn\":\"UND01\",\"providerTitle\":\"Undefined Network\"},{\"enabled\":true,\"id\":54,\"isocode\":\"USA\",\"mccmnc\":\"310410\",\"name\":\"USA\",\"pmn\":\"USACG\",\"providerTitle\":\"AT&T\"}],\"currency\":\"EUR\",\"customExpirationDateEnabled\":true,\"customVolumeEnabled\":true,\"description\":\"Add 100 Mb 10 Days\",\"externalPurchase\":true,\"id\":121,\"minutes\":0,\"price\":0.1,\"priority\":3,\"resetCounterEnabled\":false,\"surfingCountries\":[{\"continentIsoCode\":\"\",\"enabled\":false,\"id\":890,\"isocode\":\"GB\",\"mccmnc\":\"23494\",\"name\":\"UNITED KINGDOM\",\"pmn\":\"GBRH1\",\"providerTitle\":\"3UK\"},{\"continentIsoCode\":\"EU\",\"enabled\":true,\"id\":13,\"isocode\":\"GB\",\"mccmnc\":\"23420\",\"name\":\"UNITED KINGDOM\",\"pmn\":\"GBRHU\",\"providerTitle\":\"3\"}],\"validityTerm\":10,\"volume\":104857600}]}";
		//BssResponse bssResp = JSON.parseObject(response,BssResponse.class,new BssParseProcess("surfPasses",new TypeReference<List<SurfPass>>(){}),new Feature[0]);
		//System.out.println(JSONUtils.serialize(bssResp));
*//*		String response = bssApis.GetAuthTokenAPI("dashigroup", "9eded806eaafd811501a8dcabcceb17a");
		System.out.println(response);
//		System.out.println(bssApis.PingAPI("ping"));
//		List<BssUser> users = new ArrayList<BssUser>();
//		BssUser user = new BssUser();
//		user.setFirstName("10640093");
//		user.setLastName("894420001278900111");
//		user.setPassword(MD5Utils.generateValue(user.getFirstName()));
//		user.setEmail("dc"+user.getFirstName()+"@roam-tech.com");
//		user.setImsi("234206210000111");
//		//user.setUserIdentifier("SsPzQ/okjY4=");
//		users.add(user);
//		user = new BssUser();
//		user.setFirstName("10640095");
//		user.setLastName("894420001278900121");
//		user.setPassword(MD5Utils.generateValue(user.getFirstName()));
//		user.setEmail("dc"+user.getFirstName()+"@roam-tech.com");
//		user.setImsi("234206210000121");
//		users.add(user);
//		String batchJobToken = bssApis.RegisterUsersBatchAsyncAPI(users);
//		bssApis.waitBatchJobComplete(batchJobToken);
		//String batchJobToken = bssApis.UpdateUsersBatchAsyncAPI(users);
		//System.out.println(batchJobToken);
		List<ExtraPass> surfPasses = bssApis.GetExtraPassesAPI("SsPzQ/okjY4=");
		System.out.println(JSONUtils.serialize(surfPasses));
		List<BssUserSummary> usersSummary = bssApis.GetUsersAPI(null, null, null, 50);
		System.out.println(JSONUtils.serialize(usersSummary));
		List<String> userIdentifiers = new ArrayList<String>();
		List<UserVolumeDataRequest> userVolumeDataReqs = new ArrayList<UserVolumeDataRequest>();
		int i=0;
		for(BssUserSummary userSummary:usersSummary) {
			userIdentifiers.add(userSummary.getUserIdentifier());
			//List<SurfPass> surfPasses = bssApis.GetSurfPassesAPI(userSummary.getUserIdentifier());
			//System.out.println(JSONUtils.serialize(surfPasses));
			
			i++;
			if(i%2==0) {
				userVolumeDataReqs.add(new UserVolumeDataRequest(userSummary.getUserIdentifier()));
			} else {
				userVolumeDataReqs.add(new UserVolumeDataRequest(userSummary.getUserIdentifier(),true));
			}
		}
		List<UserVolumeDataResponse> volumeDataResps = bssApis.GetUserVolumeDataBatchAPI(userVolumeDataReqs);
		System.out.println(JSONUtils.serialize(volumeDataResps));
		List<UserSessionLogData> sessionLogs = bssApis.GetUserSessionLogsBatchAPI(userIdentifiers);
		System.out.println(JSONUtils.serialize(sessionLogs));
		List<TransactionData> transactions = bssApis.GetTransactionsBatchAPI(false,userIdentifiers);
		System.out.println(JSONUtils.serialize(transactions));
		List<PurchaseExtraPassRequest> surfPassReqs = new ArrayList<PurchaseExtraPassRequest>();
		PurchaseExtraPassRequest pspr = new PurchaseExtraPassRequest();
		pspr.setExtraPassId(1);
		pspr.setUserIdentifier("SsPzQ/okjY4=");
		pspr.setExternalTransactionId("123");
		pspr.setExpirationDate("2016-08-20 23:59:59");
		surfPassReqs.add(pspr);
		String batchJobToken = bssApis.PurchaseExtraPassesBatchAsyncAPI(surfPassReqs);
		*//**//*//**//**//**//*String batchJobToken = bssApis.SuspendUsersBatchAsyncAPI(userIdentifiers);
		//bssApis.waitBatchJobComplete(batchJobToken);
		Map<String,String> params = bssApis.buildAuthParams();
		params.put("batchJobToken",batchJobToken);
		final String url = "batch/async/GetAsyncBatchJobStatusAPI?"+bssApis.buildUrlParams(params);
		bssApis.asyncWaitBatchJobComplete(url, new Action1<BatchJobStatus>() {
			@Override
			public void call(BatchJobStatus batchJobStatus) {
				List<String> userids = new ArrayList<String>();
				userids.add("SsPzQ/okjY4=");
				List<UserPurchaseData> upds = bssApis.GetUserPurchasesBatchAPI(userids);
				System.out.println(JSONUtils.serialize(upds));
				List<UpdateUserPurchaseRequest> uuprs = new ArrayList<UpdateUserPurchaseRequest>();
				UpdateUserPurchaseRequest uupr = new UpdateUserPurchaseRequest();
				uupr.setUserIdentifier("SsPzQ/okjY4=");
				uupr.setUserPurchaseId(upds.get(0).getEntries().get(0).getId().toString());
				uupr.setRecurring(false);
				uuprs.add(uupr);
				String batchJobToken = bssApis.UpdateUserPurchasesBatchAsyncAPI(uuprs);
				//batchJobToken = bssApis.ResumeUsersBatchAsyncAPI(userIdentifiers);
				bssApis.waitBatchJobComplete(batchJobToken);
				List<RemoveUserPurchaseRequest> ruprs = new ArrayList<RemoveUserPurchaseRequest>();
				RemoveUserPurchaseRequest rupr = new RemoveUserPurchaseRequest();
				rupr.setUserIdentifier("SsPzQ/okjY4=");
				List<Long> ids = new ArrayList<Long>();
				ids.add(upds.get(0).getEntries().get(0).getId());
				rupr.setIds(ids);
				ruprs.add(rupr);
				batchJobToken = bssApis.RemoveUserPurchasesBatchAsyncAPI(ruprs);
				bssApis.waitBatchJobComplete(batchJobToken);
			}
		});
		System.out.println("******end******");
		List<String> usersids = new ArrayList<String>();
		usersids.add("SsPzQ/okjY4=");
		List<UserPurchaseData> upds = bssApis.GetUserPurchasesBatchAPI(usersids);
		System.out.println(JSONUtils.serialize(upds));
		List<UpdateUserPurchaseRequest> uuprs = new ArrayList<UpdateUserPurchaseRequest>();
		UpdateUserPurchaseRequest uupr = new UpdateUserPurchaseRequest();
		uupr.setUserIdentifier("SsPzQ/okjY4=");
		uupr.setUserPurchaseId(upds.get(0).getEntries().get(0).getId().toString());
		uupr.setRecurring(false);
		uuprs.add(uupr);
		batchJobToken = bssApis.UpdateUserPurchasesBatchAsyncAPI(uuprs);
		//batchJobToken = bssApis.ResumeUsersBatchAsyncAPI(userIdentifiers);
		bssApis.waitBatchJobComplete(batchJobToken);
		List<RemoveUserPurchaseRequest> ruprs = new ArrayList<RemoveUserPurchaseRequest>();
		RemoveUserPurchaseRequest rupr = new RemoveUserPurchaseRequest();
		rupr.setUserIdentifier("SsPzQ/okjY4=");
		List<Long> ids = new ArrayList<Long>();
		ids.add(upds.get(0).getEntries().get(0).getId());
		rupr.setIds(ids);
		ruprs.add(rupr);
		batchJobToken = bssApis.RemoveUserPurchasesBatchAsyncAPI(ruprs);
		bssApis.waitBatchJobComplete(batchJobToken);
		upds = bssApis.GetUserPurchasesBatchAPI(usersids);
		System.out.println(JSONUtils.serialize(upds));
		
		List<BssUser> usersA = bssApis.GetUsersBatchAPI(userIdentifiers);
		System.out.println(JSONUtils.serialize(usersA));*//*
	}*/
    public String getBssApiUri() {
		return bssApiUri;
	}
	public void setBssApiUri(String bssApiUri) {
		this.bssApiUri = bssApiUri;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public String getHkApiUri() {
		return hkApiUri;
	}

	public void setHkApiUri(String hkApiUri) {
		this.hkApiUri = hkApiUri;
	}

	public Boolean getRunning() {
		return running;
	}

	public void setRunning(Boolean running) {
		this.running = running;
	}

	private void purchaseEffectTraffics() {
		LOG.info("purchaseEffectTraffics() scheduling");
		registerDataCards();
		loadBssUsersAndSurfPasses();
		boolean locked = dlock.lock("userlock",10);
		if(locked) {
			Long updatetime = jedisclient.incr(cache.getCacheKey("updatetime"));
			Date now = new Date();
			if(now.getTime()-updatetime < 3600-30) {
				dlock.unlock("userlock",locked);
				return;
			}
			jedisclient.set(cache.getCacheKey("updatetime"), now.getTime()+"");
		}
		dlock.unlock("userlock",locked);
		List<DataCardTraffic> dcts = dctRepo.findByEffectDatetimeLessThanAndFailureDatetimeGreaterThanAndStatusAndPurchaseIdIsNull(new Date(),new Date(), Order.ORDER_STATUS_CONFIRMED);
		//Map<String,String> activeusers = new HashMap<String, String>();//jedisclient.hgetAll(cache.getCacheKey("activeusers"));
				/*List<String> userIdentities = new ArrayList<String>();
				for(DataCardTraffic dct:dcts) {
					String useridentity = jedisclient.hget(cache.getCacheKey("activeusers"),dct.getIccid());
					if(StringUtils.isBlank(useridentity)) {
						*//*need resume user*//*
						userIdentities.add(useridentity);
					}
					activeusers.put(dct.getIccid(),useridentity);
				}
				String batchJobToken = ResumeUsersBatchAsyncAPI(userIdentities);
				Set<String> iccids = jedisclient.hkeys(cache.getCacheKey("activeusers"));
				iccids.removeAll(activeusers.keySet());
				List<String> suspendUsers = jedisclient.hmget(cache.getCacheKey("activeusers"),(String[])iccids.toArray());
				batchJobToken = SuspendUsersBatchAsyncAPI(suspendUsers);
				jedisclient.hdel(cache.getCacheKey("activeusers"),(String[])iccids.toArray());*/
		purchaseSurfPass(dcts);
	}
	/*private void purchaseEffectTraffics() {
		Observable.interval(100,3600,TimeUnit.SECONDS).subscribe(new Observer<Long>() {

			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable throwable) {

			}

			@Override
			public void onNext(Long number) {
				*//*查找所有到开始日期的流量套餐进行激活处理*//*


			}
		});
	}*/
}
class BssParseProcess<T> implements ExtraProcessor, ExtraTypeProvider {
	private String resultName;
	private TypeReference<T> type;
	public BssParseProcess(String resultName, TypeReference<T> type) {
		this.resultName = resultName;
		this.type = type;
	}
	@Override
	public void processExtra(Object object, String key, Object value) {
		if(key.equals(resultName)) {
			((BssResponse)(object)).setResult((List<?>)value);
		}/* else {
			try {
				Field field = object.getClass().getField(key);
				field.set(object, value);
			} catch (NoSuchFieldException e) {
				
			} catch (SecurityException e) {
				
			} catch (IllegalArgumentException e) {
				
			} catch (IllegalAccessException e) {
				
			}
		}*/
	}
	@Override
	public Type getExtraType(Object object, String key) {
		if(key.equals(resultName)) {
			return type.getType();
		}
		return null;
	}
	
}

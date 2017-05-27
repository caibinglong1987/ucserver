package com.roamtech.uc.handler.om;

import com.roamtech.uc.cache.RDCache;
import com.roamtech.uc.cache.RDCounter;
import com.roamtech.uc.cache.handler.SessionHandler;
import com.roamtech.uc.cache.model.Session;
import com.roamtech.uc.handler.SNGenerator;
import com.roamtech.uc.handler.UserHandlerUtil;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.model.*;
import com.roamtech.uc.repository.*;
import com.roamtech.uc.util.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component("omService")
public class OMServiceImpl implements OMService, InitializingBean {
	private RDCache cache;
	private RDCounter counter;
	@Autowired
	@Qualifier("ucCacheManager")
	private CacheManager cacheManager;

	@Autowired
	PhoneRepository phoneRepo;	
	@Autowired
	TouchRepository touchRepo;
	@Autowired
	TouchChansRepository touchchansRepo;
	@Autowired
	DataCardRepository datacardRepo;
	@Autowired
	PrdUnitPriceRepository prdUnitPriceRepo;
	@Autowired
	PrdDiscountRepository prdDiscountRepo;
	@Autowired
	EVoucherRepository evoucherRepo;
	@Autowired
	PrdEVoucherRepository prdEVoucherRepo;
	@Autowired
	UserEVoucherRepository userEVoucherRepo;
	@Autowired
	EVoucherVerifyRepository evoucherVerifyRepo;
	@Autowired
	UserRepository userRepo;
	@Autowired
	UserThirdPartyRepository utpRepo;
	@Autowired
	AdminRoleRepository arRepo;
	
	private Integer nodeid;
	private Integer nodenum;
	private SNGenerator snGen;
	@Autowired
	SessionHandler sessionHandler;
	@Override
	public int checkSessionValid(String sessionId) {
		if(StringUtils.isBlank(sessionId)) {
			return -1;
		}
		
		Session session = sessionHandler.findOne(sessionId);
		if(session == null) {
			return -1;
		}
		return session.getUsertype();
	}
	@Override
	public Touch setTouch(Touch touch) {
		if(touch.getId()==null) {
			Touch origTouch=touchRepo.findByDevid(touch.getDevid());
			if(origTouch != null) {
				touch.setVerified(origTouch.getVerified());
				touch.setId(origTouch.getId());
			}
		}
		return touchRepo.save(touch);
	}
	@Override
	public List<TouchChans> setTouchChans(List<TouchChans> chans) {		
		return CollectionUtils.toList(touchchansRepo.save(chans));
	}
	@Override
	public DataCard setDataCard(DataCard card) {
		if(card.getId() == 0L) {
			DataCard ocard = datacardRepo.findByIccid(card.getIccid());
			if(ocard != null) {
				boolean bcommit = false;
				if(StringUtils.isNotBlank(card.getImsi())&& StringUtils.isBlank(ocard.getImsi())) {
					ocard.setImsi(card.getImsi());
					bcommit = true;
				}
				if(StringUtils.isNotBlank(card.getKi())&& StringUtils.isBlank(ocard.getKi())) {
					ocard.setKi(card.getKi());
					bcommit = true;
				}
				if(card.getUserId() != 0L && ocard.getUserId() == 0L) {
					ocard.setUserId(card.getUserId());
					bcommit = true;
				}
				if(!bcommit) {
					return ocard;
				}
				card = ocard;
			} else {
				card.setId(Long.valueOf(snGen.generateSEQ()));
				card.setCreatetime(new Date());
			}
		} else {
			DataCard ocard = datacardRepo.findOne(card.getId());
			if(ocard == null) {
				card.setCreatetime(new Date());
			}
		}
		
		return datacardRepo.save(card);
	}
	@Override
	public PrdUnitPrice setPrdUnitPrice(PrdUnitPrice prdUnitPrice) {
		return prdUnitPriceRepo.save(prdUnitPrice);
	}
	@Override
	public PrdDiscount setPrdDiscount(PrdDiscount prdDiscount) {
		return prdDiscountRepo.save(prdDiscount);
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
	@Override
	public void afterPropertiesSet() throws Exception {
		Date now = new Date();
		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");

		String startTime = format.format(now.getTime());
		format.parse(startTime);
		//String endTime = format.format(now.getTime()+86400000L);
		//System.out.println(format.format(now)+" "+startTime+" "+endTime);
		Long seq = datacardRepo.countByCreatetimeGreaterThan(format.parse(startTime));//, endTime);
		
		int calseq = ((seq.intValue()+nodenum-nodeid)/nodenum)*nodenum;
		System.out.println(seq+" "+calseq);
		snGen = new SNGenerator(nodeid, nodenum, 8, calseq);
		cache  = (RDCache)cacheManager.getCache("oms");
		counter = cache.getHSCounter();
	}
	@Override
	public UserEVoucher verifyEVoucher(Long evoucherSn) {
		UserEVoucher uev = userEVoucherRepo.findBySn(evoucherSn);

		return uev;
	}
	@Override
	public EVoucherVerify saveEVoucherVerify(Long actionUserid, Long evoucherSn, UserEVoucher uev, Integer status) {
		EVoucherVerify evv = new EVoucherVerify();
		evv.setSn(evoucherSn);
		evv.setActionUserid(actionUserid);
		
		if(uev == null) {
			evv.setStatus(EVoucherVerify.EVOUCHER_NOEXIST);
		} else {
			evv.setUserid(uev.getUserid());
			evv.setOrderid(uev.getOrderid());
			evv.setEvoucherid(uev.getId());
			evv.setEvoucherName(uev.getName());
			if(status == ErrorCode.ERR_EVOUCHER_USED) {
				evv.setStatus(EVoucherVerify.EVOUCHER_USED);
			} else if(status == ErrorCode.ERR_EVOUCHER_INVALID){
				evv.setStatus(EVoucherVerify.EVOUCHER_INVALID);
			} else {
				evv.setStatus(EVoucherVerify.EVOUCHER_VALID);
			}
		}		
		return evoucherVerifyRepo.save(evv);
	}
	@Override
	public void deliveryDataCard(Long actionUserid,Long evoucherSn) {
		UserEVoucher uev = userEVoucherRepo.findBySn(evoucherSn);
		uev.setUsedDatetime(new Date());
		saveEVoucherVerify(actionUserid,evoucherSn,uev, ErrorCode.ERR_EVOUCHER_USED);
		userEVoucherRepo.save(uev);
	}
	@Override
	public List<User> createAccounts(Long companyid, Long roleid, Integer quantity, String username, String password) {
		List<User> users = new ArrayList<User>();
		Touch touch = null;
		if(roleid == 3 && quantity == 1 && StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
			touch = touchRepo.findByPhone(username);
			if(touch == null) {
				return users;
			}
		} 
		for(int i=0;i<quantity;i++) {
			if(StringUtils.isNotBlank(username)) {
				User user = userRepo.findByUserNameAndTenantIdAndPhoneType(username, User.ROAM_TENANT_ID, Phone.TYPE_PHONE);
				if(user != null) {
					users.add(user);
					return users;
				}
			}
			User user = new User();
			user.setUserName(StringUtils.isNotBlank(username)?username:snGen.generateSN((int)counter.incr("accountid", 1)));
			user.setPassword(StringUtils.isNotBlank(password)?password:"666666");
			if(StringUtils.isNotBlank(username)) {
				user.setPhone(username);
			}
			user.setStatus(User.STATUS_ACTIVE);
			if(roleid == 1) {
				user.setType(User.CLERK_USER);
			} else if(roleid == 2) {
				user.setType(User.ADMIN_USER);
			} else if(roleid == 3) {
				user.setType(User.NORMAL_USER);
			}
			users.add(user);
		}

		users = CollectionUtils.toList(userRepo.save(users));
		List<UserThirdParty> utps = new ArrayList<UserThirdParty>();
		List<AdminRole> ars = new ArrayList<AdminRole>();
		for(User user:users) {
			UserThirdParty utp = new UserThirdParty();
			utp.setUserId(user.getUserId());
			utp.setCompanyid(companyid);
			utp.setOpenid(user.getUserId().toString());
			utps.add(utp);
			AdminRole ar = new AdminRole();
			ar.setRoleId(roleid);
			ar.setUserid(user.getUserId());
			ars.add(ar);
			if(touch != null) {
				touch.setUserId(user.getUserId());
				touchRepo.save(touch);
				Phone phoneObj = new Phone();
				phoneObj.setPhone(touch.getPhone());
				phoneObj.setUserId(user.getUserId());
				phoneObj.setVerified(false);
				phoneRepo.save(phoneObj);
			}
		}
		utpRepo.save(utps);
		arRepo.save(ars);
		return users;
	}
	@Override
	public User deleteAccount(String username) {
		User user = userRepo.findByUserNameAndTenantIdAndPhoneType(username, User.ROAM_TENANT_ID, Phone.TYPE_PHONE);
		if(user == null) {
			return null;
		}
		List<UserThirdParty> utps = utpRepo.findByUserId(user.getUserId());
		if(utps != null && !utps.isEmpty()) {
			utpRepo.delete(utps);
		}		
		arRepo.delete(arRepo.findByUserid(user.getUserId()));
		phoneRepo.delete(phoneRepo.findByUserId(user.getUserId()));
		List<Touch> touchs = touchRepo.findByUserId(user.getUserId());
		if(!touchs.isEmpty()) {
			for(Touch touch:touchs) {
				touch.setUserId(0L);
			}
			touchRepo.save(touchs);
		}
		List<DataCard> dcs = datacardRepo.findByUserId(user.getUserId());
		if(!dcs.isEmpty()) {
			for(DataCard dc:dcs) {
				dc.setUserId(0L);
			}
			datacardRepo.save(dcs);
		}
		userRepo.delete(user);
		return user;
	}
	@Override
	public List<Touch> getTouchs(String devid, String phone, Integer devtype, Integer pageindex, Integer pagesize) {
		List<Touch> touchs = new ArrayList<Touch>();
		if(StringUtils.isNotBlank(devid)) {
			Touch touch = touchRepo.findByDevid(devid);
			if(touch != null) {
				touchs.add(touch);
				return touchs;
			}
		}
		if(StringUtils.isNotBlank(phone)) {
			Touch touch = touchRepo.findByPhone(phone);
			if(touch != null) {
				touchs.add(touch);
				return touchs;
			}
		}
		if(devtype != 0) {
			touchs = touchRepo.findByDevtype(devtype, new PageRequest(pageindex,pagesize));
		}
		return touchs;
	}

	@Override
	public EVoucher createEVoucher(EVoucher evoucher, List<Long> prdids) {
		EVoucher eVoucher = evoucherRepo.save(evoucher);
		if(prdids != null && !prdids.isEmpty()) {
			List<PrdEVoucher> prdevs = new ArrayList<PrdEVoucher>();
			for(Long prdid:prdids) {
				PrdEVoucher prdev = new PrdEVoucher();
				prdev.setEvoucherid(eVoucher.getId());
				prdev.setProductid(prdid);
				prdevs.add(prdev);
			}
			eVoucher.setPrdEVouchers(CollectionUtils.toList(prdEVoucherRepo.save(prdevs)));
		}
		return eVoucher;
	}

	@Override
	public List<String> generateEVouchers(Long evoucherId, Integer quantity) {
		EVoucher voucher = evoucherRepo.findOne(evoucherId);
		Date now = new Date();
		List<UserEVoucher> uevs = new ArrayList<UserEVoucher>();
		List<String> sns = new ArrayList<String>();
		for(int i=0;i<quantity;i++) {
			UserEVoucher uev = generateUserEVoucher(voucher, now);
			uevs.add(uev);
			sns.add(uev.getSn().toString());
		}
		userEVoucherRepo.save(uevs);
		return sns;
	}

	@Override
	public void updateUserCreateTime(Long userid) {
		UserHandlerUtil.upgradeUsers(userid);
	}

	private UserEVoucher generateUserEVoucher(EVoucher voucher,Date now) {
		UserEVoucher uev = UserEVoucher.createUserEVoucher(voucher);
		uev.setImage(voucher.getBackground());
		uev.setSn(Long.parseLong(snGen.generateSN((int)counter.incr("uevid", 1))));
		uev.setCreateTime(now);
		return uev;
	}
}

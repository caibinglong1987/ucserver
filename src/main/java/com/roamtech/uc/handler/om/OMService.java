package com.roamtech.uc.handler.om;

import java.util.List;

import com.roamtech.uc.model.*;

public interface OMService {
	int checkSessionValid(String sessionId);
	Touch setTouch(Touch touch);
	List<TouchChans> setTouchChans(List<TouchChans> chans);
	DataCard setDataCard(DataCard card);
	PrdUnitPrice setPrdUnitPrice(PrdUnitPrice prdUnitPrice);
	PrdDiscount setPrdDiscount(PrdDiscount prdDiscount);
	UserEVoucher verifyEVoucher(Long evoucherSn);
	EVoucherVerify saveEVoucherVerify(Long actionUserid, Long evoucherSn, UserEVoucher uev, Integer status);
	void deliveryDataCard(Long actionUserid,Long evoucherSn);
	List<User> createAccounts(Long companyid, Long roleid, Integer quantity, String username, String password);
	User deleteAccount(String userame);
	List<Touch> getTouchs(String devid, String phone, Integer devtype, Integer pageindex, Integer pagesize);
	EVoucher createEVoucher(EVoucher evoucher, List<Long> prdids);
	List<String> generateEVouchers(Long evoucherId, Integer quantity);

    void updateUserCreateTime(Long userid);
}

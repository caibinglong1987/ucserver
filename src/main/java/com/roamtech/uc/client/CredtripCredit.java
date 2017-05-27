package com.roamtech.uc.client;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.roamtech.uc.util.JSONUtils;

public class CredtripCredit implements Serializable {
	private static final long serialVersionUID = 1L;
	@JSONField(name = "accountno")
	private String accountNo;
	private Double credit;
	private Double balcredit;
	private String status;
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public Double getCredit() {
		return credit;
	}
	public void setCredit(Double credit) {
		this.credit = credit;
	}
	public Double getBalcredit() {
		return balcredit;
	}
	public void setBalcredit(Double balcredit) {
		this.balcredit = balcredit;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return JSONUtils.serialize(this);
	}
}

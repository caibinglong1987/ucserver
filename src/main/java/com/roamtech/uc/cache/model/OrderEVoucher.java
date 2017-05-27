package com.roamtech.uc.cache.model;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.roamtech.uc.model.Order;
import com.roamtech.uc.model.UserEVoucher;

public class OrderEVoucher implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Order order;
	private UserEVoucher myevoucher;
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	public UserEVoucher getMyevoucher() {
		return myevoucher;
	}
	public void setMyevoucher(UserEVoucher myevoucher) {
		this.myevoucher = myevoucher;
	}
}

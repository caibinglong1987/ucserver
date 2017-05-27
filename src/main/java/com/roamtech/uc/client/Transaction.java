package com.roamtech.uc.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Transaction implements Serializable {
	private String amount;
	private String currency;
	private String date;
	private String card;
	private AddressData addressData;
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getCard() {
		return card;
	}
	public void setCard(String card) {
		this.card = card;
	}
	public AddressData getAddressData() {
		return addressData;
	}
	public void setAddressData(AddressData addressData) {
		this.addressData = addressData;
	}
	
}

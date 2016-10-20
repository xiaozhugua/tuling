package com.abct.tljr.hangqing.model;

public class Foreign {

	private String name;
	private String market;
	private String code;
	private float price;
	private float change;
	private float yclose;
	private String status;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public float getYclose() {
		return yclose;
	}
	public void setYclose(float yclose) {
		this.yclose = yclose;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public float getChange() {
		return change;
	}
	public void setChange(float change) {
		this.change = change;
	}
	
	
	
	
}

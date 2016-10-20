package com.abct.tljr.hangqing.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ZiXuanGu extends RealmObject{
	
	@PrimaryKey
	private String id;
	private String code;
	private String market;
	private String name;
	private float price;
	private float change;
	private int location;
	private float yclose;
	private String status;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public float getPrice() {
		return price;
	}
	public float getYclose() {
		return yclose;
	}
	public void setYclose(float yclose) {
		this.yclose = yclose;
	}
	public int getLocation() {
		return location;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public float getChange() {
		return change;
	}
	public void setChange(float change) {
		this.change = change;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}

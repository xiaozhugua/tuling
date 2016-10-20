package com.abct.tljr.kline;

public class OneData {
	// [1416553200000,21.3,21.98,21.25,21.8,159636]
	// 时间戳，开盘价，最高价，最低价，收盘价，成交量
	private long time;//时间戳
	private String date;
	private float startPrice;
	private float endPrice;
	private float highPrice;
	private float lowPrice;
	private long changes;
	private long changesnow;
	private long totlechange;
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public float getStartPrice() {
		return startPrice;
	}
	public void setStartPrice(float startPrice) {
		this.startPrice = startPrice;
	}
	public float getEndPrice() {
		return endPrice;
	}
	public void setEndPrice(float endPrice) {
		this.endPrice = endPrice;
	}
	public float getHighPrice() {
		return highPrice;
	}
	public void setHighPrice(float highPrice) {
		this.highPrice = highPrice;
	}
	public float getLowPrice() {
		return lowPrice;
	}
	public void setLowPrice(float lowPrice) {
		this.lowPrice = lowPrice;
	}
	public long getChanges() {
		return changes;
	}
	public void setChanges(long changes) {
		this.changes = changes;
	}
	public long getChangesnow() {
		return changesnow;
	}
	public void setChangesnow(long changesnow) {
		this.changesnow = changesnow;
	}
	public long getTotlechange() {
		return totlechange;
	}
	public void setTotlechange(long totlechange) {
		this.totlechange = totlechange;
	}
	
}

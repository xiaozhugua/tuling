package com.abct.tljr.kline;

public class Stock {
	public String time;
	public String open;
	public String maxh;
	public String minh;
	public String close;
	public String volume;
	public Stock(String time,String open,String maxh,String minh,String close,String volume){
		this.time = time;
		this.open = open;
		this.maxh = maxh;
		this.minh = minh;
		this.close = close;
		this.volume = volume;
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getOpen() {
		return open;
	}
	public void setOpen(String open) {
		this.open = open;
	}
	public String getMaxh() {
		return maxh;
	}
	public void setMaxh(String maxh) {
		this.maxh = maxh;
	}
	public String getMinh() {
		return minh;
	}
	public void setMinh(String minh) {
		this.minh = minh;
	}
	public String getClose() {
		return close;
	}
	public void setClose(String close) {
		this.close = close;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	
	
}

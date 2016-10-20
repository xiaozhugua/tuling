package com.abct.tljr.hangqing.model;

import java.io.Serializable;

public class RankViewDataModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String code;//股票代码
	private String grade;//状态
	private String iconUrl;//图片url
	private String name;//股票名称
	private String reason;//推荐原因
	private String time;//时间
	private long times;//long型时间
	private String market;//市场
	
	public RankViewDataModel(String code, String grade, String iconUrl,
			String name, String reason, String time,long times,String market) {
		this.code = code;
		this.grade = grade;
		this.iconUrl = iconUrl;
		this.name = name;
		this.reason = reason;
		this.time = time;
		this.times=times;
		this.market=market;
	}
	
	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	public long getTimes() {
		return times;
	}
	public void setTimes(long times) {
		this.times = times;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}


}

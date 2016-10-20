package com.abct.tljr.hangqing.model;

public class JingWuModel {

	private String timeormoney;
	private String actionornum;
	private String dataorpercent;
	private String timedateorname;
	private String sign;
	private int status;
	public String code;
	public String market;
	
	public JingWuModel(String timeormoney, String actionornum,String dataorpercent
			, String timedateorname, String sign, int status,String code,String market) {
		this.timeormoney = timeormoney;
		this.actionornum = actionornum;
		this.dataorpercent = dataorpercent;
		this.timedateorname = timedateorname;
		this.sign = sign;
		this.status = status;
		this.code=code;
		this.market=market;
	}

	
	
	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getTimeormoney() {
		return timeormoney;
	}
	public void setTimeormoney(String timeormoney) {
		this.timeormoney = timeormoney;
	}
	public String getActionornum() {
		return actionornum;
	}
	public void setActionornum(String actionornum) {
		this.actionornum = actionornum;
	}
	public String getDataorpercent() {
		return dataorpercent;
	}

	public void setDataorpercent(String dataorpercent) {
		this.dataorpercent = dataorpercent;
	}

	public String getTimedateorname() {
		return timedateorname;
	}

	public void setTimedateorname(String timedateorname) {
		this.timedateorname = timedateorname;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
	
}

package com.abct.tljr.ranking;

/**
 * @author xbw
 * @version 创建时间：2015年8月8日 下午4:35:05
 */
public class OneHistory {

	private long time;// 交易日期
	private String name;// 股票ID
	private float srcP;// 调整前仓位
	private float toP;// 调整后仓位
	private String market;
	private String code;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getSrcP() {
		return srcP;
	}

	public void setSrcP(float srcP) {
		this.srcP = srcP;
	}

	public float getToP() {
		return toP;
	}

	public void setToP(float toP) {
		this.toP = toP;
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
	
}

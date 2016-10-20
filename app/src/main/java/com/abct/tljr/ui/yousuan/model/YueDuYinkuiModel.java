package com.abct.tljr.ui.yousuan.model;

public class YueDuYinkuiModel {

	private Long date;
	private int profit=0;
	private String name="";
	
	public YueDuYinkuiModel(Long date, int profit, String name) {
		this.date = date;
		this.profit = profit;
		this.name = name;
	}
	
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public int getProfit() {
		return profit;
	}
	public void setProfit(int profit) {
		this.profit = profit;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}

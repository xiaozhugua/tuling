package com.abct.tljr.ui.yousuan.model;

public class YouSuanRecordModel {

	private String productName="";
	private String direction="";
	private int count=0;
	private int point=0;
	private String date="";
	private String name="";
	private int leftCount=0;
	private boolean deal=false;
	private boolean complete=false;
	
	public YouSuanRecordModel(String productName, String direction, int count,
			int point, String date, String name, int leftCount, boolean deal,
			boolean complete) {
		this.productName = productName;
		this.direction = direction;
		this.count = count;
		this.point = point;
		this.date = date;
		this.name = name;
		this.leftCount = leftCount;
		this.deal = deal;
		this.complete = complete;
	}
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLeftCount() {
		return leftCount;
	}
	public void setLeftCount(int leftCount) {
		this.leftCount = leftCount;
	}
	public boolean isDeal() {
		return deal;
	}
	public void setDeal(boolean deal) {
		this.deal = deal;
	}
	public boolean isComplete() {
		return complete;
	}
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	
}

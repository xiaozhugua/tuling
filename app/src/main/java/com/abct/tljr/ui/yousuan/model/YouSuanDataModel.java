package com.abct.tljr.ui.yousuan.model;

public class YouSuanDataModel {

	private String productName;
	private String direction;
	private int count;
	private int point;
	private String date;
	private String name;
	private int leftCount;
	private boolean deal;
	private boolean complete;

    /**
     * 平仓盈亏
     */
    public double currentProfit;
    /**
     * 累计盈亏
     */
    public double allProfit;

    public double openprice;

    public long opentime;

	public YouSuanDataModel(String productName, String direction, int count,
			int point, String date, String name, int leftCount, boolean deal,
			boolean complete,double currentProfit,double allProfit) {
		this.productName = productName;
		this.direction = direction;
		this.count = count;
		this.point = point;
		this.date = date;
		this.name = name;
		this.leftCount = leftCount;
		this.deal = deal;
		this.complete = complete;
        this.currentProfit=currentProfit;
        this.allProfit=allProfit;
	}

    public void setCurrentProfit(double currentProfit) {
        this.currentProfit = currentProfit;
    }

    public void setAllProfit(double allProfit) {
        this.allProfit = allProfit;
    }

    public void setOpenprice(double openprice) {
        this.openprice = openprice;
    }

    public void setOpentime(long opentime) {
        this.opentime = opentime;
    }

    public double getCurrentProfit() {
        return currentProfit;
    }

    public double getAllProfit() {
        return allProfit;
    }

    public double getOpenprice() {
        return openprice;
    }

    public long getOpentime() {
        return opentime;
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

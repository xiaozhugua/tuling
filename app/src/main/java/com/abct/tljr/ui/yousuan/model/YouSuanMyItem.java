package com.abct.tljr.ui.yousuan.model;


import com.abct.tljr.ui.yousuan.util.YouSuanMyChart;

public class YouSuanMyItem {

	private String id="";
	private String uid="";
	private int genTouId=0;
	private int status=0;
	private int money=0;
	private int profitAndLossMoney=0;
	private String createDate="";
	private String updateDate="";
	private float maxDrawDown=0;
	private float moonRate=0;
	private float dayRate=0;
	private float allRate=0;
	private float quan=0;
	private String payWay="";
	private String no="";
	private float allMoney=0;
	private int couponId=0;
	private YouSuanItemModel getTou=null;
	private boolean test;

    private YouSuanMyChart youSuanMyChart;

	public YouSuanMyItem(String id, String uid, int genTouId,int status, int money,
			int profitAndLossMoney, String createDate, String updateDate,
			float maxDrawDown, float moonRate, float dayRate, float allRate,
			float quan, String payWay, String no, float allMoney, int couponId,boolean test) {
		this.id = id;
		this.uid = uid;
		this.genTouId = genTouId;
		this.status=status;
		this.money = money;
		this.profitAndLossMoney = profitAndLossMoney;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.maxDrawDown = maxDrawDown;
		this.moonRate = moonRate;
		this.dayRate = dayRate;
		this.allRate = allRate;
		this.quan = quan;
		this.payWay = payWay;
		this.no = no;
		this.allMoney = allMoney;
		this.couponId = couponId;
		this.test=test;
	}

    public void setYouSuanMyChart(YouSuanMyChart youSuanMyChart) {
        this.youSuanMyChart = youSuanMyChart;
    }

    public YouSuanMyChart getYouSuanMyChart() {
        return youSuanMyChart;
    }

    public boolean isTest() {
		return test;
	}

	public void setTest(boolean test) {
		this.test = test;
	}

	public YouSuanItemModel getGetTou() {
		return getTou;
	}

	public void setGetTou(YouSuanItemModel getTou) {
		this.getTou = getTou;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public int getGenTouId() {
		return genTouId;
	}
	public void setGenTouId(int genTouId) {
		this.genTouId = genTouId;
	}
	public int getMoney() {
		return money;
	}
	public void setMoney(int money) {
		this.money = money;
	}
	public int getProfitAndLossMoney() {
		return profitAndLossMoney;
	}
	public void setProfitAndLossMoney(int profitAndLossMoney) {
		this.profitAndLossMoney = profitAndLossMoney;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public float getMaxDrawDown() {
		return maxDrawDown;
	}
	public void setMaxDrawDown(float maxDrawDown) {
		this.maxDrawDown = maxDrawDown;
	}
	public float getMoonRate() {
		return moonRate;
	}
	public void setMoonRate(float moonRate) {
		this.moonRate = moonRate;
	}
	public float getDayRate() {
		return dayRate;
	}
	public void setDayRate(float dayRate) {
		this.dayRate = dayRate;
	}
	public float getAllRate() {
		return allRate;
	}
	public void setAllRate(float allRate) {
		this.allRate = allRate;
	}
	public float getQuan() {
		return quan;
	}
	public void setQuan(float quan) {
		this.quan = quan;
	}
	public String getPayWay() {
		return payWay;
	}
	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public float getAllMoney() {
		return allMoney;
	}
	public void setAllMoney(float allMoney) {
		this.allMoney = allMoney;
	}
	public int getCouponId() {
		return couponId;
	}
	public void setCouponId(int couponId) {
		this.couponId = couponId;
	}
	
	
	
}

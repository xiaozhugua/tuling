package com.abct.tljr.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.abct.tljr.ui.fragments.zhiyanFragment.util.ZhiYanFinishItemLineChart;

/**
 * Created by Administrator on 2016/1/27.
 */
// 内包userCrowd
public class ZhongchouBean implements Serializable{
	private static final long serialVersionUID = 1L;
	
	String code;
	int count;
	long createDate;
	long endDate;
	int hasMoney;
	String iconurl;
	String id;
	String market;
	int price;
	int totalMoney;
	String type;
	String typedesc;
	int status;
	int focus;
	int remark;
	boolean isfocus;
	int reward;
	int rewardMoney;
	ArrayList<UserCrowd> userCrowdList;
	boolean isLook;
	String section;

	int position=0;
	
	// 图灵评星
	double privateStar;
	// 用户评分
	double customerRating;

	boolean finishstatus=false; 
	
	int first=0;
    boolean firstinfo=true;
	
    ZhiYanFinishItemLineChart zLineChart=null;

    boolean CheckStatus=false;

    boolean CheckAction=false;

    public boolean isCheckAction() {
        return CheckAction;
    }

    public void setCheckAction(boolean checkAction) {
        CheckAction = checkAction;
    }

    public boolean isCheckStatus() {
        return CheckStatus;
    }

    public void setCheckStatus(boolean checkStatus) {
        CheckStatus = checkStatus;
    }

    public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public ZhiYanFinishItemLineChart getzLineChart() {
		return zLineChart;
	}

	public void setzLineChart(ZhiYanFinishItemLineChart zLineChart) {
		this.zLineChart = zLineChart;
	}

	public boolean isFirstinfo() {
		return firstinfo;
	}

	public void setFirstinfo(boolean firstinfo) {
		this.firstinfo = firstinfo;
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public boolean isFinishstatus() {
		return finishstatus;
	}

	public void setFinishstatus(boolean finishstatus) {
		this.finishstatus = finishstatus;
	}

	public double getPrivateStar() {
		return privateStar;
	}

	public void setPrivateStar(double privateStar) {
		this.privateStar = privateStar;
	}

	public double getCustomerRating() {
		return customerRating;
	}

	public void setCustomerRating(double customerRating) {
		this.customerRating = customerRating;
	}

	public ArrayList<UserCrowd> getUserCrowdList() {
		return userCrowdList;
	}

	public void setUserCrowdList(ArrayList<UserCrowd> userCrowdList) {
		this.userCrowdList = userCrowdList;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public int getHasMoney() {
		return hasMoney;
	}

	public void setHasMoney(int hasMoney) {
		this.hasMoney = hasMoney;
	}

	public String getIconurl() {
		return iconurl;
	}

	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(int totalMoney) {
		this.totalMoney = totalMoney;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypedesc() {
		return typedesc;
	}

	public void setTypedesc(String typedesc) {
		this.typedesc = typedesc;
	}

	public int getFocus() {
		return focus;
	}

	public void setFocus(int focus) {
		this.focus = focus;
	}

	public int getRemark() {
		return remark;
	}

	public void setRemark(int remark) {
		this.remark = remark;
	}

	public boolean isIsfocus() {
		return isfocus;
	}

	public void setIsfocus(boolean isfocus) {
		this.isfocus = isfocus;
	}

	public int getReward() {
		return reward;
	}

	public void setReward(int reward) {
		this.reward = reward;
	}

	public int getRewardMoney() {
		return rewardMoney;
	}

	public void setRewardMoney(int rewardMoney) {
		this.rewardMoney = rewardMoney;
	}

	public boolean isLook() {
		return isLook;
	}

	public void setLook(boolean isLook) {
		this.isLook = isLook;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

}

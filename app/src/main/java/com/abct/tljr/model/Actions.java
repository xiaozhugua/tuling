package com.abct.tljr.model;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 对自选股的操作
 * 
 * @author xbw
 * 
 */
public class Actions implements Serializable {
	private static final long serialVersionUID = 1L;
	private String type;// 操作类型 组/股票
	private String action;// 动作
	private long time;// 操作时间
	private String bgName;// 大组名
	private String gName;// 组名
	private String gNewName;// 组的新名字
	private String gTag;// 组的新标签
	private String sName;// 股票名字
	private String sCode;// 股票代码
	private String sMarket;// 股票市场
	private double sPrice;// 股票价格
	private double changeP;// 变动百分比
	private String sTag;// 股票标签
	private String key;// 股票key
	private boolean bors;// 买或卖
	private double nums;// 买卖数量
	private double price;// 买卖价格
	private String desc;//添加记账描述
	private int percent;//百分比
	
	public int getPercent(){
		return percent;
	}
	
	public void setPercent(int percent){
		this.percent=percent;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getBgName() {
		return bgName;
	}

	public void setBgName(String bgName) {
		this.bgName = bgName;
	}

	public String getgName() {
		return gName;
	}

	public void setgName(String gName) {
		this.gName = gName;
	}

	public String getgNewName() {
		return gNewName;
	}

	public void setgNewName(String gNewName) {
		this.gNewName = gNewName;
	}

	public String getGTag() {
		return gTag;
	}

	public void setGTag(String gTag) {
		this.gTag = gTag;
	}

	public String getsName() {
		return sName;
	}

	public void setsName(String sName) {
		this.sName = sName;
	}

	public String getsCode() {
		return sCode;
	}

	public void setsCode(String sCode) {
		this.sCode = sCode;
	}

	public String getsMarket() {
		return sMarket;
	}

	public void setsMarket(String sMarket) {
		this.sMarket = sMarket;
	}

	public double getsPrice() {
		return sPrice;
	}

	public void setsPrice(double sPrice) {
		this.sPrice = sPrice;
	}

	public double getChangeP() {
		return changeP;
	}

	public void setChangeP(double changeP) {
		this.changeP = changeP;
	}

	public String getsTag() {
		return sTag;
	}

	public void setsTag(String sTag) {
		this.sTag = sTag;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isBors() {
		return bors;
	}

	public void setBors(boolean bors) {
		this.bors = bors;
	}

	public double getNums() {
		return nums;
	}

	public void setNums(double nums) {
		this.nums = nums;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void addObject(JSONArray array) {
		try {
			JSONObject obj = new JSONObject();
			obj.put("type", this.type);
			obj.put("action", this.action);
			obj.put("bname", this.bgName);
			obj.put("name", this.gName);
			obj.put("time", this.getTime());
			JSONObject info = new JSONObject();
			if (this.type.equals("bg")) {
				info.put("newName", this.gNewName);
			} else if (this.type.equals("g")) {
				info.put("newTag", this.gTag);
				info.put("newName", this.gNewName);
			} else {
				info.put("newTag", this.sTag);
				info.put("code", this.getsCode());
				info.put("market", this.getsMarket());
				info.put("sprice", this.getsPrice());
				info.put("codeName", this.getsName());
				info.put("changeP", this.getChangeP());
				info.put("key", this.getKey());
				info.put("bors", this.isBors());
				info.put("nums", this.getNums());
				info.put("price", this.getPrice());
				info.put("desc", this.getDesc());
				info.put("percent", this.getPercent());
			}
			obj.put("info", info);
			array.put(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

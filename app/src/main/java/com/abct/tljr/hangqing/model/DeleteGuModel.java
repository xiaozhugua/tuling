package com.abct.tljr.hangqing.model;

public class DeleteGuModel {

	private String name;
	private String code;
	private String price;
	private String change;
	private String market;
	private int action=0;
	private float p_changes=0;
	private String id;
	
	public DeleteGuModel(String name, String code, String price, 
			String change,String market,int action,float p_changes,String id) {
		this.name = name;
		this.code = code;
		this.price = price;
		this.change = change;
		this.market=market;
		this.action = action;
		this.p_changes=p_changes;
		this.id=id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setP_changes(float p_changes) {
		this.p_changes = p_changes;
	}

	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getChange() {
		return change;
	}
	public void setChange(String change) {
		this.change = change;
	}

	public float getP_changes() {
		return p_changes;
	} 
	
}

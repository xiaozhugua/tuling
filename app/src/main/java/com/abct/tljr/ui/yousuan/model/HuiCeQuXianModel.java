package com.abct.tljr.ui.yousuan.model;

public class HuiCeQuXianModel {

	private String id;
	private long date;
	private int point;
	private String name;
	
	public HuiCeQuXianModel(String id, long date, int point, String name) {
		this.id = id;
		this.date = date;
		this.point = point;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
}

package com.abct.tljr.ui.yousuan.model;

import java.io.Serializable;

public class QiYuXianModel implements Serializable{

	private int jingZhi;
	private Long data;
	private String name;
	
	public QiYuXianModel(int jingZhi, Long data, String name) {
		this.jingZhi = jingZhi;
		this.data = data;
		this.name = name;
	}
	
	public int getJingZhi() {
		return jingZhi;
	}
	public void setJingZhi(int jingZhi) {
		this.jingZhi = jingZhi;
	}
	public Long getData() {
		return data;
	}
	public void setData(Long data) {
		this.data = data;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}

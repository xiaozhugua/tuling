package com.abct.tljr.ui.yousuan.model;

public class DuoKongYinKuiModel {

	private double duo;
	private double kong;
	private String type;
	private String name;
	
	public DuoKongYinKuiModel(double duo, double kong, String type, String name) {
		this.duo = duo;
		this.kong = kong;
		this.type = type;
		this.name = name;
	}

	
	public double getDuo() {
		return duo;
	}


	public void setDuo(double duo) {
		this.duo = duo;
	}


	public double getKong() {
		return kong;
	}


	public void setKong(double kong) {
		this.kong = kong;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}

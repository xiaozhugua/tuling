package com.abct.tljr.ui.fragments.zhiyanFragment.model;


public class ZhiYanFinishHeaderItem {

	private String area;
	private int emation=0;
	private int emationRanking=0;
	private double emationRate=0;
	private int media=0;
	private int mediaRanking=0;
	private double mediaRate=0;
	private String name="";
	private int searchRanking=0;
	private double searchRate=0;
	private int serach=0;
	private String time="";
	private int type=0;
	
	public ZhiYanFinishHeaderItem(String area,int emation,int emationRanking,double emationRate,int media,
			int mediaRanking,double mediaRate,String name,int searchRanking,
			double searchRate, int serach, String time, int type) {
		
		this.area = area;
		this.emation = emation;
		this.emationRanking=emationRanking;
		this.emationRate=emationRate;
		this.media = media;
		this.mediaRanking = mediaRanking;
		this.mediaRate = mediaRate;
		this.name = name;
		this.searchRanking = searchRanking;
		this.searchRate = searchRate;
		this.serach = serach;
		this.time = time;
		this.type = type;
	}
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public int getMedia() {
		return media;
	}
	public void setMedia(int media) {
		this.media = media;
	}
	public int getMediaRanking() {
		return mediaRanking;
	}
	public void setMediaRanking(int mediaRanking) {
		this.mediaRanking = mediaRanking;
	}
	public double getMediaRate() {
		return mediaRate;
	}
	public String getName() {
		return name;
	}
	public int getEmation() {
		return emation;
	}

	public void setEmation(int emation) {
		this.emation = emation;
	}

	public int getEmationRanking() {
		return emationRanking;
	}

	public void setEmationRanking(int emationRanking) {
		this.emationRanking = emationRanking;
	}

	public double getEmationRate() {
		return emationRate;
	}

	public void setEmationRate(double emationRate) {
		this.emationRate = emationRate;
	}

	public void setMediaRate(double mediaRate) {
		this.mediaRate = mediaRate;
	}

	public void setName(String name) {
		this.name = name;
	}
	public int getSearchRanking() {
		return searchRanking;
	}
	public void setSearchRanking(int searchRanking) {
		this.searchRanking = searchRanking;
	}
	public double getSearchRate() {
		return searchRate;
	}
	public int getSerach() {
		return serach;
	}
	public void setSearchRate(double searchRate) {
		this.searchRate = searchRate;
	}

	public void setSerach(int serach) {
		this.serach = serach;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}

package com.abct.tljr.hangqing.model;

import java.util.List;

public class JingWuItemModel {

	private String tuijian;
	private List<RankViewDataModel> listitem;
	
	public JingWuItemModel(String tuijian,List<RankViewDataModel> listitem) {
		this.tuijian=tuijian;
		this.listitem=listitem;
	}

	
	
	public List<RankViewDataModel> getListitem() {
		return listitem;
	}

	public void setListitem(List<RankViewDataModel> listitem) {
		this.listitem = listitem;
	}

	public String getTuijian() {
		return tuijian;
	}

	public void setTuijian(String tuijian) {
		this.tuijian = tuijian;
	}
	
	
}

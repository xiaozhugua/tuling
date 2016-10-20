package com.abct.tljr.ui.fragments.zhiyanFragment.model;

import com.abct.tljr.ui.fragments.zhiyanFragment.fragment.FinishViewItemShow;

public class ZhiYanLeftBean {
	private String name = "";
	private String type = "";
	private int count = 0;
	private FinishViewItemShow finishViewItemShow = null;
	private int position=0;
	private double star=0;
	
	public double getStar() {
		return star;
	}

	public void setStar(double star) {
		this.star = star;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public FinishViewItemShow getFinishViewItemShow() {
		return finishViewItemShow;
	}

	public void setFinishViewItemShow(FinishViewItemShow finishViewItemShow) {
		this.finishViewItemShow = finishViewItemShow;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}

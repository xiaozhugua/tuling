package com.abct.tljr.model;

import java.io.Serializable;
import java.util.ArrayList;

public class OneFenZu implements Serializable ,Cloneable{
	private static final long serialVersionUID = 1L;
	private ArrayList<OneGu> list = new ArrayList<OneGu>();// 当前分组下的股票

	private String name;// 分组名
	private String tag = "";// 分组标签
	private long time;// 创建时间
	private String id = "";

	@Override
	public Object clone() throws CloneNotSupportedException {
		// 实现clone方法
		return super.clone();
	}
	
	public OneFenZu(String name) {
		// TODO Auto-generated constructor stub
		this.name = name;
	}

	public ArrayList<OneGu> getList() {
		return list;
	}

	public void setList(ArrayList<OneGu> list) {
		this.list = list;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}

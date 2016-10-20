package com.abct.tljr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xbw
 * @version 创建时间：2015-6-6 下午3:16:21
 */
public class OneDm implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, OneFenZu> map = new HashMap<String, OneFenZu>();// 当前分组下的分组信息

	private String name;// 分组名
	private long time;// 创建时间

	public OneDm(String name) {
		// TODO Auto-generated constructor stub
		this.name = name;
	}

	public Map<String, OneFenZu> getMap() {
		return map;
	}

	public void setMap(Map<String, OneFenZu> map) {
		this.map = map;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}

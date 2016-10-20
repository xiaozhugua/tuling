package com.abct.tljr.kline.gegu.util;

import org.json.JSONArray;
import org.json.JSONException;

public interface DataFromLocalListener {
	/**
	 * 添加数据图表，diskJsonString是从本地读取到的数据
	 */
	public void addChartData(String diskJsonString) throws JSONException;
}

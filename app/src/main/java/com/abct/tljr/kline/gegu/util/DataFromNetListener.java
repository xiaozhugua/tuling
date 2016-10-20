package com.abct.tljr.kline.gegu.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface DataFromNetListener {
	/**
	 * 解析json数据，如果没有数据就返回null或者""
	 * @throws JSONException 
	 */
	//public String parseJsonToString(JSONObject jsonObject) throws JSONException;

	/**
	 * 
	 * 添加图表的数据
	 * @param jsonArray 是解析之后得到的数据
	 * @throws JSONException 
	 */
	public void addChartData(JSONArray jsonArray) throws JSONException;

}

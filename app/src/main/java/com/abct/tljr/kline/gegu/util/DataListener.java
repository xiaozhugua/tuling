package com.abct.tljr.kline.gegu.util;

import org.json.JSONArray;
import org.json.JSONException;

public interface DataListener {
    
	public void addChartData(JSONArray jsonArray) throws JSONException;
	
}

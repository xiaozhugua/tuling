package com.abct.tljr.utils;

/**
 * 用于首页的图灵数据部分的接口
 */
public class TulingDataContant {

	//http://ys.tuling.me/YSServer/YSServlet?class=OtherImpf&package=me.tuling.ys.others.&params={"key":"dealing_newest_key"}
	
	public   static   String   getUrl(String   param){
		return   OneGuConstance.TU_LING+"YSServer/YSServlet?class=OtherImpf&package=me.tuling.ys.others.&params={\"key\":\""+param+"\"}";
	}
}

 package com.abct.tljr.utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.qh.common.listener.HttpCallback;

public class XUtilsHelper {
	
	
	private static HttpUtils client;
	
	
	static{
		client = new HttpUtils();
	}
	
	
	public static void sendPost(String url,RequestParams params,final HttpCallback cb){
		
		client.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				cb.callback("error");
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				cb.callback(arg0.result);
			}
		});
		
	}
	
	public static void sendGet(String url,RequestParams params,final HttpCallback cb){
		
		client.send(HttpMethod.GET, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				cb.callback("error");
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				cb.callback(arg0.result);
			}
		});
		
	}
	
}

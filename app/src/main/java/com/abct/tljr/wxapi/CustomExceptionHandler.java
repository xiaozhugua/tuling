package com.abct.tljr.wxapi;

import java.lang.Thread.UncaughtExceptionHandler;

import android.util.Log;

public class CustomExceptionHandler implements UncaughtExceptionHandler {
	private static final String TAG = "CustomExceptionHandler";
	
	private UncaughtExceptionHandler mDefaultUEH;
	
	public CustomExceptionHandler() {
		mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		ex.printStackTrace();
		mDefaultUEH.uncaughtException(thread, ex); // 不加本语句会导致ANR
	}

}
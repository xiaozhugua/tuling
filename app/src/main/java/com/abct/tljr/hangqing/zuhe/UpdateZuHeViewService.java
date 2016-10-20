package com.abct.tljr.hangqing.zuhe;

import com.abct.tljr.hangqing.HangQing;

import android.app.IntentService;
import android.content.Intent;

public class UpdateZuHeViewService extends IntentService {

	
	public UpdateZuHeViewService() {
		super("ZuHeView");
	}

	@Override
	public void onCreate() {
//		Log.e("ZuHeView_OnCreate", "OnCreate");
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try{
			//HangQing.mTljrZuHe.updateFlipperAdapter();
		}catch(Exception e){
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}

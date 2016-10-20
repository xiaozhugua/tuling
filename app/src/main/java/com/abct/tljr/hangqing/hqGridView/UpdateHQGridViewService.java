package com.abct.tljr.hangqing.hqGridView;

import com.abct.tljr.hangqing.HangQing;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class UpdateHQGridViewService extends IntentService{

	public UpdateHQGridViewService() {
		super("HQGridView");
	}

	@Override
	public void onCreate() {
//		Log.e("HQGridView","HQGridView_onCreate");
		super.onCreate();
	}
	
	@Override
	protected void onHandleIntent(Intent intent){
		try{
			HangQing.hqGridView.reflushDP();
		}catch(Exception e){
		}
	}
	
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		Log.e("UpdateHQGridViewService","UpdateHQGridViewService_onStartCommand");
//		HangQing.hqGridView.reflushDP();
//		return super.onStartCommand(intent, flags, startId);
//	}

	@Override
	public void onDestroy(){
		super.onDestroy();
	}

}



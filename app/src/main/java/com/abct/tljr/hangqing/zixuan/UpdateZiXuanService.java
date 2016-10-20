package com.abct.tljr.hangqing.zixuan;

import android.app.IntentService;
import android.content.Intent;

public class UpdateZiXuanService extends IntentService {

	
	public UpdateZiXuanService() {
		super("ZiXuan");
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try{
		   //TljrZiXuanGu.reflush(null);
			tljr_zixuan_gu_recyclerview.flush(true);
		}catch(Exception e){
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}

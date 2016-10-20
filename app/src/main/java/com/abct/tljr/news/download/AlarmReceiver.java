package com.abct.tljr.news.download;

import com.abct.tljr.data.Constant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * @ClassName: AlarmReceiver
 * @Description: 闹铃时间到了会进入这个广播，这个时候可以做一些该做的业务。
 * @author HuHood
 * @date 2013-11-25 下午4:44:30
 * 
 */
public class AlarmReceiver extends BroadcastReceiver {
	private boolean isTimingDownLoad ;
	@Override
	public void onReceive(Context context, Intent intent) {
	
		isTimingDownLoad= Constant.preference.getBoolean("isTimingDownLoad", false);
		
		if(isTimingDownLoad){
			Intent bindIntent = new Intent(context, DownLoadService.class);
			bindIntent.putExtra("isTiming", "true");
			context.startService(bindIntent);
		}
		 

	}

 

}

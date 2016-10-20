package com.abct.tljr.news.download;

import java.util.Calendar;
import java.util.TimeZone;

import com.abct.tljr.data.Constant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

/**
 * 接收系统重启广播，重新注册离线下载
 * 
 */
public class BootReceiver extends BroadcastReceiver {
	private int mHour = -1;
	private int mMinute = -1;
	private boolean isTimingDownLoad;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_BOOT_COMPLETED))
		{

			isTimingDownLoad = Constant.preference.getBoolean("isTimingDownLoad", false); // 检查是否设置离线下载

			if (isTimingDownLoad)
			{
				Log.i("DownLoadActivity", "收到重启广播");

				String downloadTime = Constant.preference.getString("downLoadTime", "07:39");
				Log.i("DownLoadActivity", "downloadTime:" + downloadTime);
				String[] minHour = downloadTime.split(":");
				mHour = Integer.valueOf(minHour[0]);
				mMinute = Integer.valueOf(minHour[1]);
				settingTiming(context);
			}

		}
	}

	private void settingTiming(Context context)
	{
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);

		long firstTime = SystemClock.elapsedRealtime(); // 开机之后到现在的运行时间(包括睡眠时间)
		long systemTime = System.currentTimeMillis();

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8")); // 这里时区需要设置一下，不然会有8个小时的时间差
		calendar.set(Calendar.MINUTE, mMinute);
		calendar.set(Calendar.HOUR_OF_DAY, mHour);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		// 选择的每天定时时间
		long selectTime = calendar.getTimeInMillis();
		int INTERVAL = 1000 * 60 * 60 * 24;// 24h
		// 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
		if (systemTime > selectTime)
		{
			Toast.makeText(context, "设置的时间小于当前时间", Toast.LENGTH_SHORT).show();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			selectTime = calendar.getTimeInMillis();
		}

		// 计算现在时间到设定时间的时间差
		long time = selectTime - systemTime;
		firstTime += time;

		// 进行闹铃注册
		AlarmManager manager = (AlarmManager) context.getSystemService("alarm");
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, INTERVAL, sender);

		Log.i("DownLoadActivity", "time ==== " + time + ", selectTime ===== " + selectTime + ", systemTime ==== "
				+ systemTime + ", firstTime === " + firstTime);
		Log.i("DownLoadActivity", "设置重复闹铃成功");
		// Toast.makeText(activity, "设置重复闹铃成功! ", Toast.LENGTH_LONG).show();
	}

}

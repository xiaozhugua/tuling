package com.abct.tljr.news.download;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.news.HuanQiuShiShi;
import com.abct.tljr.news.channel.bean.ChannelItem;
import com.abct.tljr.news.channel.view.ChannelListView;
import com.abct.tljr.news.widget.NumberProgressBar;
import com.qh.common.util.LogUtil;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import io.realm.Realm;
import io.realm.RealmResults;

public class DownLoadActivity extends BaseActivity {
	private String Tag = "DownLoadActivity";
	private ChannelListView listview;
	private DownLoadAdapter adapter;
	private Context context;
	ArrayList<ChannelItem> userChannelList = new ArrayList<ChannelItem>();
	ArrayList<ChannelItem> downLoadList = new ArrayList<ChannelItem>();

	private DownLoadActivity activity;
	public static DownLoadService.MyBind myService;

	TextView time_download;

	public static boolean isDownLoadImage = false;
	public static boolean isTimingDownLoad = false;
	
	
	CheckBox cb_chooseAll; //全选按钮

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		context =this;
		Intent bindIntent = new Intent(this, DownLoadService.class);
		bindIntent.putExtra("isTiming", "false");
		startService(bindIntent);
		bindService(bindIntent, SerConnection2, BIND_AUTO_CREATE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_activity_news_download);
		ScrollView downloadScroll = (ScrollView) findViewById(R.id.downloadScroll);
		downloadScroll.smoothScrollTo(0, 0);
		activity = this;
		isTimingDownLoad = Constant.preference.getBoolean("isTimingDownLoad", false);
		isDownLoadImage = Constant.preference.getBoolean("isDownLoadImage", false);
		
		cb_chooseAll  =(CheckBox)findViewById(R.id.download_choose_all);
		cb_chooseAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked)
			{
				// TODO Auto-generated method stub
				adapter.chooseAll(isChecked);
			}
		});

		Realm myRealm = Realm.getDefaultInstance();
		RealmResults<ChannelItem> results = myRealm.where(ChannelItem.class).equalTo("selected", 1).findAll();
		for (ChannelItem item : results) {
			userChannelList.add(item);
			LogUtil.i("kk", item.getName());
		}



		for (ChannelItem item : userChannelList)
		{
			if (item.getName().equals("我的收藏"))
			{
				continue;
			}
			if (item.getChannelType () == 0 || item.getChannelType () ==1)
			{
				downLoadList.add(item);
			}
		}

		RelativeLayout tljr_img_back = (RelativeLayout) findViewById(R.id.tljr_img_news_back);
		tljr_img_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0)
			{
				finish();
				overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_right);
			}
		});

		time_download = (TextView) findViewById(R.id.timepicker_download);
		time_download.setTextColor(getResources().getColor(
				isTimingDownLoad ? R.color.tljr_statusbarcolor : R.color.tljr_djbj));
		String downloadTime = Constant.preference.getString("downLoadTime", "07:30");
		time_download.setText(downloadTime);
		String[] minHour = downloadTime.split(":");
		mHour = Integer.valueOf(minHour[0]);
		mMinute = Integer.valueOf(minHour[1]);

		listview = (ChannelListView) findViewById(R.id.downloadListView);
		  adapter= new DownLoadAdapter(DownLoadActivity.this, downLoadList,listview);
		listview.setAdapter(adapter);

		final CheckBox btn_isDownImage = (CheckBox) findViewById(R.id.btn_image_download_news);
		btn_isDownImage.setChecked(isDownLoadImage);
		btn_isDownImage.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				isDownLoadImage = isChecked ? true : false;
			}
		});
		final CheckBox btn_isTimingDown = (CheckBox) findViewById(R.id.btn_timing_download_news);
		btn_isTimingDown.setChecked(isTimingDownLoad);
		btn_isTimingDown.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				isTimingDownLoad = isChecked ? true : false;
				time_download.setTextColor(getResources().getColor(
						isChecked ? R.color.tljr_statusbarcolor : R.color.tljr_djbj));
			}
		});

	}

	private int mHour = -1;
	private int mMinute = -1;
	private TimePicker timePicker;
	private Calendar calendar;

	public void download_timePicker(View view)
	{
		
		
		 
		
		
		calendar = Calendar.getInstance();
		TimePickerDialog timePickerDialog = new TimePickerDialog(context, new OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				  System.out.println("time:" + hourOfDay + ":"  
	                        + minute);  
					mHour = hourOfDay;
					mMinute = minute;
					time_download.setText(mHour + ":" + mMinute);
			}
		}, calendar.get(Calendar.HOUR_OF_DAY),  
		        calendar.get(Calendar.MINUTE), true);  
		  
		timePickerDialog.setTitle("请选择离线下载时间");  
		timePickerDialog.setButton(TimePickerDialog.BUTTON_POSITIVE,  
		        "设定", timePickerDialog);  
		timePickerDialog.setButton(TimePickerDialog.BUTTON_NEGATIVE,  
		        "取消", timePickerDialog);
		timePickerDialog.show();  
		
		 
		

//		calendar = Calendar.getInstance();
//		LayoutInflater inflater = activity.getLayoutInflater();
//		View layout = inflater.inflate(R.layout.tljr_news_time_dialog,null);
//		timePicker = (TimePicker) layout.findViewById(R.id.timePicker);
//		timePicker.setIs24HourView(true);
//		if (mHour == -1 && mMinute == -1)
//		{
//			mHour = calendar.get(Calendar.HOUR_OF_DAY);
//			mMinute = calendar.get(Calendar.MINUTE);
//		}
//		timePicker.setCurrentHour(mHour);
//		timePicker.setCurrentMinute(mMinute);
//		 
//		
//		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//			@Override
//			public void onTimeChanged(TimePicker view, final int hourOfDay, final int minute)
//			{
//				handler.post(new Runnable() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						mHour = hourOfDay;
//						mMinute = minute;
//						time_download.setText(mHour + ":" + mMinute);
//					}
//				});
//				
//			}
//		});
		
		 

//		new AlertDialog.Builder(activity).setView(layout)
//				.setPositiveButton("设定", new android.content.DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface arg0, int arg1)
//					{
//						time_download.setText(mHour + ":" + mMinute);
//					}
//				}).setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface arg0, int arg1)
//					{
//						// TODO Auto-generated method stub
//					
//
//					}
//				}).setTitle("请选择离线下载时间").show();

		
		
		
		
		
		
		
		
		
		
		
	}

	boolean isEmpty = true;
	public void btnStart_download(View view)
	{
 

		if (Constant.netType.equals("未知"))
		{
			Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
			return;
		}
		
		boolean result =adapter.addListToDownLoadTask();
		if(!result){
			Toast.makeText(getApplicationContext(), "请添加需要下载的新闻频道", Toast.LENGTH_LONG).show();
			return;
		}
		
		if(HuanQiuShiShi.isDowanLoading){
			Toast.makeText(getApplicationContext(), "正在下载中，请稍后重试", Toast.LENGTH_LONG).show();
			return;
		}
		HuanQiuShiShi.isDowanLoading= true ;
		 
	
		Toast.makeText(getApplicationContext(), "开始下载 ", Toast.LENGTH_LONG).show();
		
		
		Editor editor = Constant.preference.edit();
		for(int i=0;i<listview.getChildCount();i++){
			editor.putString("downPosition-"+i, "0,1450065455535");
			}
		editor.commit();
		
		
		
		adapter.startDownLoad();
		
//		   Message msgJson = new Message();
//           msgJson.what = 111; 
//        //   mHandler.sendMessage(msgJson);
//           mHandler.sendMessageDelayed(msgJson, 200);
		
 
	
	

           post(new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				
				
		
				
  
 			
 			
				if (isTimingDownLoad)
				{
					Log.i(Tag, "isTimingDownLoad");
					/*
					 * 设置重复闹钟 start
					 */
					settingTiming();
					/*
					 * 设置重复闹钟 end
					 */

					String allSpecial = "";
					if (DownLoadAdapter.timeDownCacheChannel.size() <=0)
					{
						return;
					}
					for (String special : DownLoadAdapter.timeDownCacheChannel)
					{
						allSpecial = allSpecial + special + ",";
					}
					Constant.preference.edit().putString("downLoadChannelType", allSpecial).commit(); // 记录要下载的频道
					Constant.preference.edit().putString("downLoadTime", time_download.getText() + "").commit(); // 记录定时时间
					Log.i(Tag, "要下载的频道:"+allSpecial +"---定时时间:"+time_download.getText() );
				} else
				{
					Constant.preference.edit().putString("downLoadChannelType", "nothing").commit();
				}
				Constant.preference.edit().putBoolean("isTimingDownLoad", isTimingDownLoad).commit();
				Constant.preference.edit().putBoolean("isDownLoadImage", isDownLoadImage).commit();
			
			}
		});
           
	
	}

	private void settingTiming()
	{
		Intent intent = new Intent(activity, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(activity, 0, intent, 0);

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
			//Toast.makeText(activity, "设置的时间小于当前时间", Toast.LENGTH_SHORT).show();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			selectTime = calendar.getTimeInMillis();
		}

		// 计算现在时间到设定时间的时间差
		long time = selectTime - systemTime;
		firstTime += time;

		// 进行闹铃注册
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, INTERVAL, sender);

		Log.i(Tag, "time ==== " + time + ", selectTime ===== " + selectTime + ", systemTime ==== " + systemTime
				+ ", firstTime === " + firstTime);

		Toast.makeText(activity, "设置WiFi定时离线下载成功! ", Toast.LENGTH_LONG).show();
	}

	
	
	
	private ServiceConnection SerConnection2 = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name)
		{

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{

			myService = (DownLoadService.MyBind) service;
		}
	};

	@Override
	public void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();
		
		  IntentFilter filter_dynamic = new IntentFilter();  
	        filter_dynamic.addAction(DYNAMICACTION);  
	        activity.registerReceiver(dynamicReceiver, filter_dynamic);  
		
		
	}
public void updateProgress(int position,int progress){
	 if(listview !=null){
			View viewT = listview.getChildAt(position);
			if(viewT !=null){
				NumberProgressBar processbar = (NumberProgressBar) ViewHolder.get(viewT, R.id.processbar_download);
				processbar.setVisibility(View.VISIBLE);
				processbar.setProgress(progress);
				
				CheckBox btn_tick = (CheckBox) ViewHolder.get(viewT, R.id.btn_tick);
				btn_tick.setChecked(true);
				btn_tick.setEnabled(false);
				  RelativeLayout ly_tick= (RelativeLayout) ViewHolder.get(viewT, R.id.ly_tick);	
				  ly_tick.setEnabled(false);
				  
				  if(progress==100){
					  btn_tick.setChecked(false);
						btn_tick.setBackground(getResources().getDrawable(R.drawable.img_news_gouxuan3));
				  }
				
			}
	 }

	
	}
	public static final String DYNAMICACTION = "com.news.DownLoadActivity";  
	private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {  
        
        @Override  
        public void onReceive(Context context, Intent intent) {  
            if(intent.getAction().equals(DYNAMICACTION)){
            	
            	if(intent.hasExtra("progress") &&intent.hasExtra("position")){
            		updateProgress(Integer.valueOf(intent.getStringExtra("position")),Integer.valueOf(intent.getStringExtra("progress")));
            		
            		
            	} 
            	
//                String msg = intent.getStringExtra("msg");  

            }  
        }  
    };  
	
	
	protected void onPause()
	{
		super.onPause();
		Constant.preference.edit().putBoolean("isTimingDownLoad", isTimingDownLoad).commit();
		 
			boolean result =adapter.addListToDownLoadTask();
			String allSpecial = "";
			if(result){
				for (String special : DownLoadAdapter.timeDownCacheChannel)
				{
					allSpecial = allSpecial + special + ",";
				}
				Constant.preference.edit().putString("downLoadChannelType", allSpecial).commit(); // 记录要下载的频道
				Constant.preference.edit().putString("downLoadTime", time_download.getText() + "").commit(); // 记录定时时间
				Log.i(Tag, "要下载的频道:"+allSpecial +"---定时时间:"+time_download.getText() );
			}else{
				Constant.preference.edit().putString("downLoadChannelType", "nothing").commit(); 
				Constant.preference.edit().putString("downLoadTime", time_download.getText() + "").commit(); // 记录定时时间
			}
		
		 
		
		
		Constant.preference.edit().putBoolean("isDownLoadImage", isDownLoadImage).commit();
		Constant.preference.edit().putString("downLoadTime", mHour + ":" + mMinute).commit();

	};

	


	
	protected void onDestroy()
	{
		super.onDestroy();
		
		unregisterReceiver(dynamicReceiver); 
		
		
		
		
		
		unbindService(SerConnection2);

	};
	

}

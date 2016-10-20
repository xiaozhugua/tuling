package com.abct.tljr.news.download;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.news.HuanQiuShiShi;
import com.abct.tljr.news.bean.News;
import com.abct.tljr.news.fragment.NewsManager;
import com.abct.tljr.utils.Util;
import com.qh.common.util.DateUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.PreferenceUtils;
import com.qh.common.util.UrlUtil;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

public class DownLoadService extends Service {
	private NotificationManager manager;
	 

	private MyBind mb = new MyBind();

	// Notification notification;
	// Intent notificationIntent;

	private boolean isWIFI = false;

	@Override
	public void onCreate()
	{
		// TODO Auto-generated method stub
		super.onCreate();
		 
	 
		isWIFI = Util.getAPNType(this).equals("WIFI") ? true : false;
		Log.i("Tag", "当前网络状态WIFI:" + isWIFI);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO Auto-generated method stub

		 
		if (intent != null && intent.getExtras().containsKey("isTiming")
				&& intent.getExtras().getString("isTiming") != null)
		{
			if (isWIFI)
			{
				String Flag = intent.getExtras().getString("isTiming");
				if (Flag.equals("true"))
				{
					String allspeical = Constant.preference.getString("downLoadChannelType", "nothing");
					boolean downloadImage = Constant.preference.getBoolean("isDownLoadImage", false);

					if (!allspeical.equals("nothing"))
					{
						String[] channel = allspeical.split(",");
						for (int i = 0; i < channel.length; i++)
						{
							
							String pId = PreferenceUtils.getInstance().preferences.getString("UserId", "0");
							String url =   UrlUtil.URL_new+"api/uc/od" + "?" + "need=7&species=" + channel[i]+"&uid="+pId;
							mb.addTask(downloadImage, i + "", url);
						}
						mb.startDownLoad();
					}
				}
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
	 
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg)
		{

			if (msg.what == 2)
			{
				
				manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//				Notification notification = new NotificationCompat.Builder(getApplicationContext())
//						.setSmallIcon(R.drawable.tljr_launcher).setContentInfo("图灵金融").setTicker("离线新闻下载完成")
//						.setContentTitle("提示").setContentText("离线新闻下载完成").setNumber(1).setAutoCancel(true)
//						.setDefaults(Notification.DEFAULT_LIGHTS).build();
				 
				 
				NotificationCompat.Builder mBuilder = new Builder(getApplicationContext());
				mBuilder.setSmallIcon(R.drawable.tljr_launcher);
				mBuilder.setTicker("");
				mBuilder.setContentInfo("图灵金融");
				mBuilder.setTicker("离线新闻下载完成");
				mBuilder.setContentTitle("提示");
				mBuilder.setContentText("离线新闻下载完成");
				mBuilder.setNumber(1); 
				 
				ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);

				List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);

				ComponentName componentInfo = taskInfo.get(0).topActivity;
				 
				Log.d("hao1", "当前应用:" + componentInfo.getPackageName());
				PendingIntent contentIntent ;
				 if(componentInfo.getPackageName().equals("com.abct.tljr")){
					 Intent itt = new Intent(getApplicationContext(),DownLoadActivity.class);
					  contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,  itt, PendingIntent.FLAG_CANCEL_CURRENT); 
				 }else{
					    Intent[] intents = new Intent[2];  
					    intents[0] = Intent.makeRestartActivityTask(new ComponentName(getApplicationContext(), com.abct.tljr.ui.activity.StartActivity.class));  
					    intents[1] = new Intent(getApplicationContext(),  com.abct.tljr.news.download.DownLoadActivity.class);  
					    contentIntent = PendingIntent.getActivities(getApplicationContext(), 0,  intents, PendingIntent.FLAG_CANCEL_CURRENT); 
				 }
				
				      
				mBuilder.setContentIntent(contentIntent);
			
				Notification notify = mBuilder.build();
				notify.flags = Notification.FLAG_AUTO_CANCEL;
				manager.notify(0, notify);
				
				
		
			
				
				LogUtil.i("DownLoadService", "离线下载完成，发送广播"  );
				Intent it = new Intent();
				it.setAction(HuanQiuShiShi.FINISH_ACTION);
				sendBroadcast(it);
			}

		};
	};

	/**
	 * 获取当前Service的实例
	 * 
	 * @return
	 */
	public class MyBind extends Binder {
		// public void SetOnProgressListener(OnProgressListener pro) { //
		// 注册回调接口的方法，供外部调用
		// this.onProgressListener = pro;
		// }
		// private ArrayList<DownThread> taskList = new ArrayList<DownThread>();

		private HashMap<String, DownThread> taskList = new HashMap<String, DownThread>();

		private ExecutorService executorService;

		public MyBind() {
			// TODO Auto-generated constructor stub
			executorService = Executors.newFixedThreadPool(3);
		}

		public void addTask(boolean isDownLoadImage, String id, String url)
		{
			DownThread downThread = new DownThread(isDownLoadImage, url,id);
			taskList.put(id, downThread);
		}

	 

		public void startDownLoad()
		{
		
			for (DownThread thread : taskList.values())
			{
				executorService.submit(thread);
			}
		
		}

		class DownThread extends Thread {
			final int MAX_PROGRESS = 100; // 进度条最大值
			int progress = 0; // 进度条进度值
			String isoString;
			String url;
			private boolean isWorking = false;
			boolean isDownLoadImage = false;
			byte[] bytes;
			String id ;
			public DownThread(boolean isDownLoadImage, String url,String id) {
				// TODO Auto-generated constructor stub
				this.isWorking = true;
				this.url = url;
				this.id= id ;
				this.isDownLoadImage = isDownLoadImage;
			
			}

			public void stopTask()
			{
				this.isWorking = false;
			}

			public void update(int progress)
			{

				int threadCount = ((ThreadPoolExecutor) executorService).getActiveCount();

				if (threadCount == 1 && progress == MAX_PROGRESS)
				{
					Message msg = new Message();
					msg.what = 2;
					msg.arg1 = (int) Thread.currentThread().getId();
					handler.sendMessage(msg);
					taskList.clear();
				}
				
				if (progress == MAX_PROGRESS)
				{

					taskList.remove(this);
					isWorking = false;
					
				}
				
				Intent idd = new Intent();
				idd.setAction(DownLoadActivity.DYNAMICACTION) ;
				idd.putExtra("position", id);
				idd.putExtra("progress", progress+"");
				sendBroadcast(idd);
				
				
				if(progress == MAX_PROGRESS){
					
					Constant.preference.edit().putString("downPosition-"+id, "2"+","+System.currentTimeMillis()).commit();
				}
				
 

			}

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				super.run();
		
				// TODO Auto-generated method stub
				while (isWorking)
				{
					try
					{
						HttpURLConnection conn = null;
						conn = (HttpURLConnection) new URL(url).openConnection();
						conn.setConnectTimeout(5000);
						conn.setDoInput(true);
						conn.setDoOutput(false);
						conn.setUseCaches(true);
						conn.setRequestProperty("Charset", "utf-8");
						conn.connect();
						if (conn.getContentLength() < 0)
						{
							break;
						}
						bytes = new byte[conn.getContentLength()];
						InputStream in = null;
						try
						{
							in = conn.getInputStream();
							int readBytes = 0;
							while (true)
							{
								int length = in.read(bytes, readBytes, bytes.length - readBytes);
								if (length == -1)
									break;
								readBytes += length;

								update((int) (readBytes * 100 / bytes.length));

							}
							conn.disconnect();

							isoString = new String(bytes,"utf-8");
							LogUtil.i("DownLoadService", isoString);
 
						} catch (Exception ex)
						{
							ex.printStackTrace();
						} finally
						{
							try
							{
								if (in != null)
									in.close();
							} catch (Exception ignored)
							{
								ignored.printStackTrace();
							}
						}
					} catch (Exception e)
					{
					}

				}
 
				try
				{
					JSONObject object = new JSONObject(isoString);
					
					JSONObject obj = object.getJSONObject("joData");
					int layout = obj.optInt("layout");
					
				
					
					
					JSONArray array = obj.getJSONArray("news");
				 
//					NewsManager mg = new NewsManager();
//					mg.addNewsToList(array);
				 
					
					

					 
				       News news = null;
				       Realm myRealm;
				       try {
				           myRealm = Realm.getDefaultInstance();
				       } catch (RealmMigrationNeededException e) {
				           RealmConfiguration config = new RealmConfiguration.Builder(
				                   MyApplication.getInstance()).deleteRealmIfMigrationNeeded()
				                   .build();
				           Realm.setDefaultConfiguration(config);
				           myRealm = Realm.getDefaultInstance();
				       }
				 
				       myRealm.beginTransaction();
				       for (int i = 0; i < array.length(); i++) {
				           JSONObject rs;
						try {
							rs = array.getJSONObject(i);

					           news = new News();
					 
					           if(i==0){
					        	   PreferenceUtils.getInstance().preferences.edit().putInt("layout_"+rs.optString("species"), layout).commit();
					        		 LogUtil.i("DownLoadService", "species:"+rs.optString("species") +"--layout :" +layout);
					        		 
					        		 
					        		 
					           }
					           
					           
					           
					           news.setId( rs.optString("id")  );
					           news.setSpecial(rs.optString("species"));
					           news.setType(rs.optString("type", "notype"));
					           news.setTitle(removeStr(rs.optString("title")));
					           news.setSummary(rs.optString("summary", ""));
					           news.setContent(rs.optString("content"));
					           news.setDigest(rs.optString("digest"));
					           news.setTime(rs.optLong("time"));
					           news.setSource(rs.optString("source"));
					           news.setUrl(rs.optString("url"));
					           news.setpUrl(rs.optString("purl"));
					           news.setZan(rs.optString("praise"));
					           news.setCai(rs.optString("oppose"));
					           news.setCollect(rs.optString("collects"));
					           news.setSurl(rs.optString("surl"));
					            
					           news.setHaveCai(rs.optBoolean("hasOppose", false));
					           news.setHaveZan(rs.optBoolean("hasPraise", false));
					           news.setHaveCollect(rs.optBoolean("hasCollect", false));
					            news.setHaveSee(rs.optBoolean("read", false));
					          
					           news.setDate(DateUtil.getDateMDhhmm(rs.optLong("time")));
					           news.setImp_time( getNewsDate(rs.optLong("time"), true));
					           news.setSimple_time( getNewsDate(rs.optLong("time"), false));
					           news.setTextHHmm(DateUtil.getDateOnlyHour(rs.optLong("time")));
					           date.setTime(rs.optLong("time"));
					           news.setTextDate(format.format(date));
					            
					           if (rs.has("tagIDs")) {
					               JSONArray tagIdsArray = rs.getJSONArray("tagIDs");
					               String tagIds = "";
					               if (tagIdsArray.length() > 0) {
					                   for (int j = 0; j < tagIdsArray.length(); j++) {
					                       tagIds = tagIdsArray.getString(j) + "," + tagIds;
					                   }
					                   news.setTagIds(tagIds.substring(0, tagIds.length() - 1));
					                   tagIds = null;
					               }
					 
					           }
					 
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				           myRealm.copyToRealmOrUpdate(news);
				       }
				       myRealm.commitTransaction();
				       myRealm.close();
					
					
						
						if (isDownLoadImage)
						{
							DownPictureThread cstd = new DownPictureThread(array);
							cstd.start();

						}
						
				} catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub

		return new MyBind();
	}

	class DownPictureThread extends Thread {
		JSONArray info;
		int num = 0;

		public DownPictureThread(JSONArray info) {
			this.info = info;
		}

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			super.run();

			 
			for (int k = 0; k < info.length(); k++)
			{

				 try {
					JSONObject rs = info.getJSONObject(k);
					if (rs.has("purl"))
					{
						// StartActivity.imageLoader.displayImage(rs.getString("purl"),
						// iv, StartActivity.options);

						String url = rs.getString("purl");
						String pathUrl = url.substring(0, url.lastIndexOf("/") + 1);
						String change = url.substring(url.lastIndexOf("/") + 1, url.length());
						try
						{
							change = URLEncoder.encode(change, "utf-8");
						} catch (UnsupportedEncodingException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						onlyDownImage(pathUrl + change, rs.getString("id")+"");

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		public void onlyDownImage(final String url, final String newsId)
		{
			if (url.length() == 0 || newsId == null)
			{
				return;
			}
			File file =getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
			LogUtil.i("DownLoadService", "getPath :"+file.getPath());
			String pathName = file.getPath();
		 	File fileT = new File(pathName + "/"+newsId);
			LogUtil.i("DownLoadService", "getfileTPath :"+fileT.getPath());
			if (!fileT.exists())
			{

				byte[] bytes = download(url);
				if (bytes != null && bytes.length != 0)
				{
					try
					{
					  
						if (!file.exists())
						{
							file.mkdir();
						}
						if (!fileT.exists())
						{
							fileT.createNewFile();
							FileOutputStream stream = new FileOutputStream(fileT);
							stream.write(bytes, 0, bytes.length);
							stream.close();
						}

					} catch (Exception e)
					{
						e.printStackTrace();
					}
					
				}  

			}
		}

		public byte[] download(String url)
		{
			InputStream in = null;
			try
			{
				HttpURLConnection conn = null;
				conn = (HttpURLConnection) new URL(url).openConnection();
				conn.setDoInput(true);
				conn.setDoOutput(false);
				conn.setUseCaches(true);
				conn.setConnectTimeout(5000);
				conn.connect();
				in = conn.getInputStream();
				int size = conn.getContentLength();
				byte[] out = new byte[size];
				int readBytes = 0;
				while (true)
				{
					int length = in.read(out, readBytes, out.length - readBytes);
					if (length == -1)
						break;
					readBytes += length;
				}
				conn.disconnect();
				return out;
			} catch (Exception ex)
			{
				return null;
			} finally
			{
				try
				{
					if (in != null)
						in.close();
				} catch (Exception ignored)
				{
				}
			}
		}

		public void writeFileToSD(final String fileName, final byte[] b, final int byteCount)
		{


		}

	}
	  Date  date = new Date(System.currentTimeMillis()); ;
      SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
	   public static  String getNewsDate(long time,boolean isPictureNews){
	       Date date = new Date(time);
	       Calendar cal = Calendar.getInstance();
	       cal.setTimeInMillis(System.currentTimeMillis());
	       String dayCurrent = cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
	       cal.setTime(date);
	       String day = cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
	    
	           if (day.equals(dayCurrent)) {
	               return isPictureNews?DateUtil.getDateOnlyHour(time):"今天     " + DateUtil.getDateOnlyHour(time);
	 
	           } else {
	               return DateUtil.getDateMDhhmm(time);
	           } 
	        
	   
	   }
	  public String removeStr(String rs)
	   {
	       if(rs!=null && rs.length()>0){
	           String str = rs.replaceAll("\\?", "").replaceAll("target=\"_blank\">", "").replaceAll("target=\"blank\">", "")
	                   .replaceAll("target='_blank'>", "").replaceAll("target='blank'>", "").replaceAll("&nbsp;", "")
	                   .replaceAll("\r", "").replaceAll("   ", "")
	                   .replaceAll("@@@", "<br/> &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;").replaceAll("&sbquo;", ",");
	           return str;
	       }else{
	           return "";
	       }
	   
	   
	   }
}

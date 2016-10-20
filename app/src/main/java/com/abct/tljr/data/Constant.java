package com.abct.tljr.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.dialog.SearchDialog;
import com.abct.tljr.model.OneFenZu;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.model.User;
import com.qh.common.util.DataCleanManager;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Base64;

public class Constant {
	public static DecimalFormat df = new DecimalFormat("0.00");
	public static DecimalFormat df3 = new DecimalFormat("0.000");
	public static DecimalFormat dfNumber = new DecimalFormat("###,##0");
	public static boolean isRelease = false;// 是否为发布版
	public static String device_token = "";
	public static boolean isThirdLogin = false;
	public static String GUKEY = "GU";
	public static String STARTKEY = "FirstStart";
	public static String LIULIANGKEY = "liuliangkey";
	public static String LIULIANGTIMEKEY = "liuliangtimekey";
	public static String TBGPDMTIMEKEY = "tbgpdmtimekey";
	public static String TBGGXXTIMEKEY = "tbggxxtimekey";
	public static String TBXTSSTIMEKEY = "tbxtsstimekey";
	public static String FLUSHKEY = "flushrate";
	public static String NEWSFONTSIZE = "newsfontsize";
	public static int ZXFontSize = 15;
	public static int[] FontSizes = { 22, 18, 14, 12, 10 };
	public static String[] FontSizeNames = { "超大", "大", "正常", "小", "细小" };
	public static int FlushTime = 60;
	public static int[] FlushTimes = { 0, 5, 15, 30, 60 };
	public static String[] FlushTimeNames = { "不刷新", "5秒", "15秒", "30秒", "60秒" };
	public static int guVersion;
	public static final SharedPreferences preference = PreferenceManager
			.getDefaultSharedPreferences(MyApplication.getInstance());
	public static String netType = "";
	public static String appVersion = "2.0";
	public static String packageName = "";
	public static long Liuliang = 0;
	public static String lastSynTime = "";

	public static Map<String, ArrayList<OneGu>> guMap = new HashMap<String, ArrayList<OneGu>>();
	public static String[] names = { "国内指数", "其他指数", "股指期货" };
	public static int readNums = 0;

	public static boolean noPictureMode = false; // 无图模式
	public static int isNewsGuideToast = 0; // 是否弹出过新闻-界面的引导提示 , 0-未打开
											// 1-新闻界面引导已打开 2-新闻内页引导已打开
	public static int clickCount;// 积分
	public static Map<String, JSONObject> marketInfo;

	public static void addClickCount() {
		clickCount++;
	}

	public static void init() {

		// preference = PreferenceManager
		// .getDefaultSharedPreferences(MyApplication.getInstance());
		// preference = app.getSharedPreferences("TLJR",
		// Application.MODE_PRIVATE);
		netType = Util.getAPNType(MyApplication.getInstance());
		isRelease = (Util.getMeteDate("isRelease", MyApplication.getInstance()).equals("release"));
		if (isRelease) {
			LogUtil.setLevel(LogUtil.Nothing);
		}
		if (preference.getBoolean(STARTKEY, true)) {
			DataCleanManager.cleanCustomCache(Util.sdPath);
			preference.edit().clear().commit();
			preference.edit().putBoolean(STARTKEY, false).commit();
			DataCleanManager.cleanApplicationData(MyApplication.getInstance());
			StartActivity.imageLoader.clearMemoryCache();
		}
		if (Constant.marketInfo == null) {
			Constant.getMarketInfo();
		}
		Util.init();
	}

	/**
	 * 初始化分组
	 */
	public static void initFenZus(final Context app, final Complete complete) {
		readNums = 0;
		ZiXuanUtil.readStorage(complete, app);
		dataRead(GUKEY, new readComplete() {
			@Override
			public void read(Object object) {
				// TODO Auto-generated method stub
				guMap = (Map) object;
				if (guMap == null)
					guMap = new HashMap<String, ArrayList<OneGu>>();
				readNums++;
				if (readNums == 3) {
					complete.complete();
				}
			}
		});
	}

	public static HashMap<String, OneGu> getGuMap() {
		HashMap<String, OneGu> map = new HashMap<String, OneGu>();
		Iterator iter = Constant.guMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			ArrayList<OneGu> list = (ArrayList<OneGu>) entry.getValue();
			for (int i = 0; i < list.size(); i++) {
				OneGu gu = list.get(i);
				map.put(gu.getKey(), gu);
			}
		}
		return map;
	}

	public static void sortByTime(ArrayList<OneFenZu> zu) {
		for (int i = zu.size() - 1; i >= 0; i--) {
			for (int j = 0; j < i; j++) {
				if (zu.get(j).getTime() > zu.get(j + 1).getTime()) {
					OneFenZu temp = zu.get(j);
					zu.set(j, zu.get(j + 1));
					zu.set(j + 1, temp);
				}
			}
		}
	}

	public static void LLStorage() {
		MyApplication.getInstance().getUidByte();
		long now = preference.getLong(LIULIANGKEY, Liuliang);
		preference.edit().putLong(LIULIANGKEY, (now + Liuliang)).commit();
	}

	public static void dataStorage() throws Throwable {
		Editor editor = preference.edit();
		editor.putString(ZiXuanUtil.DATAKEY, getStringByObject(ZiXuanUtil.defaultfzMap));
		editor.putString(ZiXuanUtil.ACTIONSKEY, ZiXuanUtil.ACTIONS);
		editor.putString(ZiXuanUtil.ACTIONS, getStringByObject(ZiXuanUtil.actions));
		editor.putString(ZiXuanUtil.NOWFENZUKEY, ZiXuanUtil.nowFenZu);
		editor.putString(SearchDialog.SearchHistoryKey, Constant.getStringByObject(SearchDialog.historyList));
		editor.commit();
		LLStorage();
	}

	public static String getStringByObject(Object object) throws Throwable {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(object);
		String s = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
		objectOutputStream.close();
		return s;
	}

	public static void dataRead(final String key, final readComplete complete) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Object obj = null;
					String mobilesString = preference.getString(key, null);
					if (mobilesString == null) {
						complete.read(obj);
						return;
					}
					byte[] mobileBytes = Base64.decode(mobilesString.getBytes(), Base64.DEFAULT);
					ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
					ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
					obj = objectInputStream.readObject();
					objectInputStream.close();
					complete.read(obj);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					complete.read(null);
				}
			}
		}).start();

	}

	public static interface readComplete {
		public void read(Object object);
	}

	public static void sendToken(User user) {
		NetUtil.sendPost(
				UrlUtil.URL_share, "reqType=pushInfo&id=" + user.getId() + "&userName=" + user.getUserName()
						+ "&nickName=" + user.getNickName() + "&token=" + device_token + "&device=android",
				new NetResult() {

					@Override
					public void result(String arg0) {

					}
				});
	}

	public static void getMarketInfo(final Complete... complete) {
		//String urltest=UrlUtil.Url_apicavacn+"tools/index/0.2/marketinfo";
		NetUtil.sendPost(UrlUtil.Url_apicavacn + "tools/index/0.2/marketinfo", "", new NetResult() {
			@Override
			public void result(String msg) {
				JSONObject object;
				try {
					object = new JSONObject(MyApplication.getInstance().getResources().getString(R.string.marketInfo));
					try {
						JSONObject object1 = new JSONObject(msg);
						if (object1.getInt("code") == 200)
							object = object1;
					} catch (JSONException e) {
						e.printStackTrace();
					}
					marketInfo = new HashMap<String, JSONObject>();
					JSONArray array = object.getJSONArray("result");
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						marketInfo.put(obj.getString("market").toLowerCase(), obj);
					}
					for (Complete complete2 : complete) {
						complete2.complete();
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	public static OneGu cloneGu(OneGu g) {
		if (g == null) {
			return null;
		}
		OneGu gu = new OneGu();
		gu.setName(g.getName());
		gu.setCode(g.getCode());
		gu.setMarket(g.getMarket());
		gu.setPyName(g.getPyName());
		gu.setKey(g.getKey());
		gu.setMarketCode(g.getMarketCode());
		return gu;
	}
}

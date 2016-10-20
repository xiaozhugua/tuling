package com.abct.tljr.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.hangqing.HangQing;
import com.abct.tljr.hangqing.util.ParseJson;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.news.HuanQiuShiShi;
import com.abct.tljr.news.adapter.NewsAdapter;
import com.abct.tljr.news.bean.Tag;
import com.abct.tljr.news.channel.bean.ChannelItem;
import com.abct.tljr.news.fragment.PictureNewsFragment;
import com.abct.tljr.ui.activity.StartActivity;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.util.FileUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.PreferenceUtils;
import com.qh.common.util.UrlUtil;

import android.content.SharedPreferences.Editor;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * 获取数据类
 * @author Administrator
 * 
 */
public class InitData {
	public static Map<String, OneGu> guMap = new ConcurrentHashMap<String, OneGu>();
	public static Map<String, OneGu> guKeyMap = new ConcurrentHashMap<String, OneGu>();
	private StartActivity activity;

	public InitData(StartActivity activity) {
		this.activity = activity;
		initPerfrence();
	}

	public InitData() {

	}

	private void initPerfrence() {
		refreshByHangQing(activity);
		Constant.initFenZus(activity, new Complete() {
			@Override
			public void complete() {
				activity.showMessage("读取本地数据完成");
				initNewsType(new Complete() {
					@Override
					public void complete() {
						// refreshByHangQing(activity);
						activity.showMessage("读取新闻类型完成");
						Realm realm;
						try {
							realm = Realm.getDefaultInstance();
						} catch (RealmMigrationNeededException e) {
							RealmConfiguration config = new RealmConfiguration.Builder(MyApplication.getInstance())
									.deleteRealmIfMigrationNeeded().build();
							Realm.setDefaultConfiguration(config);
							realm = Realm.getDefaultInstance();
						}
						LogUtil.e("个数", realm.where(OneGu.class).count() + "");
						if (realm.where(OneGu.class).count() > 0) {
							activity.showMessage("读取股票列表完成");
							activity.handler.sendEmptyMessage(1);
						} else {
							Constant.preference.edit().putInt("VERSION", 0).commit();
						}
						realm.close();
						initGuMap(new Complete() {
							@Override
							public void complete() {
								Realm realm = Realm.getDefaultInstance();
								if (realm.where(OneGu.class).count() == 0) {
									activity.showMessage("读取股票列表失败，请检查网络重试");
								} else {
									activity.showMessage("读取股票列表完成");
									activity.handler.sendEmptyMessage(1);
								}
								realm.close();
							}
						});
					}
				});
			}
		});
	}

	/**
	 * 远程获取股票代码列表
	 */
	public static void initGuMap(final Complete complete) {
		Constant.guVersion = Constant.preference.getInt("VERSION", 0);
		LogUtil.e("当前股票版本", Constant.guVersion + "");
		NetUtil.sendGet(UrlUtil.URL_allgp, "v=" + Constant.guVersion, new NetResult() {
			@Override
			public void result(String msg) {
				try {
					JSONObject obj = new JSONObject(msg);
					int ver = obj.getInt("version");
					if (Constant.guVersion != ver) {
						LogUtil.e("版本不一样，更新服务器股票版本", ver + "");
						Editor editor = Constant.preference.edit();
						editor.putInt("VERSION", ver);
						editor.commit();
						addSearchView(obj.getJSONArray("info"), complete);
					} else {
						Realm realm = Realm.getDefaultInstance();
						if (realm.where(OneGu.class).count() == 0) {
							realm.close();
							LogUtil.e("版本一样，更新服务器股票版本", ver + "");
							addSearchView(obj.getJSONArray("info"), complete);
						} else {
							realm.close();
							complete.complete();
						}
					}
				} catch (org.json.JSONException e) {
					e.printStackTrace();
					complete.complete();
				}
			}
		});
	}

	private static void addSearchView(final JSONArray array, final Complete complete) throws JSONException {
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					if (array == null || array.length() == 0) {
						complete.complete();
						return;
					}
					Realm myRealm = Realm.getDefaultInstance();
					myRealm.beginTransaction();
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.getJSONObject(i);
						OneGu gu = new OneGu();
						gu.setCode(object.getString("code"));
						gu.setName(object.getString("name"));
						gu.setPyName(object.getString("pyName").toLowerCase());
						gu.setMarket(object.getString("class"));
						gu.setKey(object.getString("key"));
						gu.setMarketCode(gu.getMarket() + gu.getCode());
						myRealm.copyToRealmOrUpdate(gu);
					}
					myRealm.commitTransaction();
					if (complete != null)
						complete.complete();
				} catch (JSONException e) {
					e.printStackTrace();
					complete.complete();
				}

			}
		});
	}

	/**
	 * 远程获取新闻类型
	 */
	private void initNewsType(final Complete complete) {

		String newsChannel = PreferenceUtils.getInstance().getNewsChannelList();
		LogUtil.i("testttt", newsChannel);
		
		
		newsChannel = returnRight(newsChannel);

		
		
		
		initDate(newsChannel, new Complete() {
			@Override
			public void complete() {
				if (Constant.netType.equals("未知")) {
					complete.complete();
				} else {
					String pId = PreferenceUtils.getInstance().preferences.getString("UserId", "0");
					String url = UrlUtil.URL_new + "api/utc/get";
					String params = "platform="+HuanQiuShiShi.platform+"&uid=" + pId+"&version="+HuanQiuShiShi.version;
					LogUtil.i("initData", url + "?" + params);
					NetUtil.sendGet(url, params, new NetResult() {
						@Override
						public void result(String msg) {

							try {
								JSONObject obj = new JSONObject(msg);
								String status = obj.getString("status");
								if (status != null) {
									if (status.equals("1")) {
										String right = returnRight(msg);
										
										
										PreferenceUtils.getInstance().putNewsChannelList(right);
										initDate(right, complete);
									} else {
										LogUtil.i("initData", "获取用户配置失败 ");
										complete.complete();
									}
								} else {
									complete.complete();
								}
							} catch (JSONException e) {
								e.printStackTrace();
								complete.complete();
							}
							LogUtil.i("initData", msg);
						}
					}, 3000);
				}

			}
		});

	}

	private static String returnRight(String newsChannel) {
		JSONObject obj;
		try {
			obj = new JSONObject(newsChannel);
			JSONObject jsonObject = obj.getJSONObject("joData");
			JSONArray newChannel_Selected = jsonObject.getJSONArray("selected");
			
			
			
			if(newChannel_Selected.length()>0){
				JSONObject rs = newChannel_Selected.getJSONObject(0);
				if(rs.optString(("name"),"wrong").equals("wrong")){
					newsChannel = FileUtil.getFromAssets("NewsChannel.properties");
					PreferenceUtils.getInstance().putNewsChannelList(newsChannel);
				}
				
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return newsChannel;
	}

	private static void initDate(String msg, Complete complete) {
		try {
			JSONObject obj = new JSONObject(msg);
			JSONObject jsonObject = obj.getJSONObject("joData");
		 
			JSONArray tags = jsonObject.getJSONArray("tags");
			for (int i = 0; i < tags.length(); i++) {
				JSONObject rs = tags.getJSONObject(i);

				Tag tag = new Tag();
				tag.setId(rs.getString("id"));
				tag.setColor(rs.getString("color"));
				tag.setText(rs.getString("text"));
				NewsAdapter.tagsMap.put(rs.getInt("id"), tag);
				LogUtil.i("initTest", rs.getString("text"));
			}
			Realm myRealm;
			try {
				myRealm = Realm.getDefaultInstance();
			} catch (RealmMigrationNeededException e) {
				RealmConfiguration config = new RealmConfiguration.Builder(MyApplication.getInstance())
						.deleteRealmIfMigrationNeeded().build();
				Realm.setDefaultConfiguration(config);
				myRealm = Realm.getDefaultInstance();
			}
			if (myRealm.where(ChannelItem.class).findAll() != null) {
				RealmResults<ChannelItem> results = myRealm.where(ChannelItem.class).findAll();
				if (results != null) {
					myRealm.beginTransaction();
					results.clear();
					myRealm.commitTransaction();
				}
			}
			myRealm.beginTransaction();
			JSONArray newChannel_Selected = jsonObject.getJSONArray("selected");
			for (int w = 0; w < newChannel_Selected.length(); w++) {
				JSONObject rs = newChannel_Selected.getJSONObject(w);
				ChannelItem selected = new ChannelItem();
				selected.setId(w);
				selected.setOrderId(w);
				selected.setName(rs.getString("name"));
				selected.setContentPictureUrl(
						rs.has("contentPictureUrl") ? rs.getString("contentPictureUrl") : "default");
				selected.setSpecies(rs.getString("species"));
				selected.setSelected(1);
				selected.setChannelType(rs.getInt("channelType"));

				myRealm.copyToRealm(selected);
			}
			JSONArray newChannel_unSelected = jsonObject.getJSONArray("inSelected");
			for (int w = 0; w < newChannel_unSelected.length(); w++) {
				JSONObject rs = newChannel_unSelected.getJSONObject(w);
				ChannelItem unSelected = new ChannelItem();
				unSelected.setId(w);
				unSelected.setOrderId(w);
				unSelected.setName(rs.getString("name"));
				unSelected.setContentPictureUrl(
						rs.has("contentPictureUrl") ? rs.getString("contentPictureUrl") : "default");
				unSelected.setSpecies(rs.getString("species"));
				unSelected.setSelected(0);
				unSelected.setChannelType(rs.getInt("channelType"));
				myRealm.copyToRealm(unSelected);
			}
			myRealm.commitTransaction();
			complete.complete();
		} catch (JSONException e) {
			e.printStackTrace();
			complete.complete();
		}

	}

	public static void refreshByUser(final MainActivity act) {
		LogUtil.i("testchannel", "refreshByUser");
		String url = UrlUtil.URL_new + "api/utc/get";
		String pId = Constant.preference.getString("UserId", "0");
		String params = "platform="+HuanQiuShiShi.platform+"&uid=" + pId+"&version="+HuanQiuShiShi.version;

		NetUtil.sendGet(url, params, new NetResult() {
			@Override
			public void result(String msg) {
				   String newsChannel = msg != null ? msg : PreferenceUtils.getInstance().getNewsChannelList();
					String right = returnRight(newsChannel);
			 
				
				
				initDate(right, new Complete() {
					@Override
					public void complete() {
					}
				});
				PreferenceUtils.getInstance().putNewsChannelList(right);
				act.huanQiuShiShi.cleanCollect();
				act.huanQiuShiShi.refreshFragment();
			}
		}, 3000);
	}

	private String HANGQINGKEY = "tljr.hangqingkey";

	public void refreshByHangQing(final StartActivity activity) {
		String defaultData = PreferenceUtils.getInstance().preferences.getString(HANGQINGKEY,
				activity.getResources().getString(R.string.hangqingInfo));
		DealHangQingTab(defaultData);

		String url = UrlUtil.Url_apicavacn+"tools/index/0.2/qlist";
		NetUtil.sendGet(url,"",new NetResult() {
			@Override
			public void result(String msg) {
				LogUtil.e("refreshByHangQing", msg);
				if (!msg.equals("")) {
					DealHangQingTab(msg);
					PreferenceUtils.getInstance().preferences.edit().putString(HANGQINGKEY, msg).commit();
				}
			}
		});
	}

	public void DealHangQingTab(String msg) {
		HangQing.HangQingTab = ParseJson.ParseHangQingTab(msg);
	}

}

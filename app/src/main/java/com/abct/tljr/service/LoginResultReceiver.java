package com.abct.tljr.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.MyApplication;
import com.abct.tljr.chart.ChartActivity;
import com.abct.tljr.data.Constant;
import com.abct.tljr.data.InitData;
import com.abct.tljr.data.ZiXuanUtil;
import com.abct.tljr.dialog.GuessDialog;
import com.abct.tljr.hangqing.database.OneFenZuModel;
import com.abct.tljr.hangqing.database.OneFenZuRealmImpl;
import com.abct.tljr.hangqing.database.ZiXuanGuRealmImpl;
import com.abct.tljr.hangqing.model.ZiXuanOneGu;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.model.EncryptCipher;
import com.abct.tljr.model.OneFenZu;
import com.abct.tljr.model.OneGu;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.login.login.Login;
import com.qh.common.model.User;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.PreferenceUtils;
import com.qh.common.util.UrlUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import io.realm.RealmResults;

/**
 * @author xbw 接收登录或注销信息
 * @version 创建时间：2015-6-2 上午10:29:06
 */
public class LoginResultReceiver extends BroadcastReceiver {
	MainActivity activity;

	private static LoginResultReceiver instance;

	public static LoginResultReceiver getInstance(MainActivity context) {
		if (null == instance) {
			instance = new LoginResultReceiver(context);
			IntentFilter filter = new IntentFilter();
			filter.addAction(Configs.serviceName);
			try {
				context.registerReceiver(instance, filter);
			} catch (Exception e) {
			}
		}
		return instance;
	}

	public static void clearReceiver() {
		instance = null;
	}

	public LoginResultReceiver(MainActivity context) {
		this.activity = context;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtil.e(Configs.serviceName + "onReceive", intent.getStringExtra("type"));
		if (intent.getStringExtra("type").equals("login") && !intent.getStringExtra("msg").equals("")) {
			try {
				loginResult(intent.getStringExtra("msg"));
				checkLoginZiXuanData(activity.mHandler);
				ZiXuanUtil.ACTIONS = User.getUser().getId();
				activity.initUser();
				GuessDialog.login(null);
				sendOnlineStart();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Constant.clickCount = 0;
		} else if (intent.getStringExtra("type").equals("logout")) {
			sendOnlineStop();
			User.setUser(null);
			MyApplication.getInstance().self = User.getUser();
			activity.initUser();
			Configs.preference.edit().putString(Login.TLJR_AUTOLOGIN_TOKEN, "").commit();
			GuessDialog.userId = "";
			activity.mHandler.sendEmptyMessage(103);
			PreferenceUtils.getInstance().preferences.edit().putString("UserId", "0");
		} else if (intent.getStringExtra("type").equals("share")) {
			Toast.makeText(MyApplication.getInstance().getNowActivity(),
					intent.getStringExtra("msg"),Toast.LENGTH_SHORT).show();
		}
	}

	public static String startId = "";

	public static void sendOnlineStart() {
		if (MyApplication.getInstance().self != null) {
			NetUtil.sendGet(UrlUtil.URL_online + "/" + MyApplication.getInstance().self.getId() + "/begin", "",
					new NetResult() {
						@Override
						public void result(String msg) {
							try {
								startId = new JSONObject(msg).getJSONObject("result").getString("recordId");
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
		}
	}

	public static void sendOnlineStop() {
//		String text=UrlUtil.URL_online + "/" + MyApplication.getInstance().self.getId() + "/end/" + startId+
//				"number=" + Constant.clickCount;
		if (MyApplication.getInstance().self != null && startId.length() > 0) {
			NetUtil.sendGet(UrlUtil.URL_online + "/" + MyApplication.getInstance().self.getId() + "/end/" + startId,
					"number=" + Constant.clickCount, new NetResult() {
						@Override
						public void result(String msg) {
							Constant.clickCount = 0;
							LogUtil.e("sendOnlineStop", msg);
						}
					});
		}
	}

	public static void getUserInfo(final Complete... completes) {
		if (User.getUser() != null) {
			NetUtil.sendGet(UrlUtil.URL_online + "/userinfos/" + User.getUser().getId(), "", new NetResult() {
				@Override
				public void result(String msg) {
					LogUtil.e("getUserInfo", msg);
					try {
						User user = User.getUser();
						JSONObject object = new JSONObject(msg).getJSONObject("result");
						JSONObject event = object.getJSONObject("event");
						user.setIntegral(event.getInt("number"));

						JSONObject level = object.getJSONObject("level");
						user.setLevel(level.getInt("level"));
						user.setLevelNeed((float) level.getDouble("need"));
						user.setLevelNeedTotal((float) level.getDouble("needtotal"));
						user.setLevelTotal((float) level.getDouble("total"));
						user.setLevelUnit(level.getString("unit"));

						JSONObject uIdentity = object.getJSONObject("uIdentity");
						user.setIdName(uIdentity.optString("idName"));
						user.setIdNumber(uIdentity.optString("idNumber"));
						user.setIdentityId(uIdentity.optString("identityId"));
						user.setValid(uIdentity.optBoolean("isValid"));
						user.setValidStatus(uIdentity.optInt("status"));

						JSONObject vIdentity = object.getJSONObject("vIdentity");
						user.setCompany(vIdentity.optString("company"));
						user.setIdentityDvId(vIdentity.optString("identityId"));
						user.setValidDv(vIdentity.optBoolean("isValid"));
						user.setIdentityStatus(vIdentity.optInt("status"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (Complete complete : completes) {
						complete.complete();
					}
				}
			});
		} else {
			for (Complete complete : completes) {
				complete.complete();
			}
		}
	}

	public static void updateOnlineEvent(final Complete... completes) {
		if (User.getUser() != null && startId.length() > 0) {
			NetUtil.sendGet(UrlUtil.URL_online + "/" + User.getUser().getId() + "/update/" + startId,
					"number=" + Constant.clickCount, new NetResult() {
						@Override
						public void result(String msg) {
							LogUtil.e("updateOnlineEvent", msg);
							Constant.clickCount = 0;
							try {
								JSONObject obj = new JSONObject(msg).getJSONObject("result");
								startId = obj.getString("recordId");
								if (User.getUser() != null) {
									JSONObject object = obj.getJSONObject("level");
									User.getUser().setLevel(object.getInt("level"));
									User.getUser().setLevelNeed((float) object.getDouble("need"));
									User.getUser().setLevelNeedTotal((float) object.getDouble("needtotal"));
									User.getUser().setLevelTotal((float) object.getDouble("total"));
									User.getUser().setLevelUnit(object.getString("unit"));
									User.getUser().setIntegral(obj.getJSONObject("event").getInt("number"));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							for (Complete complete : completes) {
								complete.complete();
							}
						}
					});
		} else {
			for (Complete complete : completes) {
				complete.complete();
			}
		}
	}

	private void loginResult(String msg) throws JSONException {
		LogUtil.e("loginResult", msg);
		org.json.JSONObject object = new org.json.JSONObject(msg);
		User user = new User();
		User.setUser(user);
		MyApplication.getInstance().self = User.getUser();
		user.setBindeId(object.optString("id"));
		user.setId(object.optString("uid"));
		user.setUserName(object.optString("uname"));
		user.setNickName(object.optString("nickname"));
		user.setArea(object.optString("location"));
		user.setFrom(object.optString("from"));
		user.setLast(object.optJSONObject("last"));
		user.getLast();
		user.setAvatarUrl(object.optString("avatar"));
		Constant.sendToken(user);
		// 记录id key
		Constant.preference.edit().putString("Warnid", user.getId()).commit();
		ChartActivity.getYunZhiToken(object.optString("uid"), object.optString("nickname"), null,
				object.optString("avatar"));
		MyApplication.getInstance().getMainActivity().mHandler.sendEmptyMessage(94);
		try {
			Constant.preference.edit().putString("Warnkey", EncryptCipher.getKey()).commit();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		JSONObject bind = object.getJSONObject("bind");
		if (!bind.has("err")) {
			user.setEmail(bind.optString("email"));	
			user.setPhone(bind.optString("phone"));
			user.setInvail(bind.optBoolean("emailVerify"));
			user.setInvailPhone(bind.optBoolean("phoneVerify"));
		}
		if (!Constant.isThirdLogin) {
			Editor editor = Constant.preference.edit();
			editor.putString("lizai_userName", user.getUserName());
			editor.putString("UserId", object.optString("uid"));
			editor.commit();
			activity.handler.post(new Runnable() {
				@Override
				public void run() {
					InitData.refreshByUser(activity);// 登录后更新用户频道列表
				}
			});
		}
		MyApplication.getInstance().self = User.getUser();
	}

	// 应用启动加载自选组合数据
	public void checkLoginZiXuanData(Handler mHandler) {
		RealmResults<OneFenZuModel> listzu = OneFenZuRealmImpl.SelectAllZuHe();
		ZiXuanUtil.fzMap = new HashMap<String, OneFenZu>();
		OneFenZu mOneFenZu = null;
		OneGu mOneGu = null;
		if (listzu != null && !listzu.isEmpty()) {
			for (OneFenZuModel model : listzu) {
				mOneFenZu = new OneFenZu(model.getName());
				mOneFenZu.setId(model.getZuid());
				mOneFenZu.setTag(model.getBeizu());
				mOneFenZu.setTime(Long.valueOf(model.getTime()));
				RealmResults<ZiXuanOneGu> listGu = ZiXuanGuRealmImpl.SelectOneGu(model.getZuid());
				for (int i = 0; i < listGu.size(); i++) {
					mOneGu = new OneGu(listGu.get(i).getCode(), listGu.get(i).getName(), listGu.get(i).getFirst(),
							listGu.get(i).getP_change(), listGu.get(i).getMarket(), listGu.get(i).getKey(),
							listGu.get(i).getTag(), listGu.get(i).getTime(), listGu.get(i).getTop(),
							listGu.get(i).getId(), mOneFenZu.getId(), listGu.get(i).getZuidkey());
					mOneFenZu.getList().add(mOneGu);
				}
				ZiXuanUtil.fzMap.put(model.getName(), mOneFenZu);
			}
			
			ZiXuanUtil.fzLists = new ArrayList<String>(ZiXuanUtil.fzMap.keySet());
			if (OneFenZuRealmImpl.mRealm != null) {
				OneFenZuRealmImpl.mRealm.close();
				ZiXuanGuRealmImpl.mRealm.close();
				OneFenZuRealmImpl.mRealm = null;
				ZiXuanGuRealmImpl.mRealm = null;
			}
			Message message = new Message();
			message.what = 11;
			mHandler.sendMessage(message);
		} else {
			ZiXuanUtil.sendActions(User.getUser(), activity);
		}

	}

}

package com.abct.tljr.ui.activity;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.data.InitData;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.utils.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.UmengOnlineConfigureListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author xbw
 * @version 创建时间：2015-5-27 上午11:47:21
 */

public class StartActivity extends Activity {
	public static ImageLoader imageLoader;
	public static DisplayImageOptions options;
	private ImageView bj;
	private TextView pro;
	// private ProgressBar bar;
	private ImageView bar;
	private String showScreen = ".TLJR_SHOWSCREEN";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_activity_start);
		MyApplication.getInstance().addActivity(this);
		pro = (TextView) findViewById(R.id.tljr_txt_jindu);
		bar = (ImageView) findViewById(R.id.tljr_pro_qd);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		bj = (ImageView) findViewById(R.id.tljr_img_qdbj);
		try {
			String data = Util.getStringFromFile(showScreen);
			JSONObject obj = new JSONObject(data);
			String url;
			long time = obj.optLong("time");
			if (!obj.getString("fixed").equals("")
					&& Util.getDateOnlyDat(System.currentTimeMillis()).equals(Util.getDateOnlyDat(time))) {
				url = obj.getString("fixed");
			} else {
				JSONArray array = obj.getJSONArray("common");
				url = array.getString(new Random().nextInt(array.length()));
			}
			Util.setImageOnlyDown(url, bj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		bar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pro_move_left_in));
		((TextView) findViewById(R.id.tljr_txt_info)).setText("版本号:" + Constant.appVersion + " 图灵金融控股");
		getUrl();
		loadOnlineCfg();
		new Thread(new Runnable() {
			@Override
			public void run() {
				init();
				new InitData(StartActivity.this);
				if (Constant.netType.equals("WIFI")) {
					try {
						File f = new File(Util.filePath + "/PDF.apk");
						if (!f.exists()) {
							Util.downLoadFile(UrlUtil.aliyun + "/apk/PDF.apk", Util.filePath, "PDF.apk",
									new Complete() {
								@Override
								public void complete() {
									LogUtil.e("PDF", "阅读器下载完成");
								}
							});
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void init() {
		Constant.init();
		// 友盟增量更新
		UmengUpdateAgent.setUpdateCheckConfig(false);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		// UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
				switch (updateStatus) {
				case 0: // has update
					UmengUpdateAgent.showUpdateDialog(getApplicationContext(), updateInfo);
					break;
				case 1: // has no update
					showToast("已是最新版本");
					break;
				case 2: // none wifi
					showToast("没有wifi连接， 只在wifi下更新");
					break;
				case 3: // time out
					showToast("超时");
					break;

				}
			}

		});
		// 友盟数据统计
		MobclickAgent.updateOnlineConfig(this);
	}

	private void getUrl() {
		NetUtil.sendPost(UrlUtil.URL_screen, new NetResult() {
			@Override
			public void result(String msg) {
				try {
					JSONObject object = new JSONObject(msg);
					if (object.getInt("status") == 1) {
						JSONObject obj = object.getJSONObject("result");
						try {
							JSONArray array = obj.getJSONArray("common");
							for (int i = 0; i < array.length(); i++) {
								Util.onlyDownImage(array.getString(i));
							}
							obj.put("time", System.currentTimeMillis());
							byte[] b = obj.toString().getBytes();
							Util.writeFileToFile(showScreen, b, b.length);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void showMessage(String msg) {
		Message message = new Message();
		message.obj = msg;
		handler.sendMessage(message);
	}

	public void showToast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				bar.clearAnimation();
				bar.setX(0);
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						StartActivity.this.finish();
					}
				}, 500);
				Intent intent = new Intent();
				intent.setClass(StartActivity.this, MainActivity.class);
				startActivity(intent);
				break;
			default:
				pro.setText(msg.obj.toString());
				// Toast.makeText(StartActivity.this, msg.obj.toString(),
				// Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};

	public static void loadOnlineCfg() {
		String _aboutUsUrl = MobclickAgent.getConfigParams(MyApplication.getInstance(), "aboutUsUrl");
		if (_aboutUsUrl.length() > 0) {
			UrlUtil.aboutUsUrl = _aboutUsUrl;
		}
		MobclickAgent.setOnlineConfigureListener(new UmengOnlineConfigureListener() {
			@Override
			public void onDataReceived(JSONObject arg0) {
				if (arg0 != null) {
					UrlUtil.aboutUsUrl = arg0.optString("aboutUsUrl").length() == 0
							? "http://42.120.45.171:8008/weiXin/aboutUs.html" : arg0.optString("aboutUsUrl");
				}
			}
		});
	}
}

package com.abct.tljr.shouye;

import io.realm.Realm;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.data.ZiXuanUtil;
import com.abct.tljr.dialog.PromptDialog;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.model.OneFenZu;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.model.Options;
import com.abct.tljr.model.ZhongchouBean;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.ui.fragments.zhiyanFragment.model.ZhiYanLeftBean;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.DownloadProUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.BwManager;
import com.qh.common.login.Configs;
import com.qh.common.login.util.ShareContent;
import com.qh.common.model.TitleBar;
import com.qh.common.model.User;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

/**
 * @author xbw
 * @version 创建时间：2015年12月1日 下午2:01:56
 */
public class FerdlsActivity extends BaseActivity implements OnClickListener {
	private FerdlsActivity activity;
	private JSONObject object;
	private String market, code, name;
	private RadioGroup radioGroup;
	private ViewPager viewPager;
	private TitleBar titleBar;
	private ArrayList<View> list = new ArrayList<View>();
	private HashMap<String, String> data = new HashMap<String, String>();
	private JSONObject userOpers;// 看/赞/关注人数
	private JSONObject isOperateds;// 自己是否看/赞/关注
	private RadioButton ralike;
	private TextView care, likenum;
	private String id;
	private ZhongchouBean bean = null;
	private int item = 0;
	private int itemposition = 0;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BwManager.getInstance().initShare(this);
		setContentView(R.layout.tljr_activity_ferdls);
		id = getIntent().getStringExtra("id");
		item = getIntent().getIntExtra("item", 0);
		itemposition = getIntent().getIntExtra("position", -1);
		if (itemposition != -1) {
			bean = MyApplication.getInstance().getMainActivity().zhiyanFragment.mZhiYanFinishView.listLeftBeans
					.get(item).getFinishViewItemShow().listBean
					.get(itemposition);
		} else {
			((RelativeLayout) findViewById(R.id.frelds_place))
					.setVisibility(View.GONE);
		}
		activity = this;
		LogUtil.e("FerdlsInfo", getIntent().getStringExtra("info"));
		try {
			object = new JSONObject(getIntent().getStringExtra("info"));
		} catch (JSONException e) {
			e.printStackTrace();
			showToast("获取信息失败!");
			finish();
		}
		findViewById(R.id.info).setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.add).setOnClickListener(this);
		findViewById(R.id.like).setOnClickListener(this);
		findViewById(R.id.save).setOnClickListener(this);
		findViewById(R.id.get).setOnClickListener(this);
		care = (TextView) findViewById(R.id.care);
		care.setText("+关注");
		care.setOnClickListener(this);
		ralike = (RadioButton) findViewById(R.id.ralike);
		likenum = (TextView) findViewById(R.id.likenum);
		market = object.optString("market");
		code = object.optString("code");
		name = object.optString("name");
		((TextView) findViewById(R.id.name)).setText(object.optString("name"));
		((TextView) findViewById(R.id.code)).setText(object
				.optString("marketName") + code);
		StartActivity.imageLoader.displayImage(object.optString("iconUrl"),
				(ImageView) findViewById(R.id.icon), StartActivity.options);
		radioGroup = (RadioGroup) findViewById(R.id.radio);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		initInfo();
		initUi();
		sendEvent(1, (TextView) findViewById(R.id.seenum));
	};

	private void initUi() {
		getData(new Complete() {
			@Override
			public void complete() {
				((TextView) findViewById(R.id.seenum)).setText(userOpers
						.optString("sees"));
				((TextView) findViewById(R.id.likenum)).setText(userOpers
						.optString("likes"));
				if (MyApplication.getInstance().self != null
						&& isOperateds != null) {
					ralike.setChecked(isOperateds.optBoolean("ferdls_like_") ? true
							: false);
					if (bean != null) {
						care.setText(bean.isIsfocus() ? "已关注" : "+关注");
					}
				}
				try {
					JSONArray array = object.getJSONArray("config");
					int count = 0;
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						if (!obj.getBoolean("isEmpty")) {
							count++;
						}
					}
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						if (!obj.getBoolean("isEmpty")) {
							// RadioButton btn = new RadioButton(
							// FerdlsActivity.this);
							RadioButton btn = (RadioButton) View.inflate(
									FerdlsActivity.this, R.layout.radiobutton,
									null);
							btn.setLayoutParams(new LayoutParams(Util.WIDTH
									/ count, LayoutParams.MATCH_PARENT));
							btn.setTag(obj.getString("key"));
							btn.setText(obj.getString("name") + "\n"
									+ obj.getString("cName"));
							// btn.setTextSize(14);
							// btn.setTextColor(getResources().getColor(
							// R.color.tljr_txt_checkred));
							// btn.setButtonDrawable(android.R.color.transparent);
							// btn.setGravity(Gravity.CENTER);
							radioGroup.addView(btn);
							JSONArray arr = new JSONArray(data.get(obj
									.getString("name")));
							if (arr != null && arr.length() > 0) {
								ListView m = new ListView(FerdlsActivity.this);
								m.setLayoutParams(new LayoutParams(
										LayoutParams.FILL_PARENT,
										LayoutParams.FILL_PARENT));
								m.setAdapter(new MyAdapter(arr, obj
										.getString("summaryKey")));
								list.add(m);
								// list.add(getLayout(arr,
								// obj.getString("summaryKey")));
							}
						}
					}
					if (radioGroup.getChildCount() > 0)
						titleBar = new TitleBar(FerdlsActivity.this, viewPager,
								radioGroup, findViewById(R.id.cursor));
					initPageAdapter();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}

	@SuppressWarnings("deprecation")
	private void initPageAdapter() {
		viewPager.setAdapter(new PagerAdapter() {
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(list.get(position));
				return list.get(position);
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(list.get(position));
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return list.size();
			}
		});
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				Constant.addClickCount();
				titleBar.tabChangedArrow(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	private View getLayout(JSONArray jsonarray, String key) {
		ScrollView view = new ScrollView(this);
		LinearLayout layout = new LinearLayout(FerdlsActivity.this);
		view.addView(layout);
		layout.setOrientation(LinearLayout.VERTICAL);
		for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject obj = jsonarray.optJSONObject(i);
			View v = View.inflate(FerdlsActivity.this,
					R.layout.tljr_item_ferdlsinfo, null);
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(
					AbsListView.LayoutParams.FILL_PARENT,
					AbsListView.LayoutParams.WRAP_CONTENT);
			v.setLayoutParams(params);
			((TextView) v.findViewById(R.id.txt)).setText(obj.optString(key));
			((TextView) v.findViewById(R.id.name)).setText(obj
					.optString("author"));
			((TextView) v.findViewById(R.id.time)).setText(Util.getDate(obj
					.optLong("time")));
			try {
				LinearLayout files = (LinearLayout) v.findViewById(R.id.files);
				JSONArray array = obj.getJSONArray("files");
				for (int j = 0; j < array.length(); j++) {
					files.addView(getOneFile(array.getJSONObject(j)));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			layout.addView(v);
		}
		return view;
	}

	private class MyAdapter extends BaseAdapter {
		private JSONArray array;
		private String key;

		public MyAdapter(JSONArray array, String key) {
			this.array = array;
			this.key = key;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			JSONObject obj = array.optJSONObject(position);
			if (v == null) {
				v = View.inflate(FerdlsActivity.this,
						R.layout.tljr_item_ferdlsinfo, null);
				AbsListView.LayoutParams params = new AbsListView.LayoutParams(
						AbsListView.LayoutParams.FILL_PARENT,
						AbsListView.LayoutParams.WRAP_CONTENT);
				v.setLayoutParams(params);
			}
			((TextView) v.findViewById(R.id.txt)).setText(obj.optString(key));
			if (obj.has("website") && obj.optString("website").length() > 0) {
				((TextView) v.findViewById(R.id.web))
						.setVisibility(View.VISIBLE);
				((TextView) v.findViewById(R.id.web)).setText("官方网址:"
						+ obj.optString("website"));
			} else {
				((TextView) v.findViewById(R.id.web)).setVisibility(View.GONE);
				((TextView) v.findViewById(R.id.web)).setText(obj
						.optString("website"));
			}
			((TextView) v.findViewById(R.id.name)).setText(obj
					.optString("author"));
			((TextView) v.findViewById(R.id.time)).setText(Util.getDate(obj
					.optLong("time")));
			if (!obj.optString("avatar").equals(""))
				StartActivity.imageLoader.displayImage(obj.optString("avatar"),
						(ImageView) v.findViewById(R.id.avatar),
						Options.getCircleListOptions());
			try {
				LinearLayout files = (LinearLayout) v.findViewById(R.id.files);
				files.removeAllViews();
				JSONArray array = obj.getJSONArray("files");
				for (int i = 0; i < array.length(); i++) {
					files.addView(getOneFile(array.getJSONObject(i)));
				}
			} catch (JSONException e) {
			}
			return v;
		}

		@Override
		public int getCount() {
			return array.length();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}

	private View getOneFile(JSONObject object) {
		View v = View.inflate(activity, R.layout.tljr_item_ferdlsfile, null);
		((TextView) v.findViewById(R.id.page)).setText(object
				.optString("rpages"));
		LogUtil.e("FileName:", object.optString("agency"));
		((TextView) v.findViewById(R.id.name)).setText(object
				.optString("agency"));
		((TextView) v.findViewById(R.id.author)).setText(object
				.optString("uploader"));
		((TextView) v.findViewById(R.id.time)).setText(Util
				.getDateOnlyDat((long) object.optDouble("time")));
		((TextView) v.findViewById(R.id.state)).setText(object
				.optString("stockRate"));
		((TextView) v.findViewById(R.id.state)).setTextColor(object.optString(
				"stockRate").equals("卖出") ? ColorUtil.green : ColorUtil.red);
		((TextView) v.findViewById(R.id.txt))
				.setText(object.optString("title"));
		final String name = object.optString("_id") + ".pdf";
		final String url = object.optString("rpath");
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String filename = "." + name + ".pdf";
				File file = new File(Util.fileDirPath + "/" + filename);
				if (file.exists()) {
					openPdf(file);
				} else {
					downLoadFile(url, "附件", filename, new Complete() {
						@Override
						public void complete() {
							openPdf(new File(Util.fileDirPath + "/" + filename));
						}
					});
				}
			}
		});
		return v;
	}

	private void openPdf(File file) {
		try {
			Intent intent = new Intent("android.intent.action.VIEW");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Uri uri = Uri.fromFile(file);
			intent.setDataAndType(uri, "application/pdf");// 文档格式
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			showToast("未找到打开方式");
		}
	}

	private void downLoadFile(final String url, final String title,
			final String apkName, final Complete complete) {
		if (!Constant.netType.equals("WIFI")) {
			new AlertDialog.Builder(this)
					.setTitle("图灵金融")
					.setMessage("当前为" + Constant.netType + "网络，下载会消耗流量，确认下载？")
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									DownloadProUtil.showProgressDlg(title, url,
											apkName, activity, true, complete);
								}
							}).setNegativeButton("否", null).show();

		} else {
			DownloadProUtil.showProgressDlg(title, url, apkName, activity,
					true, complete);
		}

	}

	private void getData(final Complete complete) {
		ProgressDlgUtil.showProgressDlg("", this);
		String params = "code=" + code + "&oper=[0]";
		if (MyApplication.getInstance().self != null)
			params += ("&uid=" + MyApplication.getInstance().self.getId());
		NetUtil.sendPost(UrlUtil.URL_ferdlsinfo, params, new NetResult() {
			@Override
			public void result(final String msg) {
				post(new Runnable() {
					@Override
					public void run() {
						try {
							JSONObject jsonObject = new JSONObject(msg);
							if (jsonObject.getInt("code") == 1) {
								JSONObject object = jsonObject
										.getJSONObject("data");
								Iterator<String> keys = object.keys();
								while (keys.hasNext()) {
									String key = keys.next();
									data.put(key, object.getString(key));
								}
								userOpers = object.optJSONObject("userOpers");
								isOperateds = object
										.optJSONObject("isOperateds");
								complete.complete();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							showToast("获取数据失败");
						}
					}
				});
				ProgressDlgUtil.stopProgressDlg();
			}
		});
	}

	private void initInfo() {
		Util.getRealInfo("market=" + market + "&code=" + code, new NetResult() {
			@Override
			public void result(final String msg) {
				post(new Runnable() {
					@Override
					public void run() {
						try {
							org.json.JSONObject object = new org.json.JSONObject(
									msg);
							if (object.getInt("code") == 1) {
								final org.json.JSONArray array = object
										.getJSONArray("result");
								((TextView) findViewById(R.id.now))
										.setText(array.optString(0).replace(
												"null", "--"));
								((TextView) findViewById(R.id.change))
										.setText((array.optDouble(9) > 0 ? "+"
												: "")
												+ array.optString(8).replace(
														"null", "0.0"));// 涨跌
								((TextView) findViewById(R.id.changep))
										.setText((array.optDouble(9) > 0 ? "+"
												: "")
												+ array.optString(9).replace(
														"null", "0.0") + "%");// 涨跌幅
								if (array.optDouble(9) > 0) {
									findViewById(R.id.grp)
											.setBackground(
													getResources()
															.getDrawable(
																	R.drawable.img_zhangbeijing));
								} else {
									findViewById(R.id.grp).setBackground(
											getResources().getDrawable(
													R.drawable.img_diebeijing));
								}
							}
						} catch (org.json.JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		});
	}

	private boolean isLogin = false;
	private boolean isSend = false;

	// {"see":0} {"like":1} {"cared":1}
	private void sendEvent(final int event, final TextView tv) {
		if (MyApplication.getInstance().self == null) {
			if (event == 1) {
				return;
			}
			login();
			isLogin = true;
			return;
		}
		if (isSend)
			return;
		isSend = true;
		ProgressDlgUtil.showProgressDlg("", this);
		NetUtil.sendPost(UrlUtil.URL_ferdlsevent, "oper=[" + event + "]&code="
				+ code + "&uid=" + MyApplication.getInstance().self.getId(),
				new NetResult() {
					@Override
					public void result(final String msg) {
						isSend = false;
						LogUtil.e("sendEventResult", msg);
						ProgressDlgUtil.stopProgressDlg();
						post(new Runnable() {
							@Override
							public void run() {
								try {
									JSONObject object = new JSONObject(msg);
									boolean success = object.getInt(object
											.keys().next()) == 1;
									if (event != 1) {
										String actions = (event > 3 ? "取消" : "")
												+ (event == 2 || event == 4 ? "点赞": "关注")
												+ (success ? "成功" : "失败");
										showToast(actions);
										new PromptDialog(activity, actions,
												new Complete() {
													@Override
													public void complete() {
													}
												}).showNoCancel();
									}
									if (success && tv != null) {
										if (event == 4) {
											if (isOperateds != null
													&& isOperateds
															.optBoolean("ferdls_like_")) {
												tv.setText((userOpers
														.optInt("likes") - 1)
														+ "");
												ralike.setChecked(false);
											} else {
												tv.setText((userOpers
														.optInt("likes")) + "");
												ralike.setChecked(false);
											}
										} else if (event == 2) {
											if (isOperateds != null
													&& isOperateds
															.optBoolean("ferdls_like_")) {
												tv.setText((userOpers
														.optInt("likes")) + "");
												ralike.setChecked(true);
											} else {
												tv.setText((userOpers
														.optInt("likes") + 1)
														+ "");
												ralike.setChecked(true);
											}
										} else if (event == 1) {
											if (isOperateds != null
													&& !isOperateds
															.optBoolean("ferdls_see_")) {
												tv.setText((userOpers
														.optInt("sees") + 1)
														+ "");
											}
										} else if (event == 3) {
											tv.setText("已关注");
											addGuanZhu();
										} else if (event == 5) {
											tv.setText("+关注");
											deleteGuanZhu();
										}
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}
				});
	}

	private void focus(final TextView v, final ZhongchouBean bean) {
		String a = v.getText().toString();
		int type = a.contains("已") ? 0 : 1;
		ProgressDlgUtil.showProgressDlg("", (Activity) FerdlsActivity.this);
		NetUtil.sendPost(UrlUtil.URL_ZR + "crowd/focus", "id=" + bean.getId()
				+ "&uid=" + User.getUser().getId() + "&token=" + Configs.token
				+ "&type=" + type, new NetResult() {
			@Override
			public void result(String msg) {
				ProgressDlgUtil.stopProgressDlg();
				try {

					JSONObject jsonObject = new JSONObject(msg);
					if (jsonObject.getInt("status") == 1) {
						String a = v.getText().toString();
						int type = a.contains("已") ? 0 : 1;
						if (type == 0) {
							v.setText("+关注");
							deleteGuanZhu();
						} else {
							v.setText("已关注");
							addGuanZhu();
						}
					} else {
					}
				} catch (Exception e) {

				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.info:
			Intent intent = new Intent(this, OneGuActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("code", code);
			bundle.putString("market", market);
			bundle.putString("name", name);
			bundle.putString("key", market + code);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case R.id.add:
			DialogShow("", FerdlsActivity.this, name, market + code);
			break;
		case R.id.like:
			if (ralike.isChecked()) {
				sendEvent(4, likenum);
			} else {
				sendEvent(2, likenum);
			}
			break;
		case R.id.care:
			if (care.getText().toString().equals("已关注")) {
				// sendEvent(5, care);
				focus(care, bean);
			} else {
				// sendEvent(3, care);
				focus(care, bean);
			}
			break;
		case R.id.save:
			if (id == null) {
				showToast("获取评论失败，请稍后重试");
			} else {
				startActivity(new Intent(this, SpeakActiviy.class).putExtra(
						"id", id));
			}
			break;
		case R.id.get:
			ShareContent.actionUrl = UrlUtil.Url_tuling
					+ "/FerdlsShare/ferdlsShare.html?code=" + code + "&market="
					+ market;
			ShareContent.title = "图灵智研帮我研究了\"" + name + "(" + code + ")"
					+ "\"分享给大家看看";
			ShareContent.content = "";
			showPopupView();
			break;
		default:
			break;
		}
	}

	private void save(String msg) {
		if (msg.length() < 10) {
			showToast("输入字数过短，请重新输入！");
			return;
		}
		String parm = "code=" + code + "&uid="
				+ MyApplication.getInstance().self.getId() + "&module="
				+ radioGroup.getChildAt(viewPager.getCurrentItem()).getTag()
				+ "&content=" + msg + "&author="
				+ MyApplication.getInstance().self.getNickName() + "&time="
				+ System.currentTimeMillis();
		LogUtil.e("sendsave", parm);
		ProgressDlgUtil.showProgressDlg("", this);
		NetUtil.sendPost(UrlUtil.Url_235
				+ "8080/StockDataService/rest/m_ferdls/save", parm,
				new NetResult() {
					@Override
					public void result(final String msg) {
						// msg:{"code":"1","msg":"已经为你记录你的研究，待官方审核"}
						// msg:{"code":"-1","msg":"找不到股票"}
						LogUtil.e("save", msg);
						ProgressDlgUtil.stopProgressDlg();
						post(new Runnable() {
							@Override
							public void run() {
								try {
									JSONObject object = new JSONObject(msg);
									showToast(object.optString("msg"));
								} catch (JSONException e) {
									e.printStackTrace();
									showToast("提交失败，请重试!");
								}
							}
						});
					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isLogin && MyApplication.getInstance().self != null)
			getData(new Complete() {
				@Override
				public void complete() {
					isLogin = false;
					((TextView) findViewById(R.id.seenum)).setText(userOpers.optString("sees"));
					((TextView) findViewById(R.id.likenum)).setText(userOpers.optString("likes"));
					if (MyApplication.getInstance().self != null&& isOperateds != null) {
						ralike.setChecked(isOperateds.optBoolean("ferdls_like_") ? true : false);
						care.setText(isOperateds.optBoolean("ferdls_cared_") ? "已关注":"+关注");
					}
				}
			});
	}

	// 添加股票弹窗显示
	public void DialogShow(final String zuname, final Context context,
			String guName, final String guKey) {
		// 获取所有的组
		ArrayList<OneFenZu> list = new ArrayList<OneFenZu>(
				ZiXuanUtil.fzMap.values());
		boolean dialogstatus = true;
		// 过滤添加过分组
		ArrayList<OneFenZu> templist = new ArrayList<OneFenZu>();
		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.get(i).getList().size(); j++) {
				if (list.get(i).getList().get(j).getName() == null) {
					Realm realm = Realm.getDefaultInstance();
					OneGu gu = Constant.cloneGu(realm
							.where(OneGu.class)
							.equalTo(
									"marketCode",
									list.get(i).getList().get(j).getMarket()
											+ list.get(i).getList().get(j)
													.getCode()).findFirst());
					list.get(i).getList().get(j).setName(gu.getName());
				}
				if (list.get(i).getList().get(j).getName() != null
						&& list.get(i).getList().get(j).getName()
								.equals(guName)) {
					dialogstatus = false;
				}
			}
			if (dialogstatus) {
				templist.add(list.get(i));
			} else {
				dialogstatus = true;
			}
		}
		// 初始个股数据
		final String[] items = new String[templist.size()];
		final boolean[] checkedItems = new boolean[templist.size()];
		for (int z = 0; z < templist.size(); z++) {
			items[z] = templist.get(z).getName();
			if (templist.get(z).getName().equals(zuname)) {
				checkedItems[z] = true;
			} else {
				checkedItems[z] = false;
			}
		}
		String title = "";
		if (zuname != null) {
			title = "添加股票(当前股票:" + zuname + ")";
		}
		if (items.length != 0) {
			// 初始化并显示Dialog
			AlertDialog alertDialog = new AlertDialog.Builder(context)
					.setTitle(title)
					.setMultiChoiceItems(items, checkedItems,
							new DialogInterface.OnMultiChoiceClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which, boolean isChecked) {
									// 点击时各组数据的变化
									checkedItems[which] = isChecked;
								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// ZuHeItemAdapter.TongBuStatus=items.length;
									// 确定添加股票
									for (int i = 0; i < checkedItems.length; i++) {
										if (checkedItems[i] == true) {
											Intent intent = new Intent(
													"com.tljr.mainActivityResult");
											intent.putExtra("code", guKey);
											intent.putExtra(
													"zuid",
													ZiXuanUtil.fzMap.get(
															items[i]).getId());
											intent.putExtra("zuName", items[i]);
											context.sendBroadcast(intent);
											ProgressDlgUtil.showProgressDlg("",
													(Activity) context);
										}
									}
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 取消按钮
								}
							}).create();
			alertDialog.show();
		} else {
			showToast("没有组合可以添加");
		}
	}

	private PopupWindow popupWindow;

	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	public void showPopupView() {
		if (popupWindow == null) {
			// 一个自定义的布局，作为显示的内容
			View contentView = View.inflate(this,R.layout.tljr_dialog_share_news, null);
			contentView.findViewById(R.id.btn_cancle).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							popupWindow.dismiss();
						}
					});
			LinearLayout v = (LinearLayout) contentView.findViewById(R.id.ly1);
			for (int i = 0; i < v.getChildCount(); i++) {
				final int m = i;
				v.getChildAt(i).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						share(m);
						popupWindow.dismiss();
					}
				});
			}
			popupWindow = new PopupWindow(contentView,LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
			popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setOutsideTouchable(true);
			popupWindow.setAnimationStyle(R.style.AnimationPreview);
			popupWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					setAlpha(1f);
				}
			});
		}
		setAlpha(0.8f);
		View v = findViewById(R.id.bottom);
		popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
	}// type 0:QQ 1微信 2新浪微博 3朋友圈

	private void setAlpha(float f) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = f;
		lp.dimAmount = f;
		getWindow().setAttributes(lp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	}

	private void share(int type) {
		switch (type) {
		case 0:
			BwManager.getInstance().shareQQ();
			break;
		case 1:
			BwManager.getInstance().shareWeiXin();
			break;
		case 2:
			BwManager.getInstance().shareSina();
			break;
		case 3:
			BwManager.getInstance().shareWeiXinPyq();
			break;
		default:
			break;
		}
	}

	public void addGuanZhu() {
		// 添加关注更新我的
		for (int i = 0; i < MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean
				.size(); i++) {
			if (MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean
					.get(i).getStatus() == 1) {
				MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean
						.get(i).setFinishstatus(false);
			}
		}
		bean.setFinishstatus(true);
		MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean
				.add(0, bean);
		MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.adapter
				.notifyDataSetChanged();
		MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView
				.refreshAdapterView();
		MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum = MyApplication
				.getInstance().getMainActivity().zhiyanFragment.myNum + 1;
		MyApplication.getInstance().getMainActivity().zhiyanFragment.myRadioButton
				.setText("我的("
						+ (MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum)
						+ ")");
		// 添加关注更新item的关注
		if (itemposition != -1) {
			ZhongchouBean beans = MyApplication.getInstance().getMainActivity().zhiyanFragment.mZhiYanFinishView.listLeftBeans
					.get(item).getFinishViewItemShow().listBean
					.get(itemposition);
			beans.setIsfocus(true);
			beans.setFocus(beans.getFocus() + 1);
			MyApplication.getInstance().getMainActivity().zhiyanFragment.mZhiYanFinishView.listLeftBeans
					.get(item).getFinishViewItemShow().adapter
					.notifyDataSetChanged();
		} else {
			List<ZhiYanLeftBean> zhiYanLeftBeans = MyApplication.getInstance()
					.getMainActivity().zhiyanFragment.mZhiYanFinishView.listLeftBeans;
			for (int i = 0; i < zhiYanLeftBeans.size(); i++) {
				if (zhiYanLeftBeans.get(i).getFinishViewItemShow() != null) {
					List<ZhongchouBean> listBean = zhiYanLeftBeans.get(i)
							.getFinishViewItemShow().listBean;
					for (int z = 0; z < listBean.size(); z++) {
						if (listBean.get(z).getCode().equals(bean.getCode())) {
							bean.setFocus(bean.getFocus() + 1);
							bean.setIsfocus(true);
							zhiYanLeftBeans.get(i).getFinishViewItemShow().adapter
									.notifyDataSetChanged();
						}
					}
				}
			}
		}
	}

	public void deleteGuanZhu() {
		// 取消关注更新我的
		List<ZhongchouBean> listbean = MyApplication.getInstance()
				.getMainActivity().zhiyanFragment.mMyView.listBean;
		String key = bean.getMarket() + bean.getCode();
		for (int i = 0; i < listbean.size(); i++) {
			if (key.equals(listbean.get(i).getMarket()
					+ listbean.get(i).getCode())) {
				if (i == 0) {
					listbean.get(1).setFinishstatus(true);
				}
				listbean.remove(i);
				break;
			}
		}
		MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.adapter
				.notifyDataSetChanged();
		MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum = MyApplication
				.getInstance().getMainActivity().zhiyanFragment.myNum - 1;
		MyApplication.getInstance().getMainActivity().zhiyanFragment.myRadioButton
				.setText("我的("
						+ (MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum)
						+ ")");
		// 取消关注更新item的关注
		if (itemposition != -1) {
			ZhongchouBean beans = MyApplication.getInstance().getMainActivity().zhiyanFragment.mZhiYanFinishView.listLeftBeans
					.get(item).getFinishViewItemShow().listBean
					.get(itemposition);
			beans.setIsfocus(false);
			beans.setFocus(beans.getFocus() - 1);
			MyApplication.getInstance().getMainActivity().zhiyanFragment.mZhiYanFinishView.listLeftBeans
					.get(item).getFinishViewItemShow().adapter
					.notifyDataSetChanged();
		} else {
			List<ZhiYanLeftBean> zhiYanLeftBeans = MyApplication.getInstance()
					.getMainActivity().zhiyanFragment.mZhiYanFinishView.listLeftBeans;
			for (int i = 0; i < zhiYanLeftBeans.size(); i++) {
				if (zhiYanLeftBeans.get(i).getFinishViewItemShow() != null) {
					List<ZhongchouBean> listBean = zhiYanLeftBeans.get(i)
							.getFinishViewItemShow().listBean;
					for (int z = 0; z < listBean.size(); z++) {
						if (listBean.get(z).getCode().equals(bean.getCode())) {
							bean.setFocus(bean.getFocus() - 1);
							bean.setIsfocus(false);
							zhiYanLeftBeans.get(i).getFinishViewItemShow().adapter
									.notifyDataSetChanged();
						}
					}
				}
			}
		}
	}

}

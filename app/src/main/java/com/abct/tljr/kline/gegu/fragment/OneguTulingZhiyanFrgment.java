package com.abct.tljr.kline.gegu.fragment;

import io.realm.Realm;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.dialog.PromptDialog;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.model.Options;
import com.abct.tljr.model.UserCrowd;
import com.abct.tljr.model.UserCrowdUser;
import com.abct.tljr.model.ZhongchouBean;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.ui.activity.zhiyan.LaunchReSearchActivity;
import com.abct.tljr.ui.activity.zhiyan.ReSearchActivity;
import com.abct.tljr.ui.fragments.BaseFragment;
import com.abct.tljr.ui.fragments.zhiyanFragment.adapter.ZhiyanArtZhongchouAdapter;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.DownloadProUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.BwManager;
import com.qh.common.model.TitleBar;
import com.qh.common.model.User;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

/**
 * onegu的图灵智研fragment
 */

public class OneguTulingZhiyanFrgment extends BaseFragment implements
		OnClickListener {

	private RadioGroup radioGroup;
	private View zhiyanview = null;
	private RadioButton ralike;
	private TextView care, likenum;
	private ViewPager viewPager;
	private HashMap<String, String> data = new HashMap<String, String>();
	private JSONObject userOpers;// 看/赞/关注人数
	private JSONObject isOperateds;// 自己是否看/赞/关注
	private String market, code, name, id;
	private JSONObject object;
	private TitleBar titleBar;
	private ArrayList<View> list = new ArrayList<View>();
	private boolean isSend = false;
	private LinearLayout like;
	private RelativeLayout onegutulingzhiyan_header;
	private int UpdateTemp = 0;
	private TextView save,shared;
	private PopupWindow popupWindow;
	private Button yanjiu=null;
	private RelativeLayout faqiyanjiu_eare=null;
	private RelativeLayout ferdlsContext=null;
	private UpdateZhiYan mUpdateZhiYan=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		zhiyanview = inflater.inflate(R.layout.onegutulingzhiyanfragment,
				container, false);
		// id = getActivity().getIntent().getStringExtra("id");
		BwManager.getInstance().initShare(getActivity());
		
		initView();
		getZhiYanGuData();
		return zhiyanview;
	}

	public void initView() {
		care = (TextView) zhiyanview.findViewById(R.id.care);
		care.setText("+关注");
		care.setOnClickListener(this);
		ralike = (RadioButton) zhiyanview.findViewById(R.id.ralike);
		radioGroup = (RadioGroup) zhiyanview.findViewById(R.id.radio);
		viewPager = (ViewPager) zhiyanview.findViewById(R.id.viewpager);
		likenum = (TextView) zhiyanview.findViewById(R.id.likenum);
		like = (LinearLayout) zhiyanview.findViewById(R.id.like);
		like.setOnClickListener(this);
		save=(TextView)zhiyanview.findViewById(R.id.save);
		save.setOnClickListener(this);
		shared=(TextView)zhiyanview.findViewById(R.id.shared);
		shared.setOnClickListener(this);
		onegutulingzhiyan_header = (RelativeLayout)zhiyanview.findViewById(R.id.onegutulingzhiyan_header);
		yanjiu=(Button)zhiyanview.findViewById(R.id.faqiyanjiu);
		yanjiu.setOnClickListener(this);
		faqiyanjiu_eare=(RelativeLayout)zhiyanview.findViewById(R.id.faqiyanjiu_eare);
		ferdlsContext=(RelativeLayout)zhiyanview.findViewById(R.id.onegutuling_content);
		
		mUpdateZhiYan=new UpdateZhiYan();
		IntentFilter mIntentFilter=new IntentFilter("com.tuling.OneguTuLingZhiYan");
		getActivity().registerReceiver(mUpdateZhiYan,mIntentFilter);
		
//		((OneGuActivity)getActivity()).code;
//		((OneGuActivity)getActivity()).market;
		
		code = ((OneGuActivity)getActivity()).code;
		market = ((OneGuActivity)getActivity()).market;
	}

	/*
	 * 动态改变背景
	 */
	@SuppressWarnings("deprecation")
	public void changeBackground() {
		if (UpdateTemp == ((OneGuActivity) getActivity()).UpOrDowm) {
			return;
		}
		UpdateTemp = ((OneGuActivity) getActivity()).UpOrDowm;
		if (((OneGuActivity) getActivity()).UpOrDowm == 1) {
			onegutulingzhiyan_header.setBackground(getResources().getDrawable(R.drawable.img_zhangbeijing));
			save.setBackground(getResources().getDrawable(R.drawable.img_zhangbeijing));
			shared.setBackground(getResources().getDrawable(R.drawable.img_zhangbeijing));
		} else if (((OneGuActivity) getActivity()).UpOrDowm == 2) {
			onegutulingzhiyan_header.setBackground(getResources().getDrawable(R.drawable.img_diebeijing));
			save.setBackground(getResources().getDrawable(R.drawable.img_diebeijing));
			shared.setBackground(getResources().getDrawable(R.drawable.img_diebeijing));
		}
	}

	/*
	 * 获取个股的智研数据
	 */
	public void getZhiYanGuData() {
		if(User.getUser()==null){
			ferdlsContext.setVisibility(View.GONE);
			faqiyanjiu_eare.setVisibility(View.VISIBLE);
			return ;
		}
		String params = "oper=1&code=" + code + "&market=" + market + "&uid="+ User.getUser().getId();
		NetUtil.sendPost(UrlUtil.Url_235 + "8080/StockDataService/ferdls",
				params, new NetResult() {
					@Override
					public void result(final String msg) {
						try {
							JSONObject jsonobject = new JSONObject(msg);
							if(jsonobject.getInt("status")==1){
								//假如有数据
								object = jsonobject.getJSONObject("result");
								ferdlsContext.setVisibility(View.VISIBLE);
								faqiyanjiu_eare.setVisibility(View.GONE);
								initUi();
							}else{
								//假如没有数据
								ferdlsContext.setVisibility(View.GONE);
								faqiyanjiu_eare.setVisibility(View.VISIBLE);
							}
						} catch (Exception e) {

						}
					}
				});
	}

	private void getData(final Complete complete) {
		ProgressDlgUtil.showProgressDlg("", getActivity());
		String params = "code=" + code + "&oper=[0]";
		if (MyApplication.getInstance().self != null)
			params += ("&uid=" + MyApplication.getInstance().self.getId());
		NetUtil.sendPost(UrlUtil.URL_ferdlsinfo, params, new NetResult() {
			@Override
			public void result(final String msg) {
				try {
					JSONObject jsonObject = new JSONObject(msg);
					if (jsonObject.getInt("code") == 1) {
						JSONObject object = jsonObject.getJSONObject("data");
						Iterator<String> keys = object.keys();
						while (keys.hasNext()) {
							String key = keys.next();
							data.put(key, object.getString(key));
						}
						userOpers = object.optJSONObject("userOpers");
						isOperateds = object.optJSONObject("isOperateds");
						complete.complete();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					showToast("获取数据失败");
				}
				ProgressDlgUtil.stopProgressDlg();
			}
		});
	}

	// 初始化智研UI
	private void initUi() {
		getData(new Complete() {
			@Override
			public void complete() {
				((TextView) zhiyanview.findViewById(R.id.seenum)).setText(userOpers.optString("sees"));
				((TextView) zhiyanview.findViewById(R.id.likenum)).setText(userOpers.optString("likes"));
				if (MyApplication.getInstance().self != null&& isOperateds != null) {
					ralike.setChecked(isOperateds.optBoolean("ferdls_like_") ? true: false);
					care.setText(isOperateds.optBoolean("ferdls_cared_") ? "已关注": "+关注");
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
							RadioButton btn = (RadioButton)LayoutInflater.from(getActivity()).inflate(R.layout.radiobutton,null);
							btn.setLayoutParams(new LayoutParams(Util.WIDTH/ count, LayoutParams.MATCH_PARENT));
							btn.setTag(obj.getString("key"));
							btn.setText(obj.getString("name") + "\n"+ obj.getString("cName"));
							radioGroup.addView(btn);
							JSONArray arr = new JSONArray(data.get(obj.getString("name")));
							if (arr != null && arr.length() > 0) {
								ListView m = new ListView(getActivity());
								m.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
								m.setAdapter(new MyAdapter(arr, obj.getString("summaryKey")));
								list.add(m);
							}
						}
					}
					if (radioGroup.getChildCount() > 0)
						titleBar = new TitleBar(getActivity(), viewPager,radioGroup,zhiyanview.findViewById(R.id.cursor));
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
				v = View.inflate(getActivity(), R.layout.tljr_item_ferdlsinfo,
						null);
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
			new AlertDialog.Builder(getActivity())
					.setTitle("图灵金融")
					.setMessage("当前为" + Constant.netType + "网络，下载会消耗流量，确认下载？")
					.setPositiveButton("是",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0,int arg1) {
							DownloadProUtil.showProgressDlg(title, url,apkName, activity, true, complete);
						}
			}).setNegativeButton("否", null).show();
		} else {
			DownloadProUtil.showProgressDlg(title,url,apkName,activity,true,complete);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.like:
			if (ralike.isChecked()) {
				sendEvent(4, likenum);
			} else {
				sendEvent(2, likenum);
			}
			break;
		case R.id.care:
			if (care.getText().toString().equals("已关注")) {
				sendEvent(5, care);
			} else {
				sendEvent(3, care);
			}
			break;
		case R.id.save:
			break;
		case R.id.shared:
			showPopupView();
			break;
		case R.id.faqiyanjiu:
			SearchCode();
			break;
		}
	}

	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	public void showPopupView() {
		if (popupWindow == null) {
			// 一个自定义的布局，作为显示的内容
			View contentView = View.inflate(getActivity(),R.layout.tljr_dialog_share_news, null);
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
		View v = zhiyanview.findViewById(R.id.bottom);
		popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
	}// type 0:QQ 1微信 2新浪微博 3朋友圈

	private void setAlpha(float f) {
		WindowManager.LayoutParams lp =getActivity().getWindow().getAttributes();
		lp.alpha = f;
		lp.dimAmount = f;
		getActivity().getWindow().setAttributes(lp);
		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	}
	
	//点赞和关注的方法
	private void sendEvent(final int event, final TextView tv) {
		if (isSend) {
			return;
		}
		isSend = true;
		ProgressDlgUtil.showProgressDlg("", getActivity());
		NetUtil.sendPost(UrlUtil.URL_ferdlsevent, "oper=[" + event + "]&code="
				+ code + "&uid=" + MyApplication.getInstance().self.getId(),
				new NetResult() {
					@Override
					public void result(final String msg) {
						isSend = false;
						ProgressDlgUtil.stopProgressDlg();
						try {
							JSONObject object = new JSONObject(msg);
							boolean success = object.getInt(object.keys().next()) == 1;
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
									if (isOperateds != null&& isOperateds.optBoolean("ferdls_like_")) {
										tv.setText((userOpers.optInt("likes") - 1)+ "");
										ralike.setChecked(false);
									} else {
										tv.setText((userOpers.optInt("likes"))+ "");
										ralike.setChecked(false);
									}
								} else if (event == 2) {
									if (isOperateds != null&& isOperateds.optBoolean("ferdls_like_")) {
										tv.setText((userOpers.optInt("likes"))+ "");
										ralike.setChecked(true);
									} else {
										tv.setText((userOpers.optInt("likes") + 1)+ "");
										ralike.setChecked(true);
									}
								} else if (event == 1) {
									if (isOperateds != null&& !isOperateds.optBoolean("ferdls_see_")) {
										tv.setText((userOpers.optInt("sees") + 1)+ "");
									}
								} else if (event == 3) {
									tv.setText("已关注");
								} else if (event == 5) {
									tv.setText("+关注");
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
		});
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
	
	public void SearchCode(){
		String url;
		if (User.getUser() == null) {
			url = UrlUtil.URL_ZR + "crowd/getCrowListByCode?code=" + code;
		} else {
			url = UrlUtil.URL_ZR + "crowd/getCrowListByCode?code=" + code + "&uid=" + User.getUser().getId();
		}
		LogUtil.e("serchCode", url);
		ProgressDlgUtil.showProgressDlg("", activity);
		NetUtil.sendGet(url, new NetResult() {
			@Override
			public void result(String arg0) {
				LogUtil.e("serchCode", arg0);
				ProgressDlgUtil.stopProgressDlg();
				try{
					JSONObject jsonObject = new JSONObject(arg0);
					if (jsonObject.getInt("status") == 1) {
						JSONArray array = jsonObject.getJSONArray("msg");
						if (array.length() == 0) {
							startLanchCode(market, code);
							return;
						}
						for (int j = 0; j < array.length(); j++) {
							JSONObject ob = array.getJSONObject(j);
							ZhongchouBean bean = new ZhongchouBean();
							bean.setCode(ob.optString("code"));
							bean.setMarket(ob.optString("market"));
							bean.setCount(ob.optInt("count"));
							bean.setCreateDate(ob.optLong("createDate"));
							bean.setEndDate(ob.optLong("endDate"));
							bean.setHasMoney(ob.optInt("hasMoney"));
							bean.setIconurl(ob.optString("icon"));
							bean.setId(ob.optString("id"));
							bean.setPrice(ob.optInt("price"));
							bean.setStatus(ob.optInt("status"));
							bean.setTotalMoney(ob.optInt("totalMoney"));
							bean.setType(ob.optString("type"));
							bean.setTypedesc(ob.optString("typedesc"));
							bean.setFocus(ob.optInt("focus"));
							bean.setRemark(ob.optInt("remark"));
							bean.setIsfocus(ob.optBoolean("isfocus"));
							bean.setReward(ob.optInt("reward"));
							bean.setRewardMoney(ob.optInt("rewardMoney"));
							bean.setSection(ob.optString("section"));
							bean.setLook(ob.optBoolean("isLook"));
							JSONArray ar2 = ob.getJSONArray("userCrowd");
							ArrayList<UserCrowd> list = new ArrayList<UserCrowd>();
							for (int x = 0; x < ar2.length(); x++) {
								JSONObject o = ar2.getJSONObject(x);
								UserCrowd crowd = new UserCrowd();
								crowd.setCcrowdId(o.getString("crowdId"));
								crowd.setCdate(o.getLong("date"));
								crowd.setCid(o.getString("id"));
								crowd.setCinit(o.getBoolean("init"));
								crowd.setCmoney(o.getInt("money"));
								crowd.setFocs(o.optBoolean("isFocus"));
								crowd.setMsg(o.optString("msg"));
								JSONObject object1 = o.getJSONObject("user");
								UserCrowdUser u = new UserCrowdUser();
								u.setUavata(object1.getString("avatar"));
								u.setUdata(object1.getLong("date"));
								u.setUlevel(object1.getInt("level"));
								u.setUuid(object1.getString("uid"));
								u.setUnickname(object1.getString("nickName"));
								crowd.setUser(u);
								list.add(crowd);
							}
							bean.setUserCrowdList(list);
							
							Intent i = new Intent(activity, ReSearchActivity.class);
							i.putExtra("market", bean.getMarket());
							i.putExtra("code", bean.getCode());
							i.putExtra("id", bean.getId());
							i.putExtra("url", bean.getIconurl());
							i.putExtra("type", bean.getType());
							i.putExtra("typedesc", bean.getTypedesc());
							int persen = bean.getHasMoney() * 100 / bean.getTotalMoney();
							i.putExtra("persen", persen);
							i.putExtra("focus", bean.getFocus());
							i.putExtra("remark", bean.getRemark());
							i.putExtra("isfocus", bean.isIsfocus());
							i.putExtra("section", bean.getSection());
							ZhiyanArtZhongchouAdapter.userCrowdList = bean.getUserCrowdList();
							activity.startActivity(i);
					}
				}
				}catch(Exception e){
					
				}
			}
		});
	}
	
	class UpdateZhiYan extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			getZhiYanGuData();
		}
	}
	
	private void startLanchCode(String market, String code) {
		Intent i = new Intent(activity, LaunchReSearchActivity.class);
		i.putExtra("market", market);
		i.putExtra("code", code);
		activity.startActivity(i);
	}
	
	public void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

}

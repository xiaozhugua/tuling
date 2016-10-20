package com.abct.tljr.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.data.Constant.readComplete;
import com.abct.tljr.data.ZiXuanUtil;
import com.abct.tljr.hangqing.HangQing;
import com.abct.tljr.hangqing.database.ZiXuanGu;
import com.abct.tljr.hangqing.database.ZiXuanGuRealmImpl;
import com.abct.tljr.hangqing.util.GuDealImpl;
import com.abct.tljr.hangqing.zixuan.tljr_zixuan_gu_recyclerview;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.util.InputTools;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import io.realm.Case;
import io.realm.Realm;

/**
 * @author xbw
 * @version 创建时间：2015年11月2日 上午11:26:39
 */

public class SearchDialog extends Dialog implements DialogInterface.OnCancelListener {
	public static ArrayList<OneGu> historyList;
	public static final String SearchHistoryKey = "TLJR_SEARCH";
	public static Map<String, OneGu> map = new HashMap<String, OneGu>();
	private ArrayList<OneGu> guList = new ArrayList<OneGu>();
	private ArrayList<OneGu> list = new ArrayList<OneGu>();
	private EditText et;
	private RecyclerView lv;
	private int lastVisibleItem;
	private ListAdapter adapter;
	private LinearLayoutManager manager;
	private MyHandler handler;
	private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(10);
	private String currentSearchTip, zuName;
	private Context context;
	private boolean isFlush = false;
	private NetResult result;
	private String url = UrlUtil.Url_235+ "8080/ZhiLiYinHang/ZiXuanServlet";
	private Map<String,ZiXuanGu> MapGu=null;
	private UpdateDeleteGu mUpdateDeleteGu=null;
	private boolean action=false;
	
	public SearchDialog(Context context, NetResult result) {
		this(context);
		this.result = result;
	}

	@SuppressWarnings("deprecation")
	public SearchDialog(final Context context) {
		super(context, R.style.dialog);
		setContentView(R.layout.tljr_dialog_serch);
		this.context = context;
		MapGu=new HashMap<>();
		Window dialogWindow = getWindow();
		dialogWindow.setWindowAnimations(R.style.AnimationPreview); // 设置窗口弹出动画
		dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
						| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); //
		p.width = (int) (Util.WIDTH);
		p.height = (int) (Util.HEIGHT);
		dialogWindow.setAttributes(p);
		findViewById(R.id.tljr_txt_close).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						hidedialog();
				    }
		});
		handler = new MyHandler();
		lv = (RecyclerView) findViewById(R.id.tljr_lv_gplb);
		et = (EditText) findViewById(R.id.tljr_edit_search);
		et.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				currentSearchTip = s.toString();
				showSearchTip(currentSearchTip);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {
				/* 判断是否是“GO”键 */
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					hideSoftInput();
					currentSearchTip = et.getText().toString();
					action=true;
					showSearchTip(currentSearchTip);
					return true;
				}
				return false;
			}
		});
		manager = new LinearLayoutManager(context);
		lv.setLayoutManager(manager);
		adapter = new ListAdapter();
		lv.setAdapter(adapter);
		lv.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,
					int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE&&lastVisibleItem + 1 == adapter.getItemCount()) {
					loadMore();
				}
			}
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				lastVisibleItem = manager.findLastVisibleItemPosition();
			}

		});
		if(tljr_zixuan_gu_recyclerview.listZiXuanGu!=null){
			for(ZiXuanGu gu:tljr_zixuan_gu_recyclerview.listZiXuanGu){
				MapGu.put(gu.getMarket()+gu.getCode(),gu);
			}
		}
		
		mUpdateDeleteGu=new UpdateDeleteGu();
		IntentFilter mIntentFilter=new IntentFilter("com.tiaocang.tljrdeletegu");
		context.registerReceiver(mUpdateDeleteGu,mIntentFilter);
		
		loadMore();
	}

	public void show(String zuName) {
		this.zuName = zuName;
		map.clear();
		if (zuName != null && !zuName.equals("")) {
			ArrayList<OneGu> list = ZiXuanUtil.fzMap.get(zuName).getList();
			for (int i = 0; i < list.size(); i++) {
				map.put(list.get(i).getKey(), list.get(i));
			}
		} else {
			for (String key : ZiXuanUtil.fzMap.keySet()) {
				ArrayList<OneGu> list = ZiXuanUtil.fzMap.get(key).getList();
				for (int i = 0; i < list.size(); i++) {
					map.put(list.get(i).getKey(), list.get(i));
				}
			}
		}
		if (historyList == null) {
			Constant.dataRead(SearchHistoryKey, new readComplete() {
				@Override
				public void read(final Object object) {
					handler.post(new Runnable() {
						@SuppressWarnings("unchecked")
						@Override
						public void run() {
							if (object != null) {
								historyList = (ArrayList<OneGu>) object;
								currentSearchTip = et.getText().toString();
								showSearchTip(et.getText().toString());
							} else {
								historyList = new ArrayList<OneGu>();
								currentSearchTip = et.getText().toString();
								showSearchTip(et.getText().toString());
							}
						}
					});
				}
			});
		} else {
			currentSearchTip = et.getText().toString();
			showSearchTip(et.getText().toString());
		}
		show();
		showSoftInput();
	}

	public void hidedialog() {
		hideSoftInput();
		this.hide();
	}

	public void showSearchTip(String newText) {
		schedule(new SearchTipThread(newText), 500);
	}

	class SearchTipThread implements Runnable {
		String newText;
		public SearchTipThread(String newText) {
			this.newText = newText;
		}
		public void run() {
			if (newText != null && newText.equals(currentSearchTip)) {
				handler.sendMessage(handler.obtainMessage(3, newText));
			}
		}
	}

	public ScheduledFuture<?> schedule(Runnable command, long delayTimeMills) {
		return scheduledExecutor.schedule(command, delayTimeMills,TimeUnit.MILLISECONDS);
	}

	@SuppressLint("HandlerLeak")
	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				ProgressDlgUtil.showProgressDlg("", (Activity) context);
				break;
			case 2:
				ProgressDlgUtil.stopProgressDlg();
				break;
			case 3:
				initAdapter((String) msg.obj);
				break;
			}
		}
	}

	/**
	 * hide soft input
	 */
	private void hideSoftInput() {
		InputTools.HideKeyboard(et);
	}

	/**
	 * show soft input
	 */
	private void showSoftInput() {
		InputTools.ShowKeyboard(et);
	}

	private void loadMore() {
		if (isFlush) {
			return;
		}
		isFlush = true;
		handler.post(new Runnable() {
			@Override
			public void run() {
				ArrayList<OneGu> alist = new ArrayList<OneGu>();
				for (int i = 0; i < 20; i++) {
					if (i < guList.size()) {
						alist.add(guList.get(i));
						if (i == 19) {
							lv.stopScroll();
							break;
						}
					} else {
						lv.stopScroll();
					}
				}
				list.addAll(alist);
				guList.removeAll(alist);
				alist = null;
				isFlush = false;
				lv.stopScroll();
				adapter.notifyDataSetChanged();
			}
		});
	}

	@SuppressLint("DefaultLocale")
	private void initAdapter(String input) {
		isShowHistory = false;
		if (input.length() == 0 || input == null || input.equals("")) {
			showhistory();
			return;
		}
		Realm realm = Realm.getDefaultInstance();
		if (realm.isEmpty()) {
			realm.close();
			return;
		}
		guList.clear();
		guList.addAll(realm.where(OneGu.class).contains("code", input).or()
				.contains("pyName", input, Case.INSENSITIVE).or()
				.contains("name", input, Case.INSENSITIVE).findAll());
		realm.close();
		if (guList.size() == 1&&action) {
			action=false;
			addHistory(guList.get(0));
		}
		list.clear();
		lv.stopScroll();
		loadMore();
	}

	private void addHistory(OneGu gu) {
		for (int i = 0; i < historyList.size(); i++) {
			if (historyList.get(i).getKey().equals(gu.getKey())) {
				return;
			}
		}
		if (historyList.size() > 30) {
			historyList.remove(0);
		}
		historyList.add(gu);
	}

	private void showhistory() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				list.clear();
				list.addAll(historyList);
				if (list.size() > 0)
					addClearView();
				lv.stopScroll();
				adapter.notifyDataSetChanged();
			}
		});
	}

	private void addClearView() {
		OneGu clearGu = new OneGu();
		list.add(clearGu);
		isShowHistory = true;
	}

	boolean isShowHistory = false;

	@Override
	public void onCancel(DialogInterface dialog) {

	}

	class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.tljr_item_search, parent, false));
		}
		@Override
		public void onBindViewHolder(final ViewHolder holder, final int position) {
			OneGu gu = list.get(position);
			if (result != null) {
				holder.addView.setVisibility(View.GONE);
			}
			holder.itemView.setTag(holder);
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					hideSoftInput();
					ViewHolder holder = (ViewHolder) v.getTag();
					if (isShowHistory && position == list.size() - 1) {
						historyList.clear();
						currentSearchTip = et.getText().toString();
						action=true;
						showSearchTip(et.getText().toString());
						return;
					}
					if (result != null) {
						result.result(holder.gu.getMarket()+"&"+holder.gu.getCode());
						hidedialog();
						return;
					}
					String key = holder.gu.getKey();
					Realm realm = Realm.getDefaultInstance();
					LogUtil.e("代码", key);
					OneGu gu1 = realm.where(OneGu.class).equalTo("marketCode", key).findFirst();
					realm.close();
					if (gu1 == null) {
						Toast.makeText(context, "获取数据失败，请重试",Toast.LENGTH_SHORT).show();
						return;
					}
					OneGu gu = Constant.cloneGu(gu1);
					Intent intent = new Intent(context, OneGuActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("code", gu.getCode());
					bundle.putString("name", gu.getName());
					bundle.putString("market", gu.getMarket());
					bundle.putString("key", gu.getKey());
					bundle.putSerializable("onegu", gu);
					if (zuName != null && !zuName.equals(""))
						bundle.putString("zuName", zuName);
					intent.putExtras(bundle);
					context.startActivity(intent);
					hidedialog();
					addHistory(gu);
				}
			});
			
			holder.addView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final OneGu gu = ((ViewHolder) ((View) v.getParent()).getTag()).gu;
					if(!holder.add.isChecked()){
						ProgressDlgUtil.showProgressDlg("",context);
						ProgressDlgUtil.Status=false;
						holder.add.setChecked(true);
						// 添加股票
						if (HangQing.addStatus == 0) {
							//自选添加
							String params = "method=add&uid="+ MyApplication.getInstance().self.getId()
									+ "&token=" + Configs.token + "&market="+ gu.getMarket() + "&code=" + gu.getCode()
									+ "&name=" + gu.getName();
							NetUtil.sendPost(url, params, new NetResult() {
								@Override
								public void result(String response) {
									try {
										JSONObject object = new JSONObject(response);
										if (object.getInt("status") == 1) {
											Intent intent = new Intent("com.zixuan.updatezixuan");
											intent.putExtra("action", "add");
											intent.putExtra("code", gu.getCode());
											intent.putExtra("id",object.getString("msg"));
											intent.putExtra("market",gu.getMarket());
											intent.putExtra("name", gu.getName());
											intent.putExtra("price", gu.getNow());
											intent.putExtra("p_change",gu.getP_change());
											intent.putExtra("status","--");
											getContext().sendBroadcast(intent);
											Toast.makeText(getContext(), "添加自选成功",Toast.LENGTH_SHORT).show();
											ZiXuanGu mZiXuanGu=new ZiXuanGu();
											mZiXuanGu.setCode(gu.getCode());
											mZiXuanGu.setChange(gu.getP_change());
											mZiXuanGu.setId(object.getString("msg"));
											mZiXuanGu.setLocation(0);
											mZiXuanGu.setMarket(gu.getMarket());
											mZiXuanGu.setName(gu.getName());
											mZiXuanGu.setPrice(0.0f);
											mZiXuanGu.setYclose(0.0f);
											MapGu.put(gu.getMarket()+gu.getCode(),mZiXuanGu);
											ProgressDlgUtil.Status=true;
											ProgressDlgUtil.stopProgressDlg();
										} else if (object.getInt("status") == 0) {
											Toast.makeText(getContext(), "添加失败",Toast.LENGTH_SHORT).show();
										} else {
											Toast.makeText(getContext(),object.getString("msg"),Toast.LENGTH_SHORT).show();
										}
									} catch (Exception e) {

									}
								}
							});
						} else {
							//组合股票添加
							GuDealImpl.AddZuHeGu(context,gu.getKey(),gu.getName(),zuName);
						}
					}else{
						holder.add.setChecked(false);
						//删除股票
						if(HangQing.addStatus==0){
							//自选删除
							GuDealImpl.DeleteZiXuanGuImpl(context,MapGu.get(gu.getMarket()+gu.getCode()),MapGu);
						}else{
							//现在的组就直接删除
							ProgressDlgUtil.showProgressDlg("",context);
							ZiXuanUtil.delStock(gu.getMarket()+gu.getCode(),zuName);
						}
					}
				}
			});
			
			holder.gu = gu;
			if (isShowHistory && position == list.size() - 1) {
				holder.code.setText("清除搜索记录");
				holder.code.setTextColor(Color.parseColor("#ff5a01"));
				holder.name.setVisibility(View.GONE);
				holder.add.setVisibility(View.GONE);
			} else {
					holder.code.setText(gu.getCode());
					holder.code.setTextColor(Color.parseColor("#000000"));
					holder.name.setVisibility(View.VISIBLE);
					holder.name.setText(gu.getName());
					if(HangQing.addStatus!=0){
						boolean isHas = map.containsKey(gu.getKey());
						holder.add.setChecked(isHas);
						holder.add.setVisibility(View.VISIBLE);
					}else{
						boolean isHas=MapGu.containsKey(gu.getKey());
						holder.add.setChecked(isHas);
						holder.add.setVisibility(View.VISIBLE);
					}
			}
		}

		@Override
		public int getItemCount() {
			return list.size();
		}

		public final class ViewHolder extends RecyclerView.ViewHolder {
			TextView name;
			TextView code;
			CheckBox add;
			View addView;
			OneGu gu;
			public ViewHolder(View v) {
				super(v);
				name = (TextView) v.findViewById(R.id.tljr_txt_search_name);
				code = (TextView) v.findViewById(R.id.tljr_txt_search_code);
				add = (CheckBox) v.findViewById(R.id.tljr_img_search_jiahao);
				addView = v.findViewById(R.id.tljr_grp_search_jiahao);
			}
		}
	}

	class UpdateDeleteGu extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			try{
				JSONArray father=new JSONArray(intent.getStringExtra("message"));
				for(int z=0;z<father.length();z++){
				    JSONObject object=father.getJSONObject(z);
					if(object.getString("action").equals("del")){
						JSONObject son=object.getJSONObject("info");
						DeleteData(son.getString("code"),son.getString("market"));
					}else{
						
					}
				}
			}catch(Exception e){
			}
		}
	}
	
	public void DeleteData(String code,String market){
		//更新组合股票列表
		Intent tiaocanggu=new Intent("tljr_TiaocangGu_update");
		tiaocanggu.putExtra("code",code);
		context.sendBroadcast(tiaocanggu);
		//更新组合股票百分比
		Intent tiaocangpercent=new Intent("tljr_tiaocang_changeupdate");
		tiaocangpercent.putExtra("code",code);
		context.sendBroadcast(tiaocangpercent);
		//删除本地数据
		ZiXuanGuRealmImpl.RemoveOneGu(zuName+market+code,zuName);
		
		map.remove(market+code);
		ProgressDlgUtil.stopProgressDlg();
	}
	
	
	
}

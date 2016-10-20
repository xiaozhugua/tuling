package com.abct.tljr.hangqing.jingwu;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterViewFlipper;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.abct.tljr.R;
import com.abct.tljr.hangqing.model.RankViewDataModel;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;

public class MainFengMian {
	private AdapterViewFlipper mViewFlipper;
	private Context context;
	private List<RankViewDataModel> ListDataModel;
	private int num;
	private ViewPager mViewPager;
	private long times;
	private View gridview;
	private String url = null;
	private OneGu mOneGu = null;
	private SharedPreferences.Editor editor = null;
	private String name;
	private GridView v;
	private View rankviewitem;
	private List<View> listView;
	
	public MainFengMian(AdapterViewFlipper mViewFlipper, Context context,List<RankViewDataModel> ListDataModel,
			int i, ViewPager mViewPager,long times, View view, String name,List<View> listView,View rankviewitem) {
		this.mViewFlipper = mViewFlipper;
		this.context = context;
		this.ListDataModel = ListDataModel;
		this.num = i;
		this.mViewPager = mViewPager;
		this.times = times;
		this.gridview = view;
		this.name = name;
		this.listView=listView;
		this.rankviewitem=rankviewitem;
		initView();
	}

	public void initView(){
		editor = context.getSharedPreferences("jingwuopen",context.MODE_PRIVATE).edit();
		rankviewitem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOneGu != null && mOneGu.getCode() != null&& mOneGu.getMarket() != null
						&& mOneGu.getName() != null&& mOneGu.getKey() != null) {
					Intent intent = new Intent(context,OneGuActivity.class);
					// 用Bundle携带数据
					Bundle bundle = new Bundle();
					if(mOneGu.getName()!=null){
						bundle.putString("code",mOneGu.getCode());
						bundle.putString("market",mOneGu.getMarket());
						bundle.putString("name",mOneGu.getName());
						bundle.putString("key",mOneGu.getKey());
						bundle.putSerializable("onegu",mOneGu);
						intent.putExtras(bundle);
						context.startActivity(intent);
					}
				} else {
					Toast.makeText(context, "没数据，暂时进不了个股信息",Toast.LENGTH_SHORT).show();
				}
			}
		});	
		if (ListDataModel.size() > num) {
			((TextView) rankviewitem.findViewById(R.id.rankview_code)).setText(ListDataModel.get(num).getCode());
			((TextView) rankviewitem.findViewById(R.id.rankview_name)).setText(ListDataModel.get(num).getName());
			StartActivity.imageLoader.displayImage(ListDataModel.get(num).getIconUrl(),
					((ImageView) rankviewitem.findViewById(R.id.rankview_img)),StartActivity.options);
		}
		MainJingWuView.MapView.put(ListDataModel.get(num).getMarket()+"|"+ListDataModel.get(num).getCode(),rankviewitem);
		mViewFlipper.showNext();
		getReflushData();
	}
	
	public void getReflushData() {
		v = (GridView) mViewPager.getChildAt(mViewPager.getCurrentItem());
		if (v.getChildCount() == 0) {
			return;
		}
		// 获取股票列表参数
		String parm = "market=" + ListDataModel.get(num).getMarket() +"&code="+ListDataModel.get(num).getCode();
		// 网上获取最新数据
		Util.getRealInfo(parm, new NetResult() {
			@Override
			public void result(final String msg) {
				try {
					JSONObject object = new JSONObject(msg);
					String code = ListDataModel.get(num).getCode();
					String market = ListDataModel.get(num).getMarket();
					JSONArray array = object.getJSONArray("result");
					Message message = new Message();
					message.what = 1;
					Bundle bundle = new Bundle();
					bundle.putString("code", code);
					bundle.putString("now", (float) array.optDouble(0) + "");
					bundle.putString("num", (float) array.optDouble(8) + "");
					bundle.putString("parent", (float) array.optDouble(9) + "");
					mOneGu = new OneGu();
					mOneGu.setCode(code);
					mOneGu.setNow(array.optDouble(0));
					mOneGu.setName(name);
					mOneGu.setMarket(market);
					mOneGu.setKey(market + code);
					mOneGu.setYclose(array.optDouble(1));
					mOneGu.setKaipanjia(array.optDouble(2));
					mOneGu.setP_changes(((float) array.optDouble(9, 0) > 0 ? "+":"")
								+ (float) array.optDouble(9, 0) + "%");
					mOneGu.setChange((float) array.optDouble(9, 0));
					editor.putString(times + code, code).commit();
					message.setData(bundle);
					mHandel.sendMessage(message);
					editor.commit();
				} catch (JSONException e) {
				}
			}
		});
	}

	@SuppressLint("HandlerLeak")
	final Handler mHandel = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Bundle bundle = msg.getData();
				AdapterViewFlipper viewFlipper = (AdapterViewFlipper)gridview.findViewById(R.id.tljr_rankview_viewflipper2);
				View view = viewFlipper.getChildAt(1);
				
				TextView rankview_now=(TextView) view.findViewById(R.id.rankview_now);
				if(rankview_now!=null)
					rankview_now.setText(bundle.getFloat("now")+"");
				
				TextView rankview_num=(TextView) view.findViewById(R.id.rankview_num);
				if(rankview_num!=null)
					rankview_num.setText(bundle.getFloat("num")+"");
				
				TextView percent = ((TextView) view.findViewById(R.id.rankview_percent));
				if(percent!=null)
					percent.setText(bundle.getString("parent") + "%");
				
				float num = 0;
				if (!bundle.getString("num").equals("")){
					num = Float.valueOf(bundle.getString("num"));
				}		
				
				if(((RelativeLayout) view.findViewById(R.id.tljr_rankview_item))!=null){
					if (num > 0) {
						((RelativeLayout) view.findViewById(R.id.tljr_rankview_item)).setBackgroundResource(R.drawable.img_tuijian02);
					} else  if(num<0){
						((RelativeLayout) view.findViewById(R.id.tljr_rankview_item)).setBackgroundResource(R.drawable.img_tuijian01);
					}else {
						((RelativeLayout) view.findViewById(R.id.tljr_rankview_item)).setBackgroundResource(R.drawable.img_wugudefault);
					}
				}
				
				break;
			}
		};
	};
	
	
}

package com.abct.tljr.ui.yousuan.activity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.abct.tljr.R;
import com.abct.tljr.ui.fragments.zhiyanFragment.util.ZhiYanParseJson;
import com.abct.tljr.ui.widget.ObservableScrollView;
import com.abct.tljr.ui.widget.ObservableScrollView.OnScrollToButtomListener;
import com.abct.tljr.ui.widget.ScrollViewListener;
import com.abct.tljr.ui.yousuan.model.YouSuanDataModel;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class YouSuanRecord implements OnScrollToButtomListener {
	private Context context = null;
	private View record;
	private LinearLayout titles;
	private LinearLayout items;
	private List<YouSuanDataModel> listYouSuan = null;
	private ObservableScrollView ScrollViewTitle = null;
	private ObservableScrollView ScrollViewItems = null;
	private int itemsize = 15;
	private int LastItemSize = 0;
	private boolean updateStatus=true;
	private int allowUpadte=0;
	private int updateNum=0;
	
	private int page=1;
	private int RecordId;
	private String key=null;
	
	private SimpleDateFormat mSimpleDateFormat=new SimpleDateFormat("yyyy/MM/dd");
	private String recordUrl=UrlUtil.URL_YS+"gentou/getTransactionRecords";
	private TextView record_tip;
    private DecimalFormat df=null;

	public YouSuanRecord(Context context,int id,String key) {
		this.context = context;
        df=new DecimalFormat("#0.00");
		this.RecordId=id;
		this.key=key;
		record = LayoutInflater.from(context).inflate(R.layout.yousuan_record,null);
		ScrollViewTitle = (ObservableScrollView) record.findViewById(R.id.tljr_yousuanrecord_title);
		ScrollViewItems = (ObservableScrollView) record.findViewById(R.id.tljr_yousuanrecord_items);
		titles = (LinearLayout) record.findViewById(R.id.tljr_yousuanrecord_title_linear);
		items = (LinearLayout) record.findViewById(R.id.tljr_yousuanrecord_items_linear);
		listYouSuan = new ArrayList<YouSuanDataModel>();
        record_tip=(TextView)record.findViewById(R.id.record_tip);
		ScrollViewTitle.setScrollViewListener(new ScrollViewListener() {
			@Override
			public void onScrollChanged(ObservableScrollView scrollView,int x,int y,int oldx,int oldy) {
				ScrollViewItems.scrollTo(x,y);
			}
		});
		ScrollViewItems.setScrollViewListener(new ScrollViewListener() {
			@Override
			public void onScrollChanged(ObservableScrollView scrollView,int x,int y,int oldx,int oldy) {
				ScrollViewTitle.scrollTo(x,y);
			}
		});
		ScrollViewItems.setScrollToButtomListener(this);
		initData();
	}

	public void initData() {
		String params="name="+key+"&page="+page+"&size=1000";
        LogUtil.e("intiData_url",recordUrl+"?"+params);
		NetUtil.sendGet(recordUrl,params,new NetResult() {
			@Override
			public void result(String response) {
				if(ZhiYanParseJson.ParseZhiYanRecord2(listYouSuan,response)){
					allowUpadte=listYouSuan.size()/itemsize;
					InitView(0,itemsize);
                    record_tip.setText("交易记录("+listYouSuan.size()+")");
				}
			}
		});
	}
	
	public void InitView(int start,int end) {
		if (listYouSuan.size() > 0) {
			if (listYouSuan.size() > end&& updateNum<=allowUpadte) {
				LastItemSize = end;
			} else {
				LastItemSize = listYouSuan.size();
			}
		} else {
			return;
		}
		updateNum++;
		for (int i = start; i < LastItemSize; i++) {
			View Viewtitle = LayoutInflater.from(context).inflate(R.layout.yousuan_data_title, null);
			View ViewItem = LayoutInflater.from(context).inflate(R.layout.yousuan_data_recorditem, null);
			((TextView) Viewtitle.findViewById(R.id.tljr_yousuantitle_name)).setText(listYouSuan.get(i).getProductName());

			((TextView) ViewItem.findViewById(R.id.tljr_yousuanrecord_maimai)).setText(listYouSuan.get(i).getDirection().equals("S")?"卖出":"买入");
			((TextView) ViewItem.findViewById(R.id.tljr_yousuanrecord_shoushu)).setText(listYouSuan.get(i).getCount()+"");
			((TextView) ViewItem.findViewById(R.id.tljr_yousuanrecord_kaitime)).setText(mSimpleDateFormat.format(new Date(Long.valueOf(listYouSuan.get(i).getDate())*1000)));
			((TextView) ViewItem.findViewById(R.id.tljr_yousuanrecord_kaiprice)).setText(df.format(listYouSuan.get(i).getPoint())+"");
			((TextView) ViewItem.findViewById(R.id.tljr_yousuanrecord_pingtime)).setText(mSimpleDateFormat.format(new Date(listYouSuan.get(i).getOpentime()*1000)));
			((TextView) ViewItem.findViewById(R.id.tljr_yousuanrecord_pingprice)).setText(df.format(listYouSuan.get(i).getOpenprice()));
			((TextView) ViewItem.findViewById(R.id.tljr_yousuanrecord_shouxufei)).setText("2");
			((TextView) ViewItem.findViewById(R.id.tljr_yousuanrecord_pingcangyinkui)).setText(df.format(listYouSuan.get(i).getCurrentProfit()));
			((TextView) ViewItem.findViewById(R.id.tljr_yousuanrecord_leijiyinkui)).setText(df.format(listYouSuan.get(i).getAllProfit()));

			titles.addView(Viewtitle);
			items.addView(ViewItem);
		}
	}

	public View getView() {
		return record;
	}
                    
	@Override
	public void onScrollBottomListener(boolean isBottom) {
		if (isBottom&&updateStatus) {
			updateStatus=false;
			if(updateNum<allowUpadte){
				int start=LastItemSize;
				LastItemSize+=itemsize;
				InitView(start,LastItemSize);
			}else if(updateNum==allowUpadte){
				InitView(LastItemSize,listYouSuan.size());
			}
			updateStatus=true;
		}
	}

}

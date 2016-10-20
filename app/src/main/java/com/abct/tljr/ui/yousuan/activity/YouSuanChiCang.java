package com.abct.tljr.ui.yousuan.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.abct.tljr.R;
import com.abct.tljr.ui.fragments.zhiyanFragment.util.ZhiYanParseJson;
import com.abct.tljr.ui.widget.ObservableScrollView;
import com.abct.tljr.ui.widget.ObservableScrollView.OnScrollToButtomListener;
import com.abct.tljr.ui.widget.ScrollViewListener;
import com.abct.tljr.ui.yousuan.model.YouSuanRecordModel;
import com.qh.common.listener.NetResult;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class YouSuanChiCang implements OnScrollToButtomListener {

	private Context context=null;
	private View ChiCangView=null;
	private ObservableScrollView chicangname=null;
	private ObservableScrollView chicangitem=null;
	private LinearLayout titles=null;
	private LinearLayout items=null;
	private String ChiCangUrl=UrlUtil.URL_YS+"gentou/getNoCompleteRecords";
	private List<YouSuanRecordModel> listModel=null;
	private SimpleDateFormat mSimpleDateFormat=null;
	private int RecordId;
	private int itemsize = 15;
	private int LastItemSize = 0;
	private boolean updateStatus=true;
	private int allowUpadte=0;
	private int updateNum=0;
	private int page=1;
	private String key=null;
	private TextView nameTip=null;

	public YouSuanChiCang(Context context,int id,String key){
		this.context=context;
		this.RecordId=id;
		this.key=key;
		mSimpleDateFormat=new SimpleDateFormat("yyyy/MM/dd");
		ChiCangView=LayoutInflater.from(context).inflate(R.layout.yousuandata_chicang,null);
		chicangname=(ObservableScrollView)ChiCangView.findViewById(R.id.tljr_yousuanchicang_title);
		chicangitem=(ObservableScrollView)ChiCangView.findViewById(R.id.tljr_page_yousuanchicang_items);
        nameTip=(TextView)ChiCangView.findViewById(R.id.chicang_tip);
		titles=(LinearLayout)chicangname.getChildAt(0);
		items=(LinearLayout)chicangitem.getChildAt(0);
		chicangname.setScrollViewListener(new ScrollViewListener() {
	            @Override
	            public void onScrollChanged(ObservableScrollView scrollView, int x,int y, int oldx, int oldy) {
	            	chicangitem.scrollTo(x, y);
	            }
	     });
		chicangitem.setScrollViewListener(new ScrollViewListener() {
	            @Override
	            public void onScrollChanged(ObservableScrollView scrollView, int x,int y, int oldx, int oldy) {
	            	chicangname.scrollTo(x, y);
	            }
	    });
		chicangitem.setScrollToButtomListener(this);
		initData();
	}
	
	public void initData(){
		listModel=new ArrayList<YouSuanRecordModel>();
		String params="name="+key+"&page="+page+"&size=1000";
//		String ChiCangTestUrl=ChiCangUrl+"?"+params;
		NetUtil.sendGet(ChiCangUrl,params,new NetResult() {
			@Override
			public void result(String response) {
				if(ZhiYanParseJson.ParseZhiYanRecord(listModel,response)){
					allowUpadte=listModel.size()/itemsize;
					InitView(0,itemsize);
                    nameTip.setText("持仓("+listModel.size()+")");
				}else{
					
				}
			}
		});
	}
	
	@SuppressLint("InflateParams")
	public void InitView(int start,int end){
		if (listModel.size() > 0) {
			if (listModel.size() > end&& updateNum<=allowUpadte) {
				LastItemSize = end;
			} else {
				LastItemSize = listModel.size();
			}
		} else {
			return;
		}
		updateNum++;
		for(int i=0;i<LastItemSize;i++){
			View Viewtitle=LayoutInflater.from(context).inflate(R.layout.yousuan_data_title,null);
			View ViewItem=LayoutInflater.from(context).inflate(R.layout.yousuan_data_item,null);
				((TextView)Viewtitle.findViewById(R.id.tljr_yousuantitle_name)).setText(listModel.get(i).getProductName());
				((TextView)ViewItem.findViewById(R.id.tljr_yousuanitem_maimai)).setText(listModel.get(i).getDirection().equals("S")?"卖出":"买入");
				((TextView)ViewItem.findViewById(R.id.tljr_yousuanitem_shoushu)).setText(listModel.get(i).getCount()+"");
				((TextView)ViewItem.findViewById(R.id.tljr_yousuanitem_kaitime)).setText(mSimpleDateFormat.format(Long.valueOf(listModel.get(i).getDate())*1000));
				((TextView)ViewItem.findViewById(R.id.tljr_yousuanitem_kaiprice)).setText(listModel.get(i).getPoint()+"");
				titles.addView(Viewtitle);
				items.addView(ViewItem);
		}
	}
	
	public View getView(){
		return ChiCangView;
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
				InitView(LastItemSize,listModel.size());
			}
			updateStatus=true;
		}
	}
	
}

//package com.abct.tljr.news;
//
//import java.util.ArrayList;
//
//import com.abct.tljr.BaseActivity;
//import com.abct.tljr.R;
//import com.abct.tljr.news.adapter.NewsFeatureAdapter;
//import com.abct.tljr.news.bean.News;
//import com.abct.tljr.zrclistview.SimpleHeader;
//import com.abct.tljr.zrclistview.ZrcListView;
//import com.abct.tljr.zrclistview.ZrcListView.OnStartListener;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.TextView;
///*
// * 新闻专题
// */
//public class NewsFeatureActivity extends   BaseActivity{
//	
//	private ZrcListView listview ;
//	private NewsFeatureAdapter adapter ;
//	private ArrayList<News> list = new ArrayList<News>();
//	@Override
//	protected void onCreate(Bundle savedInstanceState)
//	{
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.tljr_activity_news_feature) ;
//		
//	//	viewNewsListView = (RelativeLayout) view.findViewById(R.id.tljr_lv_content_hqss);
//	 	listview = (ZrcListView) findViewById(R.id.tljr_zListView);
//	 	
//	 	News n;
//	 	for(int i=0;i<10;i++){
//	 		n = new News();
//	 		n.setTitle("证监会新闻发布会上张晓军第二次回应了宝能举牌万科问题。对于宝能举牌万科，张晓军表示，目前，证监会正会同银监会、保监会对此事进行核实研判。");
//	 		n.setTime("2015-07-09");
//	 		n.setpUrl("http://qhcdn.oss-cn-hangzhou.aliyuncs.com/uploadfile/tljr/my_news/content/1451270984672_19.png");
//	 		list.add(n);
//	 		
//	 	}
//	 	
//	 	
//	 	adapter= new NewsFeatureAdapter(this, list, listview);
//	 	listview.setAdapter(adapter);
//		// listView.setSelector(R.drawable.tljr_listview_selector);
//		// 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
//		SimpleHeader listview_header = new SimpleHeader(this);
//		listview_header.setTextColor(0xffeb5041);
//		listview_header.setCircleColor(0xffeb5041);
//		listview.setHeadable(listview_header);
//		// 设置加载更多的样式（可选）
////		SimpleFooter footer = new SimpleFooter(this);
////		footer.setCircleColor(0xffeb5041);
////		listview.setFootable(footer);
////		listview.setFooterDividersEnabled(false);
//		View head = (LayoutInflater.from(this).inflate(R.layout.tljr_item_news_feature_header, null));
//	
//	//  标题
//	//	((TextView)head.findViewById(R.id.news_feature_title)).setText("");
//		
//	//  摘要
//		 ((TextView)head.findViewById(R.id.news_feature_digest)).setText("\t \t \t \t"+"中国2016年的五个主要任务包括：去产能、去库存、去逛逛打发时间点附近发生的，斯蒂芬森四等分，发送，公司发生过四个公司 。");
//		 listview.addHeaderView(head);
//		
//		
//		listview.setOnRefreshStartListener(new OnStartListener() {
//
//			@Override
//			public void onStart()
//			{
//				
//
//			}
//		});
//		
//		
//		
//	}
//
//}

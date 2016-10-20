package com.abct.tljr.news;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.model.Options;
import com.abct.tljr.news.NewsSearchEditText.StartSearch;
import com.abct.tljr.news.bean.News;
import com.abct.tljr.news.fragment.NewsManager;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.qh.common.listener.NetResult;
import com.qh.common.util.DateUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

public class NewsSeachActivity extends BaseActivity{
	
	EditText et_seach ;
	ImageView iv_delete;
	ImageView iv_search;
	NewsSeachActivity activity;
	NewsSearchEditText newsSearchEditText;
	TextView tv_search_num ;
	RelativeLayout rootView ;
	private View searchNum;
 	private ListView lv;
	private NewsSearchAdapter adapter;
	public final String Tag ="NewsSeachActivity";
	private ArrayList<News> list = new ArrayList<News>();
	private ArrayList<News> mList = new ArrayList<News>();
	
	
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		newsSearchEditText.saveHistory();
	}
	
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		rootView = (RelativeLayout) getLayoutInflater().inflate(R.layout.tljr_activity_news_search, null);
		
		 setContentView(rootView);
		 activity = this;
		(findViewById(R.id.tljr_img_news_back)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0)
			{
				finish();
			}
		});
		
		iv_search =(ImageView) findViewById(R.id.img_sousuo); 
		 iv_delete =(ImageView) findViewById(R.id.img_delete); 
		 et_seach = (EditText)findViewById(R.id.et_seach);
		 iv_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				newsSearchEditText.addHistrory();
				getData(et_seach.getText()+"");
			}
		});
		 
		 newsSearchEditText = new NewsSearchEditText(activity,rootView,et_seach, iv_delete);
		 newsSearchEditText.setStartSearch(new StartSearch() {
			
			@Override
			public void search()
			{ 
				getData(et_seach.getText()+"");
			}
		});
		 
		 lv= (ListView)findViewById(R.id.tljr_news_search);
		 
		 
		 
			
			
			
			searchNum = (LayoutInflater.from(this).inflate(  
					R.layout.tljr_listview_header_searchn_num, null));
			
			tv_search_num =(TextView)searchNum.findViewById(R.id.tv_search_num);
			searchNum.setVisibility(View.GONE);
			
			lv.addHeaderView(searchNum);
			
			
			
			
			 
			adapter = new NewsSearchAdapter(activity, list);
			lv.setAdapter(adapter);
		//	lv.startLoadMore();
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					newsSearchEditText.setHistoryVisiable();
					News n = list.get(position);
					getDetailsNews(n);
				}
				 
			});
		 
		 
	}
	
	
	private void getDetailsNews(News n){
		ProgressDlgUtil.showProgressDlg("", this);
		String pId = "0";
		if(MyApplication.getInstance().self!=null){
			pId =MyApplication.getInstance().self.getId();
		}
		String url =UrlUtil.URL_news_fn;
		String params ="time="+n.getTime()+"&id="+n.getId()+"&sp="+n.getSpecial()+"&pId="+pId;
		LogUtil.i(Tag,  url+"?"+params);
		NetUtil.sendPost(url, params, new NetResult() {

			@Override
			public void result(String msg)
			{
				ProgressDlgUtil.stopProgressDlg();
				LogUtil.i(Tag,  msg);
				  final ArrayList<News> tempList = new ArrayList<News>();
				try {
					final JSONObject object = new JSONObject(msg);
					if (object.getInt("status") == 1) {
						News news;
					 
						JSONObject oj = object.getJSONObject("result");
						final JSONObject rs = oj .getJSONObject("news");
								news = new News();
								 
				news.setId(rs.optString("id", "0"));
				 
				news.setSpecial(rs.optString("species"));
				news.setType(rs.optString("type", "notype") );
				news.setTitle(rs.optString("title"));
				news.setSummary(rs.optString("summary") );
				news.setContent(rs.optString("content"));
				news.setTime(rs.optLong("time"));
				news.setSource(rs.optString("source"));
				news.setUrl(rs.optString("url"));
				news.setpUrl(rs.optString("purl"));
				news.setZan(rs.optString("praise"));
				news.setCollect(rs.optString("collects"));
				news.setSurl(rs.optString("surl"));

				news.setHaveCai(rs.optBoolean("hasOppose",false) );
				news.setHaveZan(rs.optBoolean("hasPraise",false) );
				news.setHaveCollect(rs.optBoolean("hasCollect",false) );
				news.setHaveSee(rs.optBoolean("read",false) );
				
			  

				news.setDigest(rs.optString("digest") );
				 
						
				news.setDate(DateUtil.getDateMDhhmm(rs.getLong("time")));
				news.setImp_time(NewsManager.getNewsDate(rs.getLong("time"), true));
				news.setSimple_time(NewsManager.getNewsDate(rs.getLong("time"), false));
						
						
				tempList.add(news);
		  
						
					 
						post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (tempList.size() > 0) {
									Intent intent = new Intent(activity, NewsActivity.class);
									//		Intent intent = new Intent(activity, NewsFeatureActivity.class);
											
											Bundle bundle = new Bundle();
											bundle.putString("name", "搜索");
											// bundle.putSerializable("news", news);
											// bundle.putSerializable("newsList", dataList);
											bundle.putInt("position", 0);
											bundle.putString("nowTypeName", "搜索");
										//	HuanQiuShiShi.currentListData = tempList;

											intent.putExtras(bundle);
											activity.startActivity(intent);
								} else {
								 
									showToast("没有更多数据");
								}
							 
							}
						});
					} else {
						post(new Runnable() {

							@SuppressLint("ShowToast")
							@Override
							public void run() {
								showToast("暂无新闻");
							}
						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}
	
	 
	private void getData(String str){
		ProgressDlgUtil.showProgressDlg("", this);
		String url =UrlUtil.URL_news_ks;
		String params ="w="+str+"&platform=1";
		LogUtil.i(Tag,  url+"?"+params);
		NetUtil.sendPost(url, params, new NetResult() {

			@Override
			public void result(String msg)
			{
				ProgressDlgUtil.stopProgressDlg();
				LogUtil.i(Tag,  msg);

				try {
					final JSONObject object = new JSONObject(msg);
					if (object.getInt("status") == 1) {
						
						list.clear();
						JSONObject oj = object.getJSONObject("result");
						final JSONArray array = oj
								.getJSONArray("data");
						
						
						
						for (int i = 0; i < array.length(); i++) {
							JSONObject obj = array.getJSONObject(i);
							News news = new News();
							news.setId(obj.optString("id"));
							news.setpUrl(obj.optString("purl"));
							news.setSpecial(obj.optString("species"));
							news.setSummary(obj.optString("summary"));
							news.setTitle(obj.optString("title"));
							news.setTime(obj.optLong("time"));
							news.setImp_time(NewsManager.getNewsDate(obj.getLong("time"), true));
							mList.add(news);
						}
						post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (array.length() > 0) {
									searchNum.setVisibility(View.VISIBLE);
									tv_search_num.setText("约找到"+array.length()+"个搜索结果");
									list.addAll(mList);
									mList.clear();
									adapter.notifyDataSetChanged();
								} else {
									list.clear();
									adapter.notifyDataSetChanged();
									tv_search_num.setText("约找到0个搜索结果");
									showToast("没有搜索到相关内容");
								}
							 
							}
						});
					} else {
						post(new Runnable() {

							@SuppressLint("ShowToast")
							@Override
							public void run() {
								list.clear();
								adapter.notifyDataSetChanged();
								showToast("没有搜索到相关内容");
								tv_search_num.setText("约找到0个搜索结果");
							}
						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}
	
	private void loadMore() {
	//	getData();
//		if (isFlush || list.size() >= 100) {
//			showToast("没有更多数据");
//			lv.stopLoadMore();
//			return;
//		}
//		isFlush = true;
//		getData();
	}

} 
class NewsSearchAdapter  extends BaseAdapter{
    ArrayList<News> list = new ArrayList<News>();
  
    Activity activity ;
    
    public NewsSearchAdapter(Activity activity,ArrayList<News> list){
        this.activity = activity;
        this.list = list ;
    }
    
   
   public void setList(ArrayList<News> list){
       this.list =list ;
       notifyDataSetChanged();
   }
  
  
@Override
public View getView(final int position, View v, ViewGroup parent) {
    // TODO Auto-generated method stub
    ViewHolder holder;
    News  news = list.get(position);
    
    if (v == null) {
        v = View.inflate(activity, R.layout.tljr_item_news_search, null);
        holder = new ViewHolder();
        holder.name = (TextView) v
                .findViewById(R.id.tv_title);
        holder.time = (TextView) v
                .findViewById(R.id.tv_time);
        holder.picture = (ImageView) v
                .findViewById(R.id.iv_picture);
         
        
        v.setTag(holder);
    } else {
        holder = (ViewHolder) v.getTag();
    }
    
	final ImageAware imageAware = new ImageViewAware(holder.picture, false);
	
	
	if (holder.picture.getTag() == null
			|| !holder.picture.getTag().equals(news.getpUrl()))
	{
		StartActivity.imageLoader
				.displayImage(news.getpUrl(), imageAware, Options.getListOptions());
		holder.picture.setTag(news.getpUrl());
	}
	
	
	//StartActivity.imageLoader.displayImage(news.getpUrl(), imageAware);
    holder.name.setText(news.getTitle());
    holder.time.setText(news.getImp_time());
    return v;
}
@Override
public long getItemId(int position) {
    // TODO Auto-generated method stub
    return position;
}
@Override
public Object getItem(int position) {
    // TODO Auto-generated method stub
    return list.get(position);
}
@Override
public int getCount() {
    // TODO Auto-generated method stub
    return list.size();
}

static class ViewHolder {
    TextView name;
    ImageView picture;
    TextView time ;
}
}
package com.abct.tljr.hangqing.jingwu;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;
import com.abct.tljr.R;
import com.abct.tljr.hangqing.model.RankViewDataModel;
import com.abct.tljr.hangqing.util.CalendarImpl;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.qh.common.volley.Response;
import com.qh.common.volley.VolleyError;
import com.qh.common.volley.toolbox.StringRequest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterViewFlipper;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class MainJingWuView implements OnClickListener {

    private SimpleDateFormat mSimpleDateFormat;
    private TextView tljr_rankview_time;
    private ViewPager mViewPager;
    private String lastTime;
    private SharedPreferences editor;
    public static Map<String,View> MapView=null;
    private Map<String,Map<String,String>> resultMap;
	private Activity activity=null;	
	private List<List<RankViewDataModel>> ListDataModel;
	private View rankviewpager;
	public  List<View> listrankview;
	private ImageView[] img=null;
	private Handler mhandler=null;
	private HashMap<String,String> resultName;
	private RelativeLayout jingwu_relative;
	private Button main_jingwu_bu;
	private RelativeLayout main_chongzai;
	private ProgressBar jingwu_progressbar;
	
	@SuppressWarnings("static-access")
	public MainJingWuView(Activity activity){
		this.activity=activity;
		mSimpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
		ListDataModel=new ArrayList<List<RankViewDataModel>>();
        editor=this.activity.getSharedPreferences("jingwuopen",this.activity.MODE_PRIVATE);
        MapView=new HashMap<String,View>();
        resultMap=new HashMap<String, Map<String,String>>();
        resultName=new HashMap<String, String>();
        initView();
	}

	@SuppressLint("InflateParams")
	public void initView(){
		 rankviewpager = LayoutInflater.from(activity).inflate(R.layout.main_jingwu, null);
         tljr_rankview_time = (TextView) rankviewpager.findViewById(R.id.tljr_rankview_time);
         jingwu_relative=(RelativeLayout)rankviewpager.findViewById(R.id.jingwu_relative);
         main_jingwu_bu=(Button)rankviewpager.findViewById(R.id.main_jingwu_bu);
         main_chongzai=(RelativeLayout)rankviewpager.findViewById(R.id.main_chongzai);
         jingwu_progressbar=(ProgressBar)rankviewpager.findViewById(R.id.jingwu_progressbar);
         main_jingwu_bu.setOnClickListener(this);
         jingwu_relative.setVisibility(View.VISIBLE);
         jingwu_progressbar.setVisibility(View.VISIBLE);
         main_chongzai.setVisibility(View.GONE);
         new UpdateRankViewData().execute();
	}
	
	public View getView(){
		return rankviewpager;
	}
	
	public void setHandler(Handler mHandler){
		this.mhandler=mHandler;
	}
	
	class UpdateRankViewData extends AsyncTask<Void,Void,Integer>{
        @Override
       protected Integer doInBackground(Void... params) {
        	NetUtil.mQueue.add(new StringRequest(UrlUtil.JingWu,new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    parsejingwuJosn(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error){
                	jingwu_relative.setVisibility(View.VISIBLE);
                	main_chongzai.setVisibility(View.VISIBLE);
                	jingwu_progressbar.setVisibility(View.GONE);
                }
            }));
           return null;
       }
       @Override
       protected void onPostExecute(Integer result) {
           super.onPostExecute(result);
       }    
    }
 
    public void parsejingwuJosn(String arrayjson){
        try{
            ListDataModel.clear();
            JSONObject object=new JSONObject(arrayjson);
            if(object.getInt("code")==1){
                JSONObject data=object.getJSONObject("data");
                lastTime=object.getString("lastDate");
                Iterator<String> listString=data.keys();
                JSONArray tempArray;
                RankViewDataModel model;
                JSONObject tempObject;
                List<RankViewDataModel> listRankView;
                while(listString.hasNext()){
                    tempArray=data.getJSONArray(listString.next());
                    listRankView=new ArrayList<RankViewDataModel>();
                    for(int i=0;i<tempArray.length();i++){
                        tempObject=tempArray.getJSONObject(i);
                        model=new RankViewDataModel(tempObject.optString("code"),tempObject.optString("grade"),tempObject.optString("iconUrl")
                                ,tempObject.optString("name"),tempObject.optString("reason"),tempObject.optString("time")
                                ,mSimpleDateFormat.parse(tempObject.optString("time")).getTime(),tempObject.optString("market"));
                        listRankView.add(model);
                    }
                    ListDataModel.add(listRankView);
                }
                sortListDataByTime(ListDataModel);
                //传值给Main
                ((LinearLayout) rankviewpager.findViewById(R.id.tljr_rankview_more)).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity,JingWuActivity.class);
                        intent.putExtra("jingwudata",(Serializable)ListDataModel.get(mViewPager.getCurrentItem()));
//                      intent.putExtra("date",tljr_rankview_time.getText().toString());
                        intent.putExtra("lastTime",lastTime);
                        activity.startActivity(intent);
                    }
                });
                getRankView(ListDataModel);
            }
        }catch(Exception e){
        	LogUtil.e("jingwuview_error",e.getMessage());
        }
    }
	
    
    //初始化viewpager
    public void getRankView(final List<List<RankViewDataModel>> ListDataModel){
        mViewPager=(ViewPager)rankviewpager.findViewById(R.id.tljr_rankview_jingwu_viewpager);
        mViewPager.setOffscreenPageLimit(5);
        listrankview=new ArrayList<View>();
        for(int i=0;i<ListDataModel.size();i++){
            //添加轮播的界面
            listrankview.add(flushJingWuItem(ListDataModel.get(i),i));
        }
        img=new ImageView[listrankview.size()];
        LinearLayout mLinearLayout=(LinearLayout)rankviewpager.findViewById(R.id.rankview_footer);
        mLinearLayout.removeAllViews();
        for (int i = 0; i < listrankview.size();i++){
            if(i==5){
                break;
            }
            img[i] = new ImageView(activity);
            if (0 == i) {
                img[i].setBackgroundResource(R.drawable.img_yuandian1);
            } else {
                img[i].setBackgroundResource(R.drawable.img_yuandian2);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.leftMargin = 15;
            params.width = 15;
            params.height = 15;
            mLinearLayout.addView(img[i], params);
        }
        new UpdateDataOpenView().execute(MapView);
        
        Message message=Message.obtain();
        message.what=1;
        mHandler.sendMessage(message);
        
        setMessage();
    }
 
    //初始化每个viewpager的各个item
	@SuppressWarnings("unused")
	@SuppressLint("InflateParams")
	public GridView flushJingWuItem(final List<RankViewDataModel> ListDataModel,int page){
        int space = (Util.WIDTH * 18 / Util.IMAGEWIDTH)/2;
        GridView gridview = new GridView(activity);
        gridview.setNumColumns(3);
        gridview.setPadding(space, 0, space, 0);
        gridview.setVerticalScrollBarEnabled(false);
        gridview.setHorizontalSpacing(space);
        gridview.setVerticalSpacing(space);
        final ArrayList<View> list = new ArrayList<View>();
//      final RankViewDataModel mRankViewDataModel;
        for(int i=0;i<ListDataModel.size()+1;i++){
        	if(i==6){
                break;
            }
            final View view;
            final int item=i;
            if(i==0){
           	 //初始化第一项的日期
                view=LayoutInflater.from(activity).inflate(R.layout.tljr_rankview_date,null);
                ListDataModel.get(0).getTimes();
                List<Integer> listint=CalendarImpl.getListTime(ListDataModel.get(0).getTimes());
                ((TextView)view.findViewById(R.id.rankview_time)).setText(CalendarImpl.getWeekDay(ListDataModel.get(0).getTimes()));
                int status=CalendarImpl.getActionStatus(ListDataModel.get(0).getTimes());
                
                if(status==1){
                    ((TextView)view.findViewById(R.id.rankview_action)).setText("交易中");
                }else if(status==2){
               	 	((TextView)view.findViewById(R.id.rankview_action)).setText("已停市");
                }else if(status==3){
                	((TextView)view.findViewById(R.id.rankview_action)).setText("将开市");
                }else if(status==4){
                    ((TextView)view.findViewById(R.id.rankview_action)).setText("已闭市");
                }else if(status==5){
                	((TextView)view.findViewById(R.id.rankview_action)).setText("将开市");
                }else if(status==6){
                	((TextView)view.findViewById(R.id.rankview_action)).setText("已闭市");
                }
                
                ((TextView)view.findViewById(R.id.rankview_day)).setText(listint.get(0)+"");
                ((TextView)view.findViewById(R.id.rankview_date)).setText(listint.get(2)+"年"+listint.get(1)+"月");
            }else{
               final RankViewDataModel mRankViewDataModel=ListDataModel.get(i-1);
               if(editor.getString(mRankViewDataModel.getTimes()+mRankViewDataModel.getCode(),"").equals(mRankViewDataModel.getCode())){
               	//如果挂过
               	view=LayoutInflater.from(activity).inflate(R.layout.tljr_rankview_item_view,null);
               	((TextView)view.findViewById(R.id.rankview_code)).setText(mRankViewDataModel.getCode());
               	((TextView)view.findViewById(R.id.rankview_name)).setText(mRankViewDataModel.getName());
               	StartActivity.imageLoader.displayImage(mRankViewDataModel.getIconUrl(),
               			((ImageView)view.findViewById(R.id.rankview_img)),StartActivity.options);
               	MapView.put(mRankViewDataModel.getMarket()+"|"+mRankViewDataModel.getCode(),view);
               }else{
               	 //如果没有挂过
               	 view=LayoutInflater.from(activity).inflate(R.layout.tljr_rankview_fengitem,null);
                 View fengmain=LayoutInflater.from(activity).inflate(R.layout.tljr_rankview_fengmian,null);
                 final View rankviewitem = LayoutInflater.from(activity).inflate(R.layout.tljr_rankview_item_view, null);
                 final AdapterViewFlipper mViewFlipper=(AdapterViewFlipper)view.findViewById(R.id.tljr_rankview_viewflipper2);
                 final List<View> listView=new ArrayList<View>();
                 listView.add(fengmain);
                 listView.add(rankviewitem);
                 fengmain.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						new MainFengMian(mViewFlipper,activity,ListDataModel,item-1,mViewPager,ListDataModel.get(0).getTimes()
								,view,mRankViewDataModel.getName(),listView,rankviewitem);
					}
                 });
                 mViewFlipper.setAdapter(new ViewFlipperAdapter(listView).getAdapter());
               }
           	resultName.put(mRankViewDataModel.getMarket()+mRankViewDataModel.getCode(),mRankViewDataModel.getName());  
            }
            list.add(view);
        }
        gridview.setAdapter(new BaseAdapter(){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return list.get(position);
            }
            @Override
            public long getItemId(int position) {
                return 0;
            }
            @Override
            public Object getItem(int position) {
                return null;
            }
            @Override
            public int getCount() {
                return list.size();
            }
        });
        return gridview;
    }
    
    //对数据对象进行排序
    public void sortListDataByTime(List<List<RankViewDataModel>> listmodel) {
        for (int i = 0; i < ListDataModel.size(); i++) {
            for (int j = 0; j < ListDataModel.size() - 1 - i; j++) {
                if (ListDataModel.get(j).get(0).getTimes()<ListDataModel.get(j + 1).get(0).getTimes()) {
                    List<RankViewDataModel> temp = ListDataModel.get(j);
                    ListDataModel.set(j, ListDataModel.get(j + 1));
                    ListDataModel.set(j + 1, temp);
                }
            }
        }
    }
    
    //更新刮完的数据
    class UpdateDataOpenView extends AsyncTask<Map<String,View>,Void,Integer>{
   	 @Override
   	protected Integer doInBackground(Map<String, View>... params) {
   		final Map<String,View> mapview=params[0];
   		String param="list=";
   		int m=0;
   		for(String key:mapview.keySet()){
   			if (m == 0) {
   				param+=key;
   				m=m+1;
   			} else {
   				param+=(","+key);
   			}
   		}
   		Util.getRealInfo(param, new NetResult() {
   			@Override
   			public void result(final String msg) {
   				try {
   					final org.json.JSONObject object = new org.json.JSONObject(msg);
   					if (object.getInt("code") == 1) {
   						org.json.JSONArray arr = object.getJSONArray("result");
   						for (int i = 0; i < arr.length(); i++) {
   							JSONObject object2 = arr.getJSONObject(i);
   							JSONArray array = object2.getJSONArray("data");
   							String code = object2.getString("code");
   							String market=object2.getString("market");
   							//装载数据
   							Map<String,String> param=new HashMap<String, String>();
   							param.put("now",(float)array.getDouble(0)+"");
   							param.put("num",(float)array.getDouble(8)+"");
   							param.put("parent",(float)array.getDouble(9)+"");
   							resultMap.put(market+"|"+code,param);
   							//获取对应的view
   							final OneGu mOneGu=new OneGu();
   							mOneGu.setCode(code);
                            mOneGu.setNow((float)array.getDouble(0));
//                          mOneGu.setName(InitData.guKeyMap.get(market+code).getName());
                            mOneGu.setName(resultName.get(market+code));                               
                            mOneGu.setMarket(market);
                            mOneGu.setKey(market+code);
                            mOneGu.setYclose(array.optDouble(1));
                            mOneGu.setKaipanjia(array.optDouble(2));
                            mOneGu.setP_changes(((float) array.optDouble(9,0)>0?"+":"")+(float)array.optDouble(9,0)+"%");
                            mOneGu.setChange((float) array.optDouble(9, 0));
                            View tempview=mapview.get(market+"|"+code);
   							tempview.setOnClickListener(new OnClickListener() {
   								@Override
								public void onClick(View v) {
									if(mOneGu != null && mOneGu.getCode() != null
											&& mOneGu.getMarket() != null
											&& mOneGu.getName() != null
											&& mOneGu.getKey() != null){
										Intent intent = new Intent(activity,OneGuActivity.class);
										//用Bundle携带数据
										Bundle bundle = new Bundle();
										//传递name参数为tinyphp
										bundle.putString("code", mOneGu.getCode());
										bundle.putString("market", mOneGu.getMarket());
										bundle.putString("name", mOneGu.getName());
										bundle.putString("key", mOneGu.getKey());
										bundle.putSerializable("onegu", mOneGu);
										intent.putExtras(bundle);
										activity.startActivity(intent);
									}
									}
								});
   						}
   					}
   					Message message=Message.obtain();
   					message.what=3;
   					mHandler.sendMessage(message);
   				} catch (Exception e) {
   					LogUtil.e("jingwuview_exception",e.getMessage());
   				}
   			}
   		},true);
   		return null;
   	}
   	 	@Override
   	 	protected void onPostExecute(Integer result) {
   	 		super.onPostExecute(result);
   	 	}  
    }
    
    @SuppressLint("HandlerLeak")
	final Handler mHandler = new Handler() {
        @SuppressWarnings("deprecation")
       public void handleMessage(Message msg) {
            switch (msg.what) {
            case 1:
                if(mViewPager.getAdapter()==null){
                    mViewPager.setAdapter(adapter);
                    mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
                        @Override
                        public void onPageSelected(int page) {
                            checkFooterImage(page);
                        }
                        @Override
                        public void onPageScrolled(int arg0, float arg1, int arg2) {
                        }
                        @Override
                        public void onPageScrollStateChanged(int arg0) {
                        }
                    });
                }
                jingwu_relative.setVisibility(View.GONE);
                mViewPager.setVisibility(View.VISIBLE);
               break;
            case 2:
//           	ProgressDlgUtil.stopProgressDlg();
                break;
            case 3:
           	 for(String key:resultMap.keySet()){
           		 View view= MapView.get(key);
           		 float num = 0;
           		 ((TextView)view.findViewById(R.id.rankview_now)).setText(resultMap.get(key).get("now"));
           		 ((TextView)view.findViewById(R.id.rankview_num)).setText(resultMap.get(key).get("num"));
           		 ((TextView)view.findViewById(R.id.rankview_percent)).setText(resultMap.get(key).get("parent")+"%");
           		 num= Float.valueOf(resultMap.get(key).get("num"));
        		 if (num > 0) {
        			((RelativeLayout)view.findViewById(R.id.tljr_rankview_item)).setBackground(activity.getResources().getDrawable(R.drawable.img_tuijian02));
        		 } else {
        			 ((RelativeLayout)view.findViewById(R.id.tljr_rankview_item)).setBackground(activity.getResources().getDrawable(R.drawable.img_tuijian01));
        		 }
           	   }
           	 break;
            }
 
        };
    };
    
 // 初始化viewpager的adapter
    PagerAdapter adapter = new PagerAdapter() {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(listrankview.get(position));
        };
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(listrankview.get(position));
            return listrankview.get(position);
        };
        @Override
        public int getCount() {
            return listrankview.size();
        }
    };
    
    // 改变尾部的圆圈
    public void checkFooterImage(int page) {
        for (int i = 0; i < listrankview.size(); i++){
            if (page == i) {
                img[i].setBackgroundResource(R.drawable.img_yuandian1);
            } else {
                img[i].setBackgroundResource(R.drawable.img_yuandian2);
            }
        }
    }
    
    public void setMessage(){
    	if(mhandler!=null){
    		Message message=Message.obtain();
    		message.what=1;
    		mhandler.sendMessage(message);
    	}
    }
    public void flush(){
    	new UpdateDataOpenView().execute(MapView);	
    }
    class update extends TimerTask{
    	@SuppressWarnings("unchecked")
		@Override
    	public void run() {
    		LogUtil.e("TimerTasked","timer");
    		new UpdateDataOpenView().execute(MapView);	
    	}
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.main_jingwu_bu:
			jingwu_progressbar.setVisibility(View.VISIBLE);
			main_chongzai.setVisibility(View.GONE);
			new UpdateRankViewData().execute();
			break;
		}
	}

}

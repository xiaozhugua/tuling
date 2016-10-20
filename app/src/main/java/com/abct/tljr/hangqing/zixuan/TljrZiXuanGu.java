package com.abct.tljr.hangqing.zixuan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.abct.tljr.R;
import com.abct.tljr.data.ZiXuanUtil;
import com.abct.tljr.hangqing.util.GuDealImpl;
import com.abct.tljr.hangqing.util.ParseJson;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.model.OneFenZu;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ui.widget.ObservableScrollView;
import com.abct.tljr.ui.widget.ScrollViewListener;
import com.abct.tljr.utils.Util;
import com.abct.tljr.wxapi.WXEntryActivity;
import com.qh.common.listener.NetResult;
import com.qh.common.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/2/14.
 */

public class TljrZiXuanGu {
    private ObservableScrollView tljr_page_zixuan_title=null;
    private ObservableScrollView tljr_page_zixuan_item=null;
    private static Map<String,View> viewItem=null;
    public static Map<String,View> viewName=null;
    private static LinearLayout titles=null;
    private static LinearLayout items=null;
    private static Context context=null;
    private static int backgroundnum=0;
    private static RelativeLayout zixuanlogin=null;
    private static RelativeLayout zixuanview=null;
    
    public TljrZiXuanGu(final Context context,View view){
        this.context=context;
        tljr_page_zixuan_title=(ObservableScrollView)view.findViewById(R.id.tljr_page_zixuan_title);
        tljr_page_zixuan_item=(ObservableScrollView)view.findViewById(R.id.tljr_page_zixuan_items);
        titles = (LinearLayout) tljr_page_zixuan_title.getChildAt(0);
        items = (LinearLayout) tljr_page_zixuan_item.getChildAt(0);
        zixuanlogin=(RelativeLayout)view.findViewById(R.id.zixuan_login);
        zixuanview=(RelativeLayout)view.findViewById(R.id.zixuan_view);
        zixuanlogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(context,WXEntryActivity.class);
				context.startActivity(intent);
			}
		});
        viewItem=new HashMap<String,View>();
        viewName=new HashMap<String,View>();
        tljr_page_zixuan_title.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x,int y, int oldx, int oldy) {
                tljr_page_zixuan_item.scrollTo(x, y);
            }
        });
        tljr_page_zixuan_item.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x,int y, int oldx, int oldy) {
                tljr_page_zixuan_title.scrollTo(x, y);
            }
        });
        initUI();
        reflush(null);
    }

    @SuppressLint("InflateParams")
	public static void initUI(){
       if(User.getUser()!=null){
    	   try{
           	  if(titles!=null){
           		 titles.removeAllViews();
                 items.removeAllViews();
                 zixuanlogin.setVisibility(View.GONE);
        	     zixuanview.setVisibility(View.VISIBLE);
                 if(TljrZiXuan.ZiXuanGu.size()>0){
                     //初始化每一个item
                     for(String code:TljrZiXuan.ZiXuanGu.keySet()){
                     	 final OneGu mOneGu=GuDealImpl.getOneGu(TljrZiXuan.ZiXuanGu.get(code));
                     	 addZiXuanGuView(new ViewHolder(),context,mOneGu,false);
                     }
                     sendRefreMessage();
                 }
           	  }
           }catch(Exception e){
           }
       }else{
    	   zixuanlogin.setVisibility(View.VISIBLE);
  	       zixuanview.setVisibility(View.GONE);
       }
    }

    //为每个item赋值
    @SuppressWarnings("deprecation")
	public static void initOneItem(ViewHolder mViewHolder,OneGu mOneGu,Boolean status){
            try{
                if(mOneGu.getFirst()!=0){
                    mViewHolder.nowprice.setText(mOneGu.getNow()+"");
                }else{
                	mViewHolder.nowprice.setText("--");
                }
                if(mOneGu.getP_change()!=0){
                    mViewHolder.change.setText(mOneGu.getP_changes()+"");
                }else{
                	mViewHolder.change.setText("--");
                }
                mViewHolder.change.setTextColor(context.getResources().getColor(R.color.white));
                if(mOneGu.getP_change()>0){
                	mViewHolder.background.setBackgroundColor(context.getResources().getColor(R.color.tljr_hq_zx_up));
                }else if(mOneGu.getP_change()<0){
                	mViewHolder.background.setBackgroundColor(context.getResources().getColor(R.color.tljr_hq_zx_down));
                }else{
                	mViewHolder.background.setBackgroundColor(context.getResources().getColor(R.color.tljr_gray));
                }
                if(mOneGu.getYclose()==0.0){
                	mViewHolder.changeprice.setText("--");
                }else{
                	mViewHolder.changeprice.setText(mOneGu.getYclose()+"");
                }
                if(mOneGu.getKaipanjia()==0.0){
                	 mViewHolder.yclose.setText("--");
                }else{
                	 mViewHolder.yclose.setText(mOneGu.getKaipanjia()+"");
                }	
                if(mOneGu.getChange()==0.0){
                	mViewHolder.kaipanjia.setText("--");
                }else{
                	mViewHolder.kaipanjia.setText(mOneGu.getChange()+"");
                }
            }catch(Exception e){
            }
    }

    public static void reflush(final Handler mHandler){
      // 获取股票列表参数
      String param = "list=";
      int i=0;	
      for(String key:TljrZiXuan.ZiXuanGu.keySet()){
    	  if(i==0){
    		  param+=TljrZiXuan.ZiXuanGu.get(key).getMarket()+"|"+TljrZiXuan.ZiXuanGu.get(key).getCode();
    		  i=1;
    	  }else{
    		  param+=","+TljrZiXuan.ZiXuanGu.get(key).getMarket()+"|"+TljrZiXuan.ZiXuanGu.get(key).getCode();
    	  }
      }
      // 网上获取最新数据
      Util.getRealInfo(param, new NetResult() {
          @Override
          public void result(final String msg) {
              try {
            	if(!msg.equals("")){
            		  List<OneGu> listgu=ParseJson.ParseJosnMapGuInfo(TljrZiXuan.ZiXuanGu,msg);
                	  updateGuView(listgu);
                	  if(mHandler!=null){
                		  Message message=new Message();
                		  message.what=2;
                		  mHandler.sendMessage(message);
                	  }
            	}else{
            		Intent intent=new Intent("com.tljr.zixuan");
            		context.sendBroadcast(intent);
            	}
              } catch (Exception e) {
            	 
              }
          }
      },true);
  }
    	
   public static void updateGuView(List<OneGu> listGu){
	   OneGu mOneGu=null;
	   for(int i=0;i<listGu.size();i++){
		   View item=viewItem.get(listGu.get(i).getCode());
		   mOneGu=listGu.get(i);
		   ViewHolder mViewHolder=(ViewHolder)item.getTag();
		   initOneItem(mViewHolder,mOneGu,true);
	   }
   } 
    
   public static void sendRefreMessage(){
	   Intent intent=new Intent("com.tljr.zixuan");
	   context.sendBroadcast(intent);
   }
   
   public static void RemoveZiXuanGuView(String code){
	  if(titles!=null&&items!=null){
		  int GuNum=0;
		  ArrayList<OneFenZu> ListZu=new  ArrayList<OneFenZu>(ZiXuanUtil.fzMap.values());
		  for(OneFenZu zu:ListZu){
			  for(OneGu gu:zu.getList()){
				  if(code.equals(gu.getCode())){
					  GuNum+=1;
				  }
			  }
		  }		  
		  if(GuNum<=1){
			  titles.removeView(viewName.get(code));
			  items.removeView(viewItem.get(code));
			  viewName.remove(code);
			  viewItem.remove(code);
		  }
	  }
   }
   
   @SuppressWarnings("deprecation")
   @SuppressLint("InflateParams")
   public static void addZiXuanGuView(ViewHolder holder,final Context context,final OneGu mOneGu,boolean status){
	   View name=LayoutInflater.from(context).inflate(R.layout.tljr_page_zixuan_itemname, null);
	   View item = LayoutInflater.from(context).inflate(R.layout.tljr_page_zixuan_item,null);
	   if((backgroundnum%2)==0){
		   name.setBackgroundColor(context.getResources().getColor(R.color.bj));
		   item.setBackgroundColor(context.getResources().getColor(R.color.bj));
	   }else{
		   name.setBackgroundColor(context.getResources().getColor(R.color.white));
		   item.setBackgroundColor(context.getResources().getColor(R.color.white));
	   }
       ((TextView)name.findViewById(R.id.tljr_page_zixuan_name)).setText(mOneGu.getName());
       ((TextView)name.findViewById(R.id.tljr_page_zixuan_code)).setText(mOneGu.getCode());
       name.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,Util.HEIGHT / 15));
       item.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Util.HEIGHT / 15));
       item.setPadding(0, 10, 0, 0);
       holder.nowprice = (TextView) item.findViewById(R.id.tljr_page_zixuan_nowprice);
       holder.change = (TextView) item.findViewById(R.id.tljr_page_zixuan_change);
       holder.kaipanjia = (TextView) item.findViewById(R.id.tljr_page_zixuan_kaipanjia);
       holder.yclose = (TextView) item.findViewById(R.id.tljr_page_zixuan_yclose);
       holder.changeprice = (TextView) item.findViewById(R.id.tljr_page_zixuan_changeprice);
       holder.background=(RelativeLayout)item.findViewById(R.id.tljr_grp_changeitem);
       holder.oneGu = mOneGu;
       name.setOnClickListener(new View.OnClickListener() {
    	   @Override
    	   public void onClick(View v) {
    		   turnActivity(mOneGu);
    	   }
       });
       item.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
        	   turnActivity(mOneGu);
           }
       });
       item.setTag(holder);
       //为初始化赋值
       viewName.put(mOneGu.getCode(),name);
       viewItem.put(mOneGu.getCode(),item);
       titles.addView(viewName.get(mOneGu.getCode()));
       items.addView(viewItem.get(mOneGu.getCode()));
       if(status){
    	   TljrZiXuan.ZiXuanGu.put(mOneGu.getCode(),GuDealImpl.getZiXuanGu(mOneGu));
    	   initOneItem(holder,mOneGu,false);
       }
       backgroundnum++;
   }
   
   public static void turnActivity(OneGu mOneGu){
	   Intent intent=new Intent(context,OneGuActivity.class);
		Bundle bundle=new Bundle();
		if(mOneGu.getName()!=null){
			bundle.putString("code", mOneGu.getCode());
			bundle.putString("market", mOneGu.getMarket());
			bundle.putString("name", mOneGu.getName());
			bundle.putString("key", mOneGu.getKey());
			bundle.putSerializable("onegu", mOneGu);
			intent.putExtras(bundle);
			context.startActivity(intent);
		} 
   }
   
   public static void removeZuGu(String ZuName){
	   OneFenZu mOneFenZu=ZiXuanUtil.fzMap.get(ZuName);
	   for(OneGu gu:mOneFenZu.getList()){
		   RemoveZiXuanGuView(gu.getCode());
	   }
   }
   
   public static class ViewHolder{
       public TextView nowprice;   //当前价
       public TextView change;    //涨跌幅
       public TextView yclose;   //昨收
       public TextView changeprice;    //最低价
       public TextView kaipanjia; //昨收价
       public OneGu oneGu;
       public RelativeLayout background;
   }
   
}

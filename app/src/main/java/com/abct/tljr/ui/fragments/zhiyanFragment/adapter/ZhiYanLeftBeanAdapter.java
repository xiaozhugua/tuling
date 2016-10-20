package com.abct.tljr.ui.fragments.zhiyanFragment.adapter;

import java.util.List;
import com.abct.tljr.R;
import com.abct.tljr.ui.fragments.zhiyanFragment.model.ZhiYanLeftBean;
import com.qh.common.util.LogUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ZhiYanLeftBeanAdapter extends ArrayAdapter<ZhiYanLeftBean> {

	private Context context = null;
	private int Resource;

	public ZhiYanLeftBeanAdapter(Context context, int resource,List<ZhiYanLeftBean> objects) {
		super(context, resource, objects);
		this.context = context;
		this.Resource = resource;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ZhiYanLeftBean bean=getItem(position);
		View view=null;
		ZhiYanViewHolder viewHolder=null;
		if (convertView == null) {
            view =LayoutInflater.from(getContext()).inflate(Resource,null);
            viewHolder=new ZhiYanViewHolder();
            viewHolder.title=(TextView)view.findViewById(R.id.name);
            viewHolder.youjiantou=(ImageView)view.findViewById(R.id.zhiyan_youjiantou);
            viewHolder.mRelativeLayoutBg=(RelativeLayout)view.findViewById(R.id.finishitembg);
            viewHolder.xingxing1=(ImageView)view.findViewById(R.id.img_xingxing1);
            viewHolder.xingxing2=(ImageView)view.findViewById(R.id.img_xingxing2);
            viewHolder.xingxing3=(ImageView)view.findViewById(R.id.img_xingxing3);
            viewHolder.xingxing4=(ImageView)view.findViewById(R.id.img_xingxing4);
            viewHolder.xingxing5=(ImageView)view.findViewById(R.id.img_xingxing5);
            view.setTag(viewHolder);
        }else{
        	view=convertView;
        	viewHolder=(ZhiYanViewHolder)view.getTag();
        }

	   viewHolder.title.setText(bean.getName()+(bean.getCount()==0?"":("("+bean.getCount()+")")));
	   
	   viewHolder.xingxing1.setVisibility(View.GONE);
	   viewHolder.xingxing2.setVisibility(View.GONE);
	   viewHolder.xingxing3.setVisibility(View.GONE);
	   viewHolder.xingxing4.setVisibility(View.GONE);
	   viewHolder.xingxing5.setVisibility(View.GONE);
	   
	   for(int i=0;i<DealStart(bean.getStar());i++){
		   if(i==0)
		   viewHolder.xingxing1.setVisibility(View.VISIBLE);
           if(i==1)
		   viewHolder.xingxing2.setVisibility(View.VISIBLE);
           if(i==2)
           viewHolder.xingxing3.setVisibility(View.VISIBLE);
           if(i==3)
           viewHolder.xingxing4.setVisibility(View.VISIBLE);
           if(i==4)
           viewHolder.xingxing5.setVisibility(View.VISIBLE);
	   }
	   
	   if(bean.getPosition()<0){
		   viewHolder.title.setTextColor(context.getResources().getColor(R.color.black));
		   viewHolder.mRelativeLayoutBg.setBackgroundColor(context.getResources().getColor(R.color.white));
		   viewHolder.youjiantou.setVisibility(View.GONE);
	   }else{
		   viewHolder.title.setTextColor(context.getResources().getColor(R.color.white));
		   viewHolder.mRelativeLayoutBg.setBackgroundColor(context.getResources().getColor(R.color.red));
		   viewHolder.youjiantou.setVisibility(View.VISIBLE);
	   }
       
        return view;
	}

	class ZhiYanViewHolder{
		public TextView title=null;
		public ImageView youjiantou=null;
		public RelativeLayout mRelativeLayoutBg=null;
		public ImageView xingxing1=null;
		public ImageView xingxing2=null;
		public ImageView xingxing3=null;
		public ImageView xingxing4=null;
		public ImageView xingxing5=null;
	}
	
	public int DealStart(double start){
		if(start>0&&start<0.5){
			return 1;
		}
		return (int)Math.rint(start);
	}

	
	
}

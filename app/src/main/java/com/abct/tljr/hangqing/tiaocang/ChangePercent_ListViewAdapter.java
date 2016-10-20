package com.abct.tljr.hangqing.tiaocang;

import java.text.DecimalFormat;
import java.util.List;
import com.abct.tljr.R;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.utils.Util;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChangePercent_ListViewAdapter extends ArrayAdapter<OneGu>{

	private int resourceId;
	private Context context;
	
	public ChangePercent_ListViewAdapter(Context context, int resource,List<OneGu> objects,String Tagname) {
		super(context, resource, objects);
		this.resourceId=resource;
		this.context=context;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final OneGu gu=getItem(position);
		View view=LayoutInflater.from(getContext()).inflate(this.resourceId,null);
		TextView NameText=(TextView)view.findViewById(R.id.tljr_changepercent_name_test);
		NameText.setText(gu.getName());
		
		if(gu.getName().length()>10){
			NameText.setTextSize(12);
		}
		
		((TextView)view.findViewById(R.id.tljr_changepercent_info_test)).setText(gu.getCode());
		((TextView)view.findViewById(R.id.tljr_changepercent_now_test)).setText(Util.df.format(gu.getYclose()));
		
		 if (gu.getNow() == 0) {
			 ((TextView)view.findViewById(R.id.tljr_changepercent_now_test)).setText(Util.df.format(gu.getYclose()));
			 ((TextView)view.findViewById(R.id.tljr_changepercent_qchange_test)).setText(gu.getYclose()==0?"--":"停牌");
			 ((TextView)view.findViewById(R.id.tljr_changepercent_qchange_test)).setBackgroundColor(
				   getContext().getResources().getColor(R.color.tljr_gray));
         } else {
        	 ((TextView)view.findViewById(R.id.tljr_changepercent_now_test)).setText(Util.df.format(gu.getNow()));
        	 if(gu.getP_changes().length()>6){
        		 ((TextView)view.findViewById(R.id.tljr_changepercent_qchange_test)).setTextSize(12);
        	 }
        	 ((TextView)view.findViewById(R.id.tljr_changepercent_qchange_test)).setText(gu.getP_changes());
        	 ((TextView)view.findViewById(R.id.tljr_changepercent_qchange_test)).setBackgroundColor(gu.getP_change() > 0?
          		   getContext().getResources().getColor(R.color.tljr_hq_zx_up)
          		   :getContext().getResources().getColor(R.color.tljr_hq_zx_down));
         }
		
		 view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					Intent intent=new Intent(context,OneGuActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("code",gu.getCode());
					bundle.putString("market",gu.getMarket().toLowerCase());
					bundle.putString("name", gu.getName());
					bundle.putString("key", gu.getMarket().toLowerCase()+gu.getCode());
					intent.putExtras(bundle);
					context.startActivity(intent);
			}
		});
		 
		return view;
	}

	
}

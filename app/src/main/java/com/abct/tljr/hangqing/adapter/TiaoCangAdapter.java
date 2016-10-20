package com.abct.tljr.hangqing.adapter;

import java.util.List;
import com.abct.tljr.R;
import com.abct.tljr.hangqing.model.CodePercent;
import com.qh.common.util.LogUtil;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class TiaoCangAdapter extends ArrayAdapter<CodePercent> {

	private int ResourceId = 0;
	private Context context=null;
	private List<CodePercent> listPer;
	private int startpercent=0;
	
	public TiaoCangAdapter(Context context,int resource,List<CodePercent> objects) {
		super(context, resource, objects);
		this.ResourceId = resource;
		this.context=context;
		this.listPer=objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		final ViewHolder mViewHolder ;
		final CodePercent mCodePercent = getItem(position);

		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(this.ResourceId,parent, false);
			mViewHolder = new ViewHolder();
			mViewHolder.name = (TextView) view.findViewById(R.id.tiaocang_name);
			mViewHolder.price = (TextView) view.findViewById(R.id.tiaocang_code);
			mViewHolder.percent = (TextView) view.findViewById(R.id.tiaocang_percent);
			mViewHolder.seekbar = (SeekBar) view.findViewById(R.id.tiaocang_progressbar);
			mViewHolder.tinpai = (TextView) view.findViewById(R.id.tljr_zx_percent_tinpai);
			mViewHolder.status=(ImageView) view.findViewById(R.id.tiaocang_status);
			view.setTag(mViewHolder);
		} else {
			view = convertView;
			mViewHolder = (ViewHolder) view.getTag();
		}

		if ((mCodePercent.getCodeprice() + "").equals("NaN")) {
			mViewHolder.price.setText(0.0 + "");
		} else {
			mViewHolder.price.setText(mCodePercent.getCodeprice() + "");
		}

		mViewHolder.name.setText(mCodePercent.getCodename());
		mViewHolder.percent.setText(mCodePercent.getCangwei() + "%");
		mViewHolder.seekbar.setProgress(mCodePercent.getCangwei());
		
		LogUtil.e("CoedPercent_Change",mCodePercent.getChange()+"");
		
		if(mCodePercent.getChange()==0){
			mViewHolder.status.setImageResource(R.drawable.img_tingpaibiaozhi);
		}else if(mCodePercent.getChange()>0){
			mViewHolder.status.setImageResource(R.drawable.img_shangzhangbiaozhi);
		}else {
			mViewHolder.status.setImageResource(R.drawable.img_xiadiebiaozhi);
		}
		
		// 判断股票是不是停牌，期货，指数
		if (mCodePercent.getKaipanjia() == 0.0|| Float.isNaN((float) mCodePercent.getKaipanjia())) {
			mViewHolder.seekbar.setEnabled(false);
			mViewHolder.tinpai.setVisibility(View.VISIBLE);
			mViewHolder.tinpai.setText("股票停牌");
		} else if (!mCodePercent.getMarket().equals("sz")&& !mCodePercent.getMarket().equals("sh")) {
			mViewHolder.seekbar.setEnabled(false);
			mViewHolder.tinpai.setVisibility(View.VISIBLE);
			mViewHolder.tinpai.setText("期货不能调仓");
		} else if (mCodePercent.getCodeprice() > 1000) {
			mViewHolder.seekbar.setEnabled(false);
			mViewHolder.tinpai.setVisibility(View.VISIBLE);
			mViewHolder.tinpai.setText("指数不能调仓");
		} else {
			mViewHolder.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
					mViewHolder.percent.setText(progress + "%");
					mCodePercent.setCangwei(progress);
					int goumai = 0;
					for (CodePercent code : listPer) {
						goumai += code.getCangwei();
					}
					int shengyu = 100 - goumai;
					Intent intent = new Intent("com.changpercent.xianjin");
					if (shengyu <= 0) {
						intent.putExtra("shengyu", 0);
					} else {
						intent.putExtra("shengyu", shengyu);
					}
					intent.putExtra("goumai", goumai);
					context.sendBroadcast(intent);
				}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					int allpercent = 0;
					for (CodePercent code : listPer) {
						allpercent += code.getCangwei();
					}
					if (allpercent > 100) {
						int zuidaperctent = 100 - allpercent+ mCodePercent.getCangwei();
						mCodePercent.setCangwei(zuidaperctent);
						mViewHolder.percent.setText(zuidaperctent + "%");
						seekBar.setProgress(zuidaperctent);
					}
					int goumai = 0;
					for (CodePercent code : listPer) {
						goumai += code.getCangwei();
					}
					int shengyu = 100 - goumai;
					Intent intent = new Intent("com.changpercent.xianjin");
					intent.putExtra("goumai", goumai);
					intent.putExtra("shengyu", shengyu);
					context.sendBroadcast(intent);
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					startpercent = mCodePercent.getCangwei();
				}
			});
		}

		return view;
	}

	class ViewHolder {
		public TextView name;
		public TextView price;
		public SeekBar seekbar;
		public TextView percent;
		public TextView tinpai;
		public ImageView status;
	}

}

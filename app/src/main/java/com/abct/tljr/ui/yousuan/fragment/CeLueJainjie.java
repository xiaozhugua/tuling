package com.abct.tljr.ui.yousuan.fragment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.abct.tljr.R;
import com.abct.tljr.ui.yousuan.activity.YouSuanItem;
import com.abct.tljr.ui.yousuan.model.YouSuanItemModel;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CeLueJainjie extends Fragment{

	private View celie=null;
	private TextView jianjie_yueshouyi=null;
	private TextView jianjie_shipanshouyi=null;
	private TextView jianjie_nianshouyi=null;
	private TextView jianjie_zuidahuice=null;
	private TextView jianjie_jianyipinlu=null;
	private TextView jianjie_updatetime=null;
	private TextView jianjie_ganggang=null;
	private TextView jianjie_shouyifengxianbi=null;
	private TextView jianjie_shenglu=null;
	private TextView jianjie_yinkuibi=null;
	private TextView jianjie_shipantianshu=null;
	private TextView jianjie_peibeizijin=null;
	private TextView jianjie_pingzhong=null;
	private TextView jianjie_zhouqi=null;
	private TextView jianjie_type=null;
	private TextView jianjie_leibie=null;
	private TextView jianjie_desc=null;
	
	public YouSuanItemModel model=null;
	
	public SimpleDateFormat mSimpleDateFormat=null;
	public DecimalFormat mDecimalFormat=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		celie=inflater.inflate(R.layout.celiejianjie,container,false);
		mSimpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
		mDecimalFormat=new DecimalFormat("0.00");
		this.model=((YouSuanItem)getActivity()).model;
        if(model==null){
            this.model=((YouSuanItem)getActivity()).itemModel.getGetTou();
        }
		initView();
		initData();
		return celie;
	}
	
	public void initView(){
		jianjie_yueshouyi=(TextView)celie.findViewById(R.id.jianjie_yueshouyi);
		jianjie_shipanshouyi=(TextView)celie.findViewById(R.id.jianjie_shipanshouyi);
		jianjie_nianshouyi=(TextView)celie.findViewById(R.id.jianjie_nianshouyi);
		jianjie_zuidahuice=(TextView)celie.findViewById(R.id.jianjie_zuidahuice);
		jianjie_jianyipinlu=(TextView)celie.findViewById(R.id.jianjie_jianyipinlu);
		jianjie_updatetime=(TextView)celie.findViewById(R.id.jianjie_updatetime);
		jianjie_ganggang=(TextView)celie.findViewById(R.id.jianjie_ganggang);
		jianjie_shouyifengxianbi=(TextView)celie.findViewById(R.id.jianjie_shouyifengxianbi);
		jianjie_shenglu=(TextView)celie.findViewById(R.id.jianjie_shenglu);
		jianjie_yinkuibi=(TextView)celie.findViewById(R.id.jianjie_yinkuibi);
		jianjie_shipantianshu=(TextView)celie.findViewById(R.id.jianjie_shipantianshu);
		jianjie_peibeizijin=(TextView)celie.findViewById(R.id.jianjie_peibeizijin);
		jianjie_pingzhong=(TextView)celie.findViewById(R.id.jianjie_pingzhong);
		jianjie_zhouqi=(TextView)celie.findViewById(R.id.jianjie_zhouqi);
		jianjie_type=(TextView)celie.findViewById(R.id.jianjie_type);
		jianjie_leibie=(TextView)celie.findViewById(R.id.jianjie_leibie);
		jianjie_desc=(TextView)celie.findViewById(R.id.jianjie_desc);
	}
	
	public void initData(){
		jianjie_yueshouyi.setText(Float.valueOf(mDecimalFormat.format(model.getCurrentMoonRate()))*100+"%");
//		jianjie_yueshouyi.setText(model.getCurrentMoonRate()+"");
		jianjie_shipanshouyi.setText(Float.valueOf(mDecimalFormat.format(model.getActualRate()))*100+"%");
		jianjie_nianshouyi.setText(Float.valueOf(mDecimalFormat.format(model.getAnnualRate()))*100+"%");
		jianjie_zuidahuice.setText(mDecimalFormat.format(model.getMaxDrawDownMoney())+"");
		jianjie_jianyipinlu.setText(model.getTradingCount()+"");
		jianjie_updatetime.setText(mSimpleDateFormat.format(new Date(model.getUpdateDate()*1000)));
		jianjie_ganggang.setText(model.getLeverMultiple()+"");
		jianjie_shouyifengxianbi.setText(mDecimalFormat.format(model.getEarningsRisk())+"");
		jianjie_shenglu.setText(Float.valueOf(mDecimalFormat.format(model.getOdds()))*100+"%");
		jianjie_yinkuibi.setText(mDecimalFormat.format(model.getProfitAndLossThan())+"");
		jianjie_shipantianshu.setText(model.getDays()+"");
		jianjie_peibeizijin.setText(model.getAdviceMoney()+"");
		jianjie_pingzhong.setText(model.getVariety()+"");
		jianjie_zhouqi.setText(model.getPeriod()+"");
		jianjie_type.setText(model.getPositionType()+"");
		jianjie_leibie.setText(model.getCategory()+"");
		jianjie_desc.setText(model.getDesc()+"");
	}
	
}

package com.abct.tljr.ui.yousuan.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.utils.Util;

public class YouSuanData extends BaseActivity implements OnClickListener{
	private LinearLayout yousuandata=null;
	private YouSuanChiCang mYouSuanChiCang=null;
	private YouSuanRecord mYouSuanRecord=null;
	private View back=null;
	private View chicang;
	private View record;

	private int dataid=0;
	private String key=null;
	private TextView yousuandata_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yousuandata);
		
		dataid=getIntent().getIntExtra("DataId",0);
		key=getIntent().getStringExtra("YouSuanKey");
		
		initView();
	}
	
	public void initView(){
		yousuandata=(LinearLayout)findViewById(R.id.yousuan_linearlayout);
		back=(View)findViewById(R.id.tljr_yousuandata_fanhui);
		yousuandata_name=(TextView)findViewById(R.id.yousuandata_name);
        yousuandata_name.setText(getIntent().getStringExtra("name"));
		mYouSuanChiCang=new YouSuanChiCang(this,dataid,key);
		mYouSuanRecord=new YouSuanRecord(this,dataid,key);
		back.setOnClickListener(this);
		
		chicang=mYouSuanChiCang.getView();
		record=mYouSuanRecord.getView();
		
		chicang.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,Util.getScreenHeight(this)/3));
		
		yousuandata.addView(chicang);
		yousuandata.addView(record);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tljr_yousuandata_fanhui:
			finish();
			break;
		}
	}
	
}

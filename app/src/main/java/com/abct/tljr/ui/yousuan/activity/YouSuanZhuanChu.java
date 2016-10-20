package com.abct.tljr.ui.yousuan.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;

public class YouSuanZhuanChu extends BaseActivity implements OnClickListener{

	private View back=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yousuan_zhuanchu);
		
		back=(View)findViewById(R.id.tljr_yousuan_fanhui);
		back.setOnClickListener(this);
		
	}

	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tljr_yousuan_fanhui:
			finish();
			break;
		}
	}
	
}

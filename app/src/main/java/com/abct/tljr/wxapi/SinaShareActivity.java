package com.abct.tljr.wxapi;

import com.abct.tljr.BaseActivity;
import com.qh.common.login.BwManager;

import android.os.Bundle;

public class SinaShareActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BwManager.getInstance().initShare(this);
		postDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
			}
		}, 1);
	}
}

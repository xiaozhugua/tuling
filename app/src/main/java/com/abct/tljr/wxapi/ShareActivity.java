package com.abct.tljr.wxapi;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.qh.common.login.BwManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ShareActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BwManager.getInstance().initShare(this);
		setContentView(R.layout.tljr_activity_share);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		BwManager.getInstance().onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		BwManager.getInstance().onNewIntent(intent);
		super.onNewIntent(intent);
	}

	public void wx(View view) {
		BwManager.getInstance().shareWeiXin();
	}

	public void pyq(View view) {
		BwManager.getInstance().shareWeiXinPyq();
	}

	public void qq(View view) {
		BwManager.getInstance().shareQQ();
	}

	public void wb(View view) {
		BwManager.getInstance().shareSina();
	}

	public void back(View view) {
		finish();
	}

}

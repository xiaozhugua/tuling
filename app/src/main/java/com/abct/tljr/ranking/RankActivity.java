package com.abct.tljr.ranking;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;

/**
 * @author xbw
 * @version 创建时间：2015年8月8日 下午2:21:21
 */
public class RankActivity extends BaseActivity {
	private RankView rankView;
	private boolean isStart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_activity_rank);
		findViewById(R.id.tljr_img_rank_fanhui).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
		LinearLayout layout = (LinearLayout) findViewById(R.id.tljr_grp_rank_list);
		rankView = new RankView(this);
		layout.addView(rankView.getView());
		rankView.init();
		isStart = true;
		post(r);
	}

	Runnable r = new Runnable() {

		@Override
		public void run() {
			if (isStart)
				postDelayed(r, 1000);
			rankView.oneSecAction();
		}
	};

	protected void onDestroy() {
		super.onDestroy();
		isStart = false;
	};

}
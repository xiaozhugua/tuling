package com.abct.tljr.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;

import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.main.MainActivity;

public class CueDialog extends Dialog implements OnClickListener {
	int type  ;
	public CueDialog(Activity activity,int type) {
		super(activity, R.style.dialog);
		this.type =type ;
		if(type==1){
			setContentView(R.layout.tljr_news_cue);
		}else{
			setContentView(R.layout.tljr_news_cue2);
		}
	
		setCanceledOnTouchOutside(false);
		init();
		windowDeploy();
	}

	// 设置窗口显示
	public void windowDeploy()
	{
		Window win = getWindow(); // 得到对话框
		win.setWindowAnimations(R.style.speakdialog_bottom); // 设置窗口弹出动画
		win.getDecorView().setPadding(0, 0, 0, 0); // 宽度占满，因为style里面本身带有padding
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = WindowManager.LayoutParams.FILL_PARENT;
		win.setAttributes(lp);
	}

	private void init()
	{
		// TODO Auto-generated method stub
		if(type==1){
			findViewById(R.id.tljr_dialog_news_cue1).setOnClickListener(this);
		}	else if(type==2){
			findViewById(R.id.tljr_dialog_news_cue2).setOnClickListener(this);
		}
		
		
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.tljr_dialog_news_cue1:
			this.dismiss();
			Constant.isNewsGuideToast=type;
			Constant.preference.edit().putInt("isNewsGuideToast", type).commit(); 
			break;
			
		case R.id.tljr_dialog_news_cue2:
			this.dismiss();
			Constant.isNewsGuideToast=type;
			Constant.preference.edit().putInt("isNewsGuideToast", type).commit(); 
			break;

		default:
			break;
		}
	}

}
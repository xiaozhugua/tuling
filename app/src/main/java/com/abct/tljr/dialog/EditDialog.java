package com.abct.tljr.dialog;

import com.abct.tljr.R;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.CompleteStr;
import com.qh.common.model.AppInfo;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditDialog extends Dialog implements OnClickListener{

	private Context context;
	private CompleteStr complete;
	private EditText edit;
	
	public EditDialog(Context context,String title,String hint,CompleteStr complete) {
		super(context,R.style.dialog);
		// TODO Auto-generated constructor stub
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialogeditext);
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (AppInfo.WIDTH * 0.95);
		dialogWindow.setAttributes(p);
		findViewById(R.id.sure).setOnClickListener(this);
		findViewById(R.id.cancel).setOnClickListener(this);
		this.context = context;
		this.complete = complete;
		edit = (EditText) findViewById(R.id.edit);
		if(!hint.equals("")){
			findViewById(R.id.hint).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.hint)).setText(hint);
		}
//		findViewById(R.id.rl).getLayoutParams().width = Util.WIDTH;
		((TextView)findViewById(R.id.title)).setText(title);
		handler.sendEmptyMessageDelayed(1, 100);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.sure:
			if(edit.getText().toString().length()==0){
				Toast.makeText(context, "请输入内容", Toast.LENGTH_SHORT).show();
				return;
			}
			complete.completeStr(edit.getText().toString());
			InputMethodManager m = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			m .hideSoftInputFromWindow(edit.getWindowToken(), 0);
			cancel();
			break;
		case R.id.cancel:
			InputMethodManager m2 = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			m2 .hideSoftInputFromWindow(edit.getWindowToken(), 0);
			cancel();
			break;
		default:
			break;
		}
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				edit.setFocusable(true);
				InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				break;

			default:
				break;
			}
		};
	};

}

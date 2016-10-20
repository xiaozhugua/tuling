package com.abct.tljr.chart;

import org.json.JSONObject;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author xbw
 * @version 创建时间：2015-6-2 上午11:04:17
 */
public class EditDialog extends Dialog implements OnClickListener {
	private Context context;
	private Complete complete;
	private Complete cancelComplete;
	private EditText edit;
	private Handler handler;

	public EditDialog(Context context,Handler handler) {
		super(context, R.style.dialog);
		this.context = context;
		this.handler = handler;
		setContentView(R.layout.activity_chart_amswer);
		findViewById(R.id.sure).setOnClickListener(this);
		edit = (EditText) findViewById(R.id.edit);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.sure:
			if(!OneAskActivity.id.equals("")){
				Ask(edit.getText().toString(),OneAskActivity.id);
			}
			break;
		default:
			break;
		}
	}
	
	//回答
		public void Ask(String msg,String id){
			ProgressDlgUtil.showProgressDlg(null, (Activity)context);
			
			
			
			
			NetUtil.sendPost(UrlUtil.Url_235+"8080/TLChat/rest/userqa/answer", "uid="+MyApplication.getInstance().self.getId()+"&msg="+msg+"&id="+id, new NetResult() {

				@Override
				public void result(final String msg) {

					handler.post(new Runnable() {

						@Override
						public void run() {
							 try{
			                	   if(!msg.equals("error")){
			                       	JSONObject js =new JSONObject(msg);
			                       	if(js!=null&&js.getInt("status") == 1){
			                       		ProgressDlgUtil.stopProgressDlg();
			                       		handler.sendEmptyMessage(10);
			                       		dismiss();
			                       	}else{
			                       		ProgressDlgUtil.stopProgressDlg();
			                       		Toast.makeText(getContext(), "回答失败，请检查网络", Toast.LENGTH_LONG).show();
			                       	}
			                       }
			                   }catch(Exception e){
			                	   
			                   }
						}
					});

				}
			});
			
			 
//			RequestParams params = new RequestParams();
//			params.addBodyParameter("uid", MyApplication.getInstance().self.getId());
//			params.addBodyParameter("msg", msg);
//			params.addBodyParameter("id", id);
//			XUtilsHelper.sendPost(UrlUtil.Url_235+"8080/TLChat/rest/userqa/answer", params, new HttpCallback() {
//				
//				@Override
//				public void callback(String msg) {
//					// TODO Auto-generated method stub
//                   try{
//                	   if(!msg.equals("error")){
//                       	JSONObject js =new JSONObject(msg);
//                       	if(js!=null&&js.getInt("status") == 1){
//                       		ProgressDlgUtil.stopProgressDlg();
//                       		handler.sendEmptyMessage(10);
//                       		dismiss();
//                       	}else{
//                       		ProgressDlgUtil.stopProgressDlg();
//                       		Toast.makeText(getContext(), "回答失败，请检查网络", Toast.LENGTH_LONG).show();
//                       	}
//                       }
//                   }catch(Exception e){
//                	   
//                   }
//				}
//			});
	    }
	
	
}

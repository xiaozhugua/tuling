package com.example.tulingkuangjia.widget;

import android.app.ProgressDialog;
import android.content.Context;

public class ZiXuanShanchuProgressbar {

	private static ProgressDialog progressDialog=null;
	
	public static void showProgressDialog(Context context,String tip){
		if(progressDialog==null){
			progressDialog=ProgressDialog.show(context,"",tip, true,true);
		}else{
			progressDialog.show();
		}
	}
	
	public static void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
}

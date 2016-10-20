package com.abct.tljr.hangqing.tiaocang;

import com.abct.tljr.hangqing.database.ZiXuanGuRealmImpl;
import com.abct.tljr.hangqing.adapter.DeleteGuAdapter;
import com.abct.tljr.hangqing.model.DeleteGuModel;
import com.abct.tljr.hangqing.zixuan.TljrZiXuanGu;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import android.view.View.OnClickListener;
import android.content.BroadcastReceiver;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.data.ZiXuanUtil;
import com.abct.tljr.model.OneGu;

public class TljrDeleteGu extends BaseActivity implements OnClickListener{

	private ListView listDelete=null;
	private DeleteGuAdapter adapter=null;
	private List<OneGu> listGu=null;
	private String ZuName=null;
	private Context context=null;
	private View fanhui=null;
	private RelativeLayout allselect=null;
	private TextView delete_quxiao;
	private TextView delete_sure;
	private List<DeleteGuModel> listModel=null;
	private LinearLayout tljr_page_zh_select;
	private String ZuId=null;
	private UpdateDeleteGu mUpdateDeleteGu=null;
	private RelativeLayout zh_delete_content=null;
	private RelativeLayout nogupiao_tip=null;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_page_zh_delete);
		tljr_page_zh_select=(LinearLayout)findViewById(R.id.tljr_page_zh_select);
		this.context=this;
		listModel=new ArrayList<DeleteGuModel>();
		listGu=(ArrayList<OneGu>)getIntent().getSerializableExtra("listGus");
		ZuName=getIntent().getStringExtra("zuname");
		ZuId=getIntent().getStringExtra("zuid");
		zh_delete_content=(RelativeLayout)findViewById(R.id.zh_delete_content);
		nogupiao_tip=(RelativeLayout)findViewById(R.id.nogupiao_tip);
		IntentFilter intentFilter=new IntentFilter("com.tiaocang.tljrdeletegu");
		mUpdateDeleteGu=new UpdateDeleteGu();
		registerReceiver(mUpdateDeleteGu,intentFilter);
		
		initView();
	}
	
	public void initView(){
		listDelete=(ListView)findViewById(R.id.tljr_page_zh_gudelete);
		DeleteGuModel model=null;
		for(int i=0;i<listGu.size();i++){
			model=new DeleteGuModel(listGu.get(i).getName(),listGu.get(i).getCode()
					,listGu.get(i).getNow()+"",listGu.get(i).getP_changes()
					,listGu.get(i).getMarket(),0,listGu.get(i).getP_change(),"");
			listModel.add(model);
		}
		adapter=new DeleteGuAdapter(context,R.layout.tljr_page_zh_tiaocang_gudelete_item,listModel);
		listDelete.setAdapter(adapter);
		allselect=(RelativeLayout)findViewById(R.id.tljr_tiaocang_menu);
		fanhui=(View)findViewById(R.id.tljr_shanchu_fanhui);
		delete_quxiao=(TextView)findViewById(R.id.delete_quxiao);
		delete_sure=(TextView)findViewById(R.id.delete_shanchu);
		allselect.setOnClickListener(this);
		fanhui.setOnClickListener(this);
		delete_quxiao.setOnClickListener(this);
		delete_sure.setOnClickListener(this);
		if(listModel.size()<=0){
//			tljr_page_zh_select.setVisibility(View.GONE);
			zh_delete_content.setVisibility(View.GONE);
			nogupiao_tip.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tljr_shanchu_fanhui:
			finish();
			break;
		case R.id.tljr_tiaocang_menu:
			addSelect();
			break;
		case R.id.delete_quxiao:
			addQuxiao();
			break;
		case R.id.delete_shanchu:
			addShanChu();
			break;
		}
	}
	
	public void addSelect(){
		for(int i=0;i<listModel.size();i++){
			listModel.get(i).setAction(1);
		}
		adapter.notifyDataSetChanged();
	}
	
	public void addQuxiao(){
		for(int i=0;i<listModel.size();i++){
			listModel.get(i).setAction(0);
		}
		adapter.notifyDataSetChanged();
	}
	
	public void addShanChu(){
		int i=0;
		for(DeleteGuModel model:listModel){
			if(model.getAction()==1){
				i++;
			}
		}
		if(i<=0){
			Toast.makeText(context,"没有选中股票删除",Toast.LENGTH_SHORT).show();
		}else{
			for(DeleteGuModel model:listModel){
				if(model.getAction()==1){
					ProgressDlgUtil.showProgressDlg("",TljrDeleteGu.this);
					ProgressDlgUtil.Status=false;
					//删除更新自选
					TljrZiXuanGu.RemoveZiXuanGuView(model.getCode());
					//不是现在的组就直接删除
					ZiXuanUtil.delStock(model.getMarket()+model.getCode(), ZuName);
				}
			}
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mUpdateDeleteGu);
	}
	
	class UpdateDeleteGu extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			try{
				JSONArray father=new JSONArray(intent.getStringExtra("message"));
				for(int z=0;z<father.length();z++){
				    JSONObject object=father.getJSONObject(z);
					if(object.getString("action").equals("del")){
						JSONObject son=object.getJSONObject("info");
						DeleteData(son.getString("code"),son.getString("market"));
					}else{
						
					}
				}
			}catch(Exception e){
				
			}
		}
	}
	
	public void DeleteData(String code,String market){
		//更新组合股票列表
		Intent tiaocanggu=new Intent("tljr_TiaocangGu_update");
		tiaocanggu.putExtra("code",code);
		tiaocanggu.putExtra("market",market);
		context.sendBroadcast(tiaocanggu);
		//更新组合股票百分比
		Intent tiaocangpercent=new Intent("tljr_tiaocang_changeupdate");
		tiaocangpercent.putExtra("code",code);
		tiaocangpercent.putExtra("market",market);
		context.sendBroadcast(tiaocangpercent);
		//删除本地数据
		ZiXuanGuRealmImpl.RemoveOneGu(ZuId+market+code,ZuId);
		
		for(int i=0;i<listModel.size();i++)
			if((listModel.get(i).getMarket()+listModel.get(i).getCode()).equals(market+code)){
				listModel.remove(listModel.get(i));
				adapter.notifyDataSetChanged();
				break;
		}
		
		if(listModel.size()<=0){
			tljr_page_zh_select.setVisibility(View.GONE);
		}
		ProgressDlgUtil.Status=true;
		ProgressDlgUtil.stopProgressDlg();
	}
	
}

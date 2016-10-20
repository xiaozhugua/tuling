package com.abct.tljr.hangqing.zixuan;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.hangqing.adapter.DeleteZiXuanGuAdapter;
import com.abct.tljr.hangqing.database.ZiXuanGu;
import com.abct.tljr.hangqing.database.ZiXuanGuDataBaseImpl;
import com.abct.tljr.hangqing.model.DeleteGuModel;
import com.abct.tljr.hangqing.widget.dragsortlistview.DragSortListView;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;

public class DeleteZiXuanGu extends BaseActivity implements OnClickListener {
	private List<DeleteGuModel> listdeleteGu = null;
	private DragSortListView DeleteListView = null;
	public static DeleteZiXuanGuAdapter adapter = null;
	public List<String> tempdelete=new ArrayList<String>();
	public List<Integer> listitem=new ArrayList<>();
	private View fanhui;
	private Button quxiao;
	private Button sure;
	private Context context=null;
	private TextView deletequanxian;
	private String host="http://120.24.235.202:8080/ZhiLiYinHang/ZiXuanServlet";
	public static  Boolean updateStatus=false;
	public boolean deletestatus=true;
	
	private DragSortListView.DropListener onDrop=new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if(from!=to){
				DeleteGuModel zixuangu=adapter.getItem(from);
				adapter.remove(zixuangu);
				adapter.insert(zixuangu,to);
				ZiXuanGu gu=tljr_zixuan_gu_recyclerview.listZiXuanGu.get(from);
				tljr_zixuan_gu_recyclerview.listZiXuanGu.remove(from);
				tljr_zixuan_gu_recyclerview.listZiXuanGu.add(to,gu);
				tljr_zixuan_gu_recyclerview.adapter.notifyDataSetChanged();
				updateStatus=true;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_zh_deletezixuangu);
		this.context=this;
		deletequanxian=(TextView)findViewById(R.id.deletequanxian);
		listdeleteGu = new ArrayList<DeleteGuModel>();
		DeleteListView = (DragSortListView) findViewById(R.id.deletelist);
		fanhui = (View) findViewById(R.id.tljr_tiaocang_fanhui);
		quxiao = (Button) findViewById(R.id.deletezixuangu_quxiao);
		sure = (Button) findViewById(R.id.deletezixuangu_sure);
		quxiao.setOnClickListener(this);
		fanhui.setOnClickListener(this);
		sure.setOnClickListener(this);
		deletequanxian.setOnClickListener(this);
		DeleteListView.setDropListener(onDrop);
		DeleteListView.setItemsCanFocus(false);  
		DeleteListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		initView();
	}

	public void initView() {
		for (ZiXuanGu mZiXuanGu:tljr_zixuan_gu_recyclerview.listZiXuanGu){
			listdeleteGu.add(new DeleteGuModel(mZiXuanGu.getName(),mZiXuanGu.getCode(),""
					,"",mZiXuanGu.getMarket(),0,0,mZiXuanGu.getId()));
		}
		adapter = new DeleteZiXuanGuAdapter(this,R.layout.tljr_page_zh_gudelete_item, listdeleteGu);
		DeleteListView.setAdapter(adapter);
	}

	public String geturl(String id){
		 String url=host+"?method=del&uid="+MyApplication.getInstance().self
				 .getId()+"&token="+Configs.token+"&del="+id;
		 return url;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tljr_tiaocang_fanhui:
			finish();
			break;
		case R.id.deletezixuangu_quxiao:
			for (int i = 0; i < listdeleteGu.size();i++) {
				listdeleteGu.get(i).setAction(0);
			}
			adapter.notifyDataSetChanged();
			break;
		case R.id.deletequanxian:
			for (int i = 0; i < listdeleteGu.size();i++) {
				listdeleteGu.get(i).setAction(1);
			}
			adapter.notifyDataSetChanged();
			break;	
		case R.id.deletezixuangu_sure:
			if(deletestatus){
				deletestatus=false;
				JSONArray array=new JSONArray();
				for(int i = 0; i < listdeleteGu.size();i++){
					if(listdeleteGu.get(i).getAction()==1){
						array.put(Integer.valueOf(listdeleteGu.get(i).getId()));
						tempdelete.add(listdeleteGu.get(i).getId());
						listitem.add(i);
					}
				}
				if(tempdelete.size()>0){
					ProgressDlgUtil.showProgressDlg("",DeleteZiXuanGu.this);
					NetUtil.sendGet(geturl(array.toString()),new NetResult() {
						@Override
						public void result(String response) {
							try{
								LogUtil.e("DeleteZiXuan",response);
								if(!response.equals("")){
									JSONObject object=new JSONObject(response);
										if(object.getInt("status")==1){
											for(String id:tempdelete){
												ZiXuanGuDataBaseImpl.DeleteZiXuanGu(id);
											}
											List<DeleteGuModel> listdelete=new ArrayList<>();
											List<ZiXuanGu> listzixuan=new ArrayList<>();
											for(int item:listitem){
												listdelete.add(adapter.getItem(item));
												listzixuan.add(tljr_zixuan_gu_recyclerview.listZiXuanGu.get(item));
											}
											for(int i=0;i<listdelete.size();i++){
												adapter.remove(listdelete.get(i));
												tljr_zixuan_gu_recyclerview.listZiXuanGu.remove(listzixuan.get(i));
											}
											tljr_zixuan_gu_recyclerview.adapter.notifyDataSetChanged();
											Toast.makeText(context,"删除成功",Toast.LENGTH_SHORT).show();
										}else{
											Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
										}
										deletestatus=true;
										tempdelete.clear();
										listitem.clear();
										ProgressDlgUtil.stopProgressDlg();		
								}else{
									Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
								}
							}catch(Exception e){
									Toast.makeText(context,"服务器出错",Toast.LENGTH_SHORT).show();
							}
						}
					});
				}else{
					Toast.makeText(context,"对不起没有选择股票",Toast.LENGTH_SHORT).show();
					deletestatus=true;
				}
			}else{
				Toast.makeText(context,"点太快了，休息一下吧",Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	public String getUpdateUrl(String param){
		String url=host+"?method=update&uid="+MyApplication.getInstance().self.getId()
				+"&token="+Configs.token+"&ids="+param;
		LogUtil.e("getUpadteUrl",url);
		return url;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		JSONArray array=new JSONArray();
		if(tljr_zixuan_gu_recyclerview.listZiXuanGu.size()>0){
			for(int i = 0; i < tljr_zixuan_gu_recyclerview.listZiXuanGu.size();i++){
				tljr_zixuan_gu_recyclerview.listZiXuanGu.get(i).setLocation(i);
				array.put(Integer.valueOf(tljr_zixuan_gu_recyclerview.listZiXuanGu.get(i).getId()));
			}
			ZiXuanGuDataBaseImpl.saveZiXuanGu(tljr_zixuan_gu_recyclerview.listZiXuanGu);
		}
		if(updateStatus==true){
			updateStatus=false;
			NetUtil.sendGet(getUpdateUrl(array.toString()),new NetResult() {
				@Override
				public void result(String response) {
					try{
						LogUtil.e("response",response);
					}catch(Exception e){
						LogUtil.e("DeleteZiXian",e.getMessage());
					}
				}
			});
		}
		if(tljr_zixuan_gu_recyclerview.login_in.getVisibility()!=View.VISIBLE&&tljr_zixuan_gu_recyclerview.listZiXuanGu.size()<=0){
			tljr_zixuan_gu_recyclerview.mSwipeRefreshLayout.setVisibility(View.GONE);
			tljr_zixuan_gu_recyclerview.addgushow.setVisibility(View.VISIBLE);
		}
	}
	
}

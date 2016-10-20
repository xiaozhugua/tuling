package com.abct.tljr.wxapi.xunzhang;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.abct.tljr.BaseFragmentActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.qh.common.util.LogUtil;
import com.qh.common.volley.Response;
import com.qh.common.volley.VolleyError;
import com.qh.common.volley.toolbox.StringRequest;

public class XunzhangActivity extends BaseFragmentActivity implements OnPageChangeListener {
	private ViewPager viewPager;
	private ArrayList<Fragment> list = new ArrayList<>();
	private MyTitleBar titleBar;
	public List<XunzhangEntity> xzs = new ArrayList<>();
	public List<XunzhangEntity> myXzs = new ArrayList<>();	
	private RadioButton allxunzhang=null;
	private RadioButton hasxunzhang=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xunzhang);
		allxunzhang=(RadioButton)findViewById(R.id.all);
		hasxunzhang=(RadioButton)findViewById(R.id.have);
		initData();
	}

	public void initData(){
        LogUtil.e("initData","http://120.24.235.202:8080/MedalServer/getAllMedalList?uid=" +MyApplication.getInstance().self.getId()+"&page=1&size=1000");
		MyApplication.requestQueue.add(new StringRequest("http://120.24.235.202:8080/MedalServer/getAllMedalList?uid="
			    +MyApplication.getInstance().self.getId()+"&page=1&size=1000",new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try{
					JSONObject object =new JSONObject(response);
					if(object.optInt("status")==1){
						JSONArray ja = object.getJSONArray("data");
						for (int i = 0; i <ja.length(); i++) {
							JSONObject o = ja.getJSONObject(i);
							boolean has = o.getBoolean("has");
							XunzhangEntity entity = new XunzhangEntity();
							entity.setDetail(o.getString("desc"));
							entity.setHas(has);
							entity.setIconUrl(o.getString("iconUrl"));
							entity.setName(o.getString("name"));
							xzs.add(entity);
							if(has)myXzs.add(entity);
						}
						initview();
					}
				}catch(Exception e){
				}
			}
		},new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				Toast.makeText(XunzhangActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
			}
		}));
	}
	
	private void initview() {
		allxunzhang.setText("全部勋章("+xzs.size()+")");
		hasxunzhang.setText("已有勋章("+myXzs.size()+")");
		list.add(new AllXunzhuangFragment());
		list.add(new MyXunzhangFragment());
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		viewPager.addOnPageChangeListener(this);
		findViewById(R.id.tljr_img_news_back).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						finish();
				}
		});
		titleBar = new MyTitleBar(this, viewPager, (ViewGroup)findViewById(R.id.radioGroup1),
				findViewById(R.id.tljr_changepercent_tab_driver));
	}

	class MyPagerAdapter extends FragmentPagerAdapter {
		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		@Override
		public Fragment getItem(int arg0) {
			return list.get(arg0);
		}
		@Override
		public int getCount() {
			return list.size();
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}
	@Override
	public void onPageSelected(int arg0) {
		titleBar.tabChangedArrow(arg0);
	}
}

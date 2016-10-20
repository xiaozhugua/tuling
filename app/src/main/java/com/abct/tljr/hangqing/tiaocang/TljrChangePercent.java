package com.abct.tljr.hangqing.tiaocang;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.abct.tljr.BaseFragmentActivity;
import com.abct.tljr.R;
import com.abct.tljr.data.ZiXuanUtil;
import com.abct.tljr.dialog.SearchDialog;
import com.abct.tljr.hangqing.adapter.TljrTiaoCangFragmentAdapter;
import com.abct.tljr.hangqing.tiaocang.fragment.ChiCangPercent;
import com.abct.tljr.hangqing.tiaocang.fragment.TiaocangGu;
import com.abct.tljr.hangqing.util.FixedSpeedScroller;
import com.abct.tljr.hangqing.util.GuDealImpl;
import com.abct.tljr.hangqing.util.Menu.BinarySlidingMenu;
import com.abct.tljr.hangqing.util.Menu.BinarySlidingMenu.OnMenuOpenListener;
import com.abct.tljr.model.OneGu;
import com.qh.common.model.TitleBar;
import com.qh.common.util.ColorUtil;

public class TljrChangePercent extends BaseFragmentActivity implements OnPageChangeListener, OnClickListener{

	private ViewPager mViewPager=null;
	private TitleBar titleBar;
	//组合的基本信息
	private TextView zong=null;
	private TextView ri=null;
	private TextView yue=null;
	private TextView jingzhi=null;
	private TextView time=null;
	private TextView beta=null;
	private TextView alpha=null;
	private TextView sharpe=null;
	private TextView huicezhi=null;
	public TextView ZuName=null;
	private TextView ZuCode=null;
	public String jingzhiliebiao=null;
	private DecimalFormat mDecimalFormat=null;
	public List<OneGu> listGus=null;
	private View back=null;
	private BinarySlidingMenu mMenu;
	private View ChangePercent_item_view;
	private boolean MenuStatus=false;
	private RelativeLayout tljr_tiaocang_menu;
	private LinearLayout lishihuiche=null;
	private LinearLayout itemadd=null;
	private LinearLayout shanchu=null;
	private LinearLayout gaiming=null;
	private Context conetxt=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_zx_changpercent);
		mMenu = (BinarySlidingMenu) findViewById(R.id.changpercent_menu);
		ChangePercent_item_view=(View)findViewById(R.id.changpercent_item_view);
		tljr_tiaocang_menu=(RelativeLayout)findViewById(R.id.tljr_tiaocang_menu);
		mViewPager=(ViewPager)findViewById(R.id.changepercent_viewpager);
		listGus=(ArrayList<OneGu>)ZiXuanUtil.fzMap.get(getIntent().getStringExtra("zuName")).getList();
		ZuName=(TextView)findViewById(R.id.tljr_tiaocang_zuname);
		ZuCode=(TextView)findViewById(R.id.rljr_tiaocang_zucode);
		back=(View)findViewById(R.id.tljr_tiaocang_fanhui);
		back.setOnClickListener(this);
		tljr_tiaocang_menu.setOnClickListener(this);
		this.conetxt=this;
		ZuName.setText(getIntent().getStringExtra("TagName"));
		ZuCode.setText(getIntent().getStringExtra("zuid"));
		
		lishihuiche=(LinearLayout)findViewById(R.id.changpercent_lishihuice);
		itemadd=(LinearLayout)findViewById(R.id.changpercentitem_add);
		shanchu=(LinearLayout)findViewById(R.id.changpercentitem_shanchu);
		gaiming=(LinearLayout)findViewById(R.id.changpercentitem_gaiming);
		
		lishihuiche.setOnClickListener(this);
		itemadd.setOnClickListener(this);
		shanchu.setOnClickListener(this);
		gaiming.setOnClickListener(this);
		
		mMenu.setOnMenuOpenListener(new OnMenuOpenListener() {
			@Override
			public void onMenuOpen(boolean isOpen, int flag) {
				if (isOpen) {
					// 关闭
					((ImageView) ChangePercent_item_view.findViewById(R.id.changepercent_item_image)).setVisibility(View.INVISIBLE);
				} else {
					// 张开
					((ImageView) ChangePercent_item_view.findViewById(R.id.changepercent_item_image)).setVisibility(View.VISIBLE);
				}
			}
		});
		
		initView();
		initZuMessage();
	}

	@SuppressWarnings("deprecation")
	public void initView(){
		mDecimalFormat=new DecimalFormat("#0.0000");
		try{
			titleBar=new TitleBar(this,mViewPager,(ViewGroup)findViewById(R.id.tljr_changepercent_tab)
					,findViewById(R.id.tljr_changepercent_tab_driver));
			int[] a = {ColorUtil.lightGray,ColorUtil.red};
	        titleBar.setColor(a);
	        Field field = ViewPager.class.getDeclaredField("mScroller");
	        field.setAccessible(true);
	        FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext(), new AccelerateInterpolator());
	        field.set(mViewPager, scroller);
	        scroller.setmDuration(250);
	        
	        mViewPager.setAdapter(new TljrTiaoCangFragmentAdapter(getSupportFragmentManager()));
	        mViewPager.setOnPageChangeListener(this);
		}catch(Exception e){
			
		}
	}
	
	public void initZuMessage(){
		zong=(TextView)findViewById(R.id.tljr_zx_allgu_shuoyilu);
		ri=(TextView)findViewById(R.id.tljr_zx_allgu_day_data);
		yue=(TextView)findViewById(R.id.tljr_zx_allgu_month_data);
		time=(TextView)findViewById(R.id.tljr_zx_message_time_data);
		beta=(TextView)findViewById(R.id.tljr_zx_beta_data);
		alpha=(TextView)findViewById(R.id.tljr_zx_alpha_data);
		sharpe=(TextView)findViewById(R.id.tljr_zx_sharpe_data);
		jingzhi=(TextView)findViewById(R.id.tljr_zx_allgu_jingzhi_data);
		huicezhi=(TextView)findViewById(R.id.tljr_zx_maxdrawdown_data);
		initZuMessageData();
	}
	
	public void initZuMessageData(){
		String  msg=getIntent().getStringExtra("jsonData");
		if(msg!=null&&msg!=""){
			//个股信息不为空
			dealZuData(msg);
		}else{
			//个股信息为空
			
		}
	}
	
	//处理组合数据
	@SuppressLint("SimpleDateFormat")
	public void dealZuData(String msg){
		try {
			Message message = new Message();
			JSONObject objectmsg = new JSONObject(msg);
			JSONArray array = objectmsg.getJSONArray("jingzhiliebiao");
			jingzhiliebiao=array.toString();
			message.what = 2;
			Bundle bundle = new Bundle();
			// 初始化数据
			bundle.putFloat("jingzhi",Float.valueOf(objectmsg.getString("jingzhi")));
			bundle.putFloat("rishouyi",Float.valueOf(objectmsg.getString("rishouyi")));
			bundle.putFloat("yueshouyi",Float.valueOf(objectmsg.getString("yueshouyi")));
			bundle.putFloat("zongshouyi",Float.valueOf(objectmsg.getString("zongshouyi")));
			bundle.putFloat("beta", Float.valueOf(objectmsg.getString("beta")));
			bundle.putFloat("alpha",Float.valueOf(objectmsg.getString("alpha")));
			bundle.putFloat("maxdrawdown",Float.valueOf(objectmsg.getString("maxdrawdown")));
			bundle.putFloat("sharpe",Float.valueOf(objectmsg.getString("sharpe")));
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			// 判断创建日期
			bundle.putString("time", format.format(new Date(Long.valueOf(objectmsg.getString("date")))));
			message.setData(bundle);
			mHandler.sendMessage(message);
		} catch (Exception e) {
			showTip(1,"对不起,连接服务器失败");
		}
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int page) {
		titleBar.tabChangedArrow(page);
	}
	
	//发送提示
	public void showTip(int what,String msg){
		Message message=Message.obtain();
		message.what=what;
		if(msg!=""){
			Bundle bundle=new Bundle();
			bundle.putString("msg",msg);
			message.setData(bundle);
		}
		mHandler.sendMessage(message);
	}
	
	
	final Handler mHandler=new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case 1:
				Toast.makeText(getBaseContext(),msg.getData().getString("msg"),Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Bundle mBundle=msg.getData();
				NumberFormat format = NumberFormat.getPercentInstance();// 获取格式化类实例
				format.setMinimumFractionDigits(2);// 设置小数位
				String zongdata=format.format(mBundle.getFloat("zongshouyi"));
				zong.setText(zongdata.substring(0,zongdata.length()-1));
				
				ri.setText(format.format(mBundle.getFloat("rishouyi")));
				yue.setText(format.format(mBundle.getFloat("yueshouyi")));
				time.setText(mBundle.getString("time"));
				
				beta.setText(mDecimalFormat.format(mBundle.getFloat("beta")));
				alpha.setText(mDecimalFormat.format(mBundle.getFloat("alpha")));
				sharpe.setText(mDecimalFormat.format(mBundle.getFloat("sharpe")));
				jingzhi.setText(mBundle.getFloat("jingzhi")+"");
				
				DecimalFormat df = new DecimalFormat("0.0000%");
				huicezhi.setText(df.format(mBundle.getFloat("maxdrawdown")));
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tljr_tiaocang_fanhui:
			finish();
			break;
		case R.id.tljr_tiaocang_menu:
			if(MenuStatus==false){
				mMenu.showRight();
				MenuStatus=true;
			}else{
				mMenu.hideRight();
				MenuStatus=false;
			}
			break;
		case R.id.changpercent_lishihuice:
			Intent huiceintent = new Intent(this, HuiCeView.class);
			huiceintent.putExtra("zhid",getIntent().getStringExtra("zuid"));
			huiceintent.putExtra("zuname",ZuName.getText().toString());
			startActivity(huiceintent);
			mMenu.setScrollTo(0, 0);
			break;
		case R.id.changpercentitem_add: 
			new SearchDialog(this).show(ZuName.getText().toString());
			mMenu.setScrollTo(0, 0);
			break;
		case R.id.changpercentitem_shanchu:
			Intent intent=new Intent(this.conetxt,TljrDeleteGu.class);
			intent.putExtra("listGus",(Serializable)listGus);
			intent.putExtra("zuname",ZuName.getText().toString());
			intent.putExtra("zuid",getIntent().getStringExtra("zuid"));
			this.conetxt.startActivity(intent);
			mMenu.setScrollTo(0, 0);
			break;
		case R.id.changpercentitem_gaiming:
			GuDealImpl.showGaimingAndBeizu(this.conetxt,"分组改名","最多10个中文字符",
					1,ZuName.getText().toString(),ZuName);
			mMenu.setScrollTo(0, 0);
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(TiaocangGu.mTiaocangGuListView);
		unregisterReceiver(TiaocangGu.mTiaocangGuAdd);
		unregisterReceiver(TiaocangGu.updateId);
		unregisterReceiver(ChiCangPercent.mTiaoCangUpdateDelete);
		unregisterReceiver(ChiCangPercent.mTiaoCangUpdatePieChart);
	}
	
}

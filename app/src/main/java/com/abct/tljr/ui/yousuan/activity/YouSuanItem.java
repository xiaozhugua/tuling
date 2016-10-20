package com.abct.tljr.ui.yousuan.activity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.dialog.PromptDialog;
import com.abct.tljr.hangqing.zixuan.TljrZiXuanLineChart;
import com.abct.tljr.model.Options;
import com.abct.tljr.model.ZhongchouBean;
import com.abct.tljr.ui.activity.zhiyan.PayActivity;
import com.abct.tljr.ui.yousuan.fragment.CeLueJainjie;
import com.abct.tljr.ui.yousuan.fragment.DuoKongYinkui;
import com.abct.tljr.ui.yousuan.fragment.HuiCeQuXian;
import com.abct.tljr.ui.yousuan.fragment.Quanyiquxian;
import com.abct.tljr.ui.yousuan.fragment.YejiBaobiao;
import com.abct.tljr.ui.yousuan.fragment.Yueduyinkui;
import com.abct.tljr.ui.yousuan.model.QiYuXianModel;
import com.abct.tljr.ui.yousuan.model.YouSuanItemModel;
import com.abct.tljr.ui.yousuan.model.YouSuanMyItem;
import com.abct.tljr.ui.yousuan.util.YouSuanMyChart;
import com.abct.tljr.ui.yousuan.util.YouSuanShiShiChart;
import com.github.mikephil.charting.charts.LineChart;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.model.User;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

public class YouSuanItem extends BaseActivity implements OnClickListener{

    private CeLueJainjie mCeLueJainjie=null;
    private DuoKongYinkui mDuoKongYinkui=null;
    private HuiCeQuXian mHuiCeQuXian=null;
    private YejiBaobiao mYejiBaobiao=null;
    private Yueduyinkui mYueduyinkui=null;
    private Quanyiquxian mQuanyiquxian=null;
    private FragmentManager fragmentManager;

    private RelativeLayout celielayout=null;
    private RelativeLayout quanyilayout=null;
    private RelativeLayout yuedulayout=null;
    private RelativeLayout duokonglayout=null;
    private RelativeLayout huicelayout=null;
    private RelativeLayout yejilayout=null;

    private ImageView celieimage=null;
    private ImageView quanyiimage=null;
    private ImageView yueduuimage=null;
    private ImageView duokongimage=null;
    private ImageView huiceimage=null;
    private ImageView yejiimage=null;

    private TextView celiename=null;
    private TextView quanyiname=null;
    private TextView duokongname=null;
    private TextView huicename=null;
    private TextView yejiname=null;
    private TextView yueduname=null;

    private View tljr_yousuan_fanhui=null;
    private TextView YouSuanData=null;

    public YouSuanItemModel model=null;

    //	private ImageView yousuanitem_image=null;
    private TextView yousuan_author=null;
    private TextView yousuan_shuoyilu=null;
    private TextView yousuan_huice=null;
    private TextView yousuan_updatetime=null;
    private TextView yousuan_baohedu=null;

    private SimpleDateFormat mSimpleDateFormat=null;

    private LinearLayout yousuanitem_deal=null;
    private RelativeLayout yousuan_quhui=null;

    private Button shiyong=null;
    private Button goumai=null;
    private EditText mNum=null;
    private TextView yousuan_name;

    public String ShiyongUrl=UrlUtil.Url_235+"8080/yousuan/rest/gentou/test";
    public Context context;
    private Button yousuanitem_quhui;
    private DecimalFormat mDecimalFormat=null;
    private LineChart chart=null;
    public YouSuanMyItem itemModel=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yousaunitem);
        mDecimalFormat=new DecimalFormat("######0.00");
        this.context=this;
        initView();
        if(getIntent().getBooleanExtra("yousaunitemstatus",false)){
            yousuanitem_deal.setVisibility(View.GONE);
            yousuan_quhui.setVisibility(View.VISIBLE);
            itemModel=MyApplication.getInstance().getMainActivity().mYouSuanBaseFragment.mYouSuanMy.listYouSuan
                    .get(getIntent().getIntExtra("position",0));
        }else{
            yousuanitem_deal.setVisibility(View.VISIBLE);
            yousuan_quhui.setVisibility(View.GONE);
            model=MyApplication.getInstance().getMainActivity().mYouSuanBaseFragment.mYouSuanShishi
                    .listModel.get(getIntent().getIntExtra("position",0));
        }
        initData();
        setTitleTab(0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void initView(){
        mSimpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        chart=(LineChart)findViewById(R.id.yousuan_chart);
//		yousuanitem_image=(ImageView)findViewById(R.id.yousuanitem_image);
        yousuanitem_deal=(LinearLayout)findViewById(R.id.yousuanitem_deal);
        yousuan_author=(TextView)findViewById(R.id.yousuan_author);
        yousuan_shuoyilu=(TextView)findViewById(R.id.yousuan_shuoyilu);
        yousuan_huice=(TextView)findViewById(R.id.yousuan_huice);
        yousuan_updatetime=(TextView)findViewById(R.id.yousuan_updatetime);
        yousuan_baohedu=(TextView)findViewById(R.id.yousuan_baohedu);

        fragmentManager=getFragmentManager();
        tljr_yousuan_fanhui=findViewById(R.id.tljr_yousuan_fanhui);
        tljr_yousuan_fanhui.setOnClickListener(this);
        YouSuanData=(TextView)findViewById(R.id.yousuan_datatext);
        YouSuanData.setOnClickListener(this);
        celielayout=(RelativeLayout)findViewById(R.id.celielayout);
        quanyilayout=(RelativeLayout)findViewById(R.id.quanyilayout);
        yuedulayout=(RelativeLayout)findViewById(R.id.yuedulayout);
        duokonglayout=(RelativeLayout)findViewById(R.id.duokonglayout);
        huicelayout=(RelativeLayout)findViewById(R.id.huicelayout);
        yejilayout=(RelativeLayout)findViewById(R.id.yejilayout);

        shiyong=(Button)findViewById(R.id.yousuan_item_shiyong);
        goumai=(Button)findViewById(R.id.yousuan_item_goumai);
        mNum=(EditText)findViewById(R.id.yousuan_item_num);
        yousuanitem_quhui=(Button)findViewById(R.id.yousuanitem_quhui);

        shiyong.setOnClickListener(this);
        goumai.setOnClickListener(this);
        yousuanitem_quhui.setOnClickListener(this);

        celielayout.setOnClickListener(this);
        quanyilayout.setOnClickListener(this);
        yuedulayout.setOnClickListener(this);
        duokonglayout.setOnClickListener(this);
        huicelayout.setOnClickListener(this);
        yejilayout.setOnClickListener(this);

        celieimage=(ImageView)findViewById(R.id.celie_img);
        quanyiimage=(ImageView)findViewById(R.id.quanyi_img);
        yueduuimage=(ImageView)findViewById(R.id.yuedu_image);
        duokongimage=(ImageView)findViewById(R.id.duokong_image);
        huiceimage=(ImageView)findViewById(R.id.huice_image);
        yejiimage=(ImageView)findViewById(R.id.yeji_image);

        celiename=(TextView)findViewById(R.id.celie_name);
        quanyiname=(TextView)findViewById(R.id.quanyi_name);
        duokongname=(TextView)findViewById(R.id.duokong_name);
        huicename=(TextView)findViewById(R.id.huice_name);
        yejiname=(TextView)findViewById(R.id.yeji_name);
        yueduname=(TextView)findViewById(R.id.yuedu_name);

        yousuan_quhui=(RelativeLayout)findViewById(R.id.yousuan_quhui);
        yousuan_name=(TextView)findViewById(R.id.yousuan_name);

    }

    public void initData(){
        //更新LineChart
        if(getIntent().getBooleanExtra("yousaunitemstatus",false)){
            new YouSuanMyChart(chart,itemModel.getGetTou().getKey(),itemModel.getYouSuanMyChart().listModel);
            yousuan_name.setText(itemModel.getGetTou().getName());
            yousuan_shuoyilu.setText(Float.valueOf(mDecimalFormat.format(itemModel.getGetTou().getCurrentMoonRate()))*100+"%");
            yousuan_huice.setText(mDecimalFormat.format(itemModel.getGetTou().getMaxDrawDownMoney())+"");
            yousuan_updatetime.setText(mSimpleDateFormat.format(new Date((itemModel.getGetTou().getUpdateDate()*1000))));
            yousuan_baohedu.setText(itemModel.getGetTou().getSaturation()+"");
        }else{
            new YouSuanShiShiChart(chart,model.getKey(),model.getYouSuanShiShiChart().listModel);
            yousuan_name.setText(model.getName());
            yousuan_shuoyilu.setText(Float.valueOf(mDecimalFormat.format(model.getCurrentMoonRate()))*100+"%");
            yousuan_huice.setText(mDecimalFormat.format(model.getMaxDrawDownMoney())+"");
            yousuan_updatetime.setText(mSimpleDateFormat.format(new Date((model.getUpdateDate()*1000))));
            yousuan_baohedu.setText(model.getSaturation()+"");
        }
    }

    @SuppressWarnings("deprecation")
    public void resetTabTitle(){
        celieimage.setVisibility(View.GONE);
        quanyiimage.setVisibility(View.GONE);
        yueduuimage.setVisibility(View.GONE);
        duokongimage.setVisibility(View.GONE);
        huiceimage.setVisibility(View.GONE);
        yejiimage.setVisibility(View.GONE);

        celiename.setTextColor(getResources().getColor(R.color.black));
        quanyiname.setTextColor(getResources().getColor(R.color.black));
        duokongname.setTextColor(getResources().getColor(R.color.black));
        huicename.setTextColor(getResources().getColor(R.color.black));
        yejiname.setTextColor(getResources().getColor(R.color.black));
        yueduname.setTextColor(getResources().getColor(R.color.black));
    }

    public void hideFragment(FragmentTransaction transaction){
        if(mCeLueJainjie!=null){
            transaction.hide(mCeLueJainjie);
        }
        if(mQuanyiquxian!=null){
            transaction.hide(mQuanyiquxian);
        }
        if(mYueduyinkui!=null){
            transaction.hide(mYueduyinkui);
        }
        if(mDuoKongYinkui!=null){
            transaction.hide(mDuoKongYinkui);
        }
        if(mHuiCeQuXian!=null){
            transaction.hide(mHuiCeQuXian);
        }
        if(mYejiBaobiao!=null){
            transaction.hide(mYejiBaobiao);
        }
    }

    @SuppressWarnings("deprecation")
    public void setTitleTab(int index){
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        resetTabTitle();
        hideFragment(transaction);
        switch(index){
            case 0:
                celieimage.setVisibility(View.VISIBLE);
                celiename.setTextColor(getResources().getColor(R.color.red));
                if(mCeLueJainjie==null){
                    mCeLueJainjie=new CeLueJainjie();
                    transaction.add(R.id.yousuanfragment,mCeLueJainjie);
                }else{
                    transaction.show(mCeLueJainjie);
                }
                break;
            case 1:
                quanyiimage.setVisibility(View.VISIBLE);
                quanyiname.setTextColor(getResources().getColor(R.color.red));
                if(mQuanyiquxian==null){
                    mQuanyiquxian=new Quanyiquxian();
                    transaction.add(R.id.yousuanfragment,mQuanyiquxian);
                }else{
                    transaction.show(mQuanyiquxian);
                }
                break;
            case 2:
                yueduuimage.setVisibility(View.VISIBLE);
                yueduname.setTextColor(getResources().getColor(R.color.red));
                if(mYueduyinkui==null){
                    mYueduyinkui=new Yueduyinkui();
                    transaction.add(R.id.yousuanfragment,mYueduyinkui);
                }else{
                    transaction.show(mYueduyinkui);
                }
                break;
            case 3:
                duokongimage.setVisibility(View.VISIBLE);
                duokongname.setTextColor(getResources().getColor(R.color.red));
                if(mDuoKongYinkui==null){
                    mDuoKongYinkui=new DuoKongYinkui();
                    transaction.add(R.id.yousuanfragment,mDuoKongYinkui);
                }else{
                    transaction.show(mDuoKongYinkui);
                }
                break;
            case 4:
                huiceimage.setVisibility(View.VISIBLE);
                huicename.setTextColor(getResources().getColor(R.color.red));
                if(mHuiCeQuXian==null){
                    mHuiCeQuXian=new HuiCeQuXian();
                    transaction.add(R.id.yousuanfragment,mHuiCeQuXian);
                }else{
                    transaction.show(mHuiCeQuXian);
                }
                break;
            case 5:
                yejiimage.setVisibility(View.VISIBLE);
                yejiname.setTextColor(getResources().getColor(R.color.red));
                if(mYejiBaobiao==null){
                    mYejiBaobiao=new YejiBaobiao();
                    transaction.add(R.id.yousuanfragment,mYejiBaobiao);
                }else{
                    transaction.show(mYejiBaobiao);
                }
                break;
        }
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.celielayout:
                setTitleTab(0);
                break;
            case R.id.quanyilayout:
                setTitleTab(1);
                break;
            case R.id.yuedulayout:
                setTitleTab(2);
                break;
            case R.id.duokonglayout:
                setTitleTab(3);
                break;
            case R.id.huicelayout:
                setTitleTab(4);
                break;
            case R.id.yejilayout:
                setTitleTab(5);
                break;
            case R.id.tljr_yousuan_fanhui:
                finish();
                break;
            case R.id.yousuan_datatext:
                if(getIntent().getBooleanExtra("yousaunitemstatus",false)) {
                    Intent intent=new Intent(this,YouSuanData.class);
                    intent.putExtra("DataId",itemModel.getGetTou().getId());
                    intent.putExtra("YouSuanKey",itemModel.getGetTou().getKey());
                    intent.putExtra("name",itemModel.getGetTou().getName());
                    startActivity(intent);
                }else{
                    Intent intent=new Intent(this,YouSuanData.class);
                    intent.putExtra("DataId",model.getId());
                    intent.putExtra("YouSuanKey",model.getKey());
                    intent.putExtra("name",model.getName());
                    startActivity(intent);
                }
                break;
            case R.id.yousuan_item_shiyong:
                shiyong();
                break;
            case R.id.yousuan_item_goumai:
                goumai();
                break;
            case R.id.yousuanitem_quhui:
                startActivity(new Intent(this,YouSuanZhuanChu.class));
                break;
        }
    }

    public void goumai(){
        if(mNum.getText().toString().equals("")){
            Toast.makeText(getBaseContext(),"请输入金额",Toast.LENGTH_SHORT).show();
            return ;
        }

        if(Integer.valueOf(mNum.getText().toString())<10){
            Toast.makeText(getBaseContext(),"跟投金额不能少于10块",Toast.LENGTH_SHORT).show();
            return ;
        }

        final float money=Float.valueOf(mNum.getText().toString()+"");
        if (User.getUser() == null) {
            Toast.makeText(getBaseContext(),"请先登录！",Toast.LENGTH_SHORT).show();
            return;
        }
        new PromptDialog(context,"购买需要支付"+money+"元,是否支付？",
                new Complete() {
                    @Override
                    public void complete() {
                        MyApplication.getInstance().getMainActivity().mYouSuanBaseFragment.mYouSuanShishi.adapter.paybean= new ZhongchouBean();
                        MyApplication.getInstance().getMainActivity().mYouSuanBaseFragment.mYouSuanShishi.adapter.paybean.setRewardMoney(new Float(money*100).intValue());
                        MyApplication.getInstance().getMainActivity().mYouSuanBaseFragment.mYouSuanShishi.adapter.paybean.setId(model.getId()+"");
                        ((Activity) context).startActivityForResult(new Intent(context, PayActivity.class)
                                .putExtra("money",(float)money).putExtra("canuse", false),4);
                    }
                }).show();
    }

    public void shiyong(){
        if(User.getUser()==null){
            return ;
        }
        if(mNum.getText().toString().equals("")){
            Toast.makeText(this,"金额不能为空",Toast.LENGTH_SHORT).show();
            return ;
        }
        float money=Float.valueOf(mNum.getText().toString()+"");

        String param="uid="+User.getUser().getId()+"&token="+Configs.token+"&money="+money+"&id="+model.getId();
        NetUtil.sendPost(ShiyongUrl,param,new NetResult() {
            @Override
            public void result(String response) {
                try{
                    JSONObject object=new JSONObject(response);
                    if(object.getInt("status")==1){
                        Toast.makeText(getBaseContext(),object.getString("msg"),Toast.LENGTH_SHORT).show();
                        mNum.setText("");
                    }
                    LogUtil.e("yousuanitem_response",response);
                }catch(Exception e){

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0 && TljrZiXuanLineChart.mSYChart != null) {
            TljrZiXuanLineChart.mSYChart.initView();
        }
        if (resultCode == 1) {
            String type = data.getStringExtra("type");
            float quan = data.getFloatExtra("quan", 0);
            if(requestCode == 4){
                LogUtil.e("requestCode",requestCode+"");
                MyApplication.getInstance().getMainActivity().mYouSuanBaseFragment.mYouSuanShishi.onpay(type, quan*100, null);
            }
        }
    }

}

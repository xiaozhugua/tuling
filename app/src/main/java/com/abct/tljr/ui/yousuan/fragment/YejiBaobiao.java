package com.abct.tljr.ui.yousuan.fragment;

import com.abct.tljr.R;
import com.abct.tljr.ui.yousuan.activity.YouSuanItem;
import com.abct.tljr.ui.yousuan.model.YouSuanItemModel;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class YejiBaobiao extends Fragment{

	private View yeji=null;
    public YouSuanItemModel model=null;

    private TextView chushiquanyi=null;
    private TextView zuixinquanyi=null;
    private TextView zongshouyilu=null;
    private TextView nianhuashouyilu=null;
    private TextView xiapubilu=null;

    private TextView zuidahuicezhi=null;
    private TextView adhc_fashengtime=null;
    private TextView adhc_jieshutime=null;
    private TextView shouyifengxianbi=null;
    private TextView zuidahuicelu=null;
    private TextView syfxb_fashengtime=null;
    private TextView syfxb_jieshitime=null;

    private SimpleDateFormat mSimpleDateFormat=null;
    private DecimalFormat mDecimalFormat=null;

	@Override
	public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		yeji=inflater.inflate(R.layout.yejibaobiao, container, false);
        mSimpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        mDecimalFormat=new DecimalFormat("###0.00");
        model=((YouSuanItem)getActivity()).model;
        if(model==null){
            this.model=((YouSuanItem)getActivity()).itemModel.getGetTou();
        }
        initView();
        initValue();
        return yeji;
	}

    public void initView(){
        chushiquanyi=(TextView)yeji.findViewById(R.id.syl_chushiquanyi);
        zuixinquanyi=(TextView)yeji.findViewById(R.id.syl_zuixinquanyi);
        zongshouyilu=(TextView)yeji.findViewById(R.id.syl_zongshouyilu);
        nianhuashouyilu=(TextView)yeji.findViewById(R.id.syl_nianhuashouyilu);
        xiapubilu=(TextView)yeji.findViewById(R.id.syl_xiapubilu);

        zuidahuicezhi=(TextView)yeji.findViewById(R.id.hczb_zuidahuicezhi);
        adhc_fashengtime=(TextView)yeji.findViewById(R.id.hczb_fashengtime);
        adhc_jieshutime=(TextView)yeji.findViewById(R.id.hczb_jieshutime);
        shouyifengxianbi=(TextView)yeji.findViewById(R.id.hczb_shouyifengxianbi);
        zuidahuicelu=(TextView)yeji.findViewById(R.id.hczb_zuidahuicelu);
        syfxb_fashengtime=(TextView)yeji.findViewById(R.id.hczb_zdfsl_fashengshijian);
        syfxb_jieshitime=(TextView)yeji.findViewById(R.id.hczb_zdfsl_jieshutime);
    }

    public void initValue(){
        chushiquanyi.setText(model.getChuShiJingZhi()+"");
        zuixinquanyi.setText(mDecimalFormat.format(model.getZuiXinJingZhi())+"");
        zongshouyilu.setText(Float.valueOf(mDecimalFormat.format(model.getTotalYield()))*100+"%");
        nianhuashouyilu.setText(Float.valueOf(mDecimalFormat.format(model.getAnnualRate()))*100+"%");
        xiapubilu.setText(Float.valueOf(mDecimalFormat.format(model.getSharpRate()))*100+"");

        zuidahuicezhi.setText(model.getMaxDrawDownMoney()+"");
        adhc_fashengtime.setText(mSimpleDateFormat.format(model.getMaxDrawDownMoneyStartTime()*1000)+"");
        adhc_jieshutime.setText(mSimpleDateFormat.format(model.getMaxDrawDownMoneyEndTime()*1000)+"");
        shouyifengxianbi.setText(mDecimalFormat.format(model.getEarningsRisk())+"");
        zuidahuicelu.setText(Float.valueOf(mDecimalFormat.format(model.getMaxDrawDown()))*100+"%");
        syfxb_fashengtime.setText(mSimpleDateFormat.format(model.getMaxDrawDownStartTime()*1000)+"");
        syfxb_jieshitime.setText(mSimpleDateFormat.format(model.getMaxDrawDownEndTime()*1000)+"");
    }

}

package com.abct.tljr.ui.yousuan.util;

import android.graphics.Color;

import com.abct.tljr.ui.fragments.zhiyanFragment.util.ZhiYanParseJson;
import com.abct.tljr.ui.yousuan.model.QiYuXianModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/5/27.
 */
public class YouSuanMyChart {

    private ArrayList<Integer> QuanyiColors = new ArrayList<Integer>();
    private SimpleDateFormat mSimpleDateFormat=null;
    private LineChart quanyiquxian=null;
    private List<Entry> quangyi=null;
    private LineData quanyidata=null;
    private List<String> xVals=null;
    private int TempData=0;
    private String key;
    private String QuanyiquxianUrl= UrlUtil.URL_YS+"gentou/getJingZhi";
    public List<QiYuXianModel> listModel=null;
    private int size=1000;
    private int page=1;
    private boolean refresh=false;

    public YouSuanMyChart(LineChart quanyiquxian,String key){
        this.quanyiquxian=quanyiquxian;
        this.key=key;
        listModel=new ArrayList<>();
        initLineChart();
        getNetData();
    }

    public YouSuanMyChart(LineChart quanyiquxian,String key,List<QiYuXianModel> listModel){
        this.quanyiquxian=quanyiquxian;
        this.key=key;
        initLineChart();
        if(listModel==null||listModel.size()==0){
            getNetData();
        }else{
            hide();
            initData(listModel);
        }
    }

    public void initLineChart(){
        mSimpleDateFormat=new SimpleDateFormat("MM/dd");
        quanyiquxian.setDescription("");
        quanyiquxian.setMaxVisibleValueCount(60);
        quanyiquxian.setPinchZoom(true);
        quanyiquxian.setDrawGridBackground(true);
        quanyiquxian.setNoDataTextDescription("");
        quanyiquxian.setTouchEnabled(true);
        quanyiquxian.setDragEnabled(true);// 是否可以拖拽
        quanyiquxian.setScaleEnabled(true);

        XAxis xAxis =quanyiquxian.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(2);
        xAxis.setAdjustXLabels(true);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(139, 148, 153));
        xAxis.setTextSize(7);
        xAxis.setAvoidFirstLastClipping(true);

        YAxis leftAxis =quanyiquxian.getAxisLeft();
        leftAxis.setLabelCount(5);
        leftAxis.setStartAtZero(false);
        leftAxis.setTextColor(Color.rgb(139, 148, 153));
        leftAxis.setTextSize(7.5f);

        YAxis rightAxis=quanyiquxian.getAxisRight();
        rightAxis.setEnabled(false);

        quanyiquxian.setDrawGridBackground(true);
        quanyiquxian.getLegend().setEnabled(false);
    }

    public void hide(){
        quanyiquxian.getAxisLeft().setEnabled(false);
        quanyiquxian.getXAxis().setEnabled(false);
    }

    public void getNetData(){
        refresh=true;
        String params="name="+key+"&page="+page+"&size="+size;
        NetUtil.sendGet(QuanyiquxianUrl,params,new NetResult() {
            @Override
            public void result(String response){
                if(!response.equals("")){
                    refresh=false;
                    ZhiYanParseJson.ParseQuanyiquxian(listModel,response);
                    if(listModel.size()>0){
                        initData(listModel);
                    }
                }
            }
        });
    }

    public void UpdateChart(LineChart quanyiquxian){
        LogUtil.e("YouSuanShiShiSize",listModel.size()+"");
        LogUtil.e("YouSuanShiShiKey",key+"");
        if(refresh){
            return ;
        }
        this.quanyiquxian=quanyiquxian;
        quanyiquxian.clear();
        initLineChart();
        TempData=0;
        quanyiquxian.setData(quanyidata);
        quanyiquxian.invalidate();
        //initData(listModel);
    }

    public void initData(List<QiYuXianModel> listModel){
        if(listModel.size()<=0){
            return ;
        }
        quangyi=new ArrayList<Entry>();
        xVals=new ArrayList<String>();
        for(int i=0;i<listModel.size();i++){
            TempData+=listModel.get(i).getJingZhi();
            quangyi.add(new Entry(TempData,i));
            xVals.add(mSimpleDateFormat.format(new Date(listModel.get(i).getData()*1000))+"");
        }
        LineDataSet lineDataQuanyi = new LineDataSet(quangyi, "权益曲线");
        lineDataQuanyi.setDrawCircles(false);
        lineDataQuanyi.setHighLightColor(Color.rgb(255, 183, 183));
        lineDataQuanyi.setDrawCubic(true);
        lineDataQuanyi.setCubicIntensity(0.2f);
        lineDataQuanyi.setDrawFilled(true);
        lineDataQuanyi.setColor(Color.rgb(255, 183, 183));
        lineDataQuanyi.setHighLightColor(Color.rgb(255, 183, 183));
        lineDataQuanyi.setFillColor(ColorTemplate.getHoloBlue());
        quanyidata=new LineData(xVals,lineDataQuanyi);

        initView();
    }

    public void initView(){
        quanyiquxian.setData(quanyidata);
        quanyiquxian.invalidate();
    }

}

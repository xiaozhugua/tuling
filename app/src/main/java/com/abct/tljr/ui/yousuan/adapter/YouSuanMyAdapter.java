package com.abct.tljr.ui.yousuan.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.abct.tljr.R;
import com.abct.tljr.model.Options;
import com.abct.tljr.ui.yousuan.activity.YouSuanItem;
import com.abct.tljr.ui.yousuan.model.YouSuanMyItem;
import com.abct.tljr.ui.yousuan.util.YouSuanMyChart;
import com.github.mikephil.charting.charts.LineChart;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class YouSuanMyAdapter extends ArrayAdapter<YouSuanMyItem>{

    private int Resource;
    private Context context;
    private SimpleDateFormat mSimpleDateFormat=null;

    public YouSuanMyAdapter(Context context, int resource,List<YouSuanMyItem> objects) {
        super(context, resource, objects);
        this.Resource=resource;
        this.context=context;
        mSimpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View yousuanview=null;
        YousuanViewHolder viewHolder=null;
        final YouSuanMyItem model=getItem(position);
        if(convertView==null){
            viewHolder=new YousuanViewHolder();
            yousuanview=LayoutInflater.from(getContext()).inflate(Resource,parent,false);
            viewHolder.name=(TextView)yousuanview.findViewById(R.id.yousuan_item_name);
            viewHolder.zhangdiefu=(TextView)yousuanview.findViewById(R.id.yousuan_item_percent);
            viewHolder.dangyueshouyi=(TextView)yousuanview.findViewById(R.id.yousuan_item_yuepercent);
            viewHolder.updatetime=(TextView)yousuanview.findViewById(R.id.yousuan_item_updatetime);
            viewHolder.huice=(TextView)yousuanview.findViewById(R.id.yousuan_item_huice);
            viewHolder.baohedu=(TextView)yousuanview.findViewById(R.id.yousuan_item_baohe);
            viewHolder.buycash=(TextView)yousuanview.findViewById(R.id.yousuan_cash);
            viewHolder.yinkui=(TextView)yousuanview.findViewById(R.id.yousuan_thing);
            viewHolder.zoushi=(LineChart)yousuanview.findViewById(R.id.yousuan_item_zoushi);
            viewHolder.testImage=(ImageView)yousuanview.findViewById(R.id.yousuan_my_testimage);
            viewHolder.yousuanitem_bg=(LinearLayout)yousuanview.findViewById(R.id.yousuanitem_bg);
            yousuanview.setTag(viewHolder);
        }else{
            yousuanview=convertView;
            viewHolder=(YousuanViewHolder)yousuanview.getTag();
        }

        viewHolder.name.setText(model.getGetTou().getName());
        viewHolder.zhangdiefu.setText(model.getDayRate()+"");
        viewHolder.dangyueshouyi.setText(model.getMoonRate()+"");
        viewHolder.updatetime.setText(mSimpleDateFormat.format(new Date(Long.valueOf(model.getUpdateDate())*1000)));
        viewHolder.huice.setText(model.getMaxDrawDown()+"");
        viewHolder.baohedu.setText(model.getGetTou().getSaturation()+"");
        viewHolder.buycash.setText(model.getAllMoney()+"");
        viewHolder.yinkui.setText(model.getProfitAndLossMoney()+"");

        if(model.getYouSuanMyChart()==null){
            model.setYouSuanMyChart(new YouSuanMyChart(viewHolder.zoushi,model.getGetTou().getKey()));
        }else{
            model.getYouSuanMyChart().UpdateChart(viewHolder.zoushi);
        }

        yousuanview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,YouSuanItem.class);
                intent.putExtra("YouSuanItem",model.getGetTou());
                intent.putExtra("yousaunitemstatus",true);
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });

        if(model.isTest()){
            viewHolder.testImage.setVisibility(View.VISIBLE);
            viewHolder.yousuanitem_bg.setBackground(context.getResources().getDrawable(R.drawable.img_shiyong));
        }else{
            viewHolder.testImage.setVisibility(View.GONE);
            viewHolder.yousuanitem_bg.setBackground(context.getResources().getDrawable(R.drawable.img_shangzhang));
        }

        return yousuanview;
    }

    class YousuanViewHolder{
        public TextView name;
        public TextView zhangdiefu;
        public TextView dangyueshouyi;
        public TextView updatetime;
        public TextView huice;
        public TextView baohedu;
        public TextView buycash;
        public TextView yinkui;
        public LineChart zoushi;
        public ImageView testImage;
        public LinearLayout yousuanitem_bg;
    }

}

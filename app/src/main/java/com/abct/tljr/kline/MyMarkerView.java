
package com.abct.tljr.kline;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.abct.tljr.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.qh.common.util.ColorUtil;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;


/**
 * Custom implementation of the MarkerView.
 * 
 * @author Philipp Jahoda
 */
public class MyMarkerView extends MarkerView {

	 private TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7;
	    private static ArrayList<List<Stock>> Stocks;
	    private String TAG = "MyMarkerView";
	    public static ArrayList<OneData> datas;
	    public static int start = 0;
	    
	    public MyMarkerView(Context context, int layoutResource) {
	        super(context, layoutResource);

	        tv1 = (TextView) findViewById(R.id.tvValue);
	        tv2 = (TextView) findViewById(R.id.tv_time);
	        tv3 = (TextView) findViewById(R.id.tv_updowm);
	        tv4 = (TextView) findViewById(R.id.tv_updowmpersen);
	        tv5 = (TextView) findViewById(R.id.tv_size);
	        tv6 = (TextView) findViewById(R.id.tv_average);
	        tv7 = (TextView) findViewById(R.id.tv_total);
	        Stocks = new ArrayList<List<Stock>>();
	        for(int i = 0;i<3;i++){
	        	ArrayList<Stock> stock = new ArrayList<Stock>();
	        	Stocks.add(stock);
	        }
	    }
	    
	    
	    // callbacks everytime the MarkerView is redrawn, can be used to update the
	    // content (user-interface)
	    @Override
	    public void refreshContent(Entry e, int dataSetIndex) {
	    	int id = e.getXIndex()+start;
	    	if(datas.size()>id){
	    		if (e instanceof CandleEntry) {
	    			tv4.setVisibility(View.VISIBLE);
	    			tv5.setVisibility(View.VISIBLE);
	    			tv6.setVisibility(View.VISIBLE);
	    			tv7.setVisibility(View.VISIBLE);
	    			tv1.setText("时间："+datas.get(id).getDate());
	    			tv3.setText(StringChangeRed("最高："+datas.get(id).getHighPrice(),3));
	    			tv4.setText(StringChangeGreed("最低："+datas.get(id).getLowPrice(),3));
	    			float x;
	    			if(id>0){
	    				x = (datas.get(id).getEndPrice()-datas.get(id-1).getEndPrice())/datas.get(id-1).getEndPrice();
	    			}else{
	    				x = (datas.get(id).getEndPrice()-datas.get(id).getStartPrice())/datas.get(id).getStartPrice();
	    			}
	    			String s = floachange(x*100);
	    			if(s.equals("0")){
	    				
	    			}else if(s.subSequence(0, 1).equals(".")){
	    				s = "0" + s;
	    			}else if(s.subSequence(0, 2).equals("-.")){
	    				s = "-0"+s.substring(1, s.length());
	    			}
	    			if(x>=0){
	    				tv2.setText(StringChangeRed("开盘："+datas.get(id).getStartPrice(),3));
	    				tv5.setText(StringChangeRed("收盘："+datas.get(id).getEndPrice(),3));
	    				tv6.setText(StringChangeRed("涨幅：+"+s+"%",3));
	    			}else{
	    				tv2.setText(StringChangeGreed("开盘："+datas.get(id).getStartPrice(),3));
	    				tv5.setText(StringChangeGreed("收盘："+datas.get(id).getEndPrice(),3));
	    				tv6.setText(StringChangeGreed("涨幅："+s+"%",3));
	    			}
	    			tv7.setText("交量："+datas.get(id).getChanges());
	    		} else {
	    			tv4.setVisibility(View.GONE);
	    			tv5.setVisibility(View.GONE); 
	    			tv6.setVisibility(View.GONE);
	    			tv7.setVisibility(View.GONE);
	    			tv1.setText("时间："+datas.get(id).getDate());
	    			tv2.setText("现价："+datas.get(id).getEndPrice());
	    			tv3.setText("交易量："+datas.get(id).getChanges());
	    		}
	    	}else{
	    	}
	    }
	    
	    public static SpannableStringBuilder StringChangeRed(String st,int startnum){
	        SpannableStringBuilder style=new SpannableStringBuilder(st);  
	        style.setSpan(new ForegroundColorSpan(ColorUtil.red),startnum,st.length(),Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 
	    	return style;
	    }
	    
	    public static SpannableStringBuilder StringChangeGreed(String st,int startnum){
	        SpannableStringBuilder style=new SpannableStringBuilder(st);  
	        style.setSpan(new ForegroundColorSpan(ColorUtil.green),startnum,st.length(),Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 
	    	return style;
	    }
	    
	    public static String floachange(float price){
	    	DecimalFormat decimalFormat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
	    	String p = decimalFormat.format(price);//format 返回的是字符串
			return p;
	    }

	    @Override
	    public int getXOffset() {
	        // this will center the marker-view horizontally
	        return -(getWidth() / 2);
	    }

	    @Override
	    public int getYOffset() {
	        // this will cause the marker-view to be above the selected value
	        return -getHeight();
	    }

}

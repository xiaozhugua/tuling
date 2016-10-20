package com.abct.tljr.kline;

import java.text.DecimalFormat;

import com.abct.tljr.utils.Util;
import com.github.mikephil.charting.utils.ValueFormatter;

public class MyValueFormatter implements ValueFormatter {

	private DecimalFormat mFormat;
	private float startvalue = 0;

	public MyValueFormatter(int PriceSmallerOne) {
		if (PriceSmallerOne == 4) {
			mFormat = new DecimalFormat("###,###,###,##0");
		} else if (PriceSmallerOne == 3) {
			mFormat = new DecimalFormat("###,###,###,##0.000");
		} else if (PriceSmallerOne == 2) {
			mFormat = new DecimalFormat("###,###,###,##0.00");
		} else if (PriceSmallerOne == 1) {
			mFormat = new DecimalFormat("###,###,###,##0.0");
		} else if (PriceSmallerOne == 5) {
			mFormat = new DecimalFormat("###,###,###,##0.0%");
		}
	}

	@Override
	public String getFormattedValue(float value) {
		String s = "";
		if(startvalue == 0){
			if (value >= 100000000000l) {
				s = mFormat.format(value / 1000000000000l) + "万亿";
			} else if (value >= 100000000) {
				s = mFormat.format(value / 100000000) + "亿";
			} else if (value >= 10000) {
				mFormat = new DecimalFormat("###,###,###,##0.0");
				s = mFormat.format(value / 10000) + "万";
			} else {
				s = mFormat.format(value);
			}
		}else{
			float f = (value - startvalue)/startvalue*100;
			if(f<10.7 && f>-10.7){
//			Log.d("moveNest", "f :"+f+"  value :"+value+"  startva :"+startvalue);
				s = Util.df.format(f)+"%";
			}
		}
		return s; 
	}

	public void setStartvalue(float Startvalues){
		this.startvalue = Startvalues;
	}
	public float getStartvalue(){
		return startvalue;
	}

}

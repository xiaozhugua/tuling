package com.abct.tljr.hangqing.linechar;


import java.text.NumberFormat;
import com.github.mikephil.charting.utils.ValueFormatter;

public class MyValueFormatter2 implements ValueFormatter {

	//获取格式化对象
	NumberFormat nt;
	   
	public MyValueFormatter2() {
		nt = NumberFormat.getPercentInstance();
		nt.setMaximumIntegerDigits(2);
		nt.setMinimumFractionDigits(1);
	}

	@Override
	public String getFormattedValue(float value) {
		// TODO Auto-generated method stub
		return nt.format((double)value);
	}

}

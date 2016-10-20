package com.abct.tljr.ui.yousuan.util;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.NumberFormat;

public class DuoKongValueFormatter implements ValueFormatter {

	//获取格式化对象
	NumberFormat nt;
	   
	public DuoKongValueFormatter() {
		nt = NumberFormat.getPercentInstance();
		nt.setMaximumIntegerDigits(2);
		nt.setMinimumFractionDigits(1);
	}

	@Override
	public String getFormattedValue(float value) {
		return (value/100)+"%";
	}

}
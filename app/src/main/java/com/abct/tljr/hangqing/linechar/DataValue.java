package com.abct.tljr.hangqing.linechar;

import java.text.NumberFormat;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class DataValue implements ValueFormatter {

	// 获取格式化对象
	NumberFormat nt;

	public DataValue() {
		nt = NumberFormat.getPercentInstance();
		// 设置百分数精确度2即保留两位小数
		nt.setMinimumFractionDigits(2);
	}


	@Override
	public String getFormattedValue(float value) {
		// TODO Auto-generated method stub
		return nt.format((double) value);
	}

}

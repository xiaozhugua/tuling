package com.abct.tljr.kline.gegu.util;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.abct.tljr.R;
import com.abct.tljr.utils.ViewUtil;
import com.github.mikephil.charting.data.Entry;
import com.qh.common.model.AppInfo;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ChartUtils {

	private static int screenWith;
	private static int itemWith;

	/**
	 * 设置营业收入比重图表下面的标签
	 */
	@SuppressWarnings("unused")
	public static void setBelowLables(ArrayList<String> xVals, ArrayList<Entry> yVals, ArrayList<Integer> colors,
			float countAll, LinearLayout container, Context context) {
		// TODO Auto-generated method stub
		// perLables
		DecimalFormat mFormat = new DecimalFormat("###,###,##0.0");

		screenWith = ViewUtil.getScreenWidth(context);// 当前屏幕宽度
		itemWith = (screenWith - AppInfo.dp2px(context, 30)) / 3;// 条目宽度
		if (xVals.size() <= 3) {
			LinearLayout     linearLayoutShort=getLinearLayout(context);
			linearLayoutShort.setGravity(Gravity.CENTER);
			for (int i = 0; i < xVals.size(); i++) {
				View lableView = View.inflate(context, R.layout.layout_yysr_lable, null);

				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemWith, LayoutParams.WRAP_CONTENT);
				lableView.setLayoutParams(params);
				View diamondsView = lableView.findViewById(R.id.layout_yysr_lable_view);// 小图块
				TextView tv = (TextView) lableView.findViewById(R.id.layout_yysr_lable_tv);// 文字说明

				diamondsView.setBackgroundColor(colors.get(i));

				float value = yVals.get(i).getVal() * (100.0f) / countAll;

				//tv.setText(xVals.get(i) + mFormat.format(value) + " %");
				tv.setText(xVals.get(i) );
				if (i != 3) {

					lableView.setPadding(0, 0, AppInfo.dp2px(context, 5), 0);
				}
                linearLayoutShort.addView(lableView);       

			}
			container.addView(linearLayoutShort);
		} else {

			int number = (xVals.size() % 3 == 0) ? (xVals.size() / 3) : (xVals.size() / 3 + 1);// 行数

			int  lineCount=1;//行数的计时器
			int count = 0;// 计数器
			LinearLayout linearLayout = getLinearLayout(context);
			for (int i = 0; i < xVals.size(); i++) {
				count++;
                          
				if (count > 3) {
					count = 1;
					lineCount++;
					container.addView(linearLayout);
					linearLayout = getLinearLayout(context);
				}

				View lableView = View.inflate(context, R.layout.layout_yysr_lable, null);
				View diamondsView = lableView.findViewById(R.id.layout_yysr_lable_view);// 小图块
				TextView tv = (TextView) lableView.findViewById(R.id.layout_yysr_lable_tv);// 文字说明

				diamondsView.setBackgroundColor(colors.get(i));

				float value = yVals.get(i).getVal() * (100.0f) / countAll;
				//tv.setText(xVals.get(i) + mFormat.format(value) + " %");
				tv.setText(xVals.get(i));
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemWith, LayoutParams.WRAP_CONTENT);
				if (count != 3) {
					params.rightMargin = AppInfo.dp2px(context, 5);
				}

				lableView.setLayoutParams(params);
				linearLayout.addView(lableView);
				
				if (lineCount==number  && i==xVals.size()-1) {
					container.addView(linearLayout);
				}

			}
		}

	}

	private static LinearLayout getLinearLayout(Context context) {
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setGravity(Gravity.CENTER_VERTICAL);
		return linearLayout;
	}

}

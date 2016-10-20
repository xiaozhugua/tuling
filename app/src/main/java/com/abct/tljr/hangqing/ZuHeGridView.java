package com.abct.tljr.hangqing;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class ZuHeGridView extends GridView{

	public ZuHeGridView(Context context, AttributeSet attrs) {   
        super(context, attrs);   
    }   
   
    public ZuHeGridView(Context context) {   
        super(context);   
    }   
   
    public ZuHeGridView(Context context, AttributeSet attrs, int defStyle) {   
        super(context, attrs, defStyle);   
    }   
   
    @Override   
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {   
   
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);   
        super.onMeasure(widthMeasureSpec, expandSpec);   
    }   
}

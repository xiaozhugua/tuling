package com.abct.tljr.ui.widget;

import com.abct.tljr.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Carr on 2015/10/9.
 */
public class RatingBar extends LinearLayout {

    private boolean mClickable;
    private int starCount;
    private RatingBar.OnRatingChangeListener onRatingChangeListener;
    private float starImageSize;
    private Drawable starEmptyDrawable;
    private Drawable starFillDrawable;
    private Drawable starHalfDrawable;

    public void setOnRatingChangeListener(RatingBar.OnRatingChangeListener onRatingChangeListener) {
        this.onRatingChangeListener = onRatingChangeListener;
    }

    public void setmClickable(boolean clickable) {
        this.mClickable = clickable;
    }

    public void setStarFillDrawable(Drawable starFillDrawable) {
        this.starFillDrawable = starFillDrawable;
    }

    public void setStarEmptyDrawable(Drawable starEmptyDrawable) {
        this.starEmptyDrawable = starEmptyDrawable;
    }

    public void setStarHalfDrawable(Drawable starHalfDrawable) {
        this.starHalfDrawable = starHalfDrawable;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public void setStarImageSize(float starImageSize) {
        this.starImageSize = starImageSize;
    }



    public RatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOrientation(HORIZONTAL);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RatingBar);
        this.starImageSize = mTypedArray.getDimension(R.styleable.RatingBar_starImageSize, 20.0F);
        this.starCount = mTypedArray.getInteger(R.styleable.RatingBar_starCount, 5);
        this.StarPadding = mTypedArray.getInteger(R.styleable.RatingBar_starPadding, 5);
        this.starEmptyDrawable = mTypedArray.getDrawable(R.styleable.RatingBar_starEmpty);
        this.starFillDrawable = mTypedArray.getDrawable(R.styleable.RatingBar_starFill);
        this.starHalfDrawable=mTypedArray.getDrawable(R.styleable.RatingBar_starHalf);
        this.mClickable = mTypedArray.getBoolean(R.styleable.RatingBar_clickable, false);

        mTypedArray.recycle();

        for (int i = 0; i < this.starCount; ++i) {
            ImageView imageView = this.getStarImageView(context, attrs);
            imageView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (RatingBar.this.mClickable) {  //判断星星可以点击
                        RatingBar.this.setStar(RatingBar.this.indexOfChild(v) + 1);  //设置当前评分
                        if (RatingBar.this.onRatingChangeListener != null) {
                            RatingBar.this.onRatingChangeListener.onRatingChange(RatingBar.this.indexOfChild(v) + 1);  //调用监听接口
                        }
                    }
                }
            });
            this.addView(imageView);
        }

    }

    
    private int StarPadding =5;
    
      

	//初始化单个星星控件
    private ImageView getStarImageView(Context context, AttributeSet attrs) {
        ImageView imageView = new ImageView(context);
        LayoutParams para = new LayoutParams(Math.round(this.starImageSize), Math.round(this.starImageSize));
        imageView.setLayoutParams(para);
        imageView.setPadding(0, 0, StarPadding, 0);
        imageView.setImageDrawable(this.starEmptyDrawable);
        imageView.setMaxWidth(10);
        imageView.setMaxHeight(10);
        return imageView;
    }

    //设置当前评分
    public void setStar(float mark) {

        //判断评分，不能大于星星总数，不能小于0
        mark = mark > this.starCount ? this.starCount : mark;
        mark = starCount < 0 ? 0 : mark;

        float xiaoshu=mark-(int)(mark); //计算分数的小数部分
        int zhengshu=(int)mark; //计算分数的整数部分

        //显示整数部分的星星，全部是实心星星
        for (int i = 0; i < zhengshu; ++i) {
            ((ImageView) this.getChildAt(i)).setImageDrawable(this.starFillDrawable);
        }

        //显示小数部分的星星
        if(xiaoshu>0)//如果小数部分大于0，则显示半分星星，当然这里可以做更加精确一点的判断
        {
            if(zhengshu<this.starCount)
            ((ImageView) this.getChildAt(zhengshu)).setImageDrawable(this.starHalfDrawable);
        }else   //如果小数部分小于或等于0，显示全空星星
        {
            if(zhengshu<this.starCount)
            ((ImageView) this.getChildAt(zhengshu)).setImageDrawable(this.starEmptyDrawable);
        }

       //剩余部分用全空星星显示
        for(int j=++zhengshu;j<this.starCount;j++)
        {
            ((ImageView) this.getChildAt(j)).setImageDrawable(this.starEmptyDrawable);
        }

    }

    //定义星星点击的监听接口
    public interface OnRatingChangeListener {
        void onRatingChange(int var1);
    }

}

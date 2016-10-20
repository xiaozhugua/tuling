package com.abct.tljr.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.abct.tljr.R;

/**
 * Created by Administrator on 2016/1/27.
 */
public class ShapeTextView extends TextView {
    int solidColor, stroke_Color, gradientStartColor, gradientEndColor,
            gradientCenterColor, touchColor;
    int cornesRadius, topLeftRadius, topRightRadius, bottomLeftRadius,
            bottomRightRadius, stroke_Width, strokeDashWidth, strokeDashGap,
            gradientAngle, gradientRadius, gradientType, gradientOrientation, shapeType;
    boolean gradientUseLevel;
    GradientDrawable gradientDrawable;
    public ShapeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public ShapeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context, attrs);
    }
    public ShapeTextView(Context context) {
        super(context);
    }
    private void initData(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ShapeTextview);
        solidColor = a.getColor(R.styleable.ShapeTextview_solidColor,
                Color.TRANSPARENT);
        stroke_Color = a.getColor(R.styleable.ShapeTextview_stroke_Color,
                Color.TRANSPARENT);
        gradientStartColor = a
                .getColor(R.styleable.ShapeTextview_gradientStartColor,
                        Color.TRANSPARENT);
        gradientEndColor = a.getColor(
                R.styleable.ShapeTextview_gradientEndColor, Color.TRANSPARENT);
        gradientCenterColor = a.getColor(
                R.styleable.ShapeTextview_gradientCenterColor,
                Color.TRANSPARENT);
        touchColor = a.getColor(R.styleable.ShapeTextview_touchSolidColor,
                Color.TRANSPARENT);
        cornesRadius = (int) a.getDimension(
                R.styleable.ShapeTextview_cornesRadius, 0);
        topLeftRadius = (int) a.getDimension(
                R.styleable.ShapeTextview_topLeftRadius, 0);
        topRightRadius = (int) a.getDimension(
                R.styleable.ShapeTextview_topRightRadius, 0);
        bottomLeftRadius = (int) a.getDimension(
                R.styleable.ShapeTextview_bottomLeftRadius, 0);
        bottomRightRadius = (int) a.getDimension(
                R.styleable.ShapeTextview_bottomRightRadius, 0);
        stroke_Width = (int) a.getDimension(
                R.styleable.ShapeTextview_stroke_Width, 0);
        strokeDashWidth = (int) a.getDimension(
                R.styleable.ShapeTextview_strokeDashWidth, 0);
        strokeDashGap = (int) a.getDimension(
                R.styleable.ShapeTextview_strokeDashGap, 0);
        gradientAngle = (int) a.getDimension(
                R.styleable.ShapeTextview_gradientAngle, 0);
        gradientRadius = (int) a.getDimension(
                R.styleable.ShapeTextview_gradientRadius, 0);
        gradientUseLevel = a.getBoolean(
                R.styleable.ShapeTextview_gradientUseLevel, false);
        gradientType = a.getInt(R.styleable.ShapeTextview_gradientType, -1);
        gradientOrientation = a.getInt(
                R.styleable.ShapeTextview_gradientOrientation, -1);
        shapeType = a.getInt(
                R.styleable.ShapeTextview_shapeType, -1);

        gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(stroke_Width, stroke_Color, strokeDashWidth,
                strokeDashGap);
        // 如果设定的有Orientation 就默认为是渐变色的Button，否则就是纯色的Button
        if (gradientOrientation != -1) {
            gradientDrawable
                    .setOrientation(getOrientation(gradientOrientation));
            gradientDrawable.setColors(new int[] { gradientStartColor,
                    gradientCenterColor, gradientEndColor });
        } else {
            gradientDrawable.setColor(solidColor);
        }

        if(shapeType != -1){
            gradientDrawable.setShape(shapeType);
        }
        //是否为圆形
        if(shapeType != GradientDrawable.OVAL){
            // 如果设定的有Corner Radius就认为是4个角一样的Button， 否则就是4个不一样的角 Button
            if (cornesRadius != 0) {
                gradientDrawable.setCornerRadius(cornesRadius);
            } else {
                //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
                gradientDrawable.setCornerRadii(new float[] { topLeftRadius,
                        topLeftRadius, topRightRadius, topRightRadius,
                        bottomRightRadius, bottomRightRadius, bottomLeftRadius,
                        bottomLeftRadius });
            }
        }

        if (gradientUseLevel)
            gradientDrawable.setUseLevel(gradientUseLevel);
        if (gradientType != -1)
            gradientDrawable.setGradientType(gradientType);
        gradientDrawable.setGradientRadius(gradientRadius);
        setBackground(gradientDrawable);

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (touchColor != Color.TRANSPARENT) {
                gradientDrawable.setColor(touchColor);
                setBackground(gradientDrawable);
                postInvalidate();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (touchColor != Color.TRANSPARENT) {
                gradientDrawable.setColor(solidColor);
                setBackground(gradientDrawable);
            }
        }
        return super.onTouchEvent(event);
    }

    private GradientDrawable.Orientation getOrientation(int gradientOrientation) {
        GradientDrawable.Orientation orientation = null;
        switch (gradientOrientation) {
            case 0:
                orientation = GradientDrawable.Orientation.BL_TR;
                break;
            case 1:
                orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                break;
            case 2:
                orientation = GradientDrawable.Orientation.BR_TL;
                break;
            case 3:
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            case 4:
                orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                break;
            case 5:
                orientation = GradientDrawable.Orientation.TL_BR;
                break;
            case 6:
                orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                break;
            case 7:
                orientation = GradientDrawable.Orientation.TR_BL;
                break;
        }
        return orientation;
    }
}

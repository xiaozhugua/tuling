package com.abct.tljr.ui.widget;

import java.util.Random;

import com.qh.common.util.ColorUtil;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class DefaultIconDrawable extends Drawable {
	private int colors[] = { ColorUtil.red, ColorUtil.blue, ColorUtil.green, ColorUtil.orange, ColorUtil.pink,
			ColorUtil.purple };
	private String text;
	private boolean isv;
	private int textSize = 50;
	private int size;

	public DefaultIconDrawable(int size, String text, boolean isv) {
		// TODO Auto-generated constructor stub
		if (text == null || text.equals("")) {
			this.text = "";
		} else {
			this.text = text.substring(0, 1);
		}
		this.isv = isv;
		this.size = size;
	}

	@Override
	public void draw(Canvas canvas) {
		Rect targetRect = new Rect(0, 0, size, size);
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(colors[new Random().nextInt(colors.length)]);
		if (isv) {
			canvas.drawCircle(size / 2, size / 2, size / 2, p);
		} else {
			p.setStyle(Paint.Style.FILL);
			canvas.drawRect(targetRect, p);
		}

		p.setTextSize(textSize);
		p.setColor(ColorUtil.white);
		canvas.drawText(text, size / 2 - textSize / 2, size / 2 + textSize / 2, p);
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColorFilter(ColorFilter colorFilter) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

}

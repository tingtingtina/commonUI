package com.tina.widgetlibrary.widget.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/*
 * Create by Tina
 * Date: 2018/7/22
 * Description：
 */
public class TSeekBar extends BaseSeekBar {
    public TSeekBar(Context context) {
        super(context);
    }

    public TSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // slider
        if (isEnabled()) {
            mSliderPaint.setColor(mProgressColor);
        } else {
            mSliderPaint.setColor(mDisableColor);
        }
        mSliderPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mProgLen + mSliderRadius, mHeight / 2, mSliderRadius, mSliderPaint);
        mSliderPaint.setColor(Color.WHITE);
        canvas.drawCircle(mProgLen + mSliderRadius, mHeight / 2, mSliderRadius * 8 / 9, mSliderPaint);
    }
}

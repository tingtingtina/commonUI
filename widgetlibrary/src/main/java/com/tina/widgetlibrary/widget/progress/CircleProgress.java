package com.tina.widgetlibrary.widget.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/*
 * Create by Tina
 * Date: 2018/7/8
 * Description：
 */
public class CircleProgress extends BaseCircleProgress {

    private RectF mFrameRectF;
    private RectF mBaseRectF;

    public CircleProgress(Context context) {
        super(context);
    }

    public CircleProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mFrameWidth != 0) {
            drawFrame(canvas);
        }
        drawProgress(canvas);
        drawCenterText(canvas);
        drawSideText(canvas);
    }

    private void drawFrame(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mFrameColor);
        mPaint.setStrokeWidth(mFrameWidth);
//        mPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(mFrameRectF, mStartAngle, mSpanAngle, false, mPaint);
    }

    private void drawProgress(Canvas canvas) {
        mPaint.setColor(mBaseColor);

        if (mProgressWidth != mBaseWidth) {
            mPaint.setStrokeWidth(mBaseWidth);
            canvas.drawArc(mBaseRectF, mStartAngle, mSpanAngle, false, mPaint);
            mPaint.setStrokeWidth(mProgressWidth);
        } else {
            mPaint.setStrokeWidth(mProgressWidth);
            canvas.drawArc(mProgressRectF, mStartAngle, mSpanAngle, false, mPaint);
        }

        mAngle = mProgress * mSpanAngle / (mMax - mMin);
        mPaint.setColor(mProgressColor);
        canvas.drawArc(mProgressRectF, mStartAngle, mAngle, false, mPaint);
    }

    protected void drawCenterText(Canvas canvas) {
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        String text = mPrefixText + (int) getProgress() + mSuffixText;
        if (text != null && text.trim().length() != 0) {
            float textHeight = /*textPaint.descent() + */mTextPaint.ascent();
            canvas.drawText(text, (getWidth() - mTextPaint.measureText(text)) / 2.0f, (getHeight() - textHeight) / 2.0f, mTextPaint);
        }
    }

    protected void drawSideText(Canvas canvas) {
        float radian = (float) (((mAngle + mStartAngle) * 2 * PI) / 360);//弧度
        float mTextRadius = mCenterX - mPadding / 2;
        float mTextX = (float) (mTextRadius * Math.cos(-radian));
        float mTextY = (float) (mTextRadius * Math.sin(-radian));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float fontTotalHeight = fontMetrics.bottom - fontMetrics.top;
        float offY = fontTotalHeight / 2 - fontMetrics.bottom;
        canvas.drawText((int) mProgress + "", mCenterX + mTextX, mCenterY - mTextY + offY, mTextPaint);
    }

    @Override
    protected void updateDimension(int width, int height) {
        super.updateDimension(width, height);
        if (mFrameWidth != 0) {
            float frameRadius = mWidth / 2 - mPadding - mFrameWidth / 2;
            mFrameRectF = new RectF(mCenterX - frameRadius, mCenterY - frameRadius,
                    mCenterX + frameRadius, mCenterY + frameRadius);
        }
        if (mProgressWidth != mBaseWidth) {
            float baseRadius = mWidth / 2 - mPadding - mFrameWidth - mBaseWidth / 2;
            mBaseRectF = new RectF(mCenterX - baseRadius, mCenterY - baseRadius,
                    mCenterX + baseRadius, mCenterY + baseRadius);
        }
    }
}

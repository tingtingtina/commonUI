package com.tina.widgetlibrary.widget.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.tina.widgetlibrary.R;

/*
 * Create by Tina
 * Date: 2018/7/22
 * Description：
 */
public class BaseSeekBar extends BaseProgress {

    protected int mDisableColor;
    protected int mBaseColor;
    protected int mProgressColor;

    protected float mActiveLen;
    protected float mProgLen;
    protected float mSliderRadius;
    protected float mSeekHeight;

    protected Paint mPaint;
    protected Paint mSliderPaint;

    protected Point mStartPoint; // Start Point
    protected Point mEndPoint; // End Point
    protected Point mPoint; // Current Point

    boolean mIsTouching;
    protected SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;

    public BaseSeekBar(Context context) {
        this(context, null);
    }

    public BaseSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        super.init(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.BaseSeekBar, defStyleAttr, 0);
        mDisableColor = a.getColor(R.styleable.BaseSeekBar_seek_disable_color, mDisableColor);
        mBaseColor = a.getColor(R.styleable.BaseSeekBar_seek_base_color, mBaseColor);
        mProgressColor = a.getColor(R.styleable.BaseSeekBar_seek_progress_color, mProgressColor);
        mSeekHeight = a.getDimension(R.styleable.BaseSeekBar_seek_height, mSeekHeight);

        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(mSeekHeight);

        mSliderPaint = new Paint();
        mSliderPaint.setAntiAlias(true);
        mSliderPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void initProgress() {
        super.initProgress();
        mDisableColor = Color.parseColor("#DCDCDC");
        mBaseColor = Color.parseColor("#DCDCDC");
        mProgressColor = Color.parseColor("#00FFFF");
        mSeekHeight = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgress(canvas);
    }

    protected void drawProgress(Canvas canvas) {
        mProgLen = (mProgress - mMin) * mActiveLen / (mMax - mMin);
        // progress line
        if (isEnabled()) {
            mPaint.setColor(mProgressColor);
        } else {
            mPaint.setColor(mDisableColor);
        }
        canvas.drawLine(0, mHeight / 2, mSliderRadius + mProgLen, mHeight / 2, mPaint);

        // base line
        if (isEnabled()) {
            mPaint.setColor(mBaseColor);
        } else {
            mPaint.setColor(mDisableColor);
        }
        canvas.drawLine(mProgLen + mSliderRadius,
                mHeight / 2, mWidth, mHeight / 2, mPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isValidPoint(event.getX(), event.getY())) {
                    mIsTouching = true;
                    float x = event.getX();
                    if (x < mStartPoint.x) {
                        mPoint.set(mStartPoint.x, mStartPoint.y);
                    } else if (x > mEndPoint.x) {
                        mPoint.set(mEndPoint.x, mEndPoint.y);
                    } else {
                        mPoint.set((int) event.getX(), (int) mHeight / 2);
                    }
                    if (mOnSeekBarChangeListener != null) {
                        mOnSeekBarChangeListener.onStartTrackingTouch(null);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsTouching) {
                    float x = event.getX();
                    if ((x < mStartPoint.x)) {
                        mPoint.set(mStartPoint.x, mStartPoint.y);
                    } else if (x > mEndPoint.x) {
                        mPoint.set(mEndPoint.x, mEndPoint.y);
                    } else {
                        mPoint.set((int) event.getX(), (int) (mHeight / 2));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsTouching) {
                    float x = event.getX();
                    if ((x < mStartPoint.x)) {
                        mPoint.set(mStartPoint.x, mStartPoint.y);
                    } else if (x > mEndPoint.x) {
                        mPoint.set(mEndPoint.x, mEndPoint.y);
                    } else {
                        mPoint.set((int) event.getX(), (int) (mHeight / 2));
                    }
                    mIsTouching = false;
                    if (mOnSeekBarChangeListener != null) {
                        mOnSeekBarChangeListener.onStopTrackingTouch(null);
                    }
                }
                break;
        }
        updateProgress(mPoint);
        return true;
    }

    @Override
    protected void updateDimension(int width, int height) {
        super.updateDimension(width, height);
        mWidth = width;
        mHeight = height;

        mSliderRadius = mHeight / 2;
        mActiveLen = mWidth - mSliderRadius * 2;

        mPoint = new Point();
        mStartPoint = new Point((int) mSliderRadius, (int) mHeight / 2);
        mEndPoint = new Point((int) (mWidth - mSliderRadius), (int) mHeight / 2);
    }

    protected void updateProgress(Point point) {
        int progress = (int) ((point.x - mStartPoint.x) * (mMax - mMin) / mActiveLen + mMin);
        if (mProgress != progress) {
            setProgress(progress);
            if (mOnSeekBarChangeListener != null) {
                mOnSeekBarChangeListener.onProgressChanged(null, (int) getProgress(), false);
            }
        }
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
        mOnSeekBarChangeListener = listener;
    }

    public boolean isValidPoint(float x, float y) {
        return ((x > 0) && (x < mWidth) && (y > 0) && (y < mHeight));
    }
}

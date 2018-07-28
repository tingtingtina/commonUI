package com.tina.widgetlibrary.widget.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.math.MathUtils;
import android.util.AttributeSet;
import android.view.View;

import com.tina.widgetlibrary.R;

/*
 * Create by Tina
 * Date: 2018/7/8
 * Description：
 */
public class BaseProgress extends View {

    public int mWidth;
    public int mHeight;
    protected int mMin;
    private boolean mMinInitialized;
    protected int mMax;
    private boolean mMaxInitialized;
    protected int mProgress;

    public BaseProgress(Context context) {
        this(context, null);
    }

    public BaseProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        initProgress();
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.BaseProgress, defStyleAttr, 0);
        setMin(a.getInt(R.styleable.BaseProgress_min, mMin));
        setMax(a.getInt(R.styleable.BaseProgress_max, mMax));
        setProgress(a.getInt(R.styleable.BaseProgress_progress, mProgress));

        a.recycle();
    }

    public void initProgress() {
        mMin = 0;
        mMax = 100;
        mProgress = 0;
    }

    /**
     * <p>Set the lower range of the progress bar to <tt>min</tt>.</p>
     *
     * @param min the lower range of this progress bar
     * @see #getMin()
     */
    public synchronized void setMin(int min) {
        if (mMaxInitialized) {
            if (min > mMax) {
                min = mMax;
            }
        }
        mMinInitialized = true;
        if (mMaxInitialized && min != mMin) {
            mMin = min;
            postInvalidate();

            if (mProgress < min) {
                mProgress = min;
            }
            refreshProgress(mProgress);
        } else {
            mMin = min;
        }
    }

    /**
     * <p>Set the upper range of the progress bar <tt>max</tt>.</p>
     *
     * @param max the upper range of this progress bar
     * @see #getMax()
     */
    public synchronized void setMax(int max) {
        if (mMinInitialized) {
            if (max < mMin) {
                max = mMin;
            }
        }
        mMaxInitialized = true;
        if (mMinInitialized && max != mMax) {
            mMax = max;
            postInvalidate();

            if (mProgress > max) {
                mProgress = max;
            }
            refreshProgress(mProgress);
        } else {
            mMax = max;
        }
    }

    /**
     * <p>Return the lower limit of this progress bar's range.</p>
     *
     * @return a positive int
     */
    public synchronized int getMin() {
        return mMin;
    }

    /**
     * <p>Return the upper limit of this progress bar's range.</p>
     *
     * @return a positive int
     **/
    public synchronized int getMax() {
        return mMax;
    }

    /**
     * @param progress the new progress, between {@link #getMin()} and {@link #getMax()}
     */
    public void setProgress(int progress) {
        progress = MathUtils.clamp(progress, mMin, mMax);

        if (progress == mProgress) {
            // No change from current.
            return;
        }
        refreshProgress(progress);
    }

    public int getProgress() {
        return mProgress;
    }

    public void refreshProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);//设置控件的宽高
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateDimension(w, h);
    }

    /**
     * 更新与控件大小相关的参数
     *
     * @param width
     * @param height
     */
    protected void updateDimension(int width, int height) {

    }
}

package com.tina.widgetlibrary.widget.progress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

import com.tina.widgetlibrary.R;

/*
 * Create by Tina
 * Date: 2018/7/8
 * Description：
 */
public class BaseCircleProgress extends BaseProgress {

    /**
     * Duration of smooth progress animations.
     */
    private static final int PROGRESS_ANIM_DURATION = 200;

    /**
     * Interpolator used for smooth progress animations.
     */
    private static final DecelerateInterpolator PROGRESS_ANIM_INTERPOLATOR =
            new DecelerateInterpolator();

    public final static double PI = 3.1415926;

    protected float mCenterX; // 中心X轴坐标
    protected float mCenterY; // 中心Y轴坐标

    protected float mStartAngle;
    protected float mSpanAngle;
    protected float mRadius;

    protected float mFrameWidth;
    protected int mFrameColor;
    protected float mPadding;
    protected float mBaseWidth;
    protected int mBaseColor; // 留白颜色
    protected int mProgressColor;
    protected float mProgressWidth;
    protected RectF mProgressRectF;
    protected Paint mPaint; //绘制进度

    protected Paint mTextPaint;//绘制文字
    protected int mTextColor;
    protected float mTextSize;
    protected Paint mSuffixTextPaint;//绘制文字
    protected int mSuffixTextColor;
    protected float mSuffixTextSize;
    protected String mPrefixText = "";
    protected String mSuffixText = "%";

    protected float mAngle;
    private ValueAnimator mAnimator;

    public BaseCircleProgress(Context context) {
        this(context, null);
    }

    public BaseCircleProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseCircleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        super.init(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.BaseCircleProgress, defStyleAttr, 0);
        mStartAngle = a.getFloat(R.styleable.BaseCircleProgress_cp_start_angle, mStartAngle);
        mSpanAngle = a.getFloat(R.styleable.BaseCircleProgress_cp_span_angle, mSpanAngle);
        mProgressColor = a.getColor(R.styleable.BaseCircleProgress_cp_progress_color, mProgressColor);
        mProgressWidth = a.getDimension(R.styleable.BaseCircleProgress_cp_progress_width, mProgressWidth);
        mFrameColor = a.getColor(R.styleable.BaseCircleProgress_cp_frame_color, mFrameColor);
        mFrameWidth = a.getDimension(R.styleable.BaseCircleProgress_cp_frame_width, mFrameWidth);
        mBaseWidth = a.getDimension(R.styleable.BaseCircleProgress_cp_base_width, mBaseWidth);
        mBaseColor = a.getColor(R.styleable.BaseCircleProgress_cp_base_color, mBaseColor);

        mTextColor = a.getColor(R.styleable.BaseCircleProgress_cp_textColor, mTextColor);
        mTextSize = a.getDimension(R.styleable.BaseCircleProgress_cp_textSize, mTextSize);
        mSuffixTextColor = a.getColor(R.styleable.BaseCircleProgress_cp_suffix_textColor, mSuffixTextColor);
        mSuffixTextSize = a.getDimension(R.styleable.BaseCircleProgress_cp_suffix_textSize, mSuffixTextSize);
        mPadding = a.getDimension(R.styleable.BaseCircleProgress_cp_padding, mPadding);
        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mFrameColor);
        mPaint.setStrokeWidth(mFrameWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);

        mSuffixTextPaint = new TextPaint();
        mSuffixTextPaint.setColor(mSuffixTextColor);
        mSuffixTextPaint.setTextSize(mSuffixTextSize);
        mSuffixTextPaint.setAntiAlias(true);
    }

    @Override
    public void initProgress() {
        super.initProgress();
        mStartAngle = -90;
        mSpanAngle = 360;
        mProgressColor = Color.parseColor("#FF01DCF2");
        mFrameColor = Color.parseColor("#FF1E90FF");
        mBaseColor = Color.parseColor("#00000000");
        mFrameWidth = 0;
        mProgressWidth = 8;
        mBaseWidth = mProgressWidth;
        mTextColor = Color.parseColor("#FF000000");
        mTextSize = 14;
        mSuffixTextColor = Color.parseColor("#00000000");
        mSuffixTextSize = 0;
        mPadding = 0;
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(float startAngle) {
        this.mStartAngle = startAngle;
        invalidate();
    }

    @Override
    public void refreshProgress(int progress) {
        mAnimator = ValueAnimator.ofInt(mProgress, progress);
        mAnimator.setDuration(PROGRESS_ANIM_DURATION);
        mAnimator.setInterpolator(PROGRESS_ANIM_INTERPOLATOR);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                value = (value > 0.01) ? value : 0;
                mProgress = value;
                invalidate();
            }
        });
        mAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width >= height) {
            width = height;
        } else {
            height = width;
        }
        setMeasuredDimension(width, height);//设置控件的宽高
    }

    @Override
    protected void updateDimension(int width, int height) {
        super.updateDimension(width, height);
        mWidth = mHeight = Math.min(width, height);
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        mRadius = mWidth / 2 - mPadding - mFrameWidth - mProgressWidth / 2;
        if (mProgressRectF == null) {
            mProgressRectF = new RectF(mCenterX - mRadius, mCenterY - mRadius,
                    mCenterX + mRadius, mCenterY + mRadius);
        } else {
            mProgressRectF.set(mCenterX - mRadius, mCenterY - mRadius,
                    mCenterX + mRadius, mCenterY + mRadius);
        }
    }
}

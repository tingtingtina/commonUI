package com.tina.widgetlibrary.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.tina.widgetlibrary.R;

/*
 * Create by Tina
 * Date: 2018/5/28
 * Description：开关View
 */
public class SwitchView extends View {

    protected static final float DEF_WIDTH_HEIGHT_RADIO = 1.8f;//控件宽高比
    protected static final float DEF_HEIGHT_RADIUS_RADIO = 3f;//控件宽高比
    protected static final float DEF_SLIDE_DELT = 3; //判定为滑动偏差值

    private int mCheckedFrameColor = Color.parseColor("#FF0098EF");// 边框颜色
    private int mUnCheckedFrameColor = Color.parseColor("#28000000");// 边框颜色
    private int mCheckedColor = Color.parseColor("#FFFFFF");//选中时滑块颜色
    private int mUnCheckedColor = Color.parseColor("#DCDCDC");// 未选中时滑块颜色
    private int mCheckedBackgroundColor = Color.parseColor("#FF0098EF");// 选中时背景颜色
    private int mUncheckedBackgroundColor = Color.parseColor("#FFFFFF");// 未选中时背景颜色

    private float mFrameWidth;//边框线宽
    private float mSliderRadius;//滑块半径

    private float mStartX; //起始坐标
    private float mLastX = 0; //第一次按下的有效区域
    private float mCurrentX = 0; //滚动的当前位置
    private float mCurrentAnimX; // 动画中位置
    private boolean mIsScrolled = false; //Switch 判断是否在拖动
    private boolean mSwitchOn = false; //Switch 开关状态，默认是  开：true
    // 控件长宽
    private float mWidth;
    private float mHeight;
    private Paint mPaint;

    private OnSwitchChangedListener mSwitchListener = null;

    public SwitchView(Context context) {
        this(context, null);
    }

    public SwitchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwitchView);

        mCheckedFrameColor = a.getColor(R.styleable.SwitchView_checked_frame_color, mCheckedFrameColor);
        mUnCheckedFrameColor = a.getColor(R.styleable.SwitchView_unchecked_frame_color, mUnCheckedFrameColor);
        mCheckedColor = a.getColor(R.styleable.SwitchView_checked_color, mCheckedColor);
        mUnCheckedColor = a.getColor(R.styleable.SwitchView_unchecked_color, mUnCheckedColor);
        mCheckedBackgroundColor = a.getColor(R.styleable.SwitchView_checked_background_color, mCheckedBackgroundColor);
        mUncheckedBackgroundColor = a.getColor(R.styleable.SwitchView_unchecked_background_color, mUncheckedBackgroundColor);
        mSliderRadius = a.getDimension(R.styleable.SwitchView_slider_radius, 0);
        mFrameWidth = a.getDimension(R.styleable.SwitchView_frame_width, 0);
        mSwitchOn = a.getBoolean(R.styleable.SwitchView_default_checked, false);
        a.recycle();

        updateDimension((int) mWidth, (int) mHeight);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    //重写onMeasure方法，计算控件大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width >= height * DEF_WIDTH_HEIGHT_RADIO) {
            width = (int) (height * DEF_WIDTH_HEIGHT_RADIO);
        } else {
            height = (int) (width / DEF_WIDTH_HEIGHT_RADIO);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float dis = mCurrentX;
        // 绘制滑动开关// 如果当前状态是滑动中 则动态绘制开关
        if (mIsScrolled && mLastX < mWidth / 2) { //手动 从左侧向右滑
            if (dis < mStartX) {
                dis = mStartX;
            } else if (dis > mWidth - mStartX) {
                dis = mWidth - mStartX;
            }
            drawSlider(canvas, mSwitchOn, dis);
        } else if (mIsScrolled && mLastX > mWidth / 2) {// 手动从右侧向左滑 关闭的
            if (dis < mStartX) {
                dis = mStartX;
            } else if (dis > mWidth - mStartX) {
                dis = mWidth - mStartX;
            }
            drawSlider(canvas, mSwitchOn, dis);
        } else if (mAnim != null && mAnim.isRunning()) { // 点击滑动中
            drawSlider(canvas, !mSwitchOn, mCurrentAnimX);
        } else {
            if (mSwitchOn) { // 绘制开关为开的状态 终态
                drawSlider(canvas, true, mWidth - mStartX);
            } else { // 绘制开关为关的状态
                drawSlider(canvas, false, mStartX);
            }
        }
    }

    //绘制滑块
    private void drawSlider(Canvas canvas, boolean switchOn, float positionX) {
        drawBackground(canvas, switchOn);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(switchOn && isEnabled() ? mCheckedColor : mUnCheckedColor);
        canvas.drawCircle(positionX, mHeight / 2, mSliderRadius, mPaint);
    }

    /**
     * 绘制背景颜色及边框
     *
     * @param canvas
     * @param switchOn
     */
    public void drawBackground(Canvas canvas, boolean switchOn) {
        // 绘制底色
        RectF rectF = new RectF(mFrameWidth / 2, mFrameWidth / 2, mWidth - mFrameWidth / 2, mHeight - mFrameWidth / 2);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(switchOn ? mCheckedBackgroundColor : mUncheckedBackgroundColor);
        canvas.drawRoundRect(rectF, (mHeight - mFrameWidth) / 2, (mHeight - mFrameWidth) / 2, mPaint);

        // 绘制边线
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mFrameWidth);
        mPaint.setColor(switchOn ? mCheckedFrameColor : mUnCheckedFrameColor);
        canvas.drawRoundRect(rectF, (mHeight - mFrameWidth) / 2, (mHeight - mFrameWidth) / 2, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsScrolled = false;
                mCurrentX = event.getX();
                mLastX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentX = event.getX();
                float mMoveDeltX = Math.abs(mCurrentX - mLastX);
                mIsScrolled = mMoveDeltX > DEF_SLIDE_DELT;
                if (mIsScrolled) {
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                mCurrentX = event.getX();
            case MotionEvent.ACTION_CANCEL:// ACTION_CANCEL事件发生时，event的X,Y坐标为屏幕的坐标，不是控件的坐标。所以不可取用
                if (mIsScrolled) {
                    mSwitchOn = mCurrentX > mWidth / 2;
                } else {
                    mSwitchOn = !mSwitchOn;
                }
                changeSwitch(mSwitchOn);
                if (mSwitchListener != null) {// 添加回调
                    mSwitchListener.onSwitchChange(mSwitchOn);
                }
                mIsScrolled = false;
                break;
        }
        invalidate();
        return true;
    }

    ValueAnimator mAnim;

    private void changeSwitch(boolean checked) {
        float dis = mCurrentX;
        if (dis < mStartX) {
            dis = mStartX;
        } else if (dis > mWidth - mStartX) {
            dis = mWidth - mStartX;
        }

        if (checked) {
            mAnim = ValueAnimator.ofFloat(dis, mWidth - mStartX);
        } else {
            mAnim = ValueAnimator.ofFloat(dis, mStartX);
        }
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        mAnim.setInterpolator(linearInterpolator);
        mAnim.setDuration(200);
        mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentAnimX = (float) animation.getAnimatedValue();
                //利用每一帧返回的值，可以做一些改变view大小，坐标，透明度等等操作
                invalidate();
            }
        });
        mAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        mAnim.start();
    }

    //设置控件的宽高
    private void updateDimension(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        if (width >= height * DEF_WIDTH_HEIGHT_RADIO) {
            mWidth = height * DEF_WIDTH_HEIGHT_RADIO;
            mHeight = height;
        } else {
            mWidth = width;
            mHeight = height / DEF_WIDTH_HEIGHT_RADIO;
        }
        if (mSliderRadius > mHeight / DEF_HEIGHT_RADIUS_RADIO) {
            mSliderRadius = mHeight / DEF_HEIGHT_RADIUS_RADIO;
        }
        mStartX = mHeight / 2;
    }

    //定义接口，实现回调
    public interface OnSwitchChangedListener {
        void onSwitchChange(boolean state);
    }

    /**
     * 设置 switch 状态监听
     *
     * @param listener OnSwitchChangedListener
     */
    public void setOnSwitchChangeListener(OnSwitchChangedListener listener) {
        mSwitchListener = listener;
    }

    /**
     * 设置选中的状态
     *
     * @param checked
     */
    public void setChecked(boolean checked) {
        mSwitchOn = checked;
        invalidate();
    }
}

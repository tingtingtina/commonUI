package com.tina.widgetlibrary.widget.colorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tina.widgetlibrary.R;

/**
 * @author Tina
 * @version V1.0
 * @date 2018/8/2
 */
public class ColorPickerView extends View {
    private static final float PI = 3.1415926f;

    private int[] mColors;          //色环数组
    private Shader mShader;
    private Paint mPaint;
    private Paint mPointerPaint; //三角Paint
    private Paint mOuterPaint; // 外圈
    private int mCurColor;
    private int mRotationAngle; //旋转角度

    private float mDiskWidth;
    private float mDiskPadding;
    private float mDiskRadius;

    public int mWidth;
    public int mHeight;
    protected int mCenterX; // 中心X轴坐标
    protected int mCenterY; // 中心Y轴坐标
    protected boolean mOnDisk; // 是否在色盘上

    protected float mOuterRingWidth;    // 外圆线宽
    protected float mOuterRingRadius;    // 外圆半径
    protected RectF mOuterRingRectF;
    protected int mOuterRingColor; // 外圆颜色

    protected float mPointerHeight; // 方向指示三角高度

    private OnColorChangedListener mListener;

    public ColorPickerView(Context context) {
        this(context, null);
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        mColors = new int[]{
                0xff00ffff, 0xff00ff00, 0xffffff00, 0xffff0000, 0xffff00ff,
                0xff0000ff, 0xff00ffff
        };

        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPickerView);
        mDiskWidth = a.getDimension(R.styleable.ColorPickerView_cp_color_disk_width, 20f);
        mDiskPadding = a.getDimension(R.styleable.ColorPickerView_cp_color_disk_padding, 10f);
        mOuterRingWidth = a.getDimension(R.styleable.ColorPickerView_cp_outer_ring_width, 2f);
        mOuterRingColor = a.getColor(R.styleable.ColorPickerView_cp_outer_ring_color, Color.parseColor("#20000000"));
        a.recycle();

        mShader = new SweepGradient(0, 0, mColors, null);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mDiskWidth);

        mOuterPaint = new Paint();
        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setColor(mOuterRingColor);
        mOuterPaint.setStrokeWidth(mOuterRingWidth);
        mOuterPaint.setAntiAlias(true);

        mPointerPaint = new Paint();
        mPointerPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawColorDisk(canvas);
    }

    private void drawColorDisk(Canvas canvas) {
        drawDisk(canvas);
        drawOuterCircle(canvas);
        if (isEnabled()) { // 使能的情况下才画指示箭头。否则不画指示箭头
            drawTriangle(canvas);
        }
    }

    private void drawDisk(Canvas canvas){
        canvas.translate(mCenterX, mCenterY);
        if (isEnabled()){
            mPaint.setShader(mShader);
            canvas.drawOval(new RectF(-mDiskRadius, -mDiskRadius, mDiskRadius, mDiskRadius), mPaint);
        }else{
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(mOuterRingColor);
            paint.setStrokeWidth(mDiskWidth);
            paint.setAntiAlias(true);
            canvas.drawCircle(0, 0, mDiskRadius, paint);
        }
    }
    /**
     * paint outer ring
     *
     * @param canvas
     */
    private void drawOuterCircle(Canvas canvas) {
        canvas.drawCircle(0, 0, mOuterRingRadius, mOuterPaint);
    }

    /**
     * paint pointer
     *
     * @param canvas
     */
    public void drawTriangle(Canvas canvas) {
        canvas.save();
        canvas.rotate(mRotationAngle);
        mPointerPaint.setColor(mCurColor);
        Path path = new Path();
        path.moveTo(-mPointerHeight, -mCenterY);
        path.lineTo(mPointerHeight, -mCenterY);
        path.lineTo(0, mPointerHeight - mCenterY);
        path.lineTo(-mPointerHeight, -mCenterY);
        canvas.drawPath(path, mPointerPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return super.onTouchEvent(event);
        } else {
            float x = event.getX() - mCenterX;
            float y = event.getY() - mCenterY;
            double dis = Math.sqrt(x * x + y * y);
            boolean isOnDisk = dis > (mDiskRadius - mDiskWidth) && dis < mCenterX;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mOnDisk = isOnDisk;
                    if (mOnDisk) {
                        updateView(event);
                    }
                    getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mOnDisk) {
                        updateView(event);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mOnDisk = false;
                    getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        }
    }

    /**
     * 更新控件
     *
     * @param event
     */
    private void updateView(MotionEvent event) {
        float x = event.getX() - mCenterX;
        float y = event.getY() - mCenterY;

        mRotationAngle = calcDegree((int) x, (int) -y);
        float angle = (float) Math.atan2(y, x);
        // need to turn angle [-PI ... PI] into unit [0....1]
        float unit = angle / (2 * PI);
        if (unit < 0) {
            unit += 1;
        }
        mCurColor = interpolationColor(mColors, unit);
        if (mListener != null) {
            mListener.onColorChanged(mCurColor);
        }
        invalidate();
    }

    private int ave(int s, int d, float p) {
        return s + Math.round(p * (d - s));
    }

    /**
     * 获取插值颜色
     *
     * @param colors 色组
     * @param unit   插值
     * @return color
     */
    private int interpolationColor(int colors[], float unit) {
        if (unit <= 0) {
            return colors[0];
        }
        if (unit >= 1) {
            return colors[colors.length - 1];
        }

        float interp = unit * (colors.length - 1);
        // interp = i.p  i: integer part, p: fractional part
        int i = (int) interp;
        float p = interp - i;

        // now p is just the fractional part [0...1) and i is the index
        int c0 = colors[i];
        int c1 = colors[i + 1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);

        return Color.argb(a, r, g, b);
    }

    /**
     * 触摸点的当前角度 View圆心为中心 先按照坐标轴转化
     *
     * @param x 触摸点x坐标
     * @param y 触摸点y坐标
     * @return 角度为点和圆点连线与view Y-的夹角
     */
    public int calcDegree(int x, int y) {
        Point point = new Point(x, y);
        return CalculateDegree.getRadianByPosition(point);
    }

    public void draw(int color) {
        mCurColor = color;
        mRotationAngle = calcDegreeByColor(color);
        invalidate();
    }

    /**
     * 根据颜色计算颜色所在位置的角度
     *
     * @param color current color
     * @return y负轴开始顺时针旋转角度
     */
    public int calcDegreeByColor(int color) {
        int red = ((color & 0x00FF0000) >> 16);
        int green = ((color & 0x0000FF00) >> 8);
        int blue = color & 0x000000FF;
        if (color != 0) {
            double s;
            double d;
            double p = 0;
            double unit;
            int i;
            if (green == 255) {
                if (red == 0) {
                    s = 255;
                    d = 0;
                    i = 0;
                    p = (float) ((blue - s) / (d - s) + i);
                } else if (blue == 0) {
                    s = 0;
                    d = 255;
                    i = 1;
                    p = (red - s) / (d - s) + i;
                }
            } else if (red == 255) {
                if (blue == 0) {
                    s = 255;
                    d = 0;
                    i = 2;
                    p = (green - s) / (d - s) + i;

                } else if (green == 0) {
                    s = 0;
                    d = 255;
                    i = 3;
                    p = (blue - s) / (d - s) + i;

                }
            } else if (blue == 255) {
                if (green == 0) {
                    s = 255;
                    d = 0;
                    i = 4;
                    p = (red - s) / (d - s) + i;

                } else if (red == 0) {
                    s = 0;
                    d = 255;
                    i = 5;
                    p = (green - s) / (d - s) + i;
                }
            }
            unit = p / 6;
            double angle = unit * 360; // 从x正轴开始顺时针旋转角度
            return (int) (angle + 90);
        } else {
            return 0;
        }
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
        mWidth = mHeight = Math.min(width, height);
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        mDiskRadius = (mWidth - mDiskWidth) / 2 - mDiskPadding;
        mOuterRingRadius = (mWidth - mOuterRingWidth) / 2;
        mOuterRingRectF = new RectF(-mOuterRingRadius, -mOuterRingRadius, mOuterRingRadius,
                mOuterRingRadius);
        mPointerHeight = mDiskPadding / 2;
    }

    public interface OnColorChangedListener {
        void onColorChanged(int color);
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.mListener = listener;
    }
}

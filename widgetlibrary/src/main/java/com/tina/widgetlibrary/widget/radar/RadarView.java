package com.tina.widgetlibrary.widget.radar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.tina.widgetlibrary.R;

import java.util.ArrayList;
import java.util.List;

public class RadarView extends View {
    private boolean DEBUG = false;

    private int mWidth;
    private int mHeight;
    private int mLayerCount;
    private int mItemCount;
    private int mLayoutColor;
    private float mLayoutWidth;
    private int mValueColor;
    private int mTitleColor;
    private float mTitleSize;
    private float mPadding;

    private Paint mLayerPaint;
    private Paint mValuePaint;
    private TextPaint mTitlePaint;

    private int mCenterX;
    private int mCenterY;
    private float mPreAngle; // 同一层相邻的两个点分别与圆心连线的夹角
    private float mRadius;

    private List<RadarBean> mValueList;

    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initParams();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RadarView);
        mLayerCount = a.getInteger(R.styleable.RadarView_radar_layerCount, mLayerCount);
        mItemCount = a.getInteger(R.styleable.RadarView_radar_itemCount, mItemCount);
        mLayoutColor = a.getColor(R.styleable.RadarView_radar_layerColor, mLayoutColor);
        mLayoutWidth = a.getDimension(R.styleable.RadarView_radar_layerWidth, mLayoutWidth);
        mValueColor = a.getColor(R.styleable.RadarView_radar_valueColor, mValueColor);
        mTitleColor = a.getColor(R.styleable.RadarView_radar_titleTextColor, mTitleColor);
        mTitleSize = a.getDimension(R.styleable.RadarView_radar_titleTextSize, mTitleSize);
        mPadding = a.getDimension(R.styleable.RadarView_radar_padding, mPadding);
        a.recycle();

        mPreAngle = (float) (Math.PI * 2 / mItemCount);

        if (DEBUG) {
            List<RadarBean> list = new ArrayList<>();
            for (int i = 0; i < mItemCount; i++) {
                list.add(new RadarBean("title " + i, 0, 100, 10 * i));
            }
            setData(list);
        }

        //雷达边线
        mLayerPaint = new Paint();
        mLayerPaint.setAntiAlias(true);
        mLayerPaint.setStrokeWidth(mLayoutWidth);
        mLayerPaint.setColor(mLayoutColor);
        mLayerPaint.setStyle(Paint.Style.STROKE);

        //数据区
        mValuePaint = new Paint();
        mValuePaint.setAntiAlias(true);
        mValuePaint.setColor(mValueColor);
        mValuePaint.setStyle(Paint.Style.FILL);

        mTitlePaint = new TextPaint();
        mTitlePaint.setAntiAlias(true);
        mTitlePaint.setTextSize(mTitleSize);
        mTitlePaint.setColor(Color.BLACK);
    }

    private void initParams() {
        mLayerCount = 5;
        mItemCount = 5;
        mLayoutWidth = 1f;
        mLayoutColor = Color.parseColor("#30000000");
        mValueColor = Color.parseColor("#B20E272C");
        mTitleColor = Color.BLACK;
        mPadding = 0;
        mTitleSize = 20f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(mCenterX, mCenterY);
        super.onDraw(canvas);
        drawLayer(canvas);
        drawValue(canvas);
        drawTitle(canvas);
    }

    /**
     * 绘制多边形
     *
     * @param canvas 画布
     */
    private void drawLayer(Canvas canvas) {
        Path path = new Path();
        float spacing = mRadius / mLayerCount;//蜘蛛丝之间的间距
        for (int i = 1; i <= mLayerCount; i++) {
            float curR = spacing * i;//当前半径
            path.reset();
            for (int j = 0; j < mItemCount; j++) {
                float x = (float) (curR * Math.sin(mPreAngle / 2 + mPreAngle * j));
                float y = (float) (curR * Math.cos(mPreAngle / 2 + mPreAngle * j));
                if (j == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
            path.close();
            canvas.drawPath(path, mLayerPaint);
        }
    }

    /**
     * 绘制覆盖区域
     *
     * @param canvas 画布
     */
    private void drawValue(Canvas canvas) {
        if (mValueList == null) return;
        Path path = new Path();

        for (int i = 0; i < mItemCount; i++) {
            RadarBean radarBean = mValueList.get(i);
            float percent = (radarBean.getCurValue() - radarBean.getMinValue()) / (radarBean.getMaxValue() - radarBean.getMinValue());
            float x = (float) (mRadius * Math.sin(mPreAngle / 2 + mPreAngle * i) * percent);
            float y = (float) (mRadius * Math.cos(mPreAngle / 2 + mPreAngle * i) * percent);
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
            //绘制小圆点
//            float pointR = Utils.dp2px(getContext(), valuePointRadius);
            float pointR = 0;
            float percentR = (mRadius - pointR) / mRadius;
            float pointX = (float) (mRadius * Math.sin(mPreAngle / 2 + mPreAngle * i) * percent * percentR);
            float pointY = (float) (mRadius * Math.cos(mPreAngle / 2 + mPreAngle * i) * percent * percentR);
            canvas.drawCircle(pointX, pointY, pointR, mValuePaint);
        }

        //绘制填充区域的边界
        path.close();
        canvas.drawPath(path, mValuePaint);
    }

    /**
     * 绘制文本
     *
     * @param canvas
     */
    private void drawTitle(Canvas canvas) {
        if (mValueList == null) return;
        Paint.FontMetrics fontMetrics = mTitlePaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        for (int i = 0; i < mItemCount; i++) {
            RadarBean data = mValueList.get(i);
            float x = (float) ((mRadius/* + fontHeight * 2*/) * Math.sin(mPreAngle / 2 + mPreAngle * i));
            float y = (float) ((mRadius/* + fontHeight * 2*/) * Math.cos(mPreAngle / 2 + mPreAngle * i));
            String title = data.getTitle();
            float dis = mTitlePaint.measureText(title);//文本长度
//            canvas.drawText(title, x - dis / 2, y, titlePaint);
            StaticLayout staticLayout = new StaticLayout(title, mTitlePaint,
                    (int) Math.ceil(StaticLayout.getDesiredWidth(title, mTitlePaint)),
                    Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, true);
            int width = staticLayout.getWidth();
            int height = staticLayout.getHeight();
            float disX = 0, disY = 0;
            if (y <= 0) {
                if (i == mItemCount / 2) { // 最顶端的点
                    disX = -1 * width / 2;
                    disY = -height/* - mPadding*/;
                } else if (x > 0) {
                    disX = 0;
                    disY = -1 * height / 2;
                } else if (x < 0) {
                    disX = -1 * width;
                    disY = -1 * height / 2;
                }
            } else {
                disX = -1 * width / 2;
                disY = 0;
            }

            canvas.save();
            canvas.translate(x + disX, y + disY);
            staticLayout.draw(canvas);
            canvas.restore();
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

    protected void updateDimension(int width, int height) {
        mWidth = mHeight = Math.min(width, height);
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        mRadius = mCenterX - mPadding;
    }

    public void setData(List<RadarBean> list) {
        if (list == null) return;
        mValueList = list;
        mItemCount = mValueList.size();
        invalidate();
    }
}

package com.tina.widgetlibrary.widget.radar;

public class RadarBean {
    private String mTitle;
    private float mMinValue;
    private float mMaxValue;
    private float mCurValue;

    public RadarBean(String title, float minValue, float maxValue, float curValue) {
        this.mTitle = title;
        this.mMinValue = minValue;
        this.mMaxValue = maxValue;
        this.mCurValue = curValue;
    }
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public float getMinValue() {
        return mMinValue;
    }

    public void setMinValue(float minValue) {
        this.mMinValue = minValue;
    }

    public float getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(float maxValue) {
        this.mMaxValue = maxValue;
    }

    public float getCurValue() {
        return mCurValue;
    }

    public void setCurValue(float curValue) {
        this.mCurValue = curValue;
    }
}

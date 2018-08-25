package com.tina.widgetlibrary.widget.bottombar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tina.widgetlibrary.R;
import com.tina.widgetlibrary.widget.Utils;

import java.util.Locale;

/*
 * Create by Tina
 * Date: 2018/8/25
 * Description：
 */
public class BottomBarItem extends LinearLayout {

    private Context mContext;

    private int mIconNormalResourceId;//普通状态图标的资源id
    private int mIconSelectedResourceId;//选中状态图标的资源id
    private String mText;//文本
    private int mTextSize;//文字大小
    private int mTextColorNormal;    //描述文本的默认显示颜色
    private int mTextColorSelected;  //述文本的默认选中显示颜色
    private int mMarginTop;//文字和图标的距离,默认0dp
    private boolean mIsOpenTouchBg;// 是否开启触摸背景
    private Drawable mTouchDrawable;//触摸时的背景
    private int mIconWidth;//图标的宽度
    private int mIconHeight;//图标的高度
    private int mItemPadding;//BottomBarItem的padding

    private ImageView mImageView;
    private TextView mTvUnread;
    private TextView mTvNotify;
    private TextView mTvMsg;
    private TextView mTextView;

    private int mUnreadTextSize; //未读数默认字体大小
    private int mMsgTextSize; //消息默认字体大小
    private int mUnreadNumThreshold;//未读数阈值
    private int mUnreadTextColor;//未读数字体颜色
    private Drawable mUnreadTextBg;
    private int mMsgTextColor;
    private Drawable mMsgTextBg;
    private Drawable mNotifyPointBg;

    public BottomBarItem(Context context) {
        this(context, null);
    }

    public BottomBarItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBarItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        mContext = context;
        initParams();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BottomBarItem);

        mIconNormalResourceId = a.getResourceId(R.styleable.BottomBarItem_bb_iconNormal, -1);
        mIconSelectedResourceId = a.getResourceId(R.styleable.BottomBarItem_bb_iconSelected, -1);

        mText = a.getString(R.styleable.BottomBarItem_bb_itemText);
        mTextSize = a.getDimensionPixelSize(R.styleable.BottomBarItem_bb_itemTextSize, Utils.sp2px(mContext, mTextSize));

        mTextColorNormal = a.getColor(R.styleable.BottomBarItem_bb_textColorNormal, mTextColorNormal);
        mTextColorSelected = a.getColor(R.styleable.BottomBarItem_bb_textColorSelected, mTextColorSelected);

        mMarginTop = a.getDimensionPixelSize(R.styleable.BottomBarItem_bb_itemMarginTop, Utils.dp2x(mContext, mMarginTop));

        mIsOpenTouchBg = a.getBoolean(R.styleable.BottomBarItem_bb_openTouchBg, mIsOpenTouchBg);
        mTouchDrawable = a.getDrawable(R.styleable.BottomBarItem_bb_touchDrawable);

        mIconWidth = a.getDimensionPixelSize(R.styleable.BottomBarItem_bb_iconWidth, 0);
        mIconHeight = a.getDimensionPixelSize(R.styleable.BottomBarItem_bb_iconHeight, 0);
        mItemPadding = a.getDimensionPixelSize(R.styleable.BottomBarItem_bb_itemPadding, 0);

        mUnreadTextSize = a.getDimensionPixelSize(R.styleable.BottomBarItem_bb_unreadTextSize, Utils.sp2px(mContext, mUnreadTextSize));
        mUnreadTextColor = a.getColor(R.styleable.BottomBarItem_bb_unreadTextColor, 0xFFFFFFFF);
        mUnreadTextBg = a.getDrawable(R.styleable.BottomBarItem_bb_unreadTextBg);

        mMsgTextSize = a.getDimensionPixelSize(R.styleable.BottomBarItem_bb_msgTextSize, Utils.sp2px(mContext, mMsgTextSize));
        mMsgTextColor = a.getColor(R.styleable.BottomBarItem_bb_msgTextColor, 0xFFFFFFFF);
        mMsgTextBg = a.getDrawable(R.styleable.BottomBarItem_bb_msgTextBg);

        mNotifyPointBg = a.getDrawable(R.styleable.BottomBarItem_bb_notifyPointBg);

        mUnreadNumThreshold = a.getInteger(R.styleable.BottomBarItem_bb_unreadThreshold, 99);
        a.recycle();
        checkValues();//检查值是否合法

        initView();//初始化相关操作
    }

    public void initParams() {
        mTextSize = 12;//文字大小 默认为12sp
        mTextColorNormal = 0xFF999999;
        mTextColorSelected = 0xFF46C01B;
        mIsOpenTouchBg = false;// 是否开启触摸背景，默认关闭
        mUnreadTextSize = 10;//未读数默认字体大小10sp
        mMsgTextSize = 6;//消息默认字体大小6sp
        mUnreadNumThreshold = 99;//未读数阈值
        mMarginTop = 0;//文字和图标的距离,默认0dp
    }

    /**
     * 检查传入的值是否完善
     */
    private void checkValues() {
        if (mIconNormalResourceId == -1) {
            throw new IllegalStateException("您还没有设置默认状态下的图标，请指定iconNormal的图标");
        }

        if (mIconSelectedResourceId == -1) {
            throw new IllegalStateException("您还没有设置选中状态下的图标，请指定iconSelected的图标");
        }

        if (mIsOpenTouchBg && mTouchDrawable == null) {
            //如果有开启触摸背景效果但是没有传对应的drawable
            throw new IllegalStateException("开启了触摸效果，但是没有指定touchDrawable");
        }

        if (mUnreadTextBg == null) {
            mUnreadTextBg = getResources().getDrawable(R.drawable.xml_unread_bg);
        }

        if (mMsgTextBg == null) {
            mMsgTextBg = getResources().getDrawable(R.drawable.xml_unread_bg);
        }

        if (mNotifyPointBg == null) {
            mNotifyPointBg = getResources().getDrawable(R.drawable.xml_unread_bg);
        }
    }

    private void initView() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        View view = View.inflate(mContext, R.layout.bottom_bar_item, null);
        if (mItemPadding != 0) {
            //如果有设置item的padding
            view.setPadding(mItemPadding, mItemPadding, mItemPadding, mItemPadding);
        }
        mImageView = view.findViewById(R.id.iv_icon);
        mTvUnread = view.findViewById(R.id.tv_unread_num);
        mTvMsg = view.findViewById(R.id.tv_msg);
        mTvNotify = view.findViewById(R.id.tv_point);
        mTextView = view.findViewById(R.id.tv_text);

        mImageView.setImageResource(mIconNormalResourceId);

        if (mIconWidth != 0 && mIconHeight != 0) {
            //如果有设置图标的宽度和高度，则设置ImageView的宽高
            FrameLayout.LayoutParams imageLayoutParams = (FrameLayout.LayoutParams) mImageView.getLayoutParams();
            imageLayoutParams.width = mIconWidth;
            imageLayoutParams.height = mIconHeight;
            mImageView.setLayoutParams(imageLayoutParams);
        }

        mTvUnread.setTextSize(TypedValue.COMPLEX_UNIT_PX, mUnreadTextSize);//设置未读数的字体大小
        mTvUnread.setTextColor(mUnreadTextColor);//设置未读数字体颜色
        mTvUnread.setBackground(mUnreadTextBg);//设置未读数背景

        mTvMsg.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMsgTextSize);//设置提示文字的字体大小
        mTvMsg.setTextColor(mMsgTextColor);//设置提示文字的字体颜色
        mTvMsg.setBackground(mMsgTextBg);//设置提示文字的背景颜色

        mTvNotify.setBackground(mNotifyPointBg);//设置提示点的背景颜色

        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);//设置底部文字字体大小
        mTextView.setTextColor(mTextColorNormal);//设置底部文字字体颜色
        mTextView.setText(mText);//设置标签文字

        LayoutParams textLayoutParams = (LayoutParams) mTextView.getLayoutParams();
        textLayoutParams.topMargin = mMarginTop;
        mTextView.setLayoutParams(textLayoutParams);

        if (mIsOpenTouchBg) {
            //如果有开启触摸背景
            setBackground(mTouchDrawable);
        }

        addView(view);
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public TextView getTextView() {
        return mTextView;
    }

    public void setIconNormalResourceId(int mIconNormalResourceId) {
        this.mIconNormalResourceId = mIconNormalResourceId;
    }

    public void setIconSelectedResourceId(int mIconSelectedResourceId) {
        this.mIconSelectedResourceId = mIconSelectedResourceId;
    }

    public void setStatus(boolean isSelected) {
        mImageView.setImageDrawable(getResources().getDrawable(isSelected ? mIconSelectedResourceId : mIconNormalResourceId));
        mTextView.setTextColor(isSelected ? mTextColorSelected : mTextColorNormal);
    }

    /**
     * 设置角标不可见
     *
     * @param tv
     */
    private void setTvVisible(TextView tv) {
        //都设置为不可见
        mTvUnread.setVisibility(GONE);
        mTvMsg.setVisibility(GONE);
        mTvNotify.setVisibility(GONE);

        tv.setVisibility(VISIBLE);//设置为可见
    }

    public int getUnreadNumThreshold() {
        return mUnreadNumThreshold;
    }

    public void setUnreadNumThreshold(int unreadNumThreshold) {
        this.mUnreadNumThreshold = unreadNumThreshold;
    }

    /**
     * 设置未读数
     *
     * @param unreadNum 小于等于 unreadNumThreshold 则隐藏，
     *                  大于0小于unreadNumThreshold则显示对应数字，
     *                  超过unreadNumThreshold 显示unreadNumThreshold+
     */
    public void setUnreadNum(int unreadNum) {
        setTvVisible(mTvUnread);
        if (unreadNum <= 0) {
            mTvUnread.setVisibility(GONE);
        } else if (unreadNum <= mUnreadNumThreshold) {
            mTvUnread.setText(String.valueOf(unreadNum));
        } else {
            mTvUnread.setText(String.format(Locale.CHINA, "%d+", mUnreadNumThreshold));
        }
    }

    public void setMsg(String msg) {
        setTvVisible(mTvMsg);
        mTvMsg.setText(msg);
    }

    public void hideMsg() {
        mTvMsg.setVisibility(GONE);
    }

    public void showNotify() {
        setTvVisible(mTvNotify);
    }

    public void hideNotify() {
        mTvNotify.setVisibility(GONE);
    }
}

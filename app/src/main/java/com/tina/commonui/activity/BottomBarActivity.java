package com.tina.commonui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.tina.commonui.BaseActivity;
import com.tina.commonui.R;
import com.tina.widgetlibrary.widget.bottombar.BottomBarItem;
import com.tina.widgetlibrary.widget.bottombar.BottomBarLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/*
 * Create by Tina
 * Date: 2018/8/25
 * Description：
 */
public class BottomBarActivity extends BaseActivity {
    @BindView(R.id.fl_content)
    FrameLayout mFlContent;
    @BindView(R.id.bb1)
    BottomBarItem mBb1;
    @BindView(R.id.bb2)
    BottomBarItem mBb2;
    @BindView(R.id.bb3)
    BottomBarItem mBb3;
    @BindView(R.id.bb4)
    BottomBarItem mBb4;
    @BindView(R.id.bbLayout)
    BottomBarLayout mBottomBarLayout;

    private List<TabFragment> mFragmentList = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_bottom_bar;
    }

    @Override
    public void initView() {

        TabFragment homeFragment = new TabFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString(TabFragment.CONTENT, "首页");
        homeFragment.setArguments(bundle1);
        mFragmentList.add(homeFragment);

        TabFragment videoFragment = new TabFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putString(TabFragment.CONTENT, "视频");
        videoFragment.setArguments(bundle2);
        mFragmentList.add(videoFragment);

        TabFragment microFragment = new TabFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putString(TabFragment.CONTENT, "微头条");
        microFragment.setArguments(bundle3);
        mFragmentList.add(microFragment);

        TabFragment meFragment = new TabFragment();
        Bundle bundle4 = new Bundle();
        bundle4.putString(TabFragment.CONTENT, "我的");
        meFragment.setArguments(bundle4);
        mFragmentList.add(meFragment);

        changeFragment(0); //默认显示第一页

        mBottomBarLayout.setOnItemSelectedListener(new BottomBarLayout.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final BottomBarItem bottomBarItem, int previousPosition, final int currentPosition) {
                Log.i("MainActivity", "position: " + currentPosition);

                changeFragment(currentPosition);

//                if (currentPosition == 0) {
//                    //如果是第一个，即首页
//                    if (previousPosition == currentPosition) {
//                        //如果是在原来位置上点击,更换首页图标并播放旋转动画
//                        bottomBarItem.setIconSelectedResourceId(R.mipmap.tab_loading);//更换成加载图标
//                        bottomBarItem.setStatus(true);
//
//                        //播放旋转动画
//                        if (mRotateAnimation == null) {
//                            mRotateAnimation = new RotateAnimation(0, 360,
//                                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//                                    0.5f);
//                            mRotateAnimation.setDuration(800);
//                            mRotateAnimation.setRepeatCount(-1);
//                        }
//                        ImageView bottomImageView = bottomBarItem.getImageView();
//                        bottomImageView.setAnimation(mRotateAnimation);
//                        bottomImageView.startAnimation(mRotateAnimation);//播放旋转动画
//
//                        //模拟数据刷新完毕
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                boolean tabNotChanged = mBottomBarLayout.getCurrentItem() == currentPosition; //是否还停留在当前页签
//                                bottomBarItem.setIconSelectedResourceId(R.mipmap.tab_home_selected);//更换成首页原来选中图标
//                                bottomBarItem.setStatus(tabNotChanged);//刷新图标
//                                cancelTabLoading(bottomBarItem);
//                            }
//                        }, 3000);
//                        return;
//                    }
//                }

//                //如果点击了其他条目
//                BottomBarItem bottomItem = mBottomBarLayout.getBottomItem(0);
//                bottomItem.setIconSelectedResourceId(R.mipmap.tab_home_selected);//更换为原来的图标
//                cancelTabLoading(bottomItem);//停止旋转动画
            }
        });

        mBottomBarLayout.setUnread(0, 20);//设置第一个页签的未读数为20
        mBottomBarLayout.setUnread(1, 1001);//设置第二个页签的未读数
        mBottomBarLayout.showNotify(2);//设置第三个页签显示提示的小红点
        mBottomBarLayout.setMsg(3, "NEW");//设置第四个页签显示NEW提示文字
    }
    private void changeFragment(int currentPosition) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_content, mFragmentList.get(currentPosition));
        transaction.commit();
    }

    /**
     * 停止首页页签的旋转动画
     */
    private void cancelTabLoading(BottomBarItem bottomItem) {
        Animation animation = bottomItem.getImageView().getAnimation();
        if (animation != null) {
            animation.cancel();
        }
    }

}

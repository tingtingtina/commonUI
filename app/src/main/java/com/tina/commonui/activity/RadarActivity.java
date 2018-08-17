package com.tina.commonui.activity;

import com.tina.commonui.BaseActivity;
import com.tina.commonui.R;
import com.tina.widgetlibrary.widget.radar.RadarBean;
import com.tina.widgetlibrary.widget.radar.RadarView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class RadarActivity extends BaseActivity {

    @BindView(R.id.radarView)
    RadarView mRadarView;
    @Override
    public int getLayoutId() {
        return R.layout.activity_radar;
    }

    @Override
    public void initView() {
        List<RadarBean> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new RadarBean("title " + i, 0, 100, 10 * i));
        }
        mRadarView.setData(list);
    }
}

package com.tina.commonui.activity;

import android.widget.Toast;

import com.tina.commonui.BaseActivity;
import com.tina.commonui.R;
import com.tina.widgetlibrary.widget.SwitchView;

import butterknife.BindView;

public class CommonUIActivity extends BaseActivity {

    @BindView(R.id.switchView)
    SwitchView mSwitchView;

    public int getLayoutId() {
        return R.layout.activity_common_ui;
    }

    @Override
    public void initView() {
        mSwitchView.setOnSwitchChangeListener(new SwitchView.OnSwitchChangedListener() {
            @Override
            public void onSwitchChange(boolean state) {
                Toast.makeText(CommonUIActivity.this,"state = " + state, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

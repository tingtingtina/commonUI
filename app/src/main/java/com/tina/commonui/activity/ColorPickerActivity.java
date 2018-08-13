package com.tina.commonui.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import com.tina.commonui.BaseActivity;
import com.tina.commonui.R;
import com.tina.widgetlibrary.widget.colorpicker.ColorPickerView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Tina
 * @version V1.0
 * @date 2018/8/13
 */
public class ColorPickerActivity extends BaseActivity {

    @BindView(R.id.colorPicker)
    ColorPickerView mColorPicker;
    @BindView(R.id.tvColor)
    View mTvColor;

    @Override
    public int getLayoutId() {
        return R.layout.activity_color_picker;
    }

    @Override
    public void initView() {

        mColorPicker.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                mTvColor.setBackgroundColor(color);
            }
        });
//        mColorPicker.setEnabled(false);
        mTvColor.setBackgroundColor(Color.RED);
    }

    @OnClick({R.id.tvColor})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvColor:
                mColorPicker.draw(((ColorDrawable) mTvColor.getBackground()).getColor());
                break;
        }
    }
}

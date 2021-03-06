package com.tina.commonui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tina.commonui.activity.BottomBarActivity;
import com.tina.commonui.activity.ColorPickerActivity;
import com.tina.commonui.activity.CommonUIActivity;
import com.tina.commonui.activity.RadarActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                startActivity(new Intent(MainActivity.this, CommonUIActivity.class));
                break;
            case R.id.btn2:
                startActivity(new Intent(MainActivity.this, ColorPickerActivity.class));
                break;
            case R.id.btn3:
                startActivity(new Intent(MainActivity.this, RadarActivity.class));
                break;
            case R.id.btn4:
                startActivity(new Intent(MainActivity.this, BottomBarActivity.class));
                break;
        }
    }
}

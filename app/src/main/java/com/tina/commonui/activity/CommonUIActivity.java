package com.tina.commonui.activity;

import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tina.commonui.BaseActivity;
import com.tina.commonui.R;
import com.tina.widgetlibrary.widget.SwitchView;
import com.tina.widgetlibrary.widget.progress.TSeekBar;
import com.tina.widgetlibrary.widget.progress.CircleProgress;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class CommonUIActivity extends BaseActivity {

    @BindView(R.id.switchView)
    SwitchView mSwitchView;
    @BindView(R.id.circleProgress)
    CircleProgress mCircleProgress;
    @BindView(R.id.seekBar)
    TSeekBar mSeekBar;
    @BindView(R.id.tvSeekBarValue)
    TextView mTvSeekBarValue;

    private int mProgress = 0;

    public int getLayoutId() {
        return R.layout.activity_common_ui;
    }

    @Override
    public void initView() {
        mSwitchView.setOnSwitchChangeListener(new SwitchView.OnSwitchChangedListener() {
            @Override
            public void onSwitchChange(boolean state) {
                Toast.makeText(CommonUIActivity.this, "state = " + state, Toast.LENGTH_SHORT).show();
            }
        });

        Observable.interval(3, 200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (mProgress >= mCircleProgress.getMax()) {
                            mProgress = mCircleProgress.getMin();
                        }
                        mCircleProgress.setProgress(mProgress);
                        mProgress++;
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        mSeekBar.setProgress(50);
        mTvSeekBarValue.setText(mSeekBar.getProgress() + "");
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTvSeekBarValue.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}

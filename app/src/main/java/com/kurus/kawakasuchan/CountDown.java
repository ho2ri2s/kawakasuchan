package com.kurus.kawakasuchan;

import android.os.CountDownTimer;

//厳密でないカウントダウンができるクラス。今回はカウントダウンに正確さは重視していないため採用。
public class CountDown extends CountDownTimer {

    public interface  OnFinishListener{
        void onFinish();
    }
    public interface  OnTickListener{
        void onTick();
    }

    OnFinishListener onFinishListener;
    OnTickListener onTickListener;

    public CountDown(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if(onTickListener != null)onTickListener.onTick();
    }

    @Override
    public void onFinish() {
        if(onFinishListener != null)onFinishListener.onFinish();
    }

    public void setOnTickListener(OnTickListener onTickListener){
        this.onTickListener = onTickListener;
    }

    public void setOnFinishListener(OnFinishListener onFinishListener){
        this.onFinishListener = onFinishListener;
    }
}

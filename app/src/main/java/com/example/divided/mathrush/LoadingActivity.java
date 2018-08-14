package com.example.divided.mathrush;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

public class LoadingActivity extends AppCompatActivity {

    CountDownTimer loadingTimer;
    TextView mTimeToStart;
    View view;
    ValueAnimator colorAnimator;

    private AVLoadingIndicatorView loadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.mLoadingIndicator);
        loadingIndicator.show();
        mTimeToStart = (TextView) findViewById(R.id.mTimeToStart);
        view = this.getWindow().getDecorView();

        colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), Color.WHITE, Color.BLACK);
        colorAnimator.setDuration(3000); // milliseconds
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {


            @Override
            public void onAnimationUpdate(ValueAnimator animator) {

                mTimeToStart.setTextColor((int) animator.getAnimatedValue());
                loadingIndicator.setIndicatorColor((int) animator.getAnimatedValue());
                view.setBackgroundColor(Color.BLACK - (int) animator.getAnimatedValue());

            }

        });

        colorAnimator.start();

        loadingTimer = new CountDownTimer(3000, 1) {

            public void onTick(long millisUntilFinished) {
                mTimeToStart.setText("Start in... " + ((millisUntilFinished / 1000) + 1));
            }

            public void onFinish() {

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
            }
        }.start();


    }

    @Override
    public void onBackPressed() {

    }
}

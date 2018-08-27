package com.example.divided.mathrush;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;

import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;
import com.wang.avi.AVLoadingIndicatorView;

public class LoadingActivity extends AppCompatActivity {

    CountDownTimer loadingTimer;
    TickerView mCounter;
    ConstraintLayout mLoadingLayout;
    private AVLoadingIndicatorView loadingIndicator;


    private void startCountingDown() {
        loadingTimer = new CountDownTimer(3000, 1) {

            public void onTick(long millisUntilFinished) {
                mCounter.setText(String.valueOf((millisUntilFinished / 1000) + 1));
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        loadingIndicator = findViewById(R.id.mLoadingIndicator);
        loadingIndicator.show();
        mLoadingLayout = findViewById(R.id.mLoadingLayout);
        mCounter = findViewById(R.id.mCounter);
        mCounter.setCharacterLists(TickerUtils.provideNumberList());

        startCountingDown();
    }


    @Override
    public void onBackPressed() {

    }
}

package com.example.divided.mathrush;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

public class LoadingActivity extends AppCompatActivity {

    CountDownTimer loadingTimer;
    TextView mTimeToStart;

    private AVLoadingIndicatorView loadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        loadingIndicator = findViewById(R.id.mLoadingIndicator);
        loadingIndicator.show();
        mTimeToStart = findViewById(R.id.mTimeToStart);

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

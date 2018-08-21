package com.example.divided.mathrush;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class StartGameActivity extends AppCompatActivity {

    Button mStartButton;
    Button mQuitButton;
    TextView mTitle;
    AnimationDrawable anim;
    ConstraintLayout container;

    View view;

    private void quitGame() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        container = findViewById(R.id.mStartGameLayout);
        mTitle = findViewById(R.id.mTitle);
        mStartButton = findViewById(R.id.mStartButton);
        mQuitButton = findViewById(R.id.mQuitButton2);

        //anim = (AnimationDrawable) container.getBackground();
        //anim.setEnterFadeDuration(6000);
       // anim.setExitFadeDuration(2000);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mStartButton.setEnabled(false);
                mQuitButton.setEnabled(false);

                MediaPlayer startSoundPlayer = MediaPlayer.create(getApplicationContext(), R.raw.start_click_new);
                startSoundPlayer.start();

                final CountDownTimer gameStartTimer = new CountDownTimer(1000, 1000) {


                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        Intent intent = new Intent(getBaseContext(), LoadingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                };
                gameStartTimer.start();
            }
        });

        mQuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitGame();
            }
        });


        mStartButton.startAnimation(AnimationUtils.loadAnimation(this,R.anim.start_button_animation));
        mTitle.startAnimation(AnimationUtils.loadAnimation(this,R.anim.start_button_animation));
        mQuitButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.smooth_appear_aniamtion));




    }

    @Override
    protected void onResume() {
        super.onResume();
       // if (anim != null && !anim.isRunning())
       //     anim.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
       // if (anim != null && anim.isRunning())
        //    anim.stop();
    }
}

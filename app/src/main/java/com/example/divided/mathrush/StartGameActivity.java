package com.example.divided.mathrush;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class StartGameActivity extends AppCompatActivity {

    Button mStartButton;
    Button mQuitButton;
    ImageButton mSettings;
    TextView mTitle;
    AnimationDrawable anim;
    ConstraintLayout container;
    View view;
    private boolean soundEnabled;
    private boolean vibrationEnabled;

    public void getSettings() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        soundEnabled = sharedPreferences.getBoolean("ENABLE_SOUND_EFFECTS", true);
        vibrationEnabled = sharedPreferences.getBoolean("ENABLE_VIBRATION", true);

    }

    private void setupTitle() {
        String firstWord = "Math\n";
        String secondWord = "Rush";
        Spannable titleText = new SpannableString(firstWord + secondWord);
        titleText.setSpan(
                new RelativeSizeSpan(0.5f)
                , 0
                , firstWord.length()
                , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        titleText.setSpan(
                new StyleSpan(android.graphics.Typeface.BOLD)
                , firstWord.length()
                , firstWord.length() + secondWord.length()
                , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        titleText.setSpan(
                new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL)
                , 0
                , firstWord.length() + secondWord.length()
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        titleText.setSpan(
                new UnderlineSpan()
                , 0
                , firstWord.length()
                , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        titleText.setSpan(
                new ForegroundColorSpan(Color.WHITE)
                , 0
                , firstWord.length()
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        //mTitle.setShadowLayer(1.6f,1.5f,1.3f,Color.WHITE);

        mTitle.setText(titleText);

    }

    private void quitGame() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        getSettings();

        container = findViewById(R.id.mStartGameLayout);
        mTitle = findViewById(R.id.mTitle);
        setupTitle();
        mStartButton = findViewById(R.id.mStartButton);
        mQuitButton = findViewById(R.id.mQuitButton2);
        mSettings = findViewById(R.id.mSettings);


//anim = (AnimationDrawable) container.getBackground();
        //anim.setEnterFadeDuration(6000);
        // anim.setExitFadeDuration(2000);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mStartButton.setEnabled(false);
                mQuitButton.setEnabled(false);

                if (soundEnabled == true) {
                    MediaPlayer startSoundPlayer = MediaPlayer.create(getApplicationContext(), R.raw.start_click_new);
                    startSoundPlayer.start();
                }

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

        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        mStartButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.start_button_animation));
        mTitle.startAnimation(AnimationUtils.loadAnimation(this, R.anim.title_text_animation));
        mQuitButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.quit_button_animation));
        mSettings.startAnimation(AnimationUtils.loadAnimation(this, R.anim.quit_button_animation));

    }

    @Override
    protected void onResume() {
        super.onResume();
       /* mTitle.getAnimation().start();
        mStartButton.getAnimation().start();
        */
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if(mTitle.getAnimation()!=null){
            mTitle.getAnimation().
        }

        if(mStartButton.getAnimation()!=null){
            mTitle.getAnimation().cancel();
        }*/


    }
}
